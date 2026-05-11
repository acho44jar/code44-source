package com.igore.code44.client;

import com.igore.code44.entity.PastMistakesDoorEntity;
import com.igore.code44.registry.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.joml.Matrix4f;

public class PastMistakesDoorRenderer extends EntityRenderer<PastMistakesDoorEntity> {
    private static final ResourceLocation DUMMY_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/block/oak_door.png");
    private final BlockRenderDispatcher blockRenderer;

    public PastMistakesDoorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(PastMistakesDoorEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));

        renderFrame(poseStack, buffer, packedLight);
        renderDoors(poseStack, buffer, packedLight, entity.getDirection());
        renderFloatingText(entity, poseStack, buffer, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderFrame(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState block = ModBlocks.EFBUI_VOID_BLOCK.get().defaultBlockState();

        for (int x = -2; x <= 1; x++) {
            for (int y = 0; y <= 3; y++) {
                boolean doorway = (x == -1 || x == 0) && (y == 0 || y == 1);
                if (doorway) {
                    continue;
                }

                poseStack.pushPose();
                poseStack.translate(x, y, 0.0D);
                blockRenderer.renderSingleBlock(block, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }

    private void renderDoors(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Direction direction) {
        BlockState leftLower = Blocks.OAK_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, direction)
                .setValue(DoorBlock.HINGE, net.minecraft.world.level.block.state.properties.DoorHingeSide.LEFT)
                .setValue(DoorBlock.OPEN, false)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState leftUpper = leftLower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
        BlockState rightLower = Blocks.OAK_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, direction)
                .setValue(DoorBlock.HINGE, net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT)
                .setValue(DoorBlock.OPEN, false)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState rightUpper = rightLower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);

        renderBlock(poseStack, buffer, packedLight, leftLower, -1, 0);
        renderBlock(poseStack, buffer, packedLight, leftUpper, -1, 1);
        renderBlock(poseStack, buffer, packedLight, rightLower, 0, 0);
        renderBlock(poseStack, buffer, packedLight, rightUpper, 0, 1);
    }

    private void renderBlock(PoseStack poseStack, MultiBufferSource buffer, int packedLight, BlockState state, int x, int y) {
        poseStack.pushPose();
        poseStack.translate(x, y, 0.015D);
        blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void renderFloatingText(PastMistakesDoorEntity entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Component text = entity.getDisplayName();
        poseStack.pushPose();
        poseStack.translate(0.0D, 3.12D, 0.065D);
        poseStack.scale(-0.013F, -0.013F, 0.013F);
        Matrix4f matrix = poseStack.last().pose();
        float x = -this.getFont().width(text) / 2.0F;
        this.getFont().drawInBatch(text, x, 0.0F, 0xFFFFFF, false, matrix, buffer, net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PastMistakesDoorEntity entity) {
        return DUMMY_TEXTURE;
    }
}
