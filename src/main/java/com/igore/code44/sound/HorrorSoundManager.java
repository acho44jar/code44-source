package com.igore.code44.sound;

import com.igore.code44.effect.FearManager;
import com.igore.code44.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Random;

public final class HorrorSoundManager {
    private static final Random RANDOM = new Random();

    private HorrorSoundManager() {
    }

    public static void playAmbientWhisper(ServerLevel level, ServerPlayer player) {
        int offsetX = RANDOM.nextInt(9) - 4;
        int offsetY = RANDOM.nextInt(3) - 1;
        int offsetZ = RANDOM.nextInt(9) - 4;

        playPositionedSound(
                player,
                player.getX() + offsetX,
                player.getY() + offsetY,
                player.getZ() + offsetZ,
                ModSounds.AMBIENT_WHISPER.get(),
                SoundSource.AMBIENT,
                1.0f,
                1.0f
        );
    }

    public static boolean playDoorKnock(ServerLevel level, ServerPlayer player) {
        BlockPos sourcePos = randomDoorEventPos(player);

        playPositionedSound(
                player,
                sourcePos,
                ModSounds.DOOR.get(),
                SoundSource.BLOCKS,
                1.1f,
                0.95f + (RANDOM.nextFloat() * 0.1f)
        );
        return true;
    }

    public static boolean playDoorOpenBurst(ServerLevel level, ServerPlayer player) {
        return com.igore.code44.entity.EntitySpawnManager.trySpawnDoorOpener(level, player);
    }

    public static void playLullabyEvent(ServerLevel level, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, FearManager.LULLABY_DURATION_TICKS, 255, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, FearManager.LULLABY_DURATION_TICKS, 255, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, FearManager.LULLABY_DURATION_TICKS, 0, false, true));
        playEntityBoundSound(player, player, ModSounds.LULLABY.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public static void playEfbuiDimensionHum(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.EFBUI_DIMENSION.get(), SoundSource.AMBIENT, 1.0f, 1.0f);
    }

    public static void playLonelyDoorVoidHum(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.DOORVOID.get(), SoundSource.AMBIENT, 3.4f, 1.0f);
    }

    public static void playLeaveNowSound(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.SOS.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public static void playFakeItemBreak(ServerPlayer player) {
        player.playNotifySound(net.minecraft.sounds.SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 0.9F + (RANDOM.nextFloat() * 0.2F));
    }

    public static void playFakeSkyScreamer(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.FAKE.get(), SoundSource.HOSTILE, 2.35F, 1.0F);
    }

    public static void playGodLoveU(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.GODLOVEU.get(), SoundSource.VOICE, 1.45F, 1.0F);
    }

    public static void playImWatch(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.IMWATCH.get(), SoundSource.VOICE, 1.45F, 1.0F);
    }

    public static void playSleepIsShort(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.SLEEPISSHORT.get(), SoundSource.VOICE, 1.45F, 1.0F);
    }

    public static void playUrLife(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.URLIFE.get(), SoundSource.VOICE, 1.45F, 1.0F);
    }

    public static void playYourEnd(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.YOUREND.get(), SoundSource.VOICE, 1.65F, 1.0F);
    }

    public static void playCryingAmbient(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.CRYING.get(), SoundSource.AMBIENT, 1.6F, 1.0F);
    }

    public static void playBrokenGlass(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.BROKENGLASS.get(), SoundSource.AMBIENT, 1.8F, 0.96F + (RANDOM.nextFloat() * 0.08F));
    }

    public static void playButtonClick(ServerLevel level, ServerPlayer player) {
        playNearbyVanillaSound(level, player, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.1F, 0.95F);
    }

    public static void playWoodenPressurePlate(ServerLevel level, ServerPlayer player) {
        playNearbyVanillaSound(level, player, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 1.1F, 0.95F);
    }

    public static void playBowShot(ServerLevel level, ServerPlayer player) {
        playNearbyVanillaSound(level, player, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.2F, 0.9F + (RANDOM.nextFloat() * 0.1F));
    }

    public static void playBlockPlace(ServerLevel level, ServerPlayer player) {
        playNearbyVanillaSound(level, player, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 0.96F);
    }

    public static void playBlockBreakSound(ServerLevel level, ServerPlayer player) {
        playNearbyVanillaSound(level, player, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 0.94F);
    }

    public static void playTntIgnite(ServerLevel level, ServerPlayer player) {
        playNearbyVanillaSound(level, player, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.3F, 1.0F);
    }

    public static void playPigDeath(ServerLevel level, ServerPlayer player) {
        playNearbyVanillaSound(level, player, SoundEvents.PIG_DEATH, SoundSource.HOSTILE, 1.2F, 0.96F);
    }

    public static void playDisc11(ServerPlayer player) {
        playEntityBoundSound(player, player, SoundEvents.MUSIC_DISC_11, SoundSource.RECORDS, 1.35F, 1.0F);
    }

    public static void playMazeAmbient(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.MAZE.get(), SoundSource.AMBIENT, 2.0F, 1.0F);
    }

    public static void stopMazeAmbient(ServerPlayer player) {
        player.connection.send(new ClientboundStopSoundPacket(
                BuiltInRegistries.SOUND_EVENT.getKey(ModSounds.MAZE.get()),
                SoundSource.AMBIENT
        ));
    }

    public static void playSkinwalker(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.SKINWALKER.get(), SoundSource.HOSTILE, 1.7F, 1.0F);
    }

    public static void playTunnelObserverSpawn(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.TUNNELOBSERVERSPAWN.get(), SoundSource.HOSTILE, 1.8F, 1.0F);
    }

    public static void playErr44r(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.ERR44R.get(), SoundSource.HOSTILE, 1.8F, 1.0F);
    }

    public static void playDoorEfbuiSpawn(ServerLevel level, BlockPos doorPos) {
        level.playSound(
                null,
                doorPos,
                ModSounds.DOOREFBUI.get(),
                SoundSource.HOSTILE,
                1.35F,
                1.0F
        );
    }

    public static void playMineshaftEfbui(ServerPlayer player) {
        player.playNotifySound(ModSounds.EFBUI_MINESHAFT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    public static void playEfbuiJoinSound(ServerPlayer player) {
        player.playNotifySound(ModSounds.EFBUI_JOIN.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public static void playEfbuiJoinSound(ServerLevel level) {
        level.players().forEach(HorrorSoundManager::playEfbuiJoinSound);
    }

    public static void startHeartEventSound(ServerPlayer player) {
        playEntityBoundSound(player, player, ModSounds.HEART.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public static void stopHeartEventSound(ServerPlayer player) {
        player.connection.send(new ClientboundStopSoundPacket(
                BuiltInRegistries.SOUND_EVENT.getKey(ModSounds.HEART.get()),
                SoundSource.PLAYERS
        ));
    }

    public static void startZer000Sound(ServerPlayer player, Entity zer000Entity) {
        playEntityBoundSound(player, player, ModSounds.ZER000_KILLING_YOU.get(), SoundSource.HOSTILE, 30.0f, 1.0f);
    }

    public static void stopZer000Sound(ServerPlayer player) {
        player.connection.send(new ClientboundStopSoundPacket(
                BuiltInRegistries.SOUND_EVENT.getKey(ModSounds.ZER000_KILLING_YOU.get()),
                SoundSource.HOSTILE
        ));
    }

    private static void playEntityBoundSound(ServerPlayer listener, Entity sourceEntity, net.minecraft.sounds.SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch) {
        listener.connection.send(new ClientboundSoundEntityPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent),
                soundSource,
                sourceEntity,
                volume,
                pitch,
                listener.level().random.nextLong()
        ));
    }

    private static BlockPos randomDoorEventPos(ServerPlayer player) {
        int offsetX = (RANDOM.nextBoolean() ? 1 : -1) * (4 + RANDOM.nextInt(4));
        int offsetZ = (RANDOM.nextBoolean() ? 1 : -1) * (4 + RANDOM.nextInt(4));
        int offsetY = RANDOM.nextInt(3) - 1;
        return player.blockPosition().offset(offsetX, offsetY, offsetZ);
    }

    private static void playNearbyVanillaSound(ServerLevel level, ServerPlayer player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        BlockPos sourcePos = randomDoorEventPos(player);
        playPositionedSound(player, sourcePos, sound, source, volume, pitch);
    }

    private static void playPositionedSound(ServerPlayer listener, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        playPositionedSound(listener, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, source, volume, pitch);
    }

    private static void playPositionedSound(ServerPlayer listener, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
        listener.connection.send(new ClientboundSoundPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
                source,
                x,
                y,
                z,
                volume,
                pitch,
                listener.level().random.nextLong()
        ));
    }
}
