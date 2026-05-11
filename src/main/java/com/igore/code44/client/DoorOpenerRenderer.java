package com.igore.code44.client;

import com.igore.code44.entity.DoorOpenerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DoorOpenerRenderer extends EntityRenderer<DoorOpenerEntity> {
    public DoorOpenerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DoorOpenerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    }

    @Override
    public ResourceLocation getTextureLocation(DoorOpenerEntity entity) {
        return net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;
    }
}
