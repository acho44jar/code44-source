package com.igore.code44.event;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.BlackChickenEntity;
import com.igore.code44.entity.BlackCowEntity;
import com.igore.code44.entity.BlackPigEntity;
import com.igore.code44.entity.DoorOpenerEntity;
import com.igore.code44.entity.DarkHorseBaglanEntity;
import com.igore.code44.entity.E44efbuiEntity;
import com.igore.code44.entity.Err44rEntity;
import com.igore.code44.entity.FortyFourEntity;
import com.igore.code44.entity.FootstepsEntity;
import com.igore.code44.entity.InvisibleFortyFourEntity;
import com.igore.code44.entity.GreteminosEntity;
import com.igore.code44.entity.MazeGuardianEntity;
import com.igore.code44.entity.MineshaftEfbuiEntity;
import com.igore.code44.entity.ObserverEntity;
import com.igore.code44.entity.PastMistakesDoorEntity;
import com.igore.code44.entity.SkinwalkerEntity;
import com.igore.code44.entity.WhiteDoorEntity;
import com.igore.code44.entity.WhiteNameEntity;
import com.igore.code44.entity.Zer000Entity;
import com.igore.code44.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Code44Mod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEventBusEvents {
    private ModEventBusEvents() {
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.E44EFBUI.get(), E44efbuiEntity.createAttributes().build());
        event.put(ModEntities.ZER000.get(), Zer000Entity.createAttributes().build());
        event.put(ModEntities.OBSERVER.get(), ObserverEntity.createAttributes().build());
        event.put(ModEntities.WHITE_NAME.get(), WhiteNameEntity.createAttributes().build());
        event.put(ModEntities.PAST_MISTAKES_DOOR.get(), PastMistakesDoorEntity.createAttributes().build());
        event.put(ModEntities.WHITE_DOOR.get(), WhiteDoorEntity.createAttributes().build());
        event.put(ModEntities.FORTY_FOUR.get(), FortyFourEntity.createAttributes().build());
        event.put(ModEntities.INVISIBLE_FORTY_FOUR.get(), InvisibleFortyFourEntity.createAttributes().build());
        event.put(ModEntities.FOOTSTEPS.get(), FootstepsEntity.createAttributes().build());
        event.put(ModEntities.DOOR_OPENER.get(), DoorOpenerEntity.createAttributes().build());
        event.put(ModEntities.MINESHAFT_EFBUI.get(), MineshaftEfbuiEntity.createAttributes().build());
        event.put(ModEntities.BLACK_COW.get(), BlackCowEntity.createAttributes().build());
        event.put(ModEntities.BLACK_PIG.get(), BlackPigEntity.createAttributes().build());
        event.put(ModEntities.BLACK_CHICKEN.get(), BlackChickenEntity.createAttributes().build());
        event.put(ModEntities.SKINWALKER.get(), SkinwalkerEntity.createAttributes().build());
        event.put(ModEntities.ERR44R.get(), Err44rEntity.createAttributes().build());
        event.put(ModEntities.DARK_HORSE_BAGLAN.get(), DarkHorseBaglanEntity.createAttributes().build());
        event.put(ModEntities.MAZE_GUARDIAN.get(), MazeGuardianEntity.createAttributes().build());
        event.put(ModEntities.GRETEMINOS.get(), GreteminosEntity.createAttributes().build());
    }

}
