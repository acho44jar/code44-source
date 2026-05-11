package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.WhiteNameEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class WhiteNameRenderer extends HumanoidMobRenderer<WhiteNameEntity, PlayerModel<WhiteNameEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/white_name.png");
    private static final double NAME_RENDER_DISTANCE_SQR = 512.0D * 512.0D;

    public WhiteNameRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteNameEntity entity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(WhiteNameEntity entity) {
        return true;
    }

    @Override
    protected void renderNameTag(WhiteNameEntity entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        double distanceSqr = this.entityRenderDispatcher.distanceToSqr(entity);

        if (distanceSqr > NAME_RENDER_DISTANCE_SQR) {
            return;
        }

        boolean seeThrough = !entity.isDiscrete();
        float nameOffset = entity.getNameTagOffsetY();
        poseStack.pushPose();
        poseStack.translate(0.0F, nameOffset, 0.0F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        Font font = this.getFont();
        float textOffset = (float) (-font.width(displayName) / 2);
        int backgroundColor = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        font.drawInBatch(displayName, textOffset, 0.0F, 553648127, false, matrix4f, bufferSource, seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, backgroundColor, packedLight);
        if (seeThrough) {
            font.drawInBatch(displayName, textOffset, 0.0F, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        }
        poseStack.popPose();
    }
}
