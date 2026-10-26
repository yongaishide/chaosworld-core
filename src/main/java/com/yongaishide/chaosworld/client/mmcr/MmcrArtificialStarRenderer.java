package com.yongaishide.chaosworld.client.mmcr;

import cn.howxu.mmcr.publicapi.client.render.ControllerRenderContext;
import cn.howxu.mmcr.publicapi.client.render.ControllerRenderer;
import cn.howxu.mmcr.publicapi.event.RegisterControllerRenderersEvent;
import cn.howxu.mmcr.publicapi.registration.RegistrationException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.yongaishide.chaosworld.ChaosWorld;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

/**
 * 「人造恒星」控制器渲染器——龙之研究（Draconic Evolution）能量核心式的球体。
 * <p>
 * 不再从 OBJ 模型取几何：{@link StarSphere} 生成的单位球（{@code 48×96 = 4608 quad}）才是本体，
 * 这样除了三角化，还能在<b>每个顶点上做光子学计算</b>——
 * </p>
 * <ol>
 *   <li><b>背面剔除</b>：{@code dot(法线, 视线) ≤ 0} 的 quad 直接不提交，一颗球只画面对镜头的那一半；</li>
 *   <li><b>体积感</b>：正对镜头的顶点最亮、掠射的顶点偏暗（临边昏暗），球体才有弧度；</li>
 *   <li><b>边缘热斑</b>：最后 8% 的掠射区再补一层暖色，模拟恒星临边之上的色球层。</li>
 * </ol>
 * <p>
 * 光晕（外加一圈 corona 面片）被移除了：只有球体本身，边缘靠临边热斑收住。
 * 尺寸、球心与旧 OBJ 方案完全一致（半径 33.3 格，即直径 66.6 格），OBJ 只保留了纹理。
 * </p>
 */
@EventBusSubscriber(modid = ChaosWorld.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MmcrArtificialStarRenderer implements ControllerRenderer {

    /**
     * 「人造恒星」机器 id。机器定义由 KubeJS 启动脚本注册
     * （{@code kubejs/startup_scripts/mmcr/artificial_star.js}），所以这里只存 id 字面量，
     * 不引用 mmcr 的机器定义类。
     */
    public static final ResourceLocation MACHINE_ID =
            ResourceLocation.fromNamespaceAndPath(ChaosWorld.MODID, "artificial_star");

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation STAR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ChaosWorld.MODID, "textures/block/obj/star_layer.png");

    /** 内核：不透明 + 无剔除（背面由 {@code dot} 在提交前剔掉，GL 侧不必再判）。 */
    private static final RenderType CORE_TYPE = RenderType.entityCutoutNoCull(STAR_TEXTURE);

    /** 与旧 OBJ 方案一致：74 model unit × 0.45 = 33.3 格半径。 */
    private static final float STAR_RADIUS = 33.3F;
    /** 临边昏暗：正对镜头 1.0，掠射 0.70。 */
    private static final float SHADE_MIN = 0.70F;
    /** 临边热斑：掠射区把颜色乘子抬回满亮，避免球缘出现一圈发暗的脏边。 */
    private static final float RIM_BOOST = 0.55F;

    private static final double CENTER_Y = 42.5D;
    /** 内核自转，度/tick（20 tick = 1 秒）。0.8 ≈ 16°/s，一圈 22.5 秒。 */
    private static final float SPIN_DEGREES_PER_TICK = 0.8F;
    /** 自转轴相对 Y 轴的倾斜角，避免「绕轴转」的机械感。 */
    private static final float TILT_DEGREES = 14.0F;

    private static final StarSphere SPHERE = StarSphere.create(StarSphere.RINGS, StarSphere.SEGMENTS);

    /** 复用的临时对象，render 每帧都会被调用，不要在里面 new。 */
    private static final Quaternionf TILT = new Quaternionf().rotationX((float) Math.toRadians(TILT_DEGREES));
    private static final Quaternionf YAW = new Quaternionf();
    private static final Quaternionf SPIN = new Quaternionf();
    private static final Vector3f VIEW = new Vector3f();
    private static final Vector3f LOCAL_VIEW = new Vector3f();

    public static final MmcrArtificialStarRenderer INSTANCE = new MmcrArtificialStarRenderer();

    private MmcrArtificialStarRenderer() {
    }

    @SubscribeEvent
    public static void onRegisterRenderer(RegisterControllerRenderersEvent event) {
        // 机器 id 由 KubeJS 脚本注册；脚本缺失时该 id 未注册，只告警，不让客户端崩。
        try {
            event.register(MACHINE_ID, INSTANCE);
        } catch (RegistrationException e) {
            LOGGER.warn("[chaosworld_core] 人造恒星渲染器未绑定（机器 id 未注册？请检查 kubejs/startup_scripts/mmcr/artificial_star.js）：{}",
                    e.getMessage());
        }
    }

    @Override
    public void render(ControllerRenderContext context, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !context.structure().formed()) {
            return;
        }

        // 球心相对控制器方块的偏移（结构 121x112x109，控制器在球壳上）。
        double x = 0.5D;
        double y = CENTER_Y;
        double z = 0.5D;
        Direction facing = context.facing();
        if (facing != null) {
            switch (facing) {
                case NORTH -> z = 39.5D;
                case SOUTH -> z = -38.5D;
                case WEST -> x = 39.5D;
                case EAST -> x = -38.5D;
                default -> {
                }
            }
        }

        // 球心 → 相机，做菲涅尔用；近处退化成 0 时给个兜底方向。
        BlockPos pos = context.controllerPos();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 eye = camera.getPosition();
        VIEW.set((float) (eye.x - (pos.getX() + x)),
                (float) (eye.y - (pos.getY() + y)),
                (float) (eye.z - (pos.getZ() + z)));
        if (VIEW.lengthSquared() < 1.0E-4F) {
            VIEW.set(0.0F, 0.0F, 1.0F);
        } else {
            VIEW.normalize();
        }

        float tick = minecraft.level.getGameTime() + context.partialTick();
        int light = LightTexture.FULL_BRIGHT;

        VertexConsumer core = bufferSource.getBuffer(CORE_TYPE);

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        // ---- 内核：贴图球，没有额外的光晕面片 ----
        poseStack.pushPose();
        applySpin(poseStack, tick * SPIN_DEGREES_PER_TICK);
        poseStack.scale(STAR_RADIUS, STAR_RADIUS, STAR_RADIUS);
        emitCore(core, poseStack.last(), light, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }

    /** 倾斜 14° 再绕 Y 轴自转；旋转写进 PoseStack，同时算出局部视线方向。 */
    private static void applySpin(PoseStack poseStack, float degrees) {
        YAW.rotationY((float) Math.toRadians(degrees % 360.0F));
        SPIN.set(TILT).mul(YAW);
        poseStack.mulPose(SPIN);
        SPIN.transformInverse(VIEW, LOCAL_VIEW);
    }

    /**
     * 内核：只画朝向相机的 quad；颜色逐顶点算，{@code mu = dot(顶点法线, 视线)}——
     * <ul>
     *   <li>{@code lum = SHADE_MIN + (1 - SHADE_MIN) * mu}：临边昏暗，球才有弧度；</li>
     *   <li>{@code (1 - mu)^4 * RIM_BOOST}：最外圈补一层暖白，读作恒星边缘的高光。</li>
     * </ul>
     */
    private static void emitCore(VertexConsumer consumer, PoseStack.Pose pose, int light, int overlay) {
        float[] vertices = SPHERE.vertices;
        float[] normals = SPHERE.normals;
        Vector3f view = LOCAL_VIEW;
        int vi = 0;
        int ni = 0;

        for (int q = 0; q < SPHERE.quads; q++) {
            float nx = normals[ni];
            float ny = normals[ni + 1];
            float nz = normals[ni + 2];
            ni += 3;

            if (nx * view.x + ny * view.y + nz * view.z <= 0.0F) {
                vi += 20;
                continue;
            }

            for (int k = 0; k < 4; k++) {
                int i = vi + k * 5;
                // 单位球上顶点本身就是法线。
                float vx = vertices[i];
                float vy = vertices[i + 1];
                float vz = vertices[i + 2];
                float mu = vx * view.x + vy * view.y + vz * view.z;
                if (mu < 0.0F) {
                    mu = 0.0F;
                }
                float lum = SHADE_MIN + (1.0F - SHADE_MIN) * mu;
                float q1 = 1.0F - mu;
                float rim = q1 * q1 * q1 * q1 * RIM_BOOST;
                int r = clamp255(255.0F * (lum + rim));
                int g = clamp255(255.0F * (lum + rim * 0.84F));
                int b = clamp255(255.0F * (lum + rim * 0.60F));
                consumer.addVertex(pose, vx, vy, vz)
                        .setColor(r, g, b, 255)
                        .setUv(vertices[i + 3], vertices[i + 4])
                        .setOverlay(overlay)
                        .setLight(light)
                        // 不能传真实法线：entity 着色器的顶点阶段会算
                        //   lightAccum = min(1, 0.6 * dot(太阳方向, n) + 0.6 * dot(月亮方向, n) + 0.4)
                        // 传球面法线的话夜里只剩月光的蓝色漫反射，整颗球又暗又偏蓝还带方向性阴影。
                        // 统一传「朝上」，白天太阳在上、夜里月亮在上，两种情况下 lightAccum 都≈1。
                        .setNormal(pose, 0.0F, 1.0F, 0.0F);
            }
            vi += 20;
        }
    }

    private static int clamp255(float v) {
        int i = (int) v;
        return i < 0 ? 0 : (i > 255 ? 255 : i);
    }

    /** 恒星本体在控制器视锥之外，必须关掉视锥剔除，否则靠近边缘时会整颗消失。 */
    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    /** 装置直径约 80 格，默认 64 格的渲染距离不够。 */
    @Override
    public int getViewDistance() {
        return 512;
    }
}
