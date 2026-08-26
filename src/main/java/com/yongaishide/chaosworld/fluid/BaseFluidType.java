package com.yongaishide.chaosworld.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * Classe base utilitária para facilitar a criação de tipos de fluidos com texturas e cores personalizadas no NeoForge.
 */
public class BaseFluidType extends FluidType {
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
    private final int tintColor;
    private final Vector3f fogColor;

    /**
     * Construtor padrão para definir todas as propriedades visuais e físicas de uma vez.
     *
     * @param stillTexture   Localização da textura do fluido quando parado (obrigatório).
     * @param flowingTexture Localização da textura do fluido quando escorrendo (obrigatório).
     * @param overlayTexture Localização da textura de sobreposição ao entrar no fluido (opcional, pode ser null).
     * @param tintColor      Cor de tingimento em formato ARGB (ex: 0xFFFFFFFF para não tingir).
     * @param fogColor       Vetor RGB para a cor da neblina quando o jogador está submerso.
     * @param properties     As propriedades físicas padrão do FluidType (densidade, viscosidade, sons, etc).
     */
    public BaseFluidType(final ResourceLocation stillTexture, final ResourceLocation flowingTexture, final ResourceLocation overlayTexture,
                         final int tintColor, final Vector3f fogColor, final Properties properties) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
    }

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    public int getTintColor() {
        return tintColor;
    }

    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    public Vector3f getFogColor() {
        return fogColor;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            public @NotNull ResourceLocation getStillTexture(FluidStack stack) {
                return stillTexture;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture(FluidStack stack) {
                return flowingTexture;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture(FluidStack stack) {
                return overlayTexture;
            }

            @Override
            public int getTintColor() {
                return tintColor;
            }

            @Override
            public int getTintColor(FluidStack stack) {
                return tintColor;
            }
        });
    }
}