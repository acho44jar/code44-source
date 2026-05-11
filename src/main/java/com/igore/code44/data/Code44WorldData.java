package com.igore.code44.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class Code44WorldData extends SavedData {
    private static final String DATA_NAME = "code44_world_data";
    private static final String FAKE_HELLO_USED_KEY = "FakeHelloUsed";
    private static final String DAY_TICK_SPEED_KEY = "DayTickSpeedMultiplier";
    private static final String LEGACY_BIOME_CHUNKS_KEY = "LegacyBiomeChunks";
    private static final String HELP_WORLD_KEY = "HelpWorld";

    private boolean fakeHelloUsed;
    private int dayTickSpeedMultiplier = 1;
    private final Set<Long> legacyBiomeChunks = new HashSet<>();
    private boolean helpWorld;

    public static Code44WorldData get(ServerLevel level) {
        return level.getServer()
                .overworld()
                .getDataStorage()
                .computeIfAbsent(Code44WorldData::load, Code44WorldData::new, DATA_NAME);
    }

    public static Code44WorldData load(CompoundTag tag) {
        Code44WorldData data = new Code44WorldData();
        data.fakeHelloUsed = tag.getBoolean(FAKE_HELLO_USED_KEY);
        int savedSpeed = tag.contains(DAY_TICK_SPEED_KEY) ? tag.getInt(DAY_TICK_SPEED_KEY) : 1;
        data.dayTickSpeedMultiplier = Math.max(1, savedSpeed);
        data.helpWorld = tag.getBoolean(HELP_WORLD_KEY);
        if (tag.contains(LEGACY_BIOME_CHUNKS_KEY)) {
            for (long chunkKey : tag.getLongArray(LEGACY_BIOME_CHUNKS_KEY)) {
                data.legacyBiomeChunks.add(chunkKey);
            }
        }
        return data;
    }

    public boolean isFakeHelloUsed() {
        return fakeHelloUsed;
    }

    public void setFakeHelloUsed(boolean fakeHelloUsed) {
        this.fakeHelloUsed = fakeHelloUsed;
        setDirty();
    }

    public int getDayTickSpeedMultiplier() {
        return dayTickSpeedMultiplier;
    }

    public void setDayTickSpeedMultiplier(int dayTickSpeedMultiplier) {
        this.dayTickSpeedMultiplier = Math.max(1, dayTickSpeedMultiplier);
        setDirty();
    }

    public boolean isLegacyBiomeChunkProcessed(long chunkKey) {
        return legacyBiomeChunks.contains(chunkKey);
    }

    public void markLegacyBiomeChunkProcessed(long chunkKey) {
        if (legacyBiomeChunks.add(chunkKey)) {
            setDirty();
        }
    }

    public boolean isHelpWorld() {
        return helpWorld;
    }

    public void setHelpWorld(boolean helpWorld) {
        if (this.helpWorld != helpWorld) {
            this.helpWorld = helpWorld;
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean(FAKE_HELLO_USED_KEY, fakeHelloUsed);
        tag.putInt(DAY_TICK_SPEED_KEY, dayTickSpeedMultiplier);
        tag.putBoolean(HELP_WORLD_KEY, helpWorld);
        tag.put(LEGACY_BIOME_CHUNKS_KEY, new LongArrayTag(legacyBiomeChunks.stream().mapToLong(Long::longValue).toArray()));
        return tag;
    }
}
