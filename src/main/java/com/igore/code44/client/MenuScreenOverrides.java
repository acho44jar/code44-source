package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.world.HelpWorldManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = Code44Mod.MODID, value = Dist.CLIENT)
public final class MenuScreenOverrides {
    private MenuScreenOverrides() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof CreateWorldScreen createWorldScreen) {
            applyHelpWorldPreset(createWorldScreen);
            return;
        }

        if (!(event.getScreen() instanceof TitleScreen titleScreen)) {
            return;
        }

        forceSplashText(titleScreen);

        int centerX = titleScreen.width / 2;
        int top = titleScreen.height / 4 + 48;
        int smallLeftX = centerX - 100;
        int smallRightX = centerX + 2;
        int smallRowY = top + 48;
        int lowerRowY = top + 72;

        for (GuiEventListener listener : event.getListenersList()) {
            if (!(listener instanceof AbstractWidget widget)) {
                continue;
            }

            String label = widget.getMessage().getString().toLowerCase(Locale.ROOT);
            if (label.contains("singleplayer") || label.contains("одиноч")) {
                widget.setMessage(Component.literal("err44r.title1:singleplayer"));
                continue;
            }
            if (label.contains("multiplayer") || label.contains("сетев")) {
                widget.setMessage(Component.literal("err44r.title2:multiplayer"));
                continue;
            }
            if (label.contains("mods") || label.contains("мод")) {
                widget.setMessage(Component.literal("err44r.title3:mods"));
                widget.setX(smallLeftX);
                widget.setY(smallRowY);
                continue;
            }
            if (label.contains("realms")) {
                widget.visible = false;
                widget.active = false;
                continue;
            }
            if (label.contains("options") || label.contains("настр")) {
                widget.setMessage(Component.literal("err44r.title4:settings"));
                widget.setX(smallRightX);
                widget.setY(smallRowY);
                continue;
            }
            if (label.contains("quit") || label.contains("выйт")) {
                widget.setMessage(Component.literal("err44r.title5:save my life"));
                widget.setX(centerX - (widget.getWidth() / 2));
                widget.setY(lowerRowY);
                widget.active = false;
            }
        }
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof TitleScreen titleScreen) {
            renderTitleOverrides(event.getGuiGraphics(), titleScreen.width, titleScreen.height);
        }
    }

    private static void renderTitleOverrides(GuiGraphics guiGraphics, int width, int height) {
        int copyrightWidth = 220;
        int x1 = width - copyrightWidth - 6;
        int y1 = height - 14;
        guiGraphics.fill(x1, y1, width - 2, height - 2, 0xA0101010);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                Component.literal("err44r.type:copyright Mojang"),
                x1 + 4,
                y1 + 3,
                0xFFFFFF,
                false
        );
    }

    private static void forceSplashText(TitleScreen titleScreen) {
        try {
            for (var field : TitleScreen.class.getDeclaredFields()) {
                if (field.getType() == SplashRenderer.class) {
                    field.setAccessible(true);
                    field.set(titleScreen, new SplashRenderer("help me."));
                    return;
                }
            }
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static void applyHelpWorldPreset(CreateWorldScreen createWorldScreen) {
        WorldCreationUiState uiState = createWorldScreen.getUiState();
        Optional<WorldCreationUiState.WorldTypeEntry> helpEntry = uiState.getNormalPresetList().stream()
                .filter(entry -> entry.preset()
                        .unwrapKey()
                        .map(ResourceKey::location)
                        .filter(HelpWorldManager.HELP_PRESET_ID::equals)
                        .isPresent())
                .findFirst();

        if (helpEntry.isEmpty()) {
            return;
        }

        WorldCreationUiState.WorldTypeEntry current = uiState.getWorldType();
        boolean alreadyHelp = current != null
                && current.preset().unwrapKey().map(ResourceKey::location).filter(HelpWorldManager.HELP_PRESET_ID::equals).isPresent();

        if (!alreadyHelp) {
            uiState.setWorldType(helpEntry.get());
        }

        if (uiState.getSeed() == null || uiState.getSeed().isBlank()) {
            long seed = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
            uiState.setSeed(Long.toString(seed));
        }
    }
}
