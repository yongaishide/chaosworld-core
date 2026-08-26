package com.yongaishide.chaosworld.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;

public class StellarModelRegistry {

    public static final ResourceLocation STAR = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/star");
    public static final ResourceLocation BLUE_STAR = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/blue_star");
    public static final ResourceLocation NEUTRON_STAR = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/neutron_star");
    public static final ResourceLocation SPACE = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/space");
    public static final ResourceLocation CLIMBER = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/climber");
    public static final ResourceLocation OVERWORLD = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/overworld");
    public static final ResourceLocation THE_NETHER = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/the_nether");
    public static final ResourceLocation THE_END = ResourceLocation.fromNamespaceAndPath("chaosworld_core", "obj/the_end");

    private static final Map<String, ResourceLocation> RECIPE_MODEL_MAPPING = new HashMap<>();

    // Cache for baked models - populated after model loading
    private static final Map<ResourceLocation, BakedModel> MODEL_CACHE = new HashMap<>();

    static {
        RECIPE_MODEL_MAPPING.put("ufo.simulation.neutron_bombardment", NEUTRON_STAR);
        RECIPE_MODEL_MAPPING.put("ufo.simulation.stellar_synthesis", BLUE_STAR);
        RECIPE_MODEL_MAPPING.put("ufo.simulation.supernova_harvest", STAR);
        RECIPE_MODEL_MAPPING.put("ufo.simulation.diamond_pressure", STAR);
        RECIPE_MODEL_MAPPING.put("ufo.simulation.iron_core_fusion", STAR);
        RECIPE_MODEL_MAPPING.put("ufo.simulation.red_giant_collapse", STAR);
    }

    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(STAR, "standalone"));
        event.register(new ModelResourceLocation(BLUE_STAR, "standalone"));
        event.register(new ModelResourceLocation(NEUTRON_STAR, "standalone"));
        event.register(new ModelResourceLocation(SPACE, "standalone"));
        event.register(new ModelResourceLocation(CLIMBER, "standalone"));
        event.register(new ModelResourceLocation(OVERWORLD, "standalone"));
        event.register(new ModelResourceLocation(THE_NETHER, "standalone"));
        event.register(new ModelResourceLocation(THE_END, "standalone"));
    }

    public static ResourceLocation getModelForSimulation(String simulationName) {
        return RECIPE_MODEL_MAPPING.getOrDefault(simulationName, STAR);
    }

    /**
     * Retrieves the BakedModel for the given ResourceLocation.
     * Uses ModelResourceLocation with "standalone" variant, matching how we register them.
     */
    public static BakedModel getBakedModel(ResourceLocation loc) {
        BakedModel cached = MODEL_CACHE.get(loc);
        if (cached != null) {
            return cached;
        }

        ModelResourceLocation mrl = new ModelResourceLocation(loc, "standalone");
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(mrl);
        BakedModel missingModel = Minecraft.getInstance().getModelManager().getMissingModel();

        if (model != null && model != missingModel) {
            MODEL_CACHE.put(loc, model);
            return model;
        }
        return null;
    }

    /**
     * Clear the model cache - should be called on resource reload
     */
    public static void clearCache() {
        MODEL_CACHE.clear();
    }
}
