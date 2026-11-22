package com.snakemod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import org.lwjgl.glfw.GLFW;

public class Freecam {
    private static boolean on = false, prevKey = false;
    private static RemotePlayer cam = null;

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(Freecam::tick);
    }

    private static boolean down(long w, int key) {
        return InputConstants.isKeyDown(w, key);
    }

    private static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            on = false;
            cam = null;
            return;
        }
        long w = mc.getWindow().getWindow();
        boolean k = mc.screen == null
                && down(w, SnakeConfig.getInt("freecam_key", GLFW.GLFW_KEY_F4));
        if (k && !prevKey) toggle(mc);
        prevKey = k;
        if (!on || cam == null) return;

        mc.options.keyUp.setDown(false);
        mc.options.keyDown.setDown(false);
        mc.options.keyLeft.setDown(false);
        mc.options.keyRight.setDown(false);
        mc.options.keyJump.setDown(false);
        mc.options.keyShift.setDown(false);
        mc.options.keySprint.setDown(false);

        if (mc.screen != null) return;
        double sp = down(w, GLFW.GLFW_KEY_LEFT_CONTROL) ? 1.2 : 0.6;
        int fw = (down(w, GLFW.GLFW_KEY_W) ? 1 : 0) - (down(w, GLFW.GLFW_KEY_S) ? 1 : 0);
        int rt = (down(w, GLFW.GLFW_KEY_D) ? 1 : 0) - (down(w, GLFW.GLFW_KEY_A) ? 1 : 0);
        int up = (down(w, GLFW.GLFW_KEY_SPACE) ? 1 : 0) - (down(w, GLFW.GLFW_KEY_LEFT_SHIFT) ? 1 : 0);

        cam.setOldPosAndRot();
        float yaw = mc.player.getYRot();
        cam.setYRot(yaw);
        cam.setXRot(mc.player.getXRot());
        double r = Math.toRadians(yaw);
        double fx = -Math.sin(r), fz = Math.cos(r);
        double rx = -Math.cos(r), rz = -Math.sin(r);
        cam.setPos(cam.getX() + (fx * fw + rx * rt) * sp,
                cam.getY() + up * sp,
                cam.getZ() + (fz * fw + rz * rt) * sp);
    }

    private static void toggle(Minecraft mc) {
        if (!on) {
            cam = new RemotePlayer(mc.level, mc.player.getGameProfile());
            cam.copyPosition(mc.player);
            mc.setCameraEntity(cam);
            on = true;
        } else {
            mc.setCameraEntity(mc.player);
            on = false;
            cam = null;
        }
    }
          }
