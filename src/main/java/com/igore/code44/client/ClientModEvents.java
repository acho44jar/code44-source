package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.client.BlackChickenRenderer;
import com.igore.code44.client.BlackCowRenderer;
import com.igore.code44.client.BlackPigRenderer;
import com.igore.code44.client.DarkHorseBaglanRenderer;
import com.igore.code44.client.Err44rRenderer;
import com.igore.code44.client.FortyFourRenderer;
import com.igore.code44.client.MazeGuardianRenderer;
import com.igore.code44.client.PastMistakesDoorRenderer;
import com.igore.code44.client.SkinwalkerRenderer;
import com.igore.code44.client.WhiteDoorRenderer;
import com.igore.code44.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Code44Mod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityType.BAT, BatVoidRenderer::new);
        event.registerEntityRenderer(ModEntities.E44EFBUI.get(), E44efbuiRenderer::new);
        event.registerEntityRenderer(ModEntities.ZER000.get(), Zer000Renderer::new);
        event.registerEntityRenderer(ModEntities.OBSERVER.get(), ObserverRenderer::new);
        event.registerEntityRenderer(ModEntities.WHITE_NAME.get(), WhiteNameRenderer::new);
        event.registerEntityRenderer(ModEntities.PAST_MISTAKES_DOOR.get(), PastMistakesDoorRenderer::new);
        event.registerEntityRenderer(ModEntities.WHITE_DOOR.get(), WhiteDoorRenderer::new);
        event.registerEntityRenderer(ModEntities.FORTY_FOUR.get(), FortyFourRenderer::new);
        event.registerEntityRenderer(ModEntities.INVISIBLE_FORTY_FOUR.get(), InvisibleFortyFourRenderer::new);
        event.registerEntityRenderer(ModEntities.FOOTSTEPS.get(), FootstepsRenderer::new);
        event.registerEntityRenderer(ModEntities.DOOR_OPENER.get(), DoorOpenerRenderer::new);
        event.registerEntityRenderer(ModEntities.MINESHAFT_EFBUI.get(), MineshaftEfbuiRenderer::new);
        event.registerEntityRenderer(ModEntities.BLACK_COW.get(), BlackCowRenderer::new);
        event.registerEntityRenderer(ModEntities.BLACK_PIG.get(), BlackPigRenderer::new);
        event.registerEntityRenderer(ModEntities.BLACK_CHICKEN.get(), BlackChickenRenderer::new);
        event.registerEntityRenderer(ModEntities.SKINWALKER.get(), SkinwalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.ERR44R.get(), Err44rRenderer::new);
        event.registerEntityRenderer(ModEntities.DARK_HORSE_BAGLAN.get(), DarkHorseBaglanRenderer::new);
        event.registerEntityRenderer(ModEntities.MAZE_GUARDIAN.get(), MazeGuardianRenderer::new);
        event.registerEntityRenderer(ModEntities.GRETEMINOS.get(), GreteminosRenderer::new);
    }
}
