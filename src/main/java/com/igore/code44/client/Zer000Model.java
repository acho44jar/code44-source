package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.Zer000Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Zer000Model extends GeoModel<Zer000Entity> {
    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "geo/zer000_billboard.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/zer000_billboard_sheet.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "animations/zer000.animation.json");

    @Override
    public ResourceLocation getModelResource(Zer000Entity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Zer000Entity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Zer000Entity animatable) {
        return ANIMATION;
    }
}
