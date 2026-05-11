package com.igore.code44.registry;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Code44Mod.MODID);

    public static final RegistryObject<EntityType<E44efbuiEntity>> E44EFBUI =
            ENTITY_TYPES.register("e44efbui",
                    () -> EntityType.Builder.of(E44efbuiEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "e44efbui").toString()));

    public static final RegistryObject<EntityType<Zer000Entity>> ZER000 =
            ENTITY_TYPES.register("zer000",
                    () -> EntityType.Builder.of(Zer000Entity::new, MobCategory.MONSTER)
                            .sized(4.2F, 14.8F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "zer000").toString()));

    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER =
            ENTITY_TYPES.register("observer",
                    () -> EntityType.Builder.of(ObserverEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "observer").toString()));

    public static final RegistryObject<EntityType<WhiteNameEntity>> WHITE_NAME =
            ENTITY_TYPES.register("white_name",
                    () -> EntityType.Builder.of(WhiteNameEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(12)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "white_name").toString()));

    public static final RegistryObject<EntityType<PastMistakesDoorEntity>> PAST_MISTAKES_DOOR =
            ENTITY_TYPES.register("past_mistakes_door",
                    () -> EntityType.Builder.of(PastMistakesDoorEntity::new, MobCategory.MONSTER)
                            .sized(4.0F, 4.3F)
                            .clientTrackingRange(12)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "past_mistakes_door").toString()));

    public static final RegistryObject<EntityType<WhiteDoorEntity>> WHITE_DOOR =
            ENTITY_TYPES.register("white_door",
                    () -> EntityType.Builder.of(WhiteDoorEntity::new, MobCategory.MONSTER)
                            .sized(4.0F, 4.3F)
                            .clientTrackingRange(12)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "white_door").toString()));

    public static final RegistryObject<EntityType<FortyFourEntity>> FORTY_FOUR =
            ENTITY_TYPES.register("44",
                    () -> EntityType.Builder.of(FortyFourEntity::new, MobCategory.MONSTER)
                            .sized(3.2F, 4.8F)
                            .clientTrackingRange(14)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "44").toString()));

    public static final RegistryObject<EntityType<InvisibleFortyFourEntity>> INVISIBLE_FORTY_FOUR =
            ENTITY_TYPES.register("invisible44",
                    () -> EntityType.Builder.of(InvisibleFortyFourEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 0.6F)
                            .clientTrackingRange(12)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "invisible44").toString()));

    public static final RegistryObject<EntityType<FootstepsEntity>> FOOTSTEPS =
            ENTITY_TYPES.register("footsteps",
                    () -> EntityType.Builder.of(FootstepsEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "footsteps").toString()));

    public static final RegistryObject<EntityType<DoorOpenerEntity>> DOOR_OPENER =
            ENTITY_TYPES.register("door_opener",
                    () -> EntityType.Builder.of(DoorOpenerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "door_opener").toString()));

    public static final RegistryObject<EntityType<MineshaftEfbuiEntity>> MINESHAFT_EFBUI =
            ENTITY_TYPES.register("mineshaft_efbui",
                    () -> EntityType.Builder.of(MineshaftEfbuiEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "mineshaft_efbui").toString()));

    public static final RegistryObject<EntityType<BlackCowEntity>> BLACK_COW =
            ENTITY_TYPES.register("black_cow",
                    () -> EntityType.Builder.of(BlackCowEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 1.4F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "black_cow").toString()));

    public static final RegistryObject<EntityType<BlackPigEntity>> BLACK_PIG =
            ENTITY_TYPES.register("black_pig",
                    () -> EntityType.Builder.of(BlackPigEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 0.9F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "black_pig").toString()));

    public static final RegistryObject<EntityType<BlackChickenEntity>> BLACK_CHICKEN =
            ENTITY_TYPES.register("black_chicken",
                    () -> EntityType.Builder.of(BlackChickenEntity::new, MobCategory.CREATURE)
                            .sized(0.4F, 0.7F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "black_chicken").toString()));

    public static final RegistryObject<EntityType<SkinwalkerEntity>> SKINWALKER =
            ENTITY_TYPES.register("skinwalker",
                    () -> EntityType.Builder.of(SkinwalkerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(10)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "skinwalker").toString()));

    public static final RegistryObject<EntityType<Err44rEntity>> ERR44R =
            ENTITY_TYPES.register("err44r",
                    () -> EntityType.Builder.of(Err44rEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 3.2F)
                            .clientTrackingRange(14)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "err44r").toString()));

    public static final RegistryObject<EntityType<DarkHorseBaglanEntity>> DARK_HORSE_BAGLAN =
            ENTITY_TYPES.register("dark_horse_baglan",
                    () -> EntityType.Builder.of(DarkHorseBaglanEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(12)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "dark_horse_baglan").toString()));

    public static final RegistryObject<EntityType<MazeGuardianEntity>> MAZE_GUARDIAN =
            ENTITY_TYPES.register("maze_guardian",
                    () -> EntityType.Builder.of(MazeGuardianEntity::new, MobCategory.MONSTER)
                            .sized(1.1F, 4.2F)
                            .clientTrackingRange(14)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "maze_guardian").toString()));

    public static final RegistryObject<EntityType<GreteminosEntity>> GRETEMINOS =
            ENTITY_TYPES.register("greteminos",
                    () -> EntityType.Builder.of(GreteminosEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.15F)
                            .clientTrackingRange(12)
                            .build(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "greteminos").toString()));

    private ModEntities() {
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
