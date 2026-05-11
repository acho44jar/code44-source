package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.BlackCowEntity;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlackCowRenderer extends MobRenderer<BlackCowEntity, CowModel<BlackCowEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/black_animal.png");

    public BlackCowRenderer(EntityRendererProvider.Context context) {
        super(context, new CowModel<>(context.bakeLayer(ModelLayers.COW)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(BlackCowEntity entity) {
        return TEXTURE;
    }
}
