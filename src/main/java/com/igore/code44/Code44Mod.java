package com.igore.code44;

import com.igore.code44.network.ModNetworking;
import com.igore.code44.registry.ModEntities;
import com.igore.code44.registry.ModBlocks;
import com.igore.code44.registry.ModItems;
import com.igore.code44.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import software.bernie.geckolib.GeckoLib;

import java.net.URL;
import java.util.Locale;

@Mod(Code44Mod.MODID)
public class Code44Mod {
    public static final String MODID = "code44";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Code44Mod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        GeckoLib.initialize();
        ModNetworking.register();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
    }

    public static boolean isDevCommandsBuild() {
        if (Boolean.getBoolean("code44.devCommands")) {
            return true;
        }

        try {
            URL location = Code44Mod.class.getProtectionDomain().getCodeSource().getLocation();
            if (location == null) {
                return false;
            }

            String path = location.getPath();
            if (path == null) {
                return false;
            }

            return path.toLowerCase(Locale.ROOT).contains("-dev.jar");
        } catch (Exception ignored) {
            return false;
        }
    }
}
