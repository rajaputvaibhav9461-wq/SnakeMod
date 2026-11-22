package com.snakemod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHud {
    private static final Map<UUID, Boolean> wasBlock = new HashMap<>();
    private static final Map<UUID, Long> disabledUntil = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.level == null || mc.player == null) return;
            long t = mc.level.getGameTime();
            for (Player p : mc.level.players()) {
                if (p == mc.player) continue;
                boolean b = p.isBlocking();
                boolean was = wasBlock.getOrDefault(p.getUUID(), false);
                if (was && !b && p.hurtTime > 0) {
                    for (Player q : mc.level.players()) {
                        if (q != p && q.distanceTo(p) < 8 && q.getMainHandItem().is(ItemTags.AXES)) {
                            disabledUntil.put(p.getUUID(), t + 100);
                            break;
                        }
                    }
                }
                wasBlock.put(p.getUUID(), b);
            }
        });

        HudRenderCallback.EVENT.register((g, delta) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null || mc.options.hideGui) return;
            if (!SnakeConfig.isOn("player_hud")) return;
            float pt = delta.getGameTimeDeltaPartialTick(false);
            int sw = mc.getWindow().getGuiScaledWidth();
            int sh = mc.getWindow().getGuiScaledHeight();
            long t = mc.level.getGameTime();

            if (mc.player.getInventory().contains(new ItemStack(Items.SHIELD))
                    || mc.player.getOffhandItem().is(Items.SHIELD)) {
                boolean cd = mc.player.getCooldowns().isOnCooldown(Items.SHIELD);
                g.drawString(mc.font, cd ? "Shield: DISABLED" : "Shield: OK",
                        SnakeConfig.posX("shield_self"), SnakeConfig.posY("shield_self"),
                        cd ? 0xFF5555 : 0x55FF55, true);
            }

            Camera cam = mc.gameRenderer.getMainCamera();
            Vector3f look = cam.getLookVector();
            Vector3f left = cam.getLeftVector();
            Vector3f up = cam.getUpVector();
            double fov = mc.options.fov().get();
            double tan = Math.tan(Math.toRadians(fov) / 2);
            double aspect = (double) mc.getWindow().getWidth() / mc.getWindow().getHeight();

            for (Player p : mc.level.players()) {
                if (p == mc.player || p.isSpectator() || p.isInvisible()) continue;
                if (p.distanceTo(mc.player) > 48) continue;

                double px = p.xo + (p.getX() - p.xo) * pt;
                double py = p.yo + (p.getY() - p.yo) * pt + p.getBbHeight() + 0.6;
                double pz = p.zo + (p.getZ() - p.zo) * pt;
                double dx = px - cam.getPosition().x;
                double dy = py - cam.getPosition().y;
                double dz = pz - cam.getPosition().z;

                double f = dx * look.x() + dy * look.y() + dz * look.z();
                if (f < 0.1) continue;
                double l = dx * left.x() + dy * left.y() + dz * left.z();
                double u = dx * up.x() + dy * up.y() + dz * up.z();
                int sx = (int) (sw / 2.0 - (l / (f * tan * aspect)) * (sw / 2.0));
                int sy = (int) (sh / 2.0 - (u / (f * tan)) * (sh / 2.0));

                float hp = p.getHealth() + p.getAbsorptionAmount();
                int totems = (p.getMainHandItem().is(Items.TOTEM_OF_UNDYING) ? p.getMainHandItem().getCount() : 0)
                        + (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING) ? p.getOffhandItem().getCount() : 0);
                boolean dis = disabledUntil.getOrDefault(p.getUUID(), 0L) > t;

                float ratio = hp / p.getMaxHealth();
                int hc = ratio > 0.6f ? 0x55FF55 : ratio > 0.3f ? 0xFFFF55 : 0xFF5555;
                g.drawCenteredString(mc.font, String.format("HP %.1f", hp), sx, sy, hc);
                g.drawCenteredString(mc.font, "Totem x" + totems, sx, sy - 10, 0xFFD700);
                g.drawCenteredString(mc.font, "SHIELD", sx, sy - 20, dis ? 0xFF5555 : 0xFFFFFF);
            }
        });
    }
}
