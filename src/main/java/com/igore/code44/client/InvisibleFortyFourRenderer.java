package com.igore.code44.client;

import com.igore.code44.entity.InvisibleFortyFourEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;

public class InvisibleFortyFourRenderer extends EntityRenderer<InvisibleFortyFourEntity> {
    public InvisibleFortyFourRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(InvisibleFortyFourEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    }

    @Override
    public ResourceLocation getTextureLocation(InvisibleFortyFourEntity entity) {
        return net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;
    }
}
