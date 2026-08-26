package com.yongaishide.chaosworld.client.model;

import com.yongaishide.chaosworld.entity.custom.ApocalypseTypeAEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ApocalypseTypeAModel extends DefaultedEntityGeoModel<ApocalypseTypeAEntity> {
    public ApocalypseTypeAModel() {
        super(ResourceLocation.fromNamespaceAndPath("chaosworld_core", "apocalypse_type_a"), "h_head");
    }

    @Override
    public ResourceLocation getTextureResource(ApocalypseTypeAEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("chaosworld_core", "textures/entity/apocalypse_type_a.png");
    }
}
