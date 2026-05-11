package com.igore.code44.world;

import com.igore.code44.data.Code44WorldData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public final class HelpWorldManager {
    public static final ResourceLocation HELP_NOISE_SETTINGS_ID = ResourceLocation.fromNamespaceAndPath("code44", "help_overworld");
    public static final ResourceLocation HELP_PRESET_ID = ResourceLocation.fromNamespaceAndPath("code44", "help");

    private HelpWorldManager() {
    }

    public static boolean isHelpWorld(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) {
            return false;
        }

        Code44WorldData data = Code44WorldData.get(level);
        if (data.isHelpWorld()) {
            return true;
        }

        if (level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator noiseGenerator) {
            Holder<NoiseGeneratorSettings> settings = noiseGenerator.generatorSettings();
            if (settings.unwrapKey().map(ResourceKey::location).filter(HELP_NOISE_SETTINGS_ID::equals).isPresent()) {
                data.setHelpWorld(true);
                return true;
            }
        }

        return false;
    }
}
