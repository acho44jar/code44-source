package com.igore.code44.weather;

import net.minecraft.server.level.ServerLevel;

public final class BloodRainManager {
    private static final int BLOOD_RAIN_DURATION_TICKS = 3600;
    private static final long DEEP_NIGHT_TICKS = 18000L;

    private BloodRainManager() {
    }

    public static void triggerBloodRain(ServerLevel level) {
        setSameDayNight(level);
        level.setWeatherParameters(0, BLOOD_RAIN_DURATION_TICKS, true, false);
    }

    public static void triggerRainNight(ServerLevel level) {
        setSameDayNight(level);
        level.setWeatherParameters(0, BLOOD_RAIN_DURATION_TICKS, true, false);
    }

    public static void triggerUnskippableNight(ServerLevel level) {
        setSameDayNight(level);
    }

    private static void setSameDayNight(ServerLevel level) {
        long currentDayStart = (level.getDayTime() / 24000L) * 24000L;
        level.setDayTime(currentDayStart + DEEP_NIGHT_TICKS);
    }
}
