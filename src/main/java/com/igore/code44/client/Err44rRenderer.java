package com.igore.code44.client;

import com.igore.code44.entity.Err44rEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Err44rRenderer extends EntityRenderer<Err44rEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("code44", "textures/entity/err44r.png");
    private static final float VISUAL_SCALE = 10.0F;
    private static final float HALF_HEIGHT = VISUAL_SCALE * 0.5F;

    public Err44rRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(Err44rEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, HALF_HEIGHT, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(VISUAL_SCALE, VISUAL_SCALE, VISUAL_SCALE);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderPlane(poseStack, consumer);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(Err44rEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    private static void renderPlane(PoseStack poseStack, VertexConsumer consumer) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        vertex(consumer, pose, normal, -0.5F, -0.5F, 0.0F, 1.0F);
        vertex(consumer, pose, normal, 0.5F, -0.5F, 1.0F, 1.0F);
        vertex(consumer, pose, normal, 0.5F, 0.5F, 1.0F, 0.0F);
        vertex(consumer, pose, normal, -0.5F, 0.5F, 0.0F, 0.0F);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float u, float v) {
        consumer.vertex(pose, x, y, 0.0F)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(Err44rEntity entity) {
        return TEXTURE;
    }
}
