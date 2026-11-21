package com.snakemod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class MenuScreen extends Screen {
    private static String page = SnakeConfig.PAGES.get(0);

    public MenuScreen() {
        super(Component.literal("SnakeMod Menu"));
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        int n = SnakeConfig.PAGES.size();
        int tw = 70;
        int total = n * (tw + 4) - 4;
        int x0 = (this.width - total) / 2;

        for (int i = 0; i < n; i++) {
            final String pg = SnakeConfig.PAGES.get(i);
            String label = (pg.equals(page) ? "> " : "") + pg;
            this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
                page = pg;
                this.rebuildWidgets();
            }).bounds(x0 + i * (tw + 4), 20, tw, 20).build());
        }

        int bw = 200;
        int x = (this.width - bw) / 2;
        int y = 50;
        for (SnakeConfig.Toggle t : SnakeConfig.TOGGLES) {
            if (!t.page().equals(page)) continue;
            final String id = t.id();
            String text = t.label() + ": " + (SnakeConfig.isOn(id) ? "ON" : "OFF");
            this.addRenderableWidget(Button.builder(Component.literal(text), b -> {
                SnakeConfig.setOn(id, !SnakeConfig.isOn(id));
                this.rebuildWidgets();
            }).bounds(x, y, bw, 20).build());
            y += 24;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 6, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_M) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
        }
