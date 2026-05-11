package com.igore.code44.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ambient.Bat;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BatVoidRenderer extends EntityRenderer<Bat> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("code44", "textures/entity/bat_void_square.png");

    public BatVoidRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(Bat entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.15D, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(1.9F, 1.9F, 1.9F);

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        vertex(consumer, pose, normal, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, packedLight);
        vertex(consumer, pose, normal, 0.5F, -0.5F, 1.0F, 0.0F, 1.0F, packedLight);
        vertex(consumer, pose, normal, 0.5F, 0.5F, 1.0F, 1.0F, 1.0F, packedLight);
        vertex(consumer, pose, normal, -0.5F, 0.5F, 0.0F, 1.0F, 1.0F, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Bat entity) {
        return TEXTURE;
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float u, float v, float alpha, int packedLight) {
        consumer.vertex(pose, x, y, 0.0F)
                .color(255, 255, 255, (int) (alpha * 255.0F))
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
    }
}
