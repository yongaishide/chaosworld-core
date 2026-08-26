package com.yongaishide.chaosworld.client.renderer;

import com.yongaishide.chaosworld.client.model.ApocalypseTypeAModel;
import com.yongaishide.chaosworld.entity.custom.ApocalypseTypeAEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ApocalypseTypeARenderer extends GeoEntityRenderer<ApocalypseTypeAEntity> {
    public ApocalypseTypeARenderer(EntityRendererProvider.Context context) {
        super(context, new ApocalypseTypeAModel());
        this.shadowRadius = 1.2F;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @Nullable RenderType getRenderType(ApocalypseTypeAEntity animatable, ResourceLocation texture,
                                              @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
