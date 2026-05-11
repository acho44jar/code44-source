package com.igore.code44.client;

import com.igore.code44.entity.MazeGuardianEntity;
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

public class MazeGuardianRenderer extends EntityRenderer<MazeGuardianEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("code44", "textures/entity/maze_guardian.png");
    private static final float VISUAL_SCALE = 5.0F;
    private static final float HALF_HEIGHT = VISUAL_SCALE * 0.5F;
    private static final float GROUND_OFFSET = 1.35F;

    public MazeGuardianRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(MazeGuardianEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, HALF_HEIGHT - GROUND_OFFSET, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(VISUAL_SCALE, VISUAL_SCALE, VISUAL_SCALE);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderPlane(poseStack, consumer);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(MazeGuardianEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(MazeGuardianEntity entity) {
        return TEXTURE;
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
}
