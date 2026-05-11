package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.BlackChickenEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlackChickenRenderer extends MobRenderer<BlackChickenEntity, ChickenModel<BlackChickenEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/black_animal.png");

    public BlackChickenRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(BlackChickenEntity entity) {
        return TEXTURE;
    }
}
