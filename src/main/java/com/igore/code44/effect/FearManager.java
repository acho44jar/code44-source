package com.igore.code44.effect;

import com.igore.code44.event.ModEventCatalog;
import com.igore.code44.event.ModEventCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

import java.util.Random;
import java.util.function.IntPredicate;

public final class FearManager {
    public static final String MOD_DATA_KEY = "code44";
    private static final String SESSION_INITIALIZED_KEY = "code44SessionInitialized";
    private static final String EFBUI_JOIN_DELAY_KEY = "code44EfbuiJoinDelay";
    private static final String EFBUI_JOIN_SENT_KEY = "code44EfbuiJoinSent";
    private static final String EFBUI_SPAWN_PENDING_KEY = "code44EfbuiSpawnPending";
    private static final String EFBUI_SPAWN_TRIGGER_TICK_KEY = "code44EfbuiSpawnTriggerTick";
    private static final String LULLABY_DELAY_KEY = "code44LullabyDelay";
    private static final String LULLABY_TRIGGERED_KEY = "code44LullabyTriggered";
    private static final String HEART_STARE_DELAY_KEY = "code44HeartStareDelay";
    private static final String HEART_STARE_TRIGGERED_KEY = "code44HeartStareTriggered";
    private static final String HEART_STARE_ACTIVE_UNTIL_KEY = "code44HeartStareActiveUntil";
    private static final String OBSERVER_NEXT_ATTEMPT_TICK_KEY = "code44ObserverNextAttemptTick";
    private static final String FOOTSTEPS_NEXT_ATTEMPT_TICK_KEY = "code44FootstepsNextAttemptTick";
    private static final String INVISIBLE_HIT_NEXT_ATTEMPT_TICK_KEY = "code44InvisibleHitNextAttemptTick";
    private static final String DOOR_KNOCK_NEXT_ATTEMPT_TICK_KEY = "code44DoorKnockNextAttemptTick";
    private static final String DOOR_OPEN_BURST_NEXT_ATTEMPT_TICK_KEY = "code44DoorOpenBurstNextAttemptTick";
    private static final String MINESHAFT_EFBUI_NEXT_ATTEMPT_TICK_KEY = "code44MineshaftEfbuiNextAttemptTick";
    private static final String FALSE_SECOND_PLAYER_NEXT_ATTEMPT_TICK_KEY = "code44FalseSecondPlayerNextAttemptTick";
    private static final String INVENTORY_DISTORTION_NEXT_ATTEMPT_TICK_KEY = "code44InventoryDistortionNextAttemptTick";
    private static final String HOME_REPLACEMENT_NEXT_ATTEMPT_TICK_KEY = "code44HomeReplacementNextAttemptTick";
    private static final String CHUNK_DISTORTION_NEXT_ATTEMPT_TICK_KEY = "code44ChunkDistortionNextAttemptTick";
    private static final String BOAT_BREAK_NEXT_ATTEMPT_TICK_KEY = "code44BoatBreakNextAttemptTick";
    private static final String FAKE_ITEM_BREAK_NEXT_ATTEMPT_TICK_KEY = "code44FakeItemBreakNextAttemptTick";
    private static final String TUNNEL_OBSERVER_NEXT_ATTEMPT_TICK_KEY = "code44TunnelObserverNextAttemptTick";
    private static final String BLACK_ANIMAL_NEXT_ATTEMPT_TICK_KEY = "code44BlackAnimalNextAttemptTick";
    private static final String BLACK_ANIMAL_DAY_KEY = "code44BlackAnimalDay";
    private static final String BLACK_ANIMAL_DAY_COUNT_KEY = "code44BlackAnimalDayCount";
    private static final String PAST_MISTAKES_DOOR_NEXT_ATTEMPT_TICK_KEY = "code44PastMistakesDoorNextAttemptTick";
    private static final String LONELY_DOOR_NEXT_ATTEMPT_TICK_KEY = "code44LonelyDoorNextAttemptTick";
    private static final String OUTDOOR_BAT_NEXT_ATTEMPT_TICK_KEY = "code44OutdoorBatNextAttemptTick";
    private static final String WRONG_MOB_NEXT_ATTEMPT_TICK_KEY = "code44WrongMobNextAttemptTick";
    private static final String GHOST_TREE_NEXT_ATTEMPT_TICK_KEY = "code44GhostTreeNextAttemptTick";
    private static final String PHOTO_SCREAMER_NEXT_ATTEMPT_TICK_KEY = "code44PhotoScreamerNextAttemptTick";
    private static final String YOU_ALIVE_NEXT_ATTEMPT_TICK_KEY = "code44YouAliveNextAttemptTick";
    private static final String LOOK_DOWN_NEXT_ATTEMPT_TICK_KEY = "code44LookDownNextAttemptTick";
    private static final String INVERT_MOUSE_NEXT_ATTEMPT_TICK_KEY = "code44InvertMouseNextAttemptTick";
    private static final String SENSITIVITY_SPIKE_NEXT_ATTEMPT_TICK_KEY = "code44SensitivitySpikeNextAttemptTick";
    private static final String MASTER_VOLUME_DROP_NEXT_ATTEMPT_TICK_KEY = "code44MasterVolumeDropNextAttemptTick";
    private static final String FORTY_FOUR_NEXT_ATTEMPT_TICK_KEY = "code44FortyFourNextAttemptTick";
    private static final String GODLOVEU_NEXT_ATTEMPT_TICK_KEY = "code44GodloveuNextAttemptTick";
    private static final String IMWATCH_NEXT_ATTEMPT_TICK_KEY = "code44ImwatchNextAttemptTick";
    private static final String SLEEPISSHORT_NEXT_ATTEMPT_TICK_KEY = "code44SleepisshortNextAttemptTick";
    private static final String URLIFE_NEXT_ATTEMPT_TICK_KEY = "code44UrlifeNextAttemptTick";
    private static final String YOUREND_NEXT_ATTEMPT_TICK_KEY = "code44YourendNextAttemptTick";
    private static final String SKINWALKER_NEXT_ATTEMPT_TICK_KEY = "code44SkinwalkerNextAttemptTick";
    private static final String CRYING_NEXT_ATTEMPT_TICK_KEY = "code44CryingNextAttemptTick";
    private static final String BROKEN_GLASS_NEXT_ATTEMPT_TICK_KEY = "code44BrokenGlassNextAttemptTick";
    private static final String BUTTON_CLICK_NEXT_ATTEMPT_TICK_KEY = "code44ButtonClickNextAttemptTick";
    private static final String WOODEN_PRESSURE_PLATE_NEXT_ATTEMPT_TICK_KEY = "code44WoodenPressurePlateNextAttemptTick";
    private static final String BOW_SHOT_NEXT_ATTEMPT_TICK_KEY = "code44BowShotNextAttemptTick";
    private static final String BLOCK_PLACE_NEXT_ATTEMPT_TICK_KEY = "code44BlockPlaceNextAttemptTick";
    private static final String BLOCK_BREAK_SOUND_NEXT_ATTEMPT_TICK_KEY = "code44BlockBreakSoundNextAttemptTick";
    private static final String TNT_IGNITE_NEXT_ATTEMPT_TICK_KEY = "code44TntIgniteNextAttemptTick";
    private static final String PIG_DEATH_NEXT_ATTEMPT_TICK_KEY = "code44PigDeathNextAttemptTick";
    private static final String MUSIC_DISC_11_NEXT_ATTEMPT_TICK_KEY = "code44MusicDisc11NextAttemptTick";
    private static final String FREE_HOUSE_NEXT_ATTEMPT_TICK_KEY = "code44FreeHouseNextAttemptTick";
    private static final String GLASS_FRAME_NEXT_ATTEMPT_TICK_KEY = "code44GlassFrameNextAttemptTick";
    private static final String MAZE_BUILDING_NEXT_ATTEMPT_TICK_KEY = "code44MazeBuildingNextAttemptTick";
    private static final String RAIN_NIGHT_NEXT_ATTEMPT_TICK_KEY = "code44RainNightNextAttemptTick";
    private static final String UNSKIPPABLE_NIGHT_NEXT_ATTEMPT_TICK_KEY = "code44UnskippableNightNextAttemptTick";
    private static final String UNSKIPPABLE_NIGHT_ACTIVE_UNTIL_KEY = "code44UnskippableNightActiveUntil";
    private static final String MEMORIAL_SIGN_DELAY_KEY = "code44MemorialSignDelay";
    private static final String MEMORIAL_SIGN_TRIGGERED_KEY = "code44MemorialSignTriggered";
    private static final String SECOND_DAY_ADVANCEMENT_GRANTED_KEY = "code44SecondDayAdvancementGranted";
    private static final String SECOND_DAY_ADVANCEMENT_DELAY_KEY = "code44SecondDayAdvancementDelay";
    private static final String DOORWAY_EFBUI_COOLDOWN_KEY = "code44DoorwayEfbuiCooldown";
    private static final String DOORWAY_EFBUI_DAY_KEY = "code44DoorwayEfbuiDay";
    private static final String DOORWAY_EFBUI_DAY_COUNT_KEY = "code44DoorwayEfbuiDayCount";
    private static final String ZER000_SPAWN_COUNT_KEY = "code44Zer000SpawnCount";
    private static final String ZER000_DAY_KEY = "code44Zer000Day";
    private static final String ZER000_DAY_COUNT_KEY = "code44Zer000DayCount";
    private static final String BOAT_BREAK_DAY_KEY = "code44BoatBreakDay";
    private static final String BOAT_BREAK_DAY_COUNT_KEY = "code44BoatBreakDayCount";
    private static final String BOAT_BREAK_NEXT_CHECK_GAME_TICK_KEY = "code44BoatBreakNextCheckGameTick";
    private static final String DOOR_KNOCK_DAY_KEY = "code44DoorKnockDay";
    private static final String DOOR_KNOCK_DAY_COUNT_KEY = "code44DoorKnockDayCount";
    private static final String DOOR_KNOCK_NEXT_CHECK_GAME_TICK_KEY = "code44DoorKnockNextCheckGameTick";
    private static final String MINESHAFT_EFBUI_ONCE_KEY = "code44MineshaftEfbuiOnce";
    private static final String TUNNEL_OBSERVER_ONCE_KEY = "code44TunnelObserverOnce";
    private static final String TUNNEL_DIMENSION_ORE_COUNTER_KEY = "code44TunnelDimensionOreCounter";
    private static final String TUNNEL_DIMENSION_ORE_TARGET_KEY = "code44TunnelDimensionOreTarget";
    private static final String NIGHT_JUMP_SUPPRESSED_DAY_KEY = "code44NightJumpSuppressedDay";
    private static final Random RANDOM = new Random();
    private static final int MIN_EVENT_GAP_TICKS = 450;
    public static final int LULLABY_DURATION_TICKS = 584;
    public static final int HEART_STARE_DURATION_TICKS = 600;

    private FearManager() {
    }

    public static ModEventCategory getCategory(String eventId) {
        return switch (eventId) {
            case ModEventCatalog.RANDOM_STRUCTURE, ModEventCatalog.MEMORIAL_SIGN, ModEventCatalog.SCREEN_BLINK -> ModEventCategory.ONE_TIME;
            case ModEventCatalog.RAIN_NIGHT, ModEventCatalog.UNSKIPPABLE_NIGHT,
                    ModEventCatalog.ZER000, ModEventCatalog.OBSERVER, ModEventCatalog.FOOTSTEPS,
                    ModEventCatalog.INVISIBLE_HIT,
                    ModEventCatalog.DOOR_KNOCK, ModEventCatalog.DOOR_OPEN_BURST,
                    ModEventCatalog.MINESHAFT_EFBUI, ModEventCatalog.EFBUI_STARE,
                    ModEventCatalog.FALSE_SECOND_PLAYER, ModEventCatalog.INVENTORY_DISTORTION,
                    ModEventCatalog.HOME_REPLACEMENT,
                    ModEventCatalog.CHUNK_DISTORTION,
                    ModEventCatalog.VILLAGER_STARE, ModEventCatalog.BOAT_BREAK,
                    ModEventCatalog.FAKE_ITEM_BREAK, ModEventCatalog.TUNNEL_OBSERVER,
                    ModEventCatalog.BLACK_ANIMAL,
                    ModEventCatalog.PAST_MISTAKES_DOOR, ModEventCatalog.LONELY_DOOR,
                    ModEventCatalog.WRONG_MOB, ModEventCatalog.GHOST_TREE,
                    ModEventCatalog.SKINWALKER,
                    ModEventCatalog.PHOTO_SCREAMER,
                    ModEventCatalog.YOU_ALIVE,
                    ModEventCatalog.LOOK_DOWN,
                    ModEventCatalog.INVERT_MOUSE,
                    ModEventCatalog.SENSITIVITY_SPIKE,
                    ModEventCatalog.MASTER_VOLUME_DROP,
                    ModEventCatalog.FORTY_FOUR,
                    ModEventCatalog.GODLOVEU, ModEventCatalog.IMWATCH, ModEventCatalog.SLEEPISSHORT,
                    ModEventCatalog.URLIFE, ModEventCatalog.YOUREND, ModEventCatalog.CRYING,
                    ModEventCatalog.BROKEN_GLASS, ModEventCatalog.BUTTON_CLICK,
                    ModEventCatalog.WOODEN_PRESSURE_PLATE, ModEventCatalog.BOW_SHOT,
                    ModEventCatalog.BLOCK_PLACE, ModEventCatalog.BLOCK_BREAK_SOUND,
                    ModEventCatalog.TNT_IGNITE, ModEventCatalog.PIG_DEATH,
                    ModEventCatalog.MUSIC_DISC_11,
                    ModEventCatalog.FREE_HOUSE, ModEventCatalog.GLASS_FRAME,
                    ModEventCatalog.TUNNEL_DIMENSION,
                    ModEventCatalog.MAZE_BUILDING -> ModEventCategory.REPEATING;
            case ModEventCatalog.LULLABY, ModEventCatalog.EFBUI_JOIN, ModEventCatalog.E44EFBUI_SPAWN,
                    ModEventCatalog.HEART_STARE, ModEventCatalog.EFBUI_DIMENSION -> ModEventCategory.SESSION;
            default -> ModEventCategory.REPEATING;
        };
    }

    public static int getCurrentDay(ServerLevel level) {
        return Math.max(0, (int) (level.getDayTime() / 24000L));
    }

    public static boolean isObserverUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isPastMistakesDoorUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isFortyFourUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isLonelyDoorUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isFootstepsUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isWrongMobUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isGhostTreeUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isPhotoScreamerUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isSkinwalkerUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isAccessDeniedUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isLocationAccessUnlocked(ServerLevel level) {
        return getCurrentDay(level) == 0;
    }

    public static boolean isLookDownUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isYouAliveUnlocked(ServerLevel level) {
        return getCurrentDay(level) == 0;
    }

    public static boolean isBlackAnimalUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isInvertMouseUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isSensitivitySpikeUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isMasterVolumeDropUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isGodLoveUUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isImWatchUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isSleepIsShortUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isUrLifeUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 4;
    }

    public static boolean isYourEndUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 6;
    }

    public static boolean isCryingUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 6;
    }

    public static boolean isBrokenGlassUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 4;
    }

    public static boolean isButtonClickUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isWoodenPressurePlateUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isBowShotUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isBlockPlaceUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isBlockBreakSoundUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static boolean isTntIgniteUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 5;
    }

    public static boolean isPigDeathUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 4;
    }

    public static boolean isMusicDisc11Unlocked(ServerLevel level) {
        return getCurrentDay(level) >= 6;
    }

    public static boolean isFreeHouseUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isGlassFrameUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 4;
    }

    public static boolean isMazeBuildingUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 6;
    }

    public static boolean isTunnelDimensionUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean shouldGrantSecondDayAdvancement(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (getCurrentDay(level) < 2 || data.getBoolean(SECOND_DAY_ADVANCEMENT_GRANTED_KEY)) {
            return false;
        }
        if (isRemainingDaySuppressed(level, player)) {
            return false;
        }

        long triggerTick = ensureDayWindowSchedule(level, data, SECOND_DAY_ADVANCEMENT_DELAY_KEY, 6000, 9000);
        return level.getDayTime() >= triggerTick;
    }

    public static void markSecondDayAdvancementGranted(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(SECOND_DAY_ADVANCEMENT_GRANTED_KEY, true);
        data.remove(SECOND_DAY_ADVANCEMENT_DELAY_KEY);
    }

    public static boolean isMemorialSignUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isDoorKnockUnlocked(ServerLevel level) {
        return getCurrentDay(level) <= 6;
    }

    public static boolean isDoorOpenBurstUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isInvisibleHitUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 4;
    }

    public static boolean isHeartStareUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 4;
    }

    public static boolean isVillagerStareUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isBoatBreakUnlocked(ServerLevel level) {
        return getCurrentDay(level) <= 3;
    }

    public static boolean isFakeItemBreakUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isZer000Unlocked(ServerLevel level) {
        return getCurrentDay(level) >= 6;
    }

    public static boolean isTunnelObserverUnlocked(ServerLevel level) {
        return true;
    }

    public static boolean isMineshaftEfbuiUnlocked(ServerLevel level) {
        return getCurrentDay(level) <= 6;
    }

    public static boolean isEfbuiStareUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 1;
    }

    public static boolean isEfbuiJoinUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 6;
    }

    public static boolean isBloodRainUnlocked(ServerLevel level) {
        return false;
    }

    public static boolean isRainNightUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 3;
    }

    public static boolean isUnskippableNightUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 6;
    }

    public static boolean isLullabyUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 7;
    }

    public static boolean isEfbuiDimensionUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 8;
    }

    public static boolean isFalseSecondPlayerUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 7;
    }

    public static boolean isInventoryDistortionUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 7;
    }

    public static boolean isFakeDeathUnlocked(ServerLevel level) {
        return false;
    }

    public static boolean isHomeReplacementUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 4;
    }

    public static boolean isChunkDistortionUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 2;
    }

    public static int getChunkDistortionChunkCount(ServerLevel level) {
        return switch (getCurrentDay(level)) {
            case 0, 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 4;
            case 5 -> 6;
            case 6 -> 8;
            default -> 10;
        };
    }

    public static boolean isFakeExitUnlocked(ServerLevel level) {
        return false;
    }

    public static boolean isMinimizeWindowUnlocked(ServerLevel level) {
        return getCurrentDay(level) >= 8;
    }

    public static boolean shouldPlayMineWhisper(ServerLevel level, ServerPlayer player) {
        return false;
    }

    public static boolean shouldTriggerLullaby(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (!isLullabyUnlocked(level)) {
            return false;
        }

        if (player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.WEAKNESS)) {
            return false;
        }
        if (isRemainingDaySuppressed(level, player)) {
            return false;
        }

        if (data.getBoolean(LULLABY_TRIGGERED_KEY)) {
            return false;
        }

        long triggerTick = ensureDayWindowSchedule(level, data, LULLABY_DELAY_KEY, 9000, 13000);
        return level.getDayTime() >= triggerTick;
    }

    public static void markLullabyTriggered(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(LULLABY_TRIGGERED_KEY, true);
        data.remove(LULLABY_DELAY_KEY);
    }

    public static boolean shouldTriggerHeartStare(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (!isHeartStareUnlocked(level)) {
            return false;
        }
        if (isRemainingDaySuppressed(level, player)) {
            return false;
        }

        if (data.getBoolean(HEART_STARE_TRIGGERED_KEY)) {
            return false;
        }

        long triggerTick = ensureDayWindowSchedule(level, data, HEART_STARE_DELAY_KEY, 6000, 9000);
        return level.getDayTime() >= triggerTick;
    }

    public static void startHeartStare(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(HEART_STARE_TRIGGERED_KEY, true);
        data.remove(HEART_STARE_DELAY_KEY);
        data.putInt(HEART_STARE_ACTIVE_UNTIL_KEY, player.tickCount + HEART_STARE_DURATION_TICKS);
    }

    public static boolean isHeartStareActive(ServerPlayer player) {
        CompoundTag data = getModData(player);
        return data.contains(HEART_STARE_ACTIVE_UNTIL_KEY)
                && player.tickCount < data.getInt(HEART_STARE_ACTIVE_UNTIL_KEY);
    }

    public static boolean shouldRefreshHeartSound(ServerPlayer player) {
        return false;
    }

    public static boolean shouldEndHeartStare(ServerPlayer player) {
        CompoundTag data = getModData(player);
        return data.contains(HEART_STARE_ACTIVE_UNTIL_KEY)
                && player.tickCount >= data.getInt(HEART_STARE_ACTIVE_UNTIL_KEY);
    }

    public static void clearHeartStare(ServerPlayer player) {
        getModData(player).remove(HEART_STARE_ACTIVE_UNTIL_KEY);
    }

    public static void initializeEfbuiJoin(ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (data.getBoolean(SESSION_INITIALIZED_KEY)) {
            return;
        }

        data.putBoolean(SESSION_INITIALIZED_KEY, true);
        data.putBoolean(EFBUI_JOIN_SENT_KEY, false);
        data.putBoolean(EFBUI_SPAWN_PENDING_KEY, false);
        data.putBoolean(LULLABY_TRIGGERED_KEY, false);
        data.putBoolean(HEART_STARE_TRIGGERED_KEY, false);
        data.remove(EFBUI_JOIN_DELAY_KEY);
        data.remove(LULLABY_DELAY_KEY);
        data.remove(HEART_STARE_DELAY_KEY);
        data.remove(OBSERVER_NEXT_ATTEMPT_TICK_KEY);
        data.remove(FOOTSTEPS_NEXT_ATTEMPT_TICK_KEY);
        data.remove(INVISIBLE_HIT_NEXT_ATTEMPT_TICK_KEY);
        data.remove(DOOR_KNOCK_NEXT_ATTEMPT_TICK_KEY);
        data.remove(DOOR_OPEN_BURST_NEXT_ATTEMPT_TICK_KEY);
        data.remove(MINESHAFT_EFBUI_NEXT_ATTEMPT_TICK_KEY);
        data.remove(RAIN_NIGHT_NEXT_ATTEMPT_TICK_KEY);
        data.remove(UNSKIPPABLE_NIGHT_NEXT_ATTEMPT_TICK_KEY);
        data.remove(FALSE_SECOND_PLAYER_NEXT_ATTEMPT_TICK_KEY);
        data.remove(INVENTORY_DISTORTION_NEXT_ATTEMPT_TICK_KEY);
        data.remove(HOME_REPLACEMENT_NEXT_ATTEMPT_TICK_KEY);
        data.remove(CHUNK_DISTORTION_NEXT_ATTEMPT_TICK_KEY);
        data.remove(BOAT_BREAK_NEXT_ATTEMPT_TICK_KEY);
        data.remove(FAKE_ITEM_BREAK_NEXT_ATTEMPT_TICK_KEY);
        data.remove(TUNNEL_OBSERVER_NEXT_ATTEMPT_TICK_KEY);
        data.remove(BLACK_ANIMAL_NEXT_ATTEMPT_TICK_KEY);
        data.remove(BLACK_ANIMAL_DAY_KEY);
        data.remove(BLACK_ANIMAL_DAY_COUNT_KEY);
        data.remove(PAST_MISTAKES_DOOR_NEXT_ATTEMPT_TICK_KEY);
        data.remove(FORTY_FOUR_NEXT_ATTEMPT_TICK_KEY);
        data.remove(LONELY_DOOR_NEXT_ATTEMPT_TICK_KEY);
        data.remove(OUTDOOR_BAT_NEXT_ATTEMPT_TICK_KEY);
        data.remove(WRONG_MOB_NEXT_ATTEMPT_TICK_KEY);
        data.remove(GHOST_TREE_NEXT_ATTEMPT_TICK_KEY);
        data.remove(PHOTO_SCREAMER_NEXT_ATTEMPT_TICK_KEY);
        data.remove(YOU_ALIVE_NEXT_ATTEMPT_TICK_KEY);
        data.remove(LOOK_DOWN_NEXT_ATTEMPT_TICK_KEY);
        data.remove(INVERT_MOUSE_NEXT_ATTEMPT_TICK_KEY);
        data.remove(SENSITIVITY_SPIKE_NEXT_ATTEMPT_TICK_KEY);
        data.remove(MASTER_VOLUME_DROP_NEXT_ATTEMPT_TICK_KEY);
        data.remove(GODLOVEU_NEXT_ATTEMPT_TICK_KEY);
        data.remove(IMWATCH_NEXT_ATTEMPT_TICK_KEY);
        data.remove(SLEEPISSHORT_NEXT_ATTEMPT_TICK_KEY);
        data.remove(URLIFE_NEXT_ATTEMPT_TICK_KEY);
        data.remove(YOUREND_NEXT_ATTEMPT_TICK_KEY);
        data.remove(SKINWALKER_NEXT_ATTEMPT_TICK_KEY);
        data.remove(CRYING_NEXT_ATTEMPT_TICK_KEY);
        data.remove(BROKEN_GLASS_NEXT_ATTEMPT_TICK_KEY);
        data.remove(FREE_HOUSE_NEXT_ATTEMPT_TICK_KEY);
        data.remove(GLASS_FRAME_NEXT_ATTEMPT_TICK_KEY);
        data.remove(MAZE_BUILDING_NEXT_ATTEMPT_TICK_KEY);
        data.remove(DOORWAY_EFBUI_COOLDOWN_KEY);
        data.remove(EFBUI_SPAWN_TRIGGER_TICK_KEY);
        data.remove(HEART_STARE_ACTIVE_UNTIL_KEY);
        data.remove(UNSKIPPABLE_NIGHT_ACTIVE_UNTIL_KEY);
        data.remove(NIGHT_JUMP_SUPPRESSED_DAY_KEY);
        data.remove(MEMORIAL_SIGN_DELAY_KEY);
        data.remove(SECOND_DAY_ADVANCEMENT_DELAY_KEY);
    }

    public static boolean shouldTriggerEfbuiJoin(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (data.getBoolean(EFBUI_JOIN_SENT_KEY)) {
            return false;
        }
        if (isRemainingDaySuppressed(level, player)) {
            return false;
        }

        long triggerTick = ensureDayWindowSchedule(level, data, EFBUI_JOIN_DELAY_KEY, 10000, 12800);
        return level.getDayTime() >= triggerTick;
    }

    public static void markEfbuiJoinTriggered(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(EFBUI_JOIN_SENT_KEY, true);
        data.remove(EFBUI_JOIN_DELAY_KEY);
    }

    public static void scheduleEfbuiSpawn(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(EFBUI_SPAWN_PENDING_KEY, true);
        data.putInt(EFBUI_SPAWN_TRIGGER_TICK_KEY, player.tickCount + 4800 + RANDOM.nextInt(2401));
    }

    public static void scheduleManualEfbuiSpawn(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(EFBUI_SPAWN_PENDING_KEY, true);
        data.putInt(EFBUI_SPAWN_TRIGGER_TICK_KEY, player.tickCount + 20 + RANDOM.nextInt(21));
    }

    public static boolean shouldExecutePendingEfbuiSpawn(ServerPlayer player) {
        CompoundTag data = getModData(player);
        return data.getBoolean(EFBUI_SPAWN_PENDING_KEY) && player.tickCount >= data.getInt(EFBUI_SPAWN_TRIGGER_TICK_KEY);
    }

    public static boolean hasPendingEfbuiSpawn(ServerPlayer player) {
        return getModData(player).getBoolean(EFBUI_SPAWN_PENDING_KEY);
    }

    public static void clearPendingEfbuiSpawn(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(EFBUI_SPAWN_PENDING_KEY, false);
        data.remove(EFBUI_SPAWN_TRIGGER_TICK_KEY);
    }

    public static boolean shouldTriggerMemorialSign(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (data.getBoolean(MEMORIAL_SIGN_TRIGGERED_KEY)) {
            return false;
        }
        if (isRemainingDaySuppressed(level, player)) {
            return false;
        }

        long triggerTick = ensureDayWindowSchedule(level, data, MEMORIAL_SIGN_DELAY_KEY, 12000, 14500);
        return level.getDayTime() >= triggerTick;
    }

    public static void markMemorialSignTriggered(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putBoolean(MEMORIAL_SIGN_TRIGGERED_KEY, true);
        data.remove(MEMORIAL_SIGN_DELAY_KEY);
    }

    public static boolean shouldTriggerRainNight(ServerLevel level, ServerPlayer player) {
        if (!isRainNightUnlocked(level) || level.isNight() || level.isRaining() || isUnskippableNightActive(level, player)) {
            return false;
        }

        int currentDay = getCurrentDay(level);
        if (currentDay <= 6) {
            return shouldTriggerGuaranteedDaily(level, player, RAIN_NIGHT_NEXT_ATTEMPT_TICK_KEY, 8500, 11000, FearManager::isRainNightUnlocked);
        }

        return shouldTriggerScheduled(level, player, RAIN_NIGHT_NEXT_ATTEMPT_TICK_KEY, 8500, 11000, FearManager::isRainNightUnlocked, day -> day >= 7 && (day - 7) % 3 == 0);
    }

    public static boolean shouldTriggerUnskippableNight(ServerLevel level, ServerPlayer player) {
        if (!isUnskippableNightUnlocked(level) || level.isNight() || isUnskippableNightActive(level, player)) {
            return false;
        }

        return shouldTriggerGuaranteedDaily(level, player, UNSKIPPABLE_NIGHT_NEXT_ATTEMPT_TICK_KEY, 11000, 14000, FearManager::isUnskippableNightUnlocked);
    }

    public static void startUnskippableNight(ServerLevel level, ServerPlayer player) {
        long currentDayStart = (level.getDayTime() / 24000L) * 24000L;
        getModData(player).putLong(UNSKIPPABLE_NIGHT_ACTIVE_UNTIL_KEY, currentDayStart + 24000L);
    }

    public static boolean isUnskippableNightActive(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (!data.contains(UNSKIPPABLE_NIGHT_ACTIVE_UNTIL_KEY)) {
            return false;
        }

        long activeUntil = data.getLong(UNSKIPPABLE_NIGHT_ACTIVE_UNTIL_KEY);

        if (level.getDayTime() >= activeUntil) {
            data.remove(UNSKIPPABLE_NIGHT_ACTIVE_UNTIL_KEY);
            return false;
        }

        return true;
    }

    public static boolean shouldTriggerObserverSpawn(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, OBSERVER_NEXT_ATTEMPT_TICK_KEY, 7600, 9200, FearManager::isObserverUnlocked, day -> day >= 1);
    }

    public static boolean shouldTriggerPastMistakesDoor(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, PAST_MISTAKES_DOOR_NEXT_ATTEMPT_TICK_KEY, 11800, 14200, FearManager::isPastMistakesDoorUnlocked, day -> day >= 1 && day % 2 == 1);
    }

    public static boolean shouldTriggerFortyFour(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, FORTY_FOUR_NEXT_ATTEMPT_TICK_KEY, 9800, 11400, FearManager::isFortyFourUnlocked, day -> day >= 2 && day % 2 == 0);
    }

    public static boolean shouldTriggerLonelyDoor(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, LONELY_DOOR_NEXT_ATTEMPT_TICK_KEY, 9000, 11800, FearManager::isLonelyDoorUnlocked, day -> day >= 3 && day % 2 == 1);
    }

    public static boolean shouldTriggerOutdoorBat(ServerLevel level, ServerPlayer player) {
        if (!level.canSeeSky(player.blockPosition())) {
            return false;
        }

        return shouldTriggerScheduled(level, player, OUTDOOR_BAT_NEXT_ATTEMPT_TICK_KEY, 1400, 2400, serverLevel -> true, day -> day >= 1);
    }

    public static boolean shouldTriggerWrongMob(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, WRONG_MOB_NEXT_ATTEMPT_TICK_KEY, 6200, 8600, FearManager::isWrongMobUnlocked, day -> day >= 1 && day <= 6);
    }

    public static boolean shouldTriggerGhostTree(ServerLevel level, ServerPlayer player) {
        if (!level.canSeeSky(player.blockPosition())) {
            return false;
        }

        return shouldTriggerScheduled(level, player, GHOST_TREE_NEXT_ATTEMPT_TICK_KEY, 5200, 7600, FearManager::isGhostTreeUnlocked, day -> day >= 1 && (day <= 4 || day % 2 == 0));
    }

    public static boolean shouldTriggerPhotoScreamer(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, PHOTO_SCREAMER_NEXT_ATTEMPT_TICK_KEY, 9000, 11000, FearManager::isPhotoScreamerUnlocked, day -> day >= 2 && day % 2 == 0);
    }

    public static boolean shouldTriggerSkinwalker(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, SKINWALKER_NEXT_ATTEMPT_TICK_KEY, 8400, 13200, FearManager::isSkinwalkerUnlocked, day -> day == 2);
    }

    public static boolean shouldTriggerYouAlive(ServerLevel level, ServerPlayer player) {
        return shouldTriggerGuaranteedDaily(level, player, YOU_ALIVE_NEXT_ATTEMPT_TICK_KEY, 3500, 16000, FearManager::isYouAliveUnlocked);
    }

    public static boolean shouldTriggerLookDown(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, LOOK_DOWN_NEXT_ATTEMPT_TICK_KEY, 2200, 3600, FearManager::isLookDownUnlocked, day -> day <= 4 || day % 2 == 1);
    }

    public static boolean shouldTriggerInvertMouse(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, INVERT_MOUSE_NEXT_ATTEMPT_TICK_KEY, 9800, 11600, FearManager::isInvertMouseUnlocked, day -> day == 1);
    }

    public static boolean shouldTriggerSensitivitySpike(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, SENSITIVITY_SPIKE_NEXT_ATTEMPT_TICK_KEY, 7200, 9200, FearManager::isSensitivitySpikeUnlocked, day -> day == 2);
    }

    public static boolean shouldTriggerMasterVolumeDrop(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, MASTER_VOLUME_DROP_NEXT_ATTEMPT_TICK_KEY, 12600, 14600, FearManager::isMasterVolumeDropUnlocked, day -> day == 2);
    }

    public static boolean shouldTriggerGodLoveU(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, GODLOVEU_NEXT_ATTEMPT_TICK_KEY, 5600, 7600, FearManager::isGodLoveUUnlocked, day -> day >= 2 && day % 2 == 0);
    }

    public static boolean shouldTriggerImWatch(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, IMWATCH_NEXT_ATTEMPT_TICK_KEY, 7200, 9000, FearManager::isImWatchUnlocked, day -> day >= 2 && day % 2 == 1);
    }

    public static boolean shouldTriggerSleepIsShort(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, SLEEPISSHORT_NEXT_ATTEMPT_TICK_KEY, 8800, 10600, FearManager::isSleepIsShortUnlocked, day -> day >= 3 && day % 2 == 1);
    }

    public static boolean shouldTriggerUrLife(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, URLIFE_NEXT_ATTEMPT_TICK_KEY, 10400, 12200, FearManager::isUrLifeUnlocked, day -> day >= 4 && day % 2 == 0);
    }

    public static boolean shouldTriggerYourEnd(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, YOUREND_NEXT_ATTEMPT_TICK_KEY, 16800, 18600, FearManager::isYourEndUnlocked, day -> day >= 6 && (day - 6) % 4 == 0);
    }

    public static boolean shouldTriggerCrying(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, CRYING_NEXT_ATTEMPT_TICK_KEY, 9400, 11200, FearManager::isCryingUnlocked, day -> day >= 6 && day % 2 == 0);
    }

    public static boolean shouldTriggerBrokenGlass(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, BROKEN_GLASS_NEXT_ATTEMPT_TICK_KEY, 11800, 13600, FearManager::isBrokenGlassUnlocked, day -> day >= 4 && day % 2 == 1);
    }

    public static boolean shouldTriggerButtonClick(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, BUTTON_CLICK_NEXT_ATTEMPT_TICK_KEY, 2400, 3600, FearManager::isButtonClickUnlocked, day -> day <= 3 || day % 2 == 1);
    }

    public static boolean shouldTriggerWoodenPressurePlate(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, WOODEN_PRESSURE_PLATE_NEXT_ATTEMPT_TICK_KEY, 4200, 6000, FearManager::isWoodenPressurePlateUnlocked, day -> day <= 3 || day % 2 == 0);
    }

    public static boolean shouldTriggerBlockPlace(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, BLOCK_PLACE_NEXT_ATTEMPT_TICK_KEY, 5200, 7200, FearManager::isBlockPlaceUnlocked, day -> day >= 2 && (day <= 4 || day % 2 == 0));
    }

    public static boolean shouldTriggerBlockBreakSound(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, BLOCK_BREAK_SOUND_NEXT_ATTEMPT_TICK_KEY, 6800, 8600, FearManager::isBlockBreakSoundUnlocked, day -> day >= 2 && (day <= 4 || day % 2 == 1));
    }

    public static boolean shouldTriggerBowShot(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, BOW_SHOT_NEXT_ATTEMPT_TICK_KEY, 9000, 10800, FearManager::isBowShotUnlocked, day -> day >= 3);
    }

    public static boolean shouldTriggerPigDeath(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, PIG_DEATH_NEXT_ATTEMPT_TICK_KEY, 10200, 12000, FearManager::isPigDeathUnlocked, day -> day >= 4);
    }

    public static boolean shouldTriggerTntIgnite(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, TNT_IGNITE_NEXT_ATTEMPT_TICK_KEY, 12400, 14200, FearManager::isTntIgniteUnlocked, day -> day >= 5);
    }

    public static boolean shouldTriggerMusicDisc11(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, MUSIC_DISC_11_NEXT_ATTEMPT_TICK_KEY, 15000, 17200, FearManager::isMusicDisc11Unlocked, day -> day >= 6 && day % 2 == 0);
    }

    public static boolean shouldTriggerFreeHouse(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, FREE_HOUSE_NEXT_ATTEMPT_TICK_KEY, 7000, 9200, FearManager::isFreeHouseUnlocked, day -> day >= 3 && day % 2 == 1);
    }

    public static boolean shouldTriggerGlassFrame(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, GLASS_FRAME_NEXT_ATTEMPT_TICK_KEY, 8200, 10400, FearManager::isGlassFrameUnlocked, day -> day >= 4 && day % 2 == 0);
    }

    public static boolean shouldTriggerMazeBuilding(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, MAZE_BUILDING_NEXT_ATTEMPT_TICK_KEY, 8800, 11000, FearManager::isMazeBuildingUnlocked, day -> day >= 6 && (day - 6) % 3 == 0);
    }

    public static boolean shouldTriggerFootsteps(ServerLevel level, ServerPlayer player) {
        if (!isFootstepsUnlocked(level)) {
            return false;
        }

        return shouldTriggerGuaranteedDaily(level, player, FOOTSTEPS_NEXT_ATTEMPT_TICK_KEY, 2600, 3800, FearManager::isFootstepsUnlocked);
    }

    public static boolean shouldTriggerBlackAnimal(ServerLevel level, ServerPlayer player) {
        return false;
    }

    public static void markBlackAnimalSpawned(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);
        int currentDay = getCurrentDay(level);
        if (data.getInt(BLACK_ANIMAL_DAY_KEY) != currentDay) {
            data.putInt(BLACK_ANIMAL_DAY_KEY, currentDay);
            data.putInt(BLACK_ANIMAL_DAY_COUNT_KEY, 0);
        }
        data.putInt(BLACK_ANIMAL_DAY_COUNT_KEY, data.getInt(BLACK_ANIMAL_DAY_COUNT_KEY) + 1);
    }

    public static boolean shouldTriggerInvisibleHit(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, INVISIBLE_HIT_NEXT_ATTEMPT_TICK_KEY, 9000, 11200, FearManager::isInvisibleHitUnlocked, day -> day >= 4);
    }

    public static boolean shouldTriggerFakeItemBreak(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, FAKE_ITEM_BREAK_NEXT_ATTEMPT_TICK_KEY, 5000, 6800, FearManager::isFakeItemBreakUnlocked, day -> day >= 3);
    }

    public static boolean shouldTriggerDoorKnock(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);
        int currentDay = getCurrentDay(level);

        if (!isDoorKnockUnlocked(level)) {
            return false;
        }
        if (!hasDoorNearby(level, player.blockPosition(), 7)) {
            return false;
        }

        if (data.getInt(DOOR_KNOCK_DAY_KEY) != currentDay) {
            data.putInt(DOOR_KNOCK_DAY_KEY, currentDay);
            data.putInt(DOOR_KNOCK_DAY_COUNT_KEY, 0);
        }
        if (data.getInt(DOOR_KNOCK_DAY_COUNT_KEY) >= 1) {
            return false;
        }

        if (!tryPeriodicCheck(level, data, DOOR_KNOCK_NEXT_CHECK_GAME_TICK_KEY, 200L)) {
            return false;
        }

        return RANDOM.nextInt(100) < 30;
    }

    public static boolean shouldTriggerDoorOpenBurst(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, DOOR_OPEN_BURST_NEXT_ATTEMPT_TICK_KEY, 7800, 9800, FearManager::isDoorOpenBurstUnlocked, day -> day >= 3 && (day <= 5 || day % 2 == 0));
    }

    public static boolean shouldTriggerMineshaftEfbui(ServerLevel level, ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        CompoundTag data = getModData(player);

        if (!isMineshaftEfbuiUnlocked(level)) {
            return false;
        }
        if (data.getBoolean(MINESHAFT_EFBUI_ONCE_KEY)) {
            return false;
        }

        if (playerPos.getY() >= 50 || level.canSeeSky(playerPos)) {
            return false;
        }

        return shouldTriggerScheduled(level, player, MINESHAFT_EFBUI_NEXT_ATTEMPT_TICK_KEY, 10800, 12800, FearManager::isMineshaftEfbuiUnlocked, day -> day <= 6);
    }

    public static boolean shouldTriggerTunnelObserver(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);
        if (player.blockPosition().getY() >= 50) {
            return false;
        }
        if (data.getBoolean(TUNNEL_OBSERVER_ONCE_KEY)) {
            return false;
        }

        return shouldTriggerScheduled(level, player, TUNNEL_OBSERVER_NEXT_ATTEMPT_TICK_KEY, 9800, 11800, FearManager::isTunnelObserverUnlocked, day -> day <= 6);
    }

    public static boolean shouldTriggerFalseSecondPlayer(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, FALSE_SECOND_PLAYER_NEXT_ATTEMPT_TICK_KEY, 14500, 16600, FearManager::isFalseSecondPlayerUnlocked, day -> day >= 7 && day % 2 == 1);
    }

    public static boolean shouldTriggerInventoryDistortion(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, INVENTORY_DISTORTION_NEXT_ATTEMPT_TICK_KEY, 17400, 19400, FearManager::isInventoryDistortionUnlocked, day -> day >= 7 && day % 2 == 0);
    }

    public static boolean shouldTriggerFakeDeath(ServerLevel level, ServerPlayer player) {
        return false;
    }

    public static boolean shouldTriggerHomeReplacement(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, HOME_REPLACEMENT_NEXT_ATTEMPT_TICK_KEY, 13200, 15000, FearManager::isHomeReplacementUnlocked, day -> day >= 4);
    }

    public static boolean shouldTriggerChunkDistortion(ServerLevel level, ServerPlayer player) {
        return shouldTriggerScheduled(level, player, CHUNK_DISTORTION_NEXT_ATTEMPT_TICK_KEY, 18800, 20800, FearManager::isChunkDistortionUnlocked, day -> day >= 2);
    }

    public static boolean shouldTriggerFakeExit(ServerLevel level, ServerPlayer player) {
        return false;
    }

    public static boolean shouldTriggerBoatBreak(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);
        int currentDay = getCurrentDay(level);

        if (!isBoatBreakUnlocked(level)) {
            return false;
        }

        if (data.getInt(BOAT_BREAK_DAY_KEY) != currentDay) {
            data.putInt(BOAT_BREAK_DAY_KEY, currentDay);
            data.putInt(BOAT_BREAK_DAY_COUNT_KEY, 0);
        }
        if (data.getInt(BOAT_BREAK_DAY_COUNT_KEY) >= 2) {
            return false;
        }

        if (!tryPeriodicCheck(level, data, BOAT_BREAK_NEXT_CHECK_GAME_TICK_KEY, 200L)) {
            return false;
        }

        return RANDOM.nextInt(100) < 30;
    }

    public static boolean shouldSpawnDoorwayEfbui(ServerLevel level, ServerPlayer player) {
        int day = getCurrentDay(level);
        if (!isEfbuiStareUnlocked(level) || day < 4) {
            return false;
        }

        CompoundTag data = getModData(player);
        if (data.getInt(DOORWAY_EFBUI_DAY_KEY) != day) {
            data.putInt(DOORWAY_EFBUI_DAY_KEY, day);
            data.putInt(DOORWAY_EFBUI_DAY_COUNT_KEY, 0);
        }
        if (data.getInt(DOORWAY_EFBUI_DAY_COUNT_KEY) >= 1) {
            return false;
        }

        if (RANDOM.nextInt(100) >= 30) {
            return false;
        }

        data.putInt(DOORWAY_EFBUI_DAY_COUNT_KEY, data.getInt(DOORWAY_EFBUI_DAY_COUNT_KEY) + 1);
        return true;
    }

    public static boolean canSpawnZer000(ServerPlayer player) {
        CompoundTag data = getModData(player);
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        int currentDay = getCurrentDay(level);
        if (data.getInt(ZER000_DAY_KEY) != currentDay) {
            data.putInt(ZER000_DAY_KEY, currentDay);
            data.putInt(ZER000_DAY_COUNT_KEY, 0);
        }
        return data.getInt(ZER000_DAY_COUNT_KEY) < 1;
    }

    public static void markZer000Spawned(ServerPlayer player) {
        CompoundTag data = getModData(player);
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        int currentDay = getCurrentDay(level);
        if (data.getInt(ZER000_DAY_KEY) != currentDay) {
            data.putInt(ZER000_DAY_KEY, currentDay);
            data.putInt(ZER000_DAY_COUNT_KEY, 0);
        }
        data.putInt(ZER000_DAY_COUNT_KEY, data.getInt(ZER000_DAY_COUNT_KEY) + 1);
        data.putInt(ZER000_SPAWN_COUNT_KEY, data.getInt(ZER000_SPAWN_COUNT_KEY) + 1);
    }

    public static boolean shouldTriggerTunnelDimensionFromOre(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);
        int target = data.getInt(TUNNEL_DIMENSION_ORE_TARGET_KEY);
        if (target <= 0) {
            target = 2 + level.random.nextInt(2);
            data.putInt(TUNNEL_DIMENSION_ORE_TARGET_KEY, target);
        }

        int counter = data.getInt(TUNNEL_DIMENSION_ORE_COUNTER_KEY) + 1;
        data.putInt(TUNNEL_DIMENSION_ORE_COUNTER_KEY, counter);
        return counter >= target;
    }

    public static void resetTunnelDimensionOreCounter(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.putInt(TUNNEL_DIMENSION_ORE_COUNTER_KEY, 0);
        data.putInt(TUNNEL_DIMENSION_ORE_TARGET_KEY, 2 + RANDOM.nextInt(2));
    }

    public static void markEventTriggered(ServerLevel level, ServerPlayer player, String eventId) {
        String scheduleKey = getScheduleKeyForEvent(eventId);
        CompoundTag data = getModData(player);
        int currentDay = getCurrentDay(level);

        if (ModEventCatalog.BOAT_BREAK.equals(eventId)) {
            if (data.getInt(BOAT_BREAK_DAY_KEY) != currentDay) {
                data.putInt(BOAT_BREAK_DAY_KEY, currentDay);
                data.putInt(BOAT_BREAK_DAY_COUNT_KEY, 0);
            }
            data.putInt(BOAT_BREAK_DAY_COUNT_KEY, data.getInt(BOAT_BREAK_DAY_COUNT_KEY) + 1);
        } else if (ModEventCatalog.DOOR_KNOCK.equals(eventId)) {
            if (data.getInt(DOOR_KNOCK_DAY_KEY) != currentDay) {
                data.putInt(DOOR_KNOCK_DAY_KEY, currentDay);
                data.putInt(DOOR_KNOCK_DAY_COUNT_KEY, 0);
            }
            data.putInt(DOOR_KNOCK_DAY_COUNT_KEY, data.getInt(DOOR_KNOCK_DAY_COUNT_KEY) + 1);
        } else if (ModEventCatalog.MINESHAFT_EFBUI.equals(eventId)) {
            data.putBoolean(MINESHAFT_EFBUI_ONCE_KEY, true);
        } else if (ModEventCatalog.TUNNEL_OBSERVER.equals(eventId)) {
            data.putBoolean(TUNNEL_OBSERVER_ONCE_KEY, true);
        }

        if (scheduleKey == null) {
            return;
        }

        data.putInt(getCompletedDayKey(scheduleKey), getCurrentDay(level));
        data.remove(scheduleKey);
    }

    public static void rescheduleEventRetry(ServerLevel level, ServerPlayer player, String eventId, int minDelay, int maxDelay) {
        String scheduleKey = getScheduleKeyForEvent(eventId);
        if (scheduleKey == null) {
            return;
        }

        CompoundTag data = getModData(player);
        long now = level.getDayTime();
        long currentDayEnd = getDayStart(level) + 23960L;
        long earliest = now + Math.max(40L, minDelay);
        long latest = Math.min(currentDayEnd, now + Math.max(minDelay, maxDelay));
        long candidate = pickScheduleTimeAvoidingClumps(data, scheduleKey, getDayStart(level), earliest, Math.max(earliest, latest));

        setStoredTime(data, scheduleKey, candidate);
    }

    public static void deferOverdueDaySchedulesAfterNightJump(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);
        int currentDay = getCurrentDay(level);
        long currentDayStart = getDayStart(level);
        data.putInt(NIGHT_JUMP_SUPPRESSED_DAY_KEY, currentDay);

        deferIfOverdue(level, data, currentDay, currentDayStart, SECOND_DAY_ADVANCEMENT_DELAY_KEY, 6000, 9000);
        deferIfOverdue(level, data, currentDay, currentDayStart, MEMORIAL_SIGN_DELAY_KEY, 16000, 18500);
        deferIfOverdue(level, data, currentDay, currentDayStart, HEART_STARE_DELAY_KEY, 6000, 9000);
        deferIfOverdue(level, data, currentDay, currentDayStart, LULLABY_DELAY_KEY, 9000, 13000);
        deferIfOverdue(level, data, currentDay, currentDayStart, EFBUI_JOIN_DELAY_KEY, 9000, 12500);
        deferIfOverdue(level, data, currentDay, currentDayStart, OBSERVER_NEXT_ATTEMPT_TICK_KEY, 10500, 14000);
        deferIfOverdue(level, data, currentDay, currentDayStart, FOOTSTEPS_NEXT_ATTEMPT_TICK_KEY, 3000, 5200);
        deferIfOverdue(level, data, currentDay, currentDayStart, INVISIBLE_HIT_NEXT_ATTEMPT_TICK_KEY, 10000, 12500);
        deferIfOverdue(level, data, currentDay, currentDayStart, DOOR_KNOCK_NEXT_ATTEMPT_TICK_KEY, 3500, 5500);
        deferIfOverdue(level, data, currentDay, currentDayStart, DOOR_OPEN_BURST_NEXT_ATTEMPT_TICK_KEY, 12500, 15000);
        deferIfOverdue(level, data, currentDay, currentDayStart, MINESHAFT_EFBUI_NEXT_ATTEMPT_TICK_KEY, 9000, 13000);
        deferIfOverdue(level, data, currentDay, currentDayStart, FALSE_SECOND_PLAYER_NEXT_ATTEMPT_TICK_KEY, 12000, 14500);
        deferIfOverdue(level, data, currentDay, currentDayStart, INVENTORY_DISTORTION_NEXT_ATTEMPT_TICK_KEY, 13500, 16000);
        deferIfOverdue(level, data, currentDay, currentDayStart, HOME_REPLACEMENT_NEXT_ATTEMPT_TICK_KEY, 10000, 12500);
        deferIfOverdue(level, data, currentDay, currentDayStart, CHUNK_DISTORTION_NEXT_ATTEMPT_TICK_KEY, 11500, 14000);
        deferIfOverdue(level, data, currentDay, currentDayStart, BOAT_BREAK_NEXT_ATTEMPT_TICK_KEY, 9500, 12000);
        deferIfOverdue(level, data, currentDay, currentDayStart, FAKE_ITEM_BREAK_NEXT_ATTEMPT_TICK_KEY, 7000, 9000);
        deferIfOverdue(level, data, currentDay, currentDayStart, TUNNEL_OBSERVER_NEXT_ATTEMPT_TICK_KEY, 13000, 16000);
        deferIfOverdue(level, data, currentDay, currentDayStart, BLACK_ANIMAL_NEXT_ATTEMPT_TICK_KEY, 8000, 11000);
        deferIfOverdue(level, data, currentDay, currentDayStart, PAST_MISTAKES_DOOR_NEXT_ATTEMPT_TICK_KEY, 12000, 14500);
        deferIfOverdue(level, data, currentDay, currentDayStart, FORTY_FOUR_NEXT_ATTEMPT_TICK_KEY, 11000, 14500);
        deferIfOverdue(level, data, currentDay, currentDayStart, LONELY_DOOR_NEXT_ATTEMPT_TICK_KEY, 9500, 12500);
        deferIfOverdue(level, data, currentDay, currentDayStart, OUTDOOR_BAT_NEXT_ATTEMPT_TICK_KEY, 1500, 3000);
        deferIfOverdue(level, data, currentDay, currentDayStart, WRONG_MOB_NEXT_ATTEMPT_TICK_KEY, 4500, 6500);
        deferIfOverdue(level, data, currentDay, currentDayStart, GHOST_TREE_NEXT_ATTEMPT_TICK_KEY, 8000, 10500);
        deferIfOverdue(level, data, currentDay, currentDayStart, PHOTO_SCREAMER_NEXT_ATTEMPT_TICK_KEY, 9500, 11500);
        deferIfOverdue(level, data, currentDay, currentDayStart, YOU_ALIVE_NEXT_ATTEMPT_TICK_KEY, 3500, 16000);
        deferIfOverdue(level, data, currentDay, currentDayStart, LOOK_DOWN_NEXT_ATTEMPT_TICK_KEY, 2200, 3800);
        deferIfOverdue(level, data, currentDay, currentDayStart, INVERT_MOUSE_NEXT_ATTEMPT_TICK_KEY, 9800, 11600);
        deferIfOverdue(level, data, currentDay, currentDayStart, SENSITIVITY_SPIKE_NEXT_ATTEMPT_TICK_KEY, 7200, 9200);
        deferIfOverdue(level, data, currentDay, currentDayStart, MASTER_VOLUME_DROP_NEXT_ATTEMPT_TICK_KEY, 12600, 14600);
        deferIfOverdue(level, data, currentDay, currentDayStart, GODLOVEU_NEXT_ATTEMPT_TICK_KEY, 5600, 7600);
        deferIfOverdue(level, data, currentDay, currentDayStart, IMWATCH_NEXT_ATTEMPT_TICK_KEY, 7600, 9400);
        deferIfOverdue(level, data, currentDay, currentDayStart, SLEEPISSHORT_NEXT_ATTEMPT_TICK_KEY, 7800, 9800);
        deferIfOverdue(level, data, currentDay, currentDayStart, URLIFE_NEXT_ATTEMPT_TICK_KEY, 9000, 11200);
        deferIfOverdue(level, data, currentDay, currentDayStart, YOUREND_NEXT_ATTEMPT_TICK_KEY, 10800, 13200);
        deferIfOverdue(level, data, currentDay, currentDayStart, CRYING_NEXT_ATTEMPT_TICK_KEY, 9200, 11400);
        deferIfOverdue(level, data, currentDay, currentDayStart, BROKEN_GLASS_NEXT_ATTEMPT_TICK_KEY, 10200, 12200);
        deferIfOverdue(level, data, currentDay, currentDayStart, FREE_HOUSE_NEXT_ATTEMPT_TICK_KEY, 7200, 9800);
        deferIfOverdue(level, data, currentDay, currentDayStart, GLASS_FRAME_NEXT_ATTEMPT_TICK_KEY, 7600, 9800);
        deferIfOverdue(level, data, currentDay, currentDayStart, MAZE_BUILDING_NEXT_ATTEMPT_TICK_KEY, 7000, 9800);
    }

    public static void copySessionData(ServerPlayer originalPlayer, ServerPlayer newPlayer) {
        CompoundTag originalData = originalPlayer.getPersistentData().getCompound(MOD_DATA_KEY);
        newPlayer.getPersistentData().put(MOD_DATA_KEY, originalData.copy());
    }

    private static CompoundTag getModData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();

        if (!persistentData.contains(MOD_DATA_KEY)) {
            persistentData.put(MOD_DATA_KEY, new CompoundTag());
        }

        return persistentData.getCompound(MOD_DATA_KEY);
    }

    private static boolean shouldTriggerLateEvent(ServerLevel level, ServerPlayer player, String key, int baseDelay, int randomDelay, int chancePercent, java.util.function.Predicate<ServerLevel> unlockCheck) {
        return shouldTriggerGuaranteedDaily(level, player, key, baseDelay, baseDelay + randomDelay, unlockCheck);
    }

    private static boolean shouldTriggerRepeating(ServerLevel level, ServerPlayer player, String key, int baseDelay, int randomDelay, int chancePercent, java.util.function.Predicate<ServerLevel> unlockCheck) {
        return shouldTriggerGuaranteedDaily(level, player, key, baseDelay, baseDelay + randomDelay, unlockCheck);
    }

    private static boolean shouldTriggerScheduled(ServerLevel level, ServerPlayer player, String key, int minOffset, int maxOffset,
                                                  java.util.function.Predicate<ServerLevel> unlockCheck, IntPredicate dayRule) {
        if (!dayRule.test(getCurrentDay(level))) {
            return false;
        }
        return shouldTriggerGuaranteedDaily(level, player, key, minOffset, maxOffset, unlockCheck);
    }

    private static boolean shouldTriggerGuaranteedDaily(ServerLevel level, ServerPlayer player, String key, int minOffset, int maxOffset, java.util.function.Predicate<ServerLevel> unlockCheck) {
        CompoundTag data = getModData(player);
        int currentDay = getCurrentDay(level);

        if (!unlockCheck.test(level)) {
            return false;
        }
        if (isRemainingDaySuppressed(level, player)) {
            return false;
        }

        if (data.getInt(getCompletedDayKey(key)) == currentDay) {
            return false;
        }

        long scheduledTick = ensureDayWindowSchedule(level, data, key, minOffset, maxOffset);
        return level.getDayTime() >= scheduledTick;
    }

    private static boolean hasDoorNearby(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    cursor.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (level.getBlockState(cursor).getBlock() instanceof net.minecraft.world.level.block.DoorBlock) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean tryPeriodicCheck(ServerLevel level, CompoundTag data, String key, long intervalTicks) {
        long now = level.getGameTime();
        long next = data.getLong(key);
        if (now < next) {
            return false;
        }
        data.putLong(key, now + intervalTicks);
        return true;
    }

    private static String getScheduleKeyForEvent(String eventId) {
        return switch (eventId) {
            case ModEventCatalog.MEMORIAL_SIGN -> MEMORIAL_SIGN_DELAY_KEY;
            case ModEventCatalog.OBSERVER -> OBSERVER_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.PAST_MISTAKES_DOOR -> PAST_MISTAKES_DOOR_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.FORTY_FOUR -> FORTY_FOUR_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.LONELY_DOOR -> LONELY_DOOR_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.FOOTSTEPS -> FOOTSTEPS_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.BLACK_ANIMAL -> BLACK_ANIMAL_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.INVISIBLE_HIT -> INVISIBLE_HIT_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.FAKE_ITEM_BREAK -> FAKE_ITEM_BREAK_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.DOOR_KNOCK -> DOOR_KNOCK_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.DOOR_OPEN_BURST -> DOOR_OPEN_BURST_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.MINESHAFT_EFBUI -> MINESHAFT_EFBUI_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.TUNNEL_OBSERVER -> TUNNEL_OBSERVER_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.FALSE_SECOND_PLAYER -> FALSE_SECOND_PLAYER_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.INVENTORY_DISTORTION -> INVENTORY_DISTORTION_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.HOME_REPLACEMENT -> HOME_REPLACEMENT_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.CHUNK_DISTORTION -> CHUNK_DISTORTION_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.BOAT_BREAK -> BOAT_BREAK_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.OUTDOOR_BAT -> OUTDOOR_BAT_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.WRONG_MOB -> WRONG_MOB_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.GHOST_TREE -> GHOST_TREE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.PHOTO_SCREAMER -> PHOTO_SCREAMER_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.SKINWALKER -> SKINWALKER_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.YOU_ALIVE -> YOU_ALIVE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.LOOK_DOWN -> LOOK_DOWN_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.INVERT_MOUSE -> INVERT_MOUSE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.SENSITIVITY_SPIKE -> SENSITIVITY_SPIKE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.MASTER_VOLUME_DROP -> MASTER_VOLUME_DROP_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.GODLOVEU -> GODLOVEU_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.IMWATCH -> IMWATCH_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.SLEEPISSHORT -> SLEEPISSHORT_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.URLIFE -> URLIFE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.YOUREND -> YOUREND_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.CRYING -> CRYING_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.BROKEN_GLASS -> BROKEN_GLASS_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.BUTTON_CLICK -> BUTTON_CLICK_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.WOODEN_PRESSURE_PLATE -> WOODEN_PRESSURE_PLATE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.BOW_SHOT -> BOW_SHOT_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.BLOCK_PLACE -> BLOCK_PLACE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.BLOCK_BREAK_SOUND -> BLOCK_BREAK_SOUND_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.TNT_IGNITE -> TNT_IGNITE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.PIG_DEATH -> PIG_DEATH_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.MUSIC_DISC_11 -> MUSIC_DISC_11_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.FREE_HOUSE -> FREE_HOUSE_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.GLASS_FRAME -> GLASS_FRAME_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.MAZE_BUILDING -> MAZE_BUILDING_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.RAIN_NIGHT -> RAIN_NIGHT_NEXT_ATTEMPT_TICK_KEY;
            case ModEventCatalog.UNSKIPPABLE_NIGHT -> UNSKIPPABLE_NIGHT_NEXT_ATTEMPT_TICK_KEY;
            default -> null;
        };
    }

    private static long ensureDayWindowSchedule(ServerLevel level, CompoundTag data, String key, int minOffset, int maxOffset) {
        long now = level.getDayTime();
        long currentDayStart = getDayStart(level);
        long currentDayEnd = currentDayStart + 23960L;
        long scheduledTick = getStoredTime(data, key);
        int clampedMin = Math.max(0, Math.min(minOffset, 23900));
        int clampedMax = Math.max(clampedMin, Math.min(maxOffset, 23950));

        if (scheduledTick <= 0L || scheduledTick < currentDayStart) {
            long windowStart = currentDayStart + clampedMin;
            long windowEnd = currentDayStart + clampedMax;
            long candidate;

            if (now > windowEnd) {
                long nextDayStart = currentDayStart + 24000L;
                candidate = pickScheduleTimeAvoidingClumps(
                        data,
                        key,
                        nextDayStart,
                        nextDayStart + clampedMin,
                        nextDayStart + clampedMax
                );
            } else {
                long earliestCurrentDay = Math.max(now + 200L, windowStart);
                long latestCurrentDay = Math.min(Math.max(earliestCurrentDay, windowEnd), currentDayEnd);
                candidate = pickScheduleTimeAvoidingClumps(
                        data,
                        key,
                        currentDayStart,
                        earliestCurrentDay,
                        latestCurrentDay
                );
            }

            setStoredTime(data, key, candidate);
            return candidate;
        }

        return scheduledTick;
    }

    private static void scheduleNextDayWindow(ServerLevel level, CompoundTag data, String key, int minOffset, int maxOffset) {
        long nextDayStart = getDayStart(level) + 24000L;
        long candidate = pickScheduleTimeAvoidingClumps(
                data,
                key,
                nextDayStart,
                nextDayStart + minOffset,
                nextDayStart + maxOffset
        );
        setStoredTime(data, key, candidate);
    }

    private static long getDayStart(ServerLevel level) {
        return (level.getDayTime() / 24000L) * 24000L;
    }

    private static long getStoredTime(CompoundTag data, String key) {
        if (data.contains(key, Tag.TAG_LONG)) {
            return data.getLong(key);
        }
        if (data.contains(key, Tag.TAG_INT)) {
            return data.getInt(key);
        }
        return 0L;
    }

    private static void setStoredTime(CompoundTag data, String key, long value) {
        data.putLong(key, value);
    }

    private static String getCompletedDayKey(String scheduleKey) {
        return scheduleKey + "CompletedDay";
    }

    private static boolean isRemainingDaySuppressed(ServerLevel level, ServerPlayer player) {
        return getModData(player).getInt(NIGHT_JUMP_SUPPRESSED_DAY_KEY) == getCurrentDay(level);
    }

    private static void deferIfOverdue(ServerLevel level, CompoundTag data, int currentDay, long currentDayStart, String key, int minOffset, int maxOffset) {
        long scheduledTick = getStoredTime(data, key);
        long nextDayStart = currentDayStart + 24000L;

        if (scheduledTick <= 0L || scheduledTick < currentDayStart || scheduledTick >= nextDayStart) {
            return;
        }

        if (data.getInt(getCompletedDayKey(key)) == currentDay) {
            return;
        }

        long candidate = pickScheduleTimeAvoidingClumps(
                data,
                key,
                nextDayStart,
                nextDayStart + minOffset,
                nextDayStart + maxOffset
        );
        setStoredTime(data, key, candidate);
    }

    private static long pickScheduleTimeAvoidingClumps(CompoundTag data, String scheduleKey, long dayStart, long earliest, long latest) {
        if (latest <= earliest) {
            return earliest;
        }

        long bestCandidate = earliest;
        long bestGap = -1L;
        for (int attempt = 0; attempt < 24; attempt++) {
            long candidate = earliest + RANDOM.nextInt((int) (latest - earliest) + 1);
            long nearestGap = nearestScheduledGap(data, scheduleKey, dayStart, candidate);
            if (nearestGap >= MIN_EVENT_GAP_TICKS) {
                return candidate;
            }
            if (nearestGap > bestGap) {
                bestGap = nearestGap;
                bestCandidate = candidate;
            }
        }
        return bestCandidate;
    }

    private static long nearestScheduledGap(CompoundTag data, String scheduleKey, long dayStart, long candidate) {
        long dayEnd = dayStart + 24000L;
        long nearestGap = Long.MAX_VALUE;

        for (String otherKey : data.getAllKeys()) {
            if (otherKey.equals(scheduleKey) || !isScheduleDataKey(otherKey)) {
                continue;
            }

            long otherTick = getStoredTime(data, otherKey);
            if (otherTick < dayStart || otherTick >= dayEnd) {
                continue;
            }

            nearestGap = Math.min(nearestGap, Math.abs(otherTick - candidate));
        }

        return nearestGap == Long.MAX_VALUE ? 24000L : nearestGap;
    }

    private static boolean isScheduleDataKey(String key) {
        return key.endsWith("NextAttemptTick") || key.endsWith("Delay");
    }
}
