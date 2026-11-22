package com.snakemod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class PositionScreen extends Screen {
    private final Map<String, int[]> pos = new HashMap<>();
    private SnakeConfig.Elem drag = null;

    public PositionScreen() {
        super(Component.literal("HUD position"));
    }

    @Override
    protected void init() {
        for (SnakeConfig.Elem e : SnakeConfig.ELEMS) {
            pos.putIfAbsent(e.id(), new int[] { SnakeConfig.posX(e.id()), SnakeConfig.posY(e.id()) });
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        g.drawCenteredString(this.font, "Drag karo, Esc se save", this.width / 2, 6, 0xFFFF55);
        for (SnakeConfig.Elem e : SnakeConfig.ELEMS) {
            int[] p = pos.get(e.id());
            g.fill(p[0] - 1, p[1] - 1, p[0] + e.w() + 1, p[1] + e.h() + 1, 0x88000000);
            g.drawString(this.font, e.label(), p[0], p[1], 0xFFFFFF, true);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (int i = SnakeConfig.ELEMS.size() - 1; i >= 0; i--) {
            SnakeConfig.Elem e = SnakeConfig.ELEMS.get(i);
            int[] p = pos.get(e.id());
            if (x >= p[0] - 1 && x <= p[0] + e.w() + 1 && y >= p[1] - 1 && y <= p[1] + e.h() + 1) {
                drag = e;
                return true;
            }
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (drag != null) {
            int[] p = pos.get(drag.id());
            p[0] = (int) Math.max(0, Math.min(this.width - 10, p[0] + dx));
            p[1] = (int) Math.max(0, Math.min(this.height - 10, p[1] + dy));
            return true;
        }
        return super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        drag = null;
        return super.mouseReleased(x, y, button);
    }

    @Override
    public void onClose() {
        for (SnakeConfig.Elem e : SnakeConfig.ELEMS) {
            int[] p = pos.get(e.id());
            SnakeConfig.setInt("x_" + e.id(), p[0]);
            SnakeConfig.setInt("y_" + e.id(), p[1]);
        }
        super.onClose();
    }
}
