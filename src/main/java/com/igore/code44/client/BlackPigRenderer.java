package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.BlackPigEntity;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlackPigRenderer extends MobRenderer<BlackPigEntity, PigModel<BlackPigEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/black_animal.png");

    public BlackPigRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(BlackPigEntity entity) {
        return TEXTURE;
    }
}
