package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.E44efbuiEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class E44efbuiRenderer extends HumanoidMobRenderer<E44efbuiEntity, PlayerModel<E44efbuiEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/e44efbui.png");

    public E44efbuiRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(E44efbuiEntity entity) {
        return TEXTURE;
    }
}
