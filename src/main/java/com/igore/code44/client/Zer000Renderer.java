package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.Zer000Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Zer000Renderer extends GeoEntityRenderer<Zer000Entity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/zer000_billboard_sheet.png");

    public Zer000Renderer(EntityRendererProvider.Context context) {
        super(context, new Zer000Model());
        this.shadowRadius = 0.35F;
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, Zer000Entity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        poseStack.scale(3.15F, 4.1F, 3.15F);
    }

    @Override
    public ResourceLocation getTextureLocation(Zer000Entity entity) {
        return TEXTURE;
    }
}
