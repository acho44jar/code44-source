package com.igore.code44.client;

import com.igore.code44.network.ModNetworking;
import com.igore.code44.network.packet.DeveloperUnlockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class DeveloperUnlockScreen extends Screen {
    private static final String SECRET = "44notreal";
    private EditBox codeBox;

    public DeveloperUnlockScreen() {
        super(Component.literal("code44"));
    }

    @Override
    protected void init() {
        int boxWidth = 180;
        int boxHeight = 20;
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.codeBox = new EditBox(this.font, centerX - (boxWidth / 2), centerY - 10, boxWidth, boxHeight, Component.literal("code"));
        this.codeBox.setMaxLength(64);
        this.codeBox.setCanLoseFocus(false);
        this.addRenderableWidget(this.codeBox);
        this.setInitialFocus(this.codeBox);

        this.addRenderableWidget(Button.builder(Component.literal("enter"), button -> submitCode())
                .bounds(centerX - 40, centerY + 20, 80, 20)
                .build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            submitCode();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, Component.literal("enter code"), centerX, (this.height / 2) - 30, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void submitCode() {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.codeBox != null && SECRET.equals(this.codeBox.getValue())) {
            ModNetworking.CHANNEL.sendToServer(new DeveloperUnlockPacket());
            minecraft.setScreen(null);
            return;
        }
        showWindowsAccessDeniedDialog(minecraft);
    }

    private static void showWindowsAccessDeniedDialog(Minecraft minecraft) {
        minecraft.stop();
    }
}
