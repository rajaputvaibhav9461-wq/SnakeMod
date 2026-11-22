package com.snakemod;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

public class FcpHud {
    public static void register() {
        HudRenderCallback.EVENT.register((g, delta) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui || mc.options.renderDebug) return;

            for (String id : new String[] { "fps", "coords", "ping" }) {
                if (!SnakeConfig.isOn(id)) continue;
                String text = textFor(mc, id);
                if (text == null) continue;
                g.drawString(mc.font, text, SnakeConfig.posX(id), SnakeConfig.posY(id), 0xFFFFFF, true);
            }
        });
    }

    private static String textFor(Minecraft mc, String id) {
        switch (id) {
            case "fps":
                return "FPS: " + mc.getFps();
            case "coords":
                return String.format("XYZ: %.1f %.1f %.1f",
                        mc.player.getX(), mc.player.getY(), mc.player.getZ());
            case "ping":
                if (mc.getConnection() == null || mc.isLocalServer()) return null;
                PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
                return info == null ? null : "Ping: " + info.getLatency() + " ms";
            default:
                return null;
        }
    }
}
