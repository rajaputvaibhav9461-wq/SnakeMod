package com.snakemod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ZoomSmooth {
    private static double f = 0, base = 70;
    private static boolean applied = false;
    private static long last = System.nanoTime();

    public static void register() {
        HudRenderCallback.EVENT.register((g, delta) -> update());
    }

    private static void update() {
        Minecraft mc = Minecraft.getInstance();
        long now = System.nanoTime();
        double dt = Math.min((now - last) / 1e9, 0.1);
        last = now;
        if (mc.player == null) {
            f = 0;
            applied = false;
            return;
        }

        boolean down = mc.screen == null && InputConstants.isKeyDown(mc.getWindow().getWindow(),
                SnakeConfig.getInt("zoom_key", GLFW.GLFW_KEY_C));
        int level = Math.max(1, Math.min(10, SnakeConfig.getInt("zoom_level", 5)));
        int smooth = Math.max(0, Math.min(10, SnakeConfig.getInt("zoom_smooth", 5)));

        double target = down ? 1 : 0;
        double alpha = smooth == 0 ? 1 : 1 - Math.exp(-dt * (25.0 / smooth));
        f += (target - f) * alpha;

        if (!down && f < 0.002) {
            if (applied) {
                mc.options.fov().set((int) Math.round(base));
                applied = false;
            }
            f = 0;
            return;
        }
        if (!applied) {
            base = mc.options.fov().get();
            applied = true;
        }
        double zoomed = Math.max(30.0, base / (1 + level * 0.3));
        double fov = base - (base - zoomed) * f;
        mc.options.fov().set((int) Math.round(fov));
    }
  }
