package com.igore.code44.event;

import com.igore.code44.Code44Mod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.igore.code44.data.Code44WorldData;
import com.igore.code44.effect.EfbuiDimensionManager;
import com.igore.code44.effect.EarlyGameEventManager;
import com.igore.code44.effect.FearManager;
import com.igore.code44.effect.LateGameEventManager;
import com.igore.code44.effect.MazeDimensionManager;
import com.igore.code44.effect.PastMistakesFieldManager;
import com.igore.code44.effect.ScreenJumpscareManager;
import com.igore.code44.effect.TunnelDimensionManager;
import com.igore.code44.effect.YourEndEventManager;
import com.igore.code44.entity.BlackAnimalManager;
import com.igore.code44.entity.BlackChickenEntity;
import com.igore.code44.entity.BlackCowEntity;
import com.igore.code44.entity.BlackPigEntity;
import com.igore.code44.entity.DarkHorseBaglanEntity;
import com.igore.code44.entity.E44efbuiEntity;
import com.igore.code44.entity.EntitySpawnManager;
import com.igore.code44.entity.Zer000Manager;
import com.igore.code44.entity.Zer000Entity;
import com.igore.code44.network.ModNetworking;
import com.igore.code44.network.packet.OpenDeveloperMenuPacket;
import com.igore.code44.registry.ModEntities;
import com.igore.code44.sound.HorrorSoundManager;
import com.igore.code44.structure.FreeHouseManager;
import com.igore.code44.structure.GlassFrameManager;
import com.igore.code44.structure.LonelyDoorManager;
import com.igore.code44.structure.MazeBuildingManager;
import com.igore.code44.structure.MemorialSignManager;
import com.igore.code44.weather.BloodRainManager;
import com.igore.code44.world.LegacyBiomeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;

@Mod.EventBusSubscriber
public class CommonEvents {
    private static final ResourceLocation SECOND_DAY_ADVANCEMENT_ID =
            ResourceLocation.fromNamespaceAndPath("code44", "story/im_in_your_world");
    private static final String DEBUG_ENABLED_KEY = "code44DebugEventsEnabled";
    private static final String DEV_COMMANDS_UNLOCKED_KEY = "code44DeveloperCommandsUnlocked";
    private static final String DEBUG_LAST_EVENT_KEY = "code44DebugLastEvent";
    private static final String DEBUG_LAST_RESULT_KEY = "code44DebugLastResult";
    private static final String DEBUG_LAST_TICK_KEY = "code44DebugLastTick";
    private static final String TICK_OVERLAY_ENABLED_KEY = "code44TickOverlayEnabled";
    private static final String LAST_WORLD_MESSAGE_DONE_KEY = "code44LastWorldMessageDone";
    private static final String ZERO_DAY_OMEN_DONE_KEY = "code44ZeroDayOmenDone";
    private static final String ZERO_DAY_BLINK_DONE_KEY = "code44ZeroDayBlinkDone";
    private static final String ZERO_DAY_YOU_ALIVE_DONE_KEY = "code44ZeroDayYouAliveDone";
    private static final String YOU_ALIVE_REPLY_PENDING_KEY = "code44YouAliveReplyPending";
    private static final String YOU_ALIVE_REPLY_TRIGGER_TICK_KEY = "code44YouAliveReplyTriggerTick";
    private static final String ERR44R_RAIN_ACTIVE_KEY = "code44Err44rRainActive";
    private static final String FAKE_HELLO_ACTIVE_KEY = "code44FakeHelloActive";
    private static final String FAKE_HELLO_STAGE_KEY = "code44FakeHelloStage";
    private static final String FAKE_HELLO_NEXT_TICK_KEY = "code44FakeHelloNextTick";
    private static final String FAKE_HELLO_LINE_INDEX_KEY = "code44FakeHelloLineIndex";
    private static final String FAKE_HELLO_REVENGE_TICK_KEY = "code44FakeHelloRevengeTick";
    private static final String FAKE_HELLO_LEAVE_TICK_KEY = "code44FakeHelloLeaveTick";
    private static final String FAKE_HELLO_CHAT_ACTIVE_KEY = "code44FakeHelloChatActive";
    private static final String FAKE_HELLO_AWAITING_REPLY_KEY = "code44FakeHelloAwaitingReply";
    private static final String FAKE_HELLO_REPLY_TICK_KEY = "code44FakeHelloReplyTick";
    private static final String FAKE_HELLO_REPLY_TYPE_KEY = "code44FakeHelloReplyType";
    private static final String PLAYER_DEATH_COUNT_KEY = "code44PlayerDeathCount";
    private static final String LAST_SLEEP_DAY_KEY = "code44LastSleepDay";
    private static final String[] FAKE_HELLO_CODE_LINES = new String[] {
            "public class NPC {",
            "    private int entityID;",
            "    private Location location;",
            "    private GameProfile gameprofile;",
            "",
            "    public NPC(String name, Location location) {",
            "        entityID = (int) Math.ceil(Math.random() * 1000) + 2000;",
            "        gameprofile = new GameProfile(UUID.randomUUID(), name);",
            "        changeSkin();",
            "        this.location = location.clone();",
            "    }",
            "}"
    };
    private static final int FAKE_REPLY_WHO = 1;
    private static final int FAKE_REPLY_FRIEND = 2;
    private static final int FAKE_REPLY_YES = 3;
    private static final int FAKE_REPLY_NO = 4;
    private static final int FAKE_REPLY_FUCK = 5;
    private static final int MIN_DAY_TICK_SPEED = 1;
    private static final int MAX_DAY_TICK_SPEED = 500;
    private static final Map<UUID, Integer> VILLAGER_STARE_UNTIL = new HashMap<>();
    private static final Set<UUID> UNLOCKED_DEVELOPER_SESSIONS = new HashSet<>();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level && PastMistakesFieldManager.isFieldLevel(level)) {
            if (event.getPlayer() instanceof ServerPlayer player
                    && level instanceof ServerLevel serverLevel
                    && isTrackedOre(event.getState())
                    && FearManager.isTunnelDimensionUnlocked(serverLevel)) {
                if (FearManager.shouldTriggerTunnelDimensionFromOre(serverLevel, player)
                        && TunnelDimensionManager.startScene(serverLevel, player)) {
                    FearManager.markEventTriggered(serverLevel, player, ModEventCatalog.TUNNEL_DIMENSION);
                    FearManager.resetTunnelDimensionOreCounter(player);
                    logEventDebug(player, ModEventCatalog.TUNNEL_DIMENSION, true, event.getPos());
                } else {
                    logEventDebug(player, ModEventCatalog.TUNNEL_DIMENSION, false, event.getPos());
                }
            }
            return;
        }

        if (event.getLevel() instanceof ServerLevel level && LonelyDoorManager.isManagedDoor(level, event.getPos())) {
            if (event.getPlayer() != null && event.getPlayer().isCreative()) {
                return;
            }
            event.setCanceled(true);
            return;
        }

        if (event.getPlayer() instanceof ServerPlayer player
                && EarlyGameEventManager.isManagedGhostTreeBlock(player, event.getPos())) {
            if (!player.isCreative()) {
                event.setCanceled(true);
                return;
            }
        }

        if (event.getState().is(com.igore.code44.registry.ModBlocks.EFBUI_VOID_BLOCK.get())
                || event.getState().is(com.igore.code44.registry.ModBlocks.EFBUI_VOID_LIGHT.get())
                || event.getState().is(com.igore.code44.registry.ModBlocks.TUNNEL_STONE.get())) {
            if (event.getPlayer() != null && event.getPlayer().isCreative()) {
                return;
            }
            event.setCanceled(true);
        }

        if (event.getPlayer() instanceof ServerPlayer player
                && event.getLevel() instanceof ServerLevel level
                && isTrackedOre(event.getState())
                && FearManager.isTunnelDimensionUnlocked(level)) {
            if (FearManager.shouldTriggerTunnelDimensionFromOre(level, player)
                    && TunnelDimensionManager.startScene(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.TUNNEL_DIMENSION);
                FearManager.resetTunnelDimensionOreCounter(player);
                logEventDebug(player, ModEventCatalog.TUNNEL_DIMENSION, true, event.getPos());
            } else {
                logEventDebug(player, ModEventCatalog.TUNNEL_DIMENSION, false, event.getPos());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!Code44Mod.isDevCommandsBuild()) {
            return;
        }

        event.getDispatcher().register(
                literal("code44")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                ModNetworking.CHANNEL.send(
                                        PacketDistributor.PLAYER.with(() -> player),
                                        new OpenDeveloperMenuPacket()
                                );
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_debug_events")
                        .requires(source -> source.hasPermission(0))
                        .then(literal("on")
                                .executes(context -> toggleEventDebug(context.getSource(), true)))
                        .then(literal("off")
                                .executes(context -> toggleEventDebug(context.getSource(), false)))
        );

        event.getDispatcher().register(
                literal("code44_debug")
                        .requires(source -> source.hasPermission(0))
                        .then(literal("on")
                                .executes(context -> toggleEventDebug(context.getSource(), true)))
                        .then(literal("off")
                                .executes(context -> toggleEventDebug(context.getSource(), false)))
        );

        event.getDispatcher().register(
                literal("tick")
                        .requires(source -> source.hasPermission(0))
                        .then(literal("on")
                                .executes(context -> toggleTickOverlay(context.getSource(), true)))
                        .then(literal("off")
                                .executes(context -> toggleTickOverlay(context.getSource(), false)))
                        .then(literal("speed")
                                .executes(context -> showTickSpeed(context.getSource()))
                                .then(net.minecraft.commands.Commands.argument(
                                                "value",
                                                IntegerArgumentType.integer(MIN_DAY_TICK_SPEED, MAX_DAY_TICK_SPEED))
                                        .executes(context -> setTickSpeed(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "value")))))
                        .executes(context -> showTickUsage(context.getSource()))
        );

        event.getDispatcher().register(
                literal("code44_lullaby")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playLullabyEvent(level, player);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_heart_stare")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null) {
                                FearManager.startHeartStare(player);
                                HorrorSoundManager.startHeartEventSound(player);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_leave_now")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null) {
                                ScreenJumpscareManager.showLeaveNow(player);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_last_world")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                sendSystem44LastWorldMessage(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_you_alive")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                sendYouAliveMessage(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_fakeplayer_hello")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (triggerFakeHelloEvent(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Could not trigger fake hello event."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_day")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .then(net.minecraft.commands.Commands.argument("day", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayer();
                                    if (player != null && player.level() instanceof ServerLevel level) {
                                        int day = IntegerArgumentType.getInteger(context, "day");
                                        level.setDayTime(day * 24000L);
                                        if (day == 0) {
                                            player.getPersistentData().putBoolean(LAST_WORLD_MESSAGE_DONE_KEY, false);
                                            player.getPersistentData().putBoolean(ZERO_DAY_OMEN_DONE_KEY, false);
                                            player.getPersistentData().putBoolean(ZERO_DAY_BLINK_DONE_KEY, false);
                                            player.getPersistentData().putBoolean(ZERO_DAY_YOU_ALIVE_DONE_KEY, false);
                                            player.getPersistentData().putBoolean(YOU_ALIVE_REPLY_PENDING_KEY, false);
                                            player.getPersistentData().remove(YOU_ALIVE_REPLY_TRIGGER_TICK_KEY);
                                        }
                                        context.getSource().sendSuccess(() -> Component.literal("day set to " + day + " tick 0"), false);
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                                    return 0;
                                }))
        );

        event.getDispatcher().register(
                literal("code44_look_down")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                LateGameEventManager.triggerLookDown(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_godloveu")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                HorrorSoundManager.playGodLoveU(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_imwatch")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                HorrorSoundManager.playImWatch(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_sleepisshort")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                HorrorSoundManager.playSleepIsShort(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_urlife")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                HorrorSoundManager.playUrLife(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_crying")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                HorrorSoundManager.playCryingAmbient(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_brokenglass")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                HorrorSoundManager.playBrokenGlass(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_button_click")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playButtonClick(level, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_wooden_pressure_plate")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playWoodenPressurePlate(level, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_bow_shot")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playBowShot(level, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_block_place")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playBlockPlace(level, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_block_break_sound")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playBlockBreakSound(level, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_tnt_ignite")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playTntIgnite(level, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_pig_death")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                HorrorSoundManager.playPigDeath(level, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_disc11")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                HorrorSoundManager.playDisc11(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_yourend")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (YourEndEventManager.trigger(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Could not start yourend scene."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_efbui")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                EntitySpawnManager.playE44efbuiSpawnCue(level, player);
                                FearManager.scheduleManualEfbuiSpawn(player);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_zer000")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnZer000(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn ZER000 near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_observer")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnObserver(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn Observer near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_44")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnFortyFour(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn 44 near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_tunnel_dimension")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (TunnelDimensionManager.startScene(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not start tunnel dimension."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_tunnel_observer")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnTunnelObserver(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn tunnel observer.")); 
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_white_name")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnWhiteName(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn white name near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_skinwalker")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnSkinwalker(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn skinwalker near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_err44r")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnErr44r(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn err44r near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_greteminos")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnGreteminos(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn Greteminos near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_past_mistakes_door")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnPastMistakesDoor(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn past mistakes door."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_legacy_biome")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (LegacyBiomeManager.forceAroundPlayer(level, player)) {
                                    context.getSource().sendSuccess(() -> Component.literal("Spawned legacy biome fragments around the player."), false);
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Legacy biome generation works only in the overworld."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_clear_entities")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> clearCode44Entities(context.getSource()))
        );

        event.getDispatcher().register(
                literal("code44_spawn_lonely_door")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (LonelyDoorManager.tryPlaceLonelyDoor(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not place lonely door."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_free_house")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (FreeHouseManager.tryPlaceFreeHouse(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Could not place free house."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_glass_frame")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (GlassFrameManager.tryPlaceGlassFrame(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Could not place glass frame."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_maze_altar")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (MazeBuildingManager.tryPlaceMazeBuilding(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Could not place maze altar."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_maze_building")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (MazeBuildingManager.tryPlaceMazeBuilding(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Could not place maze altar."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_maze_guardian")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (MazeDimensionManager.spawnMazeGuardianForCommand(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                context.getSource().sendFailure(Component.literal("Maze guardian can only be spawned inside the maze."));
                                return 0;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_footsteps")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnFootsteps(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn footsteps near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_black_animal")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (BlackAnimalManager.trySpawnBlackAnimal(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn a black fake animal."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_false_second_player")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level && EntitySpawnManager.trySpawnWhiteName(level, player)) {
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("Could not spawn false second player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_photo_screamer")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null) {
                                LateGameEventManager.triggerPhotoScreamer(player);
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_inventory_distortion")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && LateGameEventManager.triggerInventoryDistortion(player)) {
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("Could not distort inventory."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_home_replacement")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level && LateGameEventManager.triggerHomeReplacement(level, player)) {
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("Could not trigger home replacement."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_chunk_distortion")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player != null && player.level() instanceof ServerLevel level
                                    && LateGameEventManager.triggerChunkDistortion(level, player, Math.max(1, FearManager.getChunkDistortionChunkCount(level)))) {
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendFailure(Component.literal("Could not trigger chunk distortion."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_door_knock")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (HorrorSoundManager.playDoorKnock(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not play the knock event."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_invisible_hit")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                player.hurt(level.damageSources().magic(), 0.5F + (level.random.nextFloat() * 0.5F));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_fake_item_break")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null) {
                                HorrorSoundManager.playFakeItemBreak(player);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_door_open_burst")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (HorrorSoundManager.playDoorOpenBurst(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not play the door burst event."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_efbui_stare")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                net.minecraft.core.BlockPos nearestDoor = EntitySpawnManager.findNearestDoor(level, player.blockPosition(), 10);
                                if (nearestDoor != null && EntitySpawnManager.trySpawnDoorwayE44efbui(level, player, nearestDoor)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn doorway efbui near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_villager_stare")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null) {
                                VILLAGER_STARE_UNTIL.put(player.getUUID(), player.tickCount + 800);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_break_boat")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level && tryBreakPlayerBoat(level, player)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("Player is not in a boat."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_wrong_mob")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level && EarlyGameEventManager.triggerWrongMob(level, player)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("Could not trigger wrong mob."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_ghost_tree")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level && EarlyGameEventManager.triggerGhostTree(level, player)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("Could not trigger ghost tree."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_spawn_mineshaft_efbui")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (EntitySpawnManager.trySpawnMineshaftEfbui(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not spawn mineshaft efbui near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_efbui_dimension")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                EfbuiDimensionManager.startScene(level, player);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_fake_animal")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                Animal animal = level.getNearestEntity(
                                        level.getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(16.0D),
                                                Zer000Manager::isEligibleAnimalForTesting),
                                        net.minecraft.world.entity.ai.targeting.TargetingConditions.forNonCombat(),
                                        player,
                                        player.getX(),
                                        player.getY(),
                                        player.getZ()
                                );

                                if (animal != null) {
                                    Zer000Manager.forceMarkFakeAnimal(animal);
                                    context.getSource().sendSuccess(() -> Component.literal("Nearest eligible animal marked as fake."), false);
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("No eligible animal found nearby."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_memorial_sign")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player != null && player.level() instanceof ServerLevel level) {
                                if (MemorialSignManager.tryPlaceMemorialSign(level, player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                context.getSource().sendFailure(Component.literal("Could not place a memorial sign near the player."));
                                return 0;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_rain_night")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerLevel level = context.getSource().getLevel();

                            if (level != null) {
                                BloodRainManager.triggerRainNight(level);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("Could not start a rain night in this world."));
                            return 0;
                        })
        );

        event.getDispatcher().register(
                literal("code44_unskippable_night")
                        .requires(CommonEvents::canUseDeveloperCommands)
                        .executes(context -> {
                            ServerLevel level = context.getSource().getLevel();
                            ServerPlayer player = context.getSource().getPlayer();

                            if (level != null && player != null) {
                                BloodRainManager.triggerUnskippableNight(level);
                                FearManager.startUnskippableNight(level, player);
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendFailure(Component.literal("This command can only be used by a player in a world."));
                            return 0;
                        })
        );
    }

    public static void enableDeveloperCommands(ServerPlayer player) {
        player.getPersistentData().putBoolean(DEV_COMMANDS_UNLOCKED_KEY, true);
        UNLOCKED_DEVELOPER_SESSIONS.add(player.getUUID());
        player.sendSystemMessage(Component.literal("code44 developer mode enabled"));
        if (player.server != null) {
            player.server.getCommands().sendCommands(player);
        }
    }

    private static int toggleEventDebug(CommandSourceStack source, boolean enabled) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }
        player.getPersistentData().putBoolean(DEBUG_ENABLED_KEY, enabled);
        source.sendSuccess(() -> Component.literal("code44 debug: " + (enabled ? "on" : "off")), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int toggleTickOverlay(CommandSourceStack source, boolean enabled) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }
        player.getPersistentData().putBoolean(TICK_OVERLAY_ENABLED_KEY, enabled);
        source.sendSuccess(() -> Component.literal("tick overlay: " + (enabled ? "on" : "off")), false);
        if (enabled && player.level() instanceof ServerLevel level) {
            sendDayTickOverlay(player, level);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int setTickSpeed(CommandSourceStack source, int speed) {
        ServerLevel level = source.getLevel();
        Code44WorldData.get(level).setDayTickSpeedMultiplier(speed);
        source.sendSuccess(() -> Component.literal("tick speed set to x" + speed), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int showTickSpeed(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        long dayTick = level.getDayTime() % 24000L;
        int speed = getDayTickSpeed(level);
        source.sendSuccess(() -> Component.literal("tick speed x" + speed + ", day tick " + dayTick + "/24000"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int showTickUsage(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("Usage: /tick on | /tick off | /tick speed <" + MIN_DAY_TICK_SPEED + "-" + MAX_DAY_TICK_SPEED + ">"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int clearCode44Entities(CommandSourceStack source) {
        int removed = 0;
        for (ServerLevel level : source.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                ResourceLocation entityId = entity.getType().builtInRegistryHolder().key().location();
                if (Code44Mod.MODID.equals(entityId.getNamespace())) {
                    entity.discard();
                    removed++;
                }
            }
        }

        final int removedCount = removed;
        source.sendSuccess(() -> Component.literal("Removed " + removedCount + " code44 entities."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int getDayTickSpeed(ServerLevel level) {
        return Math.max(MIN_DAY_TICK_SPEED, Code44WorldData.get(level).getDayTickSpeedMultiplier());
    }

    private static void sendDayTickOverlay(ServerPlayer player, ServerLevel level) {
        long dayTick = level.getDayTime() % 24000L;
        int speed = getDayTickSpeed(level);
        player.sendSystemMessage(
                Component.literal("[tick] " + dayTick + "/24000  speed x" + speed),
                true
        );
    }

    public static boolean canUseDeveloperCommands(CommandSourceStack source) {
        if (Code44Mod.isDevCommandsBuild() && source.hasPermission(4)) {
            return true;
        }

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return false;
        }

        return UNLOCKED_DEVELOPER_SESSIONS.contains(player.getUUID())
                || player.getPersistentData().getBoolean(DEV_COMMANDS_UNLOCKED_KEY);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.getPersistentData().getBoolean(DEV_COMMANDS_UNLOCKED_KEY)) {
                UNLOCKED_DEVELOPER_SESSIONS.add(player.getUUID());
            }
            FearManager.initializeEfbuiJoin(player);
            EfbuiDimensionManager.initializeSession(player);
            EfbuiDimensionManager.recoverPlayerIfNeeded(player);
            LonelyDoorManager.recoverPlayerIfNeeded(player);
            PastMistakesFieldManager.recoverPlayerIfNeeded(player);
            TunnelDimensionManager.recoverPlayerIfNeeded(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer originalPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
            FearManager.copySessionData(originalPlayer, newPlayer);
            EfbuiDimensionManager.clearSceneData(newPlayer);
            LonelyDoorManager.clearSceneData(newPlayer);
            PastMistakesFieldManager.clearSceneData(newPlayer);
            TunnelDimensionManager.clearSceneData(newPlayer);
        if (originalPlayer.getPersistentData().getBoolean(LAST_WORLD_MESSAGE_DONE_KEY)) {
            newPlayer.getPersistentData().putBoolean(LAST_WORLD_MESSAGE_DONE_KEY, true);
        }
        if (originalPlayer.getPersistentData().getBoolean(ZERO_DAY_OMEN_DONE_KEY)) {
            newPlayer.getPersistentData().putBoolean(ZERO_DAY_OMEN_DONE_KEY, true);
        }
        if (originalPlayer.getPersistentData().getBoolean(ZERO_DAY_BLINK_DONE_KEY)) {
            newPlayer.getPersistentData().putBoolean(ZERO_DAY_BLINK_DONE_KEY, true);
        }
        if (originalPlayer.getPersistentData().getBoolean(ZERO_DAY_YOU_ALIVE_DONE_KEY)) {
            newPlayer.getPersistentData().putBoolean(ZERO_DAY_YOU_ALIVE_DONE_KEY, true);
        }
            if (originalPlayer.getPersistentData().getBoolean(DEV_COMMANDS_UNLOCKED_KEY)) {
                newPlayer.getPersistentData().putBoolean(DEV_COMMANDS_UNLOCKED_KEY, true);
                UNLOCKED_DEVELOPER_SESSIONS.add(newPlayer.getUUID());
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (event.getTarget() instanceof Villager && FearManager.isVillagerStareUnlocked(level)) {
            startVillagerStare(player, level);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Villager)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (FearManager.isVillagerStareUnlocked(level)) {
            startVillagerStare(player, level);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND) {
            return;
        }

        if (!(level.getBlockState(event.getPos()).getBlock() instanceof net.minecraft.world.level.block.DoorBlock)) {
            return;
        }

        if (FearManager.shouldSpawnDoorwayEfbui(level, player)) {
            if (EntitySpawnManager.trySpawnDoorwayE44efbui(level, player, event.getPos())) {
                logEventDebug(player, ModEventCatalog.EFBUI_STARE, true, event.getPos());
            } else {
                logEventDebug(player, ModEventCatalog.EFBUI_STARE, false, event.getPos());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Bat bat) {
            bat.setSilent(true);
        }

        if (PastMistakesFieldManager.isFieldLevel(event.getLevel()) && !PastMistakesFieldManager.canEntityExist(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (event.loadedFromDisk()) {
            return;
        }

        if (event.getEntity() instanceof Bat bat
                && bat.level() instanceof ServerLevel level
                && !bat.getPersistentData().getBoolean("code44BoostedBat")) {
            bat.getPersistentData().putBoolean("code44BoostedBat", true);

            for (int i = 0; i < 5; i++) {
                Bat extraBat = net.minecraft.world.entity.EntityType.BAT.create(level);

                if (extraBat == null) {
                    continue;
                }

                extraBat.setSilent(true);
                extraBat.getPersistentData().putBoolean("code44BoostedBat", true);
                extraBat.moveTo(
                        bat.getX() + ((level.random.nextDouble() - 0.5D) * 6.0D),
                        bat.getY() + ((level.random.nextDouble() - 0.5D) * 2.0D),
                        bat.getZ() + ((level.random.nextDouble() - 0.5D) * 6.0D),
                        level.random.nextFloat() * 360.0F,
                        0.0F
                );
                level.addFreshEntity(extraBat);
            }
        }

        if (event.getEntity() instanceof Animal animal && animal.level() instanceof ServerLevel level) {
            Zer000Manager.tryMarkFakeAnimal(level, animal);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Bat bat && bat.level() instanceof ServerLevel level) {
            level.playSound(
                    null,
                    bat.blockPosition(),
                    net.minecraft.sounds.SoundEvents.BAT_DEATH,
                    net.minecraft.sounds.SoundSource.HOSTILE,
                    0.8F,
                    1.0F
            );
        }

        if ((event.getEntity() instanceof BlackCowEntity
                || event.getEntity() instanceof BlackPigEntity
                || event.getEntity() instanceof BlackChickenEntity)
                && event.getEntity().level() instanceof ServerLevel level) {
            level.playSound(
                    null,
                    event.getEntity().blockPosition(),
                    com.igore.code44.registry.ModSounds.GLICHDEADBLACK.get(),
                    net.minecraft.sounds.SoundSource.HOSTILE,
                    1.6F,
                    1.0F
            );
        }

        if (event.getEntity() instanceof Animal animal
                && animal.level() instanceof ServerLevel level
                && event.getSource().getEntity() instanceof ServerPlayer player
                && Zer000Manager.isFakeAnimal(animal)) {
            boolean canSpawnToday = FearManager.canSpawnZer000(player);
            boolean chancePassed = level.random.nextInt(100) < 30;
            if (canSpawnToday && chancePassed) {
                if (EntitySpawnManager.trySpawnZer000At(level, player, animal.position())) {
                    FearManager.markZer000Spawned(player);
                    logEventDebug(player, ModEventCatalog.ZER000, true, animal.blockPosition());
                } else {
                    logEventDebug(player, ModEventCatalog.ZER000, false, animal.blockPosition());
                }
            } else {
                logEventDebug(player, ModEventCatalog.ZER000, false, animal.blockPosition());
            }
            return;
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            player.getPersistentData().putInt(
                    PLAYER_DEATH_COUNT_KEY,
                    player.getPersistentData().getInt(PLAYER_DEATH_COUNT_KEY) + 1
            );
            player.getPersistentData().putBoolean(YOU_ALIVE_REPLY_PENDING_KEY, false);
            player.getPersistentData().remove(YOU_ALIVE_REPLY_TRIGGER_TICK_KEY);
            HorrorSoundManager.stopZer000Sound(player);
            EfbuiDimensionManager.clearSceneData(player);
            LonelyDoorManager.clearSceneData(player);
            PastMistakesFieldManager.clearSceneData(player);
            TunnelDimensionManager.clearSceneData(player);
        }
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        String loweredMessage = event.getRawText().trim().toLowerCase();
        String normalizedMessage = normalizeFakeHelloMessage(event.getRawText());
        if (handleYouAliveResponse(level, player, loweredMessage)) {
            return;
        }
        if (handleFakeHelloDialogue(level, player, normalizedMessage)) {
            return;
        }
        if (FearManager.getCurrentDay(level) >= 0
                && (loweredMessage.equals("hello") || loweredMessage.equals("hi") || loweredMessage.equals("Р С—РЎР‚Р С‘Р Р†Р ВµРЎвЂљ"))) {
            triggerFakeHelloEvent(level, player);
        }

        E44efbuiEntity efbui = level.getEntitiesOfClass(
                E44efbuiEntity.class,
                player.getBoundingBox().inflate(24.0D),
                entity -> entity.distanceToSqr(player) <= (24.0D * 24.0D)
        ).stream().findFirst().orElse(null);

        if (efbui == null) {
            return;
        }

        String message = loweredMessage;
        String reply = null;

        if (message.contains("hello") || message.contains("hi")) {
            reply = "hello.";
        } else if (message.contains("who are you")) {
            reply = "efbui.";
        } else if (message.contains("fuck you")) {
            reply = "you talk too much.";
        } else if (message.contains("can you see me")) {
            reply = "i see you.";
        } else if (message.contains("where are you")) {
            reply = "close enough.";
        }

        if (reply != null) {
            player.sendSystemMessage(Component.translatable(
                    "chat.type.text",
                    Component.literal("efbui").withStyle(ChatFormatting.WHITE),
                    Component.literal(reply).withStyle(ChatFormatting.WHITE)
            ));
        }
    }

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        ServerLevel level = (ServerLevel) event.getLevel();

        for (ServerPlayer player : level.players()) {
            player.getPersistentData().putInt(LAST_SLEEP_DAY_KEY, FearManager.getCurrentDay(level));
            if (FearManager.isUnskippableNightActive(level, player)) {
                event.setTimeAddition(level.getDayTime());
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.level instanceof ServerLevel level)) {
            return;
        }

        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        if (!level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DAYLIGHT)) {
            return;
        }

        int speed = getDayTickSpeed(level);
        if (speed <= 1) {
            return;
        }

        level.setDayTime(level.getDayTime() + (speed - 1L));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (player.getPersistentData().getBoolean(TICK_OVERLAY_ENABLED_KEY)) {
            sendDayTickOverlay(player, level);
        }

        for (Bat bat : level.getEntitiesOfClass(Bat.class, player.getBoundingBox().inflate(128.0D), entity -> true)) {
            if (!bat.isSilent()) {
                bat.setSilent(true);
            }
        }

        if (EfbuiDimensionManager.isSceneActive(player)) {
            EfbuiDimensionManager.tick(level, player);
            return;
        }

        if (LonelyDoorManager.isSceneActive(player)) {
            LonelyDoorManager.tick(level, player);
            return;
        }

        if (PastMistakesFieldManager.isSceneActive(player)) {
            PastMistakesFieldManager.tick(level, player);
            return;
        }

        if (TunnelDimensionManager.isSceneActive(player)) {
            TunnelDimensionManager.tick(level, player);
            return;
        }

        if (MazeDimensionManager.isSceneActive(player)) {
            MazeDimensionManager.tick(level, player);
            return;
        }

        if (YourEndEventManager.isSceneActive(player)) {
            YourEndEventManager.tick(level, player);
            return;
        }

        LonelyDoorManager.tick(level, player);
        PastMistakesFieldManager.tick(level, player);
        LegacyBiomeManager.tickAroundPlayer(level, player);
        EarlyGameEventManager.tick(level, player);
        MazeBuildingManager.tick(level, player);
        MazeDimensionManager.tick(level, player);
        TunnelDimensionManager.tick(level, player);

        tickFakeHelloSequence(level, player);
        tickFakeHelloReply(level, player);
        tickFakeHelloAftermath(level, player);
        tickYouAliveReply(level, player);
        tickVillagerStare(level, player);
        LateGameEventManager.tick(level, player);

        if (FearManager.getCurrentDay(level) == 0 && !player.getPersistentData().getBoolean(LAST_WORLD_MESSAGE_DONE_KEY)) {
            sendSystem44LastWorldMessage(player);
            player.getPersistentData().putBoolean(LAST_WORLD_MESSAGE_DONE_KEY, true);
            logEventDebug(player, ModEventCatalog.LAST_WORLD_MESSAGE, true);
        }

        long dayTick = level.getDayTime() % 24000L;
        if (FearManager.getCurrentDay(level) == 0 && !player.getPersistentData().getBoolean(ZERO_DAY_OMEN_DONE_KEY) && dayTick >= 1000L) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, false, false));
            LateGameEventManager.triggerLookDown(player);
            HorrorSoundManager.playGodLoveU(player);
            player.sendSystemMessage(Component.translatable(
                    "chat.type.text",
                    Component.literal("System44.exe").withStyle(ChatFormatting.DARK_RED),
                    Component.literal("Do not trust this world.").withStyle(ChatFormatting.RED)
            ));
            player.getPersistentData().putBoolean(ZERO_DAY_OMEN_DONE_KEY, true);
            FearManager.markEventTriggered(level, player, ModEventCatalog.ZERO_DAY_OMEN);
            logEventDebug(player, ModEventCatalog.ZERO_DAY_OMEN, true);
            return;
        }
        if (FearManager.getCurrentDay(level) == 0 && !player.getPersistentData().getBoolean(ZERO_DAY_BLINK_DONE_KEY) && dayTick >= 1600L) {
            LateGameEventManager.triggerScreenBlink(player);
            player.getPersistentData().putBoolean(ZERO_DAY_BLINK_DONE_KEY, true);
            FearManager.markEventTriggered(level, player, ModEventCatalog.SCREEN_BLINK);
            logEventDebug(player, ModEventCatalog.SCREEN_BLINK, true);
        }

        if (FearManager.getCurrentDay(level) == 0 && !player.getPersistentData().getBoolean(ZERO_DAY_YOU_ALIVE_DONE_KEY) && dayTick >= 2200L) {
            sendYouAliveMessage(player);
            player.getPersistentData().putBoolean(ZERO_DAY_YOU_ALIVE_DONE_KEY, true);
            FearManager.markEventTriggered(level, player, ModEventCatalog.YOU_ALIVE);
            logEventDebug(player, ModEventCatalog.YOU_ALIVE, true);
            return;
        }

        boolean raining = level.isRaining();
        boolean rainActive = player.getPersistentData().getBoolean(ERR44R_RAIN_ACTIVE_KEY);
        if (raining && !rainActive) {
            if (EntitySpawnManager.trySpawnErr44r(level, player)) {
                logEventDebug(player, "err44r_rain", true);
            } else {
                logEventDebug(player, "err44r_rain", false);
            }
            player.getPersistentData().putBoolean(ERR44R_RAIN_ACTIVE_KEY, true);
        } else if (!raining && rainActive) {
            player.getPersistentData().putBoolean(ERR44R_RAIN_ACTIVE_KEY, false);
        }

        if (!player.getPersistentData().getBoolean(ZERO_DAY_YOU_ALIVE_DONE_KEY) && FearManager.shouldTriggerYouAlive(level, player)) {
            sendYouAliveMessage(player);
            player.getPersistentData().putBoolean(ZERO_DAY_YOU_ALIVE_DONE_KEY, true);
            FearManager.markEventTriggered(level, player, ModEventCatalog.YOU_ALIVE);
            logEventDebug(player, ModEventCatalog.YOU_ALIVE, true);
            return;
        }

        if (FearManager.shouldGrantSecondDayAdvancement(level, player)) {
            Advancement advancement = level.getServer().getAdvancements().getAdvancement(SECOND_DAY_ADVANCEMENT_ID);
            if (advancement != null) {
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
                FearManager.markSecondDayAdvancementGranted(player);
                logEventDebug(player, "second_day_advancement", true);
            } else {
                logEventDebug(player, "second_day_advancement", false);
            }
        }

        if (FearManager.isMemorialSignUnlocked(level) && FearManager.shouldTriggerMemorialSign(level, player)) {
            if (MemorialSignManager.tryPlaceMemorialSign(level, player)) {
                FearManager.markMemorialSignTriggered(player);
                logEventDebug(player, ModEventCatalog.MEMORIAL_SIGN, true);
            } else {
                FearManager.rescheduleEventRetry(level, player, ModEventCatalog.MEMORIAL_SIGN, 1200, 3600);
                logEventDebug(player, ModEventCatalog.MEMORIAL_SIGN, false);
            }
        }

        if (FearManager.shouldTriggerRainNight(level, player)) {
            BloodRainManager.triggerRainNight(level);
            EntitySpawnManager.trySpawnErr44r(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.RAIN_NIGHT);
            FearManager.deferOverdueDaySchedulesAfterNightJump(level, player);
            EfbuiDimensionManager.deferTriggerAfterNightJump(level, player);
            logEventDebug(player, ModEventCatalog.RAIN_NIGHT, true);
            return;
        }

        if (FearManager.shouldTriggerUnskippableNight(level, player)) {
            BloodRainManager.triggerUnskippableNight(level);
            FearManager.startUnskippableNight(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.UNSKIPPABLE_NIGHT);
            FearManager.deferOverdueDaySchedulesAfterNightJump(level, player);
            EfbuiDimensionManager.deferTriggerAfterNightJump(level, player);
            logEventDebug(player, ModEventCatalog.UNSKIPPABLE_NIGHT, true);
            return;
        }

        if (FearManager.isUnskippableNightActive(level, player) && level.isDay() && player.isSleeping()) {
            player.stopSleepInBed(false, true);
        }

        if (FearManager.shouldTriggerHeartStare(level, player)) {
            FearManager.startHeartStare(player);
            HorrorSoundManager.startHeartEventSound(player);
            logEventDebug(player, ModEventCatalog.HEART_STARE, true);
        }

        if (FearManager.isHeartStareActive(player)) {
            holdMobsOnPlayer(level, player);

            if (FearManager.shouldRefreshHeartSound(player)) {
                HorrorSoundManager.startHeartEventSound(player);
            }
        } else if (FearManager.shouldEndHeartStare(player)) {
            HorrorSoundManager.stopHeartEventSound(player);
            FearManager.clearHeartStare(player);
        }

        if (FearManager.isEfbuiJoinUnlocked(level) && FearManager.shouldTriggerEfbuiJoin(level, player)) {
            Component joinMessage = Component.translatable(
                    "multiplayer.player.joined",
                    Component.literal("efbui").withStyle(ChatFormatting.YELLOW)
            )
                    .withStyle(ChatFormatting.YELLOW);
            player.sendSystemMessage(joinMessage);
            HorrorSoundManager.playEfbuiJoinSound(player);
            FearManager.markEfbuiJoinTriggered(player);
            FearManager.scheduleEfbuiSpawn(player);
            logEventDebug(player, ModEventCatalog.EFBUI_JOIN, true);
        }

        if (FearManager.shouldExecutePendingEfbuiSpawn(player)) {
            EntitySpawnManager.playE44efbuiSpawnCue(level, player);
            if (EntitySpawnManager.trySpawnE44efbui(level, player)) {
                logEventDebug(player, ModEventCatalog.E44EFBUI_SPAWN, true);
            } else {
                logEventDebug(player, ModEventCatalog.E44EFBUI_SPAWN, false);
            }
            FearManager.clearPendingEfbuiSpawn(player);
        }

        if (FearManager.shouldTriggerLullaby(level, player)) {
            HorrorSoundManager.playLullabyEvent(level, player);
            FearManager.markLullabyTriggered(player);
            logEventDebug(player, ModEventCatalog.LULLABY, true);
            return;
        }

        if (EfbuiDimensionManager.shouldTrigger(level, player)) {
            EfbuiDimensionManager.startScene(level, player);
            logEventDebug(player, ModEventCatalog.EFBUI_DIMENSION, true);
            return;
        }

        if (FearManager.shouldTriggerObserverSpawn(level, player)) {
            BlockPos observerPos = EntitySpawnManager.findVisibleObserverSpawnPosition(level, player);
            if (EntitySpawnManager.spawnObserverAt(level, player, observerPos)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.OBSERVER);
                logEventDebug(player, ModEventCatalog.OBSERVER, true, observerPos);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.OBSERVER, 1200, 3600);
            logEventDebug(player, ModEventCatalog.OBSERVER, false, null);
        }

        if (FearManager.shouldTriggerPastMistakesDoor(level, player)) {
            if (EntitySpawnManager.trySpawnPastMistakesDoor(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.PAST_MISTAKES_DOOR);
                logEventDebug(player, ModEventCatalog.PAST_MISTAKES_DOOR, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.PAST_MISTAKES_DOOR, 1200, 3600);
            logEventDebug(player, ModEventCatalog.PAST_MISTAKES_DOOR, false);
        }

        if (FearManager.shouldTriggerFortyFour(level, player)) {
            if (EntitySpawnManager.trySpawnFortyFour(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.FORTY_FOUR);
                logEventDebug(player, ModEventCatalog.FORTY_FOUR, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.FORTY_FOUR, 1200, 3000);
            logEventDebug(player, ModEventCatalog.FORTY_FOUR, false);
        }

        if (FearManager.shouldTriggerWrongMob(level, player)) {
            if (EarlyGameEventManager.triggerWrongMob(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.WRONG_MOB);
                logEventDebug(player, ModEventCatalog.WRONG_MOB, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.WRONG_MOB, 1200, 3000);
            logEventDebug(player, ModEventCatalog.WRONG_MOB, false);
        }

        if (FearManager.shouldTriggerGhostTree(level, player)) {
            if (EarlyGameEventManager.triggerGhostTree(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.GHOST_TREE);
                logEventDebug(player, ModEventCatalog.GHOST_TREE, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.GHOST_TREE, 1200, 3000);
            logEventDebug(player, ModEventCatalog.GHOST_TREE, false);
        }

        if (FearManager.shouldTriggerLonelyDoor(level, player)) {
            if (LonelyDoorManager.tryPlaceLonelyDoor(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.LONELY_DOOR);
                logEventDebug(player, ModEventCatalog.LONELY_DOOR, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.LONELY_DOOR, 1800, 4200);
            logEventDebug(player, ModEventCatalog.LONELY_DOOR, false);
        }

        if (FearManager.shouldTriggerFreeHouse(level, player)) {
            if (FreeHouseManager.tryPlaceFreeHouse(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.FREE_HOUSE);
                logEventDebug(player, ModEventCatalog.FREE_HOUSE, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.FREE_HOUSE, 1800, 4200);
            logEventDebug(player, ModEventCatalog.FREE_HOUSE, false);
        }

        if (FearManager.shouldTriggerGlassFrame(level, player)) {
            if (GlassFrameManager.tryPlaceGlassFrame(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.GLASS_FRAME);
                logEventDebug(player, ModEventCatalog.GLASS_FRAME, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.GLASS_FRAME, 1800, 4200);
            logEventDebug(player, ModEventCatalog.GLASS_FRAME, false);
        }

        if (FearManager.shouldTriggerMazeBuilding(level, player)) {
            if (MazeBuildingManager.tryPlaceMazeBuilding(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.MAZE_BUILDING);
                logEventDebug(player, ModEventCatalog.MAZE_BUILDING, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.MAZE_BUILDING, 1800, 4200);
            logEventDebug(player, ModEventCatalog.MAZE_BUILDING, false);
        }

        if (FearManager.shouldTriggerTunnelObserver(level, player)) {
            if (EntitySpawnManager.trySpawnTunnelObserver(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.TUNNEL_OBSERVER);
                Entity observer = level.getEntitiesOfClass(
                        com.igore.code44.entity.ObserverEntity.class,
                        player.getBoundingBox().inflate(80.0D),
                        entity -> entity.getTarget() == player
                ).stream().findFirst().orElse(null);
                logEventDebug(player, ModEventCatalog.TUNNEL_OBSERVER, true, observer != null ? observer.blockPosition() : null);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.TUNNEL_OBSERVER, 1200, 3000);
            logEventDebug(player, ModEventCatalog.TUNNEL_OBSERVER, false, null);
        }

        if (FearManager.shouldTriggerFalseSecondPlayer(level, player)) {
            if (EntitySpawnManager.trySpawnWhiteName(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.FALSE_SECOND_PLAYER);
                logEventDebug(player, ModEventCatalog.FALSE_SECOND_PLAYER, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.FALSE_SECOND_PLAYER, 1200, 3000);
            logEventDebug(player, ModEventCatalog.FALSE_SECOND_PLAYER, false);
        }

        if (FearManager.shouldTriggerPhotoScreamer(level, player)) {
            LateGameEventManager.triggerPhotoScreamer(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.PHOTO_SCREAMER);
            logEventDebug(player, ModEventCatalog.PHOTO_SCREAMER, true);
            return;
        }

        if (FearManager.shouldTriggerYouAlive(level, player)) {
            sendYouAliveMessage(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.YOU_ALIVE);
            logEventDebug(player, ModEventCatalog.YOU_ALIVE, true);
            return;
        }

        if (FearManager.shouldTriggerLookDown(level, player)) {
            LateGameEventManager.triggerLookDown(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.LOOK_DOWN);
            logEventDebug(player, ModEventCatalog.LOOK_DOWN, true);
            return;
        }

        if (FearManager.shouldTriggerInvertMouse(level, player)) {
            LateGameEventManager.triggerInvertMouse(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.INVERT_MOUSE);
            logEventDebug(player, ModEventCatalog.INVERT_MOUSE, true);
            return;
        }

        if (FearManager.shouldTriggerSensitivitySpike(level, player)) {
            LateGameEventManager.triggerSensitivitySpike(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.SENSITIVITY_SPIKE);
            logEventDebug(player, ModEventCatalog.SENSITIVITY_SPIKE, true);
            return;
        }

        if (FearManager.shouldTriggerMasterVolumeDrop(level, player)) {
            LateGameEventManager.triggerMasterVolumeDrop(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.MASTER_VOLUME_DROP);
            logEventDebug(player, ModEventCatalog.MASTER_VOLUME_DROP, true);
            return;
        }

        if (FearManager.shouldTriggerGodLoveU(level, player)) {
            HorrorSoundManager.playGodLoveU(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.GODLOVEU);
            logEventDebug(player, ModEventCatalog.GODLOVEU, true);
            return;
        }

        if (FearManager.shouldTriggerButtonClick(level, player)) {
            HorrorSoundManager.playButtonClick(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.BUTTON_CLICK);
            logEventDebug(player, ModEventCatalog.BUTTON_CLICK, true);
            return;
        }

        if (FearManager.shouldTriggerWoodenPressurePlate(level, player)) {
            HorrorSoundManager.playWoodenPressurePlate(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.WOODEN_PRESSURE_PLATE);
            logEventDebug(player, ModEventCatalog.WOODEN_PRESSURE_PLATE, true);
            return;
        }

        if (FearManager.shouldTriggerBlockPlace(level, player)) {
            HorrorSoundManager.playBlockPlace(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.BLOCK_PLACE);
            logEventDebug(player, ModEventCatalog.BLOCK_PLACE, true);
            return;
        }

        if (FearManager.shouldTriggerBlockBreakSound(level, player)) {
            HorrorSoundManager.playBlockBreakSound(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.BLOCK_BREAK_SOUND);
            logEventDebug(player, ModEventCatalog.BLOCK_BREAK_SOUND, true);
            return;
        }

        if (FearManager.shouldTriggerImWatch(level, player)) {
            HorrorSoundManager.playImWatch(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.IMWATCH);
            logEventDebug(player, ModEventCatalog.IMWATCH, true);
            return;
        }

        if (FearManager.shouldTriggerSleepIsShort(level, player)) {
            HorrorSoundManager.playSleepIsShort(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.SLEEPISSHORT);
            logEventDebug(player, ModEventCatalog.SLEEPISSHORT, true);
            return;
        }

        if (FearManager.shouldTriggerUrLife(level, player)) {
            HorrorSoundManager.playUrLife(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.URLIFE);
            logEventDebug(player, ModEventCatalog.URLIFE, true);
            return;
        }

        if (FearManager.shouldTriggerBowShot(level, player)) {
            HorrorSoundManager.playBowShot(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.BOW_SHOT);
            logEventDebug(player, ModEventCatalog.BOW_SHOT, true);
            return;
        }

        if (FearManager.shouldTriggerCrying(level, player)) {
            HorrorSoundManager.playCryingAmbient(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.CRYING);
            logEventDebug(player, ModEventCatalog.CRYING, true);
            return;
        }

        if (FearManager.shouldTriggerBrokenGlass(level, player)) {
            HorrorSoundManager.playBrokenGlass(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.BROKEN_GLASS);
            logEventDebug(player, ModEventCatalog.BROKEN_GLASS, true);
            return;
        }

        if (FearManager.shouldTriggerPigDeath(level, player)) {
            HorrorSoundManager.playPigDeath(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.PIG_DEATH);
            logEventDebug(player, ModEventCatalog.PIG_DEATH, true);
            return;
        }

        if (FearManager.shouldTriggerTntIgnite(level, player)) {
            HorrorSoundManager.playTntIgnite(level, player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.TNT_IGNITE);
            logEventDebug(player, ModEventCatalog.TNT_IGNITE, true);
            return;
        }

        if (FearManager.shouldTriggerMusicDisc11(level, player)) {
            HorrorSoundManager.playDisc11(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.MUSIC_DISC_11);
            logEventDebug(player, ModEventCatalog.MUSIC_DISC_11, true);
            return;
        }

        if (FearManager.shouldTriggerYourEnd(level, player)) {
            if (YourEndEventManager.trigger(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.YOUREND);
                logEventDebug(player, ModEventCatalog.YOUREND, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.YOUREND, 1800, 4200);
            logEventDebug(player, ModEventCatalog.YOUREND, false);
        }

        if (FearManager.shouldTriggerOutdoorBat(level, player)) {
            if (trySpawnOutdoorBat(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.OUTDOOR_BAT);
                logEventDebug(player, ModEventCatalog.OUTDOOR_BAT, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.OUTDOOR_BAT, 1200, 3000);
            logEventDebug(player, ModEventCatalog.OUTDOOR_BAT, false);
        }

        if (FearManager.shouldTriggerFootsteps(level, player)) {
            if (EntitySpawnManager.trySpawnFootsteps(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.FOOTSTEPS);
                logEventDebug(player, ModEventCatalog.FOOTSTEPS, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.FOOTSTEPS, 1200, 3000);
            logEventDebug(player, ModEventCatalog.FOOTSTEPS, false);
        }

        if (FearManager.shouldTriggerBlackAnimal(level, player)) {
            if (BlackAnimalManager.trySpawnBlackAnimal(level, player)) {
                FearManager.markBlackAnimalSpawned(level, player);
                FearManager.markEventTriggered(level, player, ModEventCatalog.BLACK_ANIMAL);
                logEventDebug(player, ModEventCatalog.BLACK_ANIMAL, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.BLACK_ANIMAL, 1200, 3000);
            logEventDebug(player, ModEventCatalog.BLACK_ANIMAL, false);
        }

        if (FearManager.shouldTriggerSkinwalker(level, player)) {
            if (EntitySpawnManager.trySpawnSkinwalker(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.SKINWALKER);
                logEventDebug(player, ModEventCatalog.SKINWALKER, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.SKINWALKER, 1200, 3000);
            logEventDebug(player, ModEventCatalog.SKINWALKER, false);
        }

        if (FearManager.shouldTriggerInventoryDistortion(level, player)) {
            if (LateGameEventManager.triggerInventoryDistortion(player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.INVENTORY_DISTORTION);
                logEventDebug(player, ModEventCatalog.INVENTORY_DISTORTION, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.INVENTORY_DISTORTION, 1200, 3000);
            logEventDebug(player, ModEventCatalog.INVENTORY_DISTORTION, false);
        }

        if (FearManager.shouldTriggerHomeReplacement(level, player)) {
            if (LateGameEventManager.triggerHomeReplacement(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.HOME_REPLACEMENT);
                logEventDebug(player, ModEventCatalog.HOME_REPLACEMENT, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.HOME_REPLACEMENT, 1800, 4200);
            logEventDebug(player, ModEventCatalog.HOME_REPLACEMENT, false);
        }

        if (FearManager.shouldTriggerChunkDistortion(level, player)) {
            if (LateGameEventManager.triggerChunkDistortion(level, player, FearManager.getChunkDistortionChunkCount(level))) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.CHUNK_DISTORTION);
                logEventDebug(player, ModEventCatalog.CHUNK_DISTORTION, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.CHUNK_DISTORTION, 1800, 4200);
            logEventDebug(player, ModEventCatalog.CHUNK_DISTORTION, false);
        }

        if (FearManager.shouldTriggerInvisibleHit(level, player)) {
            player.hurt(level.damageSources().magic(), 0.5F + (level.random.nextFloat() * 0.5F));
            FearManager.markEventTriggered(level, player, ModEventCatalog.INVISIBLE_HIT);
            logEventDebug(player, ModEventCatalog.INVISIBLE_HIT, true);
            return;
        }

        if (FearManager.shouldTriggerFakeItemBreak(level, player)) {
            HorrorSoundManager.playFakeItemBreak(player);
            FearManager.markEventTriggered(level, player, ModEventCatalog.FAKE_ITEM_BREAK);
            logEventDebug(player, ModEventCatalog.FAKE_ITEM_BREAK, true);
            return;
        }

        if (FearManager.shouldTriggerDoorKnock(level, player)) {
            if (HorrorSoundManager.playDoorKnock(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.DOOR_KNOCK);
                logEventDebug(player, ModEventCatalog.DOOR_KNOCK, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.DOOR_KNOCK, 1200, 3000);
            logEventDebug(player, ModEventCatalog.DOOR_KNOCK, false);
        }

        if (FearManager.shouldTriggerDoorOpenBurst(level, player)) {
            if (HorrorSoundManager.playDoorOpenBurst(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.DOOR_OPEN_BURST);
                logEventDebug(player, ModEventCatalog.DOOR_OPEN_BURST, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.DOOR_OPEN_BURST, 1200, 3000);
            logEventDebug(player, ModEventCatalog.DOOR_OPEN_BURST, false);
        }

        if (FearManager.shouldTriggerMineshaftEfbui(level, player)) {
            if (EntitySpawnManager.trySpawnMineshaftEfbui(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.MINESHAFT_EFBUI);
                logEventDebug(player, ModEventCatalog.MINESHAFT_EFBUI, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.MINESHAFT_EFBUI, 1200, 3000);
            logEventDebug(player, ModEventCatalog.MINESHAFT_EFBUI, false);
        }

        if (player.getVehicle() instanceof Boat && FearManager.shouldTriggerBoatBreak(level, player)) {
            if (tryBreakPlayerBoat(level, player)) {
                FearManager.markEventTriggered(level, player, ModEventCatalog.BOAT_BREAK);
                logEventDebug(player, ModEventCatalog.BOAT_BREAK, true);
                return;
            }
            FearManager.rescheduleEventRetry(level, player, ModEventCatalog.BOAT_BREAK, 1200, 3000);
            logEventDebug(player, ModEventCatalog.BOAT_BREAK, false);
        }

        if (!FearManager.shouldPlayMineWhisper(level, player)) {
            return;
        }

        HorrorSoundManager.playAmbientWhisper(level, player);
    }

    private static void holdMobsOnPlayer(ServerLevel level, ServerPlayer player) {
        for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(256.0D), Mob::isAlive)) {
            mob.setTarget(null);
            mob.getNavigation().stop();
            mob.setDeltaMovement(Vec3.ZERO);
            mob.getLookControl().setLookAt(player, 60.0F, 60.0F);
        }
    }

    private static void tickVillagerStare(ServerLevel level, ServerPlayer player) {
        Integer activeUntil = VILLAGER_STARE_UNTIL.get(player.getUUID());
        if (activeUntil == null) {
            return;
        }

        if (player.tickCount >= activeUntil) {
            VILLAGER_STARE_UNTIL.remove(player.getUUID());
            return;
        }

        java.util.List<Villager> villagers = level.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(32.0D), Entity::isAlive);
        int total = Math.max(1, villagers.size());

        for (int i = 0; i < villagers.size(); i++) {
            Villager villager = villagers.get(i);
            villager.setTarget(null);
            double angle = ((Math.PI * 2.0D) / total) * i;
            double targetX = player.getX() + (Math.cos(angle) * 2.8D);
            double targetZ = player.getZ() + (Math.sin(angle) * 2.8D);

            if (villager.position().distanceTo(new Vec3(targetX, player.getY(), targetZ)) > 0.9D) {
                villager.getNavigation().moveTo(targetX, player.getY(), targetZ, 1.35D);
            } else {
                villager.getNavigation().stop();
                villager.setDeltaMovement(Vec3.ZERO);
            }
            villager.getLookControl().setLookAt(player, 60.0F, 60.0F);
        }
    }

    private static void startVillagerStare(ServerPlayer player, ServerLevel level) {
        if (!FearManager.isVillagerStareUnlocked(level)) {
            return;
        }

        VILLAGER_STARE_UNTIL.put(player.getUUID(), player.tickCount + 800);
        logEventDebug(player, ModEventCatalog.VILLAGER_STARE, true);
    }

    private static void sendSystem44LastWorldMessage(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable(
                "chat.type.text",
                Component.literal("System44.exe").withStyle(ChatFormatting.WHITE),
                Component.literal("This is the last world you created").withStyle(ChatFormatting.WHITE)
        ));
    }

    private static void sendYouAliveMessage(ServerPlayer player) {
        player.getPersistentData().putBoolean(YOU_ALIVE_REPLY_PENDING_KEY, true);
        player.sendSystemMessage(Component.translatable(
                "chat.type.text",
                player.getName().copy().withStyle(ChatFormatting.WHITE),
                Component.literal("you alive?").withStyle(ChatFormatting.WHITE)
        ));
    }

    private static boolean handleYouAliveResponse(ServerLevel level, ServerPlayer player, String loweredMessage) {
        if (!player.getPersistentData().getBoolean(YOU_ALIVE_REPLY_PENDING_KEY)) {
            return false;
        }

        if (!isYouAliveAffirmative(loweredMessage)) {
            return false;
        }

        player.getPersistentData().putBoolean(YOU_ALIVE_REPLY_PENDING_KEY, false);
        player.getPersistentData().putInt(YOU_ALIVE_REPLY_TRIGGER_TICK_KEY, player.tickCount + 40);
        return true;
    }

    private static boolean isYouAliveAffirmative(String loweredMessage) {
        return loweredMessage.equals("yes")
                || loweredMessage.equals("yea")
                || loweredMessage.equals("yeah")
                || loweredMessage.equals("ye")
                || loweredMessage.equals("ya");
    }

    private static void tickYouAliveReply(ServerLevel level, ServerPlayer player) {
        int triggerTick = player.getPersistentData().getInt(YOU_ALIVE_REPLY_TRIGGER_TICK_KEY);
        if (triggerTick <= 0 || player.tickCount < triggerTick) {
            return;
        }

        player.getPersistentData().remove(YOU_ALIVE_REPLY_TRIGGER_TICK_KEY);

        net.minecraft.world.entity.LightningBolt lightningBolt =
                net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
        if (lightningBolt != null) {
            lightningBolt.moveTo(player.getX(), player.getY(), player.getZ());
            lightningBolt.setVisualOnly(false);
            level.addFreshEntity(lightningBolt);
        }

        player.sendSystemMessage(Component.translatable(
                "chat.type.text",
                Component.literal("System44.exe").withStyle(ChatFormatting.DARK_RED),
                Component.literal("LEAVE ME ALONE. YOU. NOT. A... ME.").withStyle(ChatFormatting.RED)
        ));
    }

    public static void startFakeHelloAftermath(ServerPlayer player) {
        player.getPersistentData().putBoolean(FAKE_HELLO_CHAT_ACTIVE_KEY, false);
        player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, false);
        player.getPersistentData().putInt(FAKE_HELLO_REVENGE_TICK_KEY, player.tickCount + 60);
        player.getPersistentData().putInt(FAKE_HELLO_LEAVE_TICK_KEY, player.tickCount + 160);
    }

    private static boolean triggerFakeHelloEvent(ServerLevel level, ServerPlayer player) {
        Code44WorldData worldData = Code44WorldData.get(level);
        if (worldData.isFakeHelloUsed()) {
            return false;
        }

        if (player.getPersistentData().getBoolean(FAKE_HELLO_ACTIVE_KEY)) {
            return false;
        }

        worldData.setFakeHelloUsed(true);
        player.getPersistentData().putBoolean(FAKE_HELLO_ACTIVE_KEY, true);
        player.getPersistentData().putInt(FAKE_HELLO_STAGE_KEY, 1);
        player.getPersistentData().putInt(FAKE_HELLO_LINE_INDEX_KEY, 0);
        player.getPersistentData().putInt(FAKE_HELLO_NEXT_TICK_KEY, player.tickCount + 60);
        return true;
    }

    private static void tickFakeHelloSequence(ServerLevel level, ServerPlayer player) {
        if (!player.getPersistentData().getBoolean(FAKE_HELLO_ACTIVE_KEY)) {
            return;
        }

        int nextTick = player.getPersistentData().getInt(FAKE_HELLO_NEXT_TICK_KEY);
        if (player.tickCount < nextTick) {
            return;
        }

        int stage = player.getPersistentData().getInt(FAKE_HELLO_STAGE_KEY);
        if (stage == 1) {
            int lineIndex = player.getPersistentData().getInt(FAKE_HELLO_LINE_INDEX_KEY);
            if (lineIndex < FAKE_HELLO_CODE_LINES.length) {
                player.sendSystemMessage(Component.literal(FAKE_HELLO_CODE_LINES[lineIndex]));
                player.getPersistentData().putInt(FAKE_HELLO_LINE_INDEX_KEY, lineIndex + 1);
                player.getPersistentData().putInt(FAKE_HELLO_NEXT_TICK_KEY, player.tickCount + 20);
                if (lineIndex + 1 >= FAKE_HELLO_CODE_LINES.length) {
                    player.getPersistentData().putInt(FAKE_HELLO_STAGE_KEY, 2);
                    player.getPersistentData().putInt(FAKE_HELLO_NEXT_TICK_KEY, player.tickCount + 60);
                }
                return;
            }
        }

        if (stage == 2) {
            spawnFakeHelloPlayer(level, player);
            clearFakeHelloSequence(player);
        }
    }

    private static void spawnFakeHelloPlayer(ServerLevel level, ServerPlayer player) {
        BlockPos spawnPos = EntitySpawnManager.findChatFakePlayerSpawn(level, player);
        if (spawnPos == null) {
            return;
        }

        DarkHorseBaglanEntity entity = ModEntities.DARK_HORSE_BAGLAN.get().create(level);
        if (entity == null) {
            return;
        }

        player.sendSystemMessage(
                Component.translatable("multiplayer.player.joined", Component.literal("DarkHorseBaglan").withStyle(ChatFormatting.YELLOW))
                        .withStyle(ChatFormatting.YELLOW)
        );
        entity.setWatchedPlayer(player.getUUID());
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, player.getYRot(), 0.0F);
        level.addFreshEntity(entity);
        player.getPersistentData().putBoolean(FAKE_HELLO_CHAT_ACTIVE_KEY, true);
        player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, false);
    }

    private static void tickFakeHelloAftermath(ServerLevel level, ServerPlayer player) {
        int revengeTick = player.getPersistentData().getInt(FAKE_HELLO_REVENGE_TICK_KEY);
        if (revengeTick > 0 && player.tickCount >= revengeTick) {
            player.sendSystemMessage(Component.translatable(
                    "chat.type.text",
                    Component.literal("DarkHorseBaglan").withStyle(ChatFormatting.WHITE),
                    Component.literal("you'll pay for it").withStyle(ChatFormatting.WHITE)
            ));
            player.getPersistentData().remove(FAKE_HELLO_REVENGE_TICK_KEY);
        }

        int leaveTick = player.getPersistentData().getInt(FAKE_HELLO_LEAVE_TICK_KEY);
        if (leaveTick > 0 && player.tickCount >= leaveTick) {
            player.sendSystemMessage(
                    Component.translatable("multiplayer.player.left", Component.literal("DarkHorseBaglan").withStyle(ChatFormatting.YELLOW))
                            .withStyle(ChatFormatting.YELLOW)
            );
            player.getPersistentData().remove(FAKE_HELLO_LEAVE_TICK_KEY);
        }
    }

    private static void clearFakeHelloSequence(ServerPlayer player) {
        player.getPersistentData().putBoolean(FAKE_HELLO_ACTIVE_KEY, false);
        player.getPersistentData().remove(FAKE_HELLO_STAGE_KEY);
        player.getPersistentData().remove(FAKE_HELLO_NEXT_TICK_KEY);
        player.getPersistentData().remove(FAKE_HELLO_LINE_INDEX_KEY);
        player.getPersistentData().remove(FAKE_HELLO_REPLY_TYPE_KEY);
        player.getPersistentData().remove(FAKE_HELLO_REPLY_TICK_KEY);
    }

    private static boolean handleFakeHelloDialogue(ServerLevel level, ServerPlayer player, String normalizedMessage) {
        if (!player.getPersistentData().getBoolean(FAKE_HELLO_CHAT_ACTIVE_KEY)) {
            return false;
        }

        DarkHorseBaglanEntity fakePlayer = findNearbyDarkHorseBaglan(level, player);
        if (fakePlayer == null) {
            player.getPersistentData().putBoolean(FAKE_HELLO_CHAT_ACTIVE_KEY, false);
            player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, false);
            return false;
        }

        if (normalizedMessage.equals("who are you") || normalizedMessage.equals("who are u")) {
            scheduleFakeHelloReply(player, FAKE_REPLY_WHO);
            return true;
        }

        if (normalizedMessage.equals("friend")) {
            scheduleFakeHelloReply(player, FAKE_REPLY_FRIEND);
            return true;
        }

        if (normalizedMessage.equals("fuck you") || normalizedMessage.equals("fuck u")) {
            scheduleFakeHelloReply(player, FAKE_REPLY_FUCK);
            return true;
        }

        if (!player.getPersistentData().getBoolean(FAKE_HELLO_AWAITING_REPLY_KEY)) {
            return false;
        }

        if (normalizedMessage.equals("yes")) {
            scheduleFakeHelloReply(player, FAKE_REPLY_YES);
            return true;
        }

        if (normalizedMessage.equals("no")) {
            scheduleFakeHelloReply(player, FAKE_REPLY_NO);
            return true;
        }

        return false;
    }

    private static String normalizeFakeHelloMessage(String message) {
        return message.toLowerCase()
                .replace("\u0451", "\u0435")
                .replaceAll("[^\\p{L}\\p{Nd}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static void scheduleFakeHelloReply(ServerPlayer player, int replyType) {
        player.getPersistentData().putInt(FAKE_HELLO_REPLY_TYPE_KEY, replyType);
        player.getPersistentData().putInt(FAKE_HELLO_REPLY_TICK_KEY, player.tickCount + 20 + player.level().random.nextInt(21));
    }

    private static void tickFakeHelloReply(ServerLevel level, ServerPlayer player) {
        int replyType = player.getPersistentData().getInt(FAKE_HELLO_REPLY_TYPE_KEY);
        if (replyType == 0) {
            return;
        }

        int replyTick = player.getPersistentData().getInt(FAKE_HELLO_REPLY_TICK_KEY);
        if (player.tickCount < replyTick) {
            return;
        }

        DarkHorseBaglanEntity fakePlayer = findNearbyDarkHorseBaglan(level, player);
        if (fakePlayer == null) {
            player.getPersistentData().remove(FAKE_HELLO_REPLY_TYPE_KEY);
            player.getPersistentData().remove(FAKE_HELLO_REPLY_TICK_KEY);
            player.getPersistentData().putBoolean(FAKE_HELLO_CHAT_ACTIVE_KEY, false);
            player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, false);
            return;
        }

        switch (replyType) {
            case FAKE_REPLY_WHO -> sendDarkHorseBaglanMessage(player, "Friend! :)");
            case FAKE_REPLY_FRIEND -> {
                sendDarkHorseBaglanMessage(player, "Sure! Are you doubting?");
                player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, true);
            }
            case FAKE_REPLY_YES -> {
                sendDarkHorseBaglanMessage(player, "So bad");
                player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, false);
                makeDarkHorseBaglanLeave(level, player, fakePlayer);
            }
            case FAKE_REPLY_NO -> {
                sendDarkHorseBaglanMessage(player, "I'm watching you");
                player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, false);
                makeDarkHorseBaglanLeave(level, player, fakePlayer);
            }
            case FAKE_REPLY_FUCK -> {
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.NETHERITE_SWORD));
                fakePlayer.swing(InteractionHand.MAIN_HAND);
                player.hurt(level.damageSources().mobAttack(fakePlayer), 8.0F);
            }
            default -> {
            }
        }

        player.getPersistentData().remove(FAKE_HELLO_REPLY_TYPE_KEY);
        player.getPersistentData().remove(FAKE_HELLO_REPLY_TICK_KEY);
    }

    private static void sendDarkHorseBaglanMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.translatable(
                "chat.type.text",
                Component.literal("DarkHorseBaglan").withStyle(ChatFormatting.WHITE),
                Component.literal(message).withStyle(ChatFormatting.WHITE)
        ));
    }

    private static DarkHorseBaglanEntity findNearbyDarkHorseBaglan(ServerLevel level, ServerPlayer player) {
        return level.getEntitiesOfClass(
                DarkHorseBaglanEntity.class,
                player.getBoundingBox().inflate(96.0D),
                Entity::isAlive
        ).stream().findFirst().orElse(null);
    }

    private static void makeDarkHorseBaglanLeave(ServerLevel level, ServerPlayer player, DarkHorseBaglanEntity fakePlayer) {
        player.getPersistentData().putBoolean(FAKE_HELLO_CHAT_ACTIVE_KEY, false);
        player.getPersistentData().putBoolean(FAKE_HELLO_AWAITING_REPLY_KEY, false);
        fakePlayer.discard();
        player.sendSystemMessage(
                Component.translatable("multiplayer.player.left", Component.literal("DarkHorseBaglan").withStyle(ChatFormatting.YELLOW))
                        .withStyle(ChatFormatting.YELLOW)
        );
    }

    private static boolean isTrackedOre(net.minecraft.world.level.block.state.BlockState state) {
        return state.is(Blocks.COAL_ORE)
                || state.is(Blocks.DEEPSLATE_COAL_ORE)
                || state.is(Blocks.IRON_ORE)
                || state.is(Blocks.DEEPSLATE_IRON_ORE)
                || state.is(Blocks.COPPER_ORE)
                || state.is(Blocks.DEEPSLATE_COPPER_ORE)
                || state.is(Blocks.GOLD_ORE)
                || state.is(Blocks.DEEPSLATE_GOLD_ORE)
                || state.is(Blocks.REDSTONE_ORE)
                || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)
                || state.is(Blocks.LAPIS_ORE)
                || state.is(Blocks.DEEPSLATE_LAPIS_ORE)
                || state.is(Blocks.DIAMOND_ORE)
                || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
                || state.is(Blocks.EMERALD_ORE)
                || state.is(Blocks.DEEPSLATE_EMERALD_ORE)
                || state.is(Blocks.NETHER_GOLD_ORE)
                || state.is(Blocks.NETHER_QUARTZ_ORE)
                || state.is(Blocks.ANCIENT_DEBRIS);
    }

    private static void logEventDebug(ServerPlayer player, String eventType, boolean triggered) {
        logEventDebug(player, eventType, triggered, player.blockPosition());
    }

    private static void logEventDebug(ServerPlayer player, String eventType, boolean triggered, BlockPos eventPos) {
        if (!player.getPersistentData().getBoolean(DEBUG_ENABLED_KEY)) {
            return;
        }

        long dayTick = player.level() instanceof ServerLevel level ? (level.getDayTime() % 24000L) : -1L;
        String lastEvent = player.getPersistentData().getString(DEBUG_LAST_EVENT_KEY);
        boolean lastResult = player.getPersistentData().getBoolean(DEBUG_LAST_RESULT_KEY);
        long lastTick = player.getPersistentData().getLong(DEBUG_LAST_TICK_KEY);

        if (eventType.equals(lastEvent) && dayTick == lastTick) {
            return;
        }

        if (eventType.equals(lastEvent)
                && triggered == lastResult
                && dayTick >= 0L
                && lastTick >= 0L
                && (dayTick - lastTick) >= 0L
                && (dayTick - lastTick) < 40L) {
            return;
        }

        player.getPersistentData().putString(DEBUG_LAST_EVENT_KEY, eventType);
        player.getPersistentData().putBoolean(DEBUG_LAST_RESULT_KEY, triggered);
        player.getPersistentData().putLong(DEBUG_LAST_TICK_KEY, dayTick);

        String coordinates = eventPos == null
                ? "n/a"
                : eventPos.getX() + " " + eventPos.getY() + " " + eventPos.getZ();

        player.sendSystemMessage(Component.literal(
                "[code44 debug] event=" + eventType
                        + " result=" + (triggered ? "success" : "attempt_failed")
                        + " dayTick=" + (dayTick >= 0L ? dayTick : "n/a")
                        + " pos=" + coordinates
        ));
    }

    private static boolean trySpawnOutdoorBat(ServerLevel level, ServerPlayer player) {
        for (int attempt = 0; attempt < 48; attempt++) {
            double angle = level.random.nextDouble() * (Math.PI * 2.0D);
            int distance = 3 + level.random.nextInt(18);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos surfacePos = level.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    player.blockPosition().offset(offsetX, 0, offsetZ)
            ).above();

            BlockPos groundPos = surfacePos.below();
            if (!level.canSeeSky(groundPos)) {
                continue;
            }

            if (!level.getBlockState(groundPos).isFaceSturdy(level, groundPos, Direction.UP)) {
                continue;
            }

            if (!level.getFluidState(groundPos).isEmpty() || !level.getFluidState(surfacePos).isEmpty()) {
                continue;
            }

            net.minecraft.world.level.block.state.BlockState feetState = level.getBlockState(surfacePos);
            net.minecraft.world.level.block.state.BlockState headState = level.getBlockState(surfacePos.above());
            if (!feetState.isAir() && !feetState.canBeReplaced()) {
                continue;
            }
            if (!headState.isAir() && !headState.canBeReplaced()) {
                continue;
            }

            if (!feetState.isAir()) {
                level.setBlock(surfacePos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
            }
            if (!headState.isAir()) {
                level.setBlock(surfacePos.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
            }

            Bat bat = net.minecraft.world.entity.EntityType.BAT.create(level);
            if (bat == null) {
                continue;
            }

            bat.setSilent(true);
            bat.moveTo(
                    surfacePos.getX() + 0.5D,
                    surfacePos.getY() + 0.6D,
                    surfacePos.getZ() + 0.5D,
                    level.random.nextFloat() * 360.0F,
                    0.0F
            );
            level.addFreshEntity(bat);
            return true;
        }

        return false;
    }

    private static boolean tryBreakPlayerBoat(ServerLevel level, ServerPlayer player) {
        if (!(player.getVehicle() instanceof Boat boat)) {
            return false;
        }

        ItemStack boatStack = new ItemStack(resolveBoatItem(boat));
        boat.discard();
        if (!player.getInventory().add(boatStack)) {
            player.drop(boatStack, false);
        }
        player.containerMenu.broadcastChanges();
        player.playNotifySound(net.minecraft.sounds.SoundEvents.WOOD_BREAK, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 0.8F);
        return true;
    }

    private static net.minecraft.world.item.Item resolveBoatItem(Boat boat) {
        return switch (boat.getVariant()) {
            case SPRUCE -> Items.SPRUCE_BOAT;
            case BIRCH -> Items.BIRCH_BOAT;
            case JUNGLE -> Items.JUNGLE_BOAT;
            case ACACIA -> Items.ACACIA_BOAT;
            case CHERRY -> Items.CHERRY_BOAT;
            case DARK_OAK -> Items.DARK_OAK_BOAT;
            case MANGROVE -> Items.MANGROVE_BOAT;
            case BAMBOO -> Items.BAMBOO_RAFT;
            default -> Items.OAK_BOAT;
        };
    }
}



