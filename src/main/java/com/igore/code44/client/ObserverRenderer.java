package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.ObserverEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ObserverRenderer extends HumanoidMobRenderer<ObserverEntity, PlayerModel<ObserverEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/e44efbui.png");

    public ObserverRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ObserverEntity entity) {
        return TEXTURE;
    }
}
