package com.snakemod;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Inventory HUD: Armor, Totem counter, Potion effects, Inventory. Ek hi block (position id: inv_hud). */
public class InventoryHud {
    private static final ItemStack TOTEM = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static final EquipmentSlot[] ARMOR = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    public static void register() {
        HudRenderCallback.EVENT.register((g, delta) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui) return;
            Inventory inv = mc.player.getInventory();

            int x = SnakeConfig.posX("inv_hud");
            int y = SnakeConfig.posY("inv_hud");

            // Armor
            if (SnakeConfig.isOn("inv_armor")) {
                for (int i = 0; i < 4; i++) {
                    ItemStack s = mc.player.getItemBySlot(ARMOR[i]);
                    if (!s.isEmpty()) {
                        g.renderItem(s, x, y);
                        if (s.isDamageableItem()) {
                            int left = s.getMaxDamage() - s.getDamageValue();
                            g.drawString(mc.font, String.valueOf(left), x + 20, y + 4, 0xFFFFFF, true);
                        }
                    }
                    y += 18;
                }
            }

            // Totem counter
            if (SnakeConfig.isOn("inv_totem")) {
                int count = 0;
                for (int i = 0; i < 36; i++) {
                    ItemStack s = inv.getItem(i);
                    if (s.is(Items.TOTEM_OF_UNDYING)) count += s.getCount();
                }
                ItemStack off = mc.player.getOffhandItem();
                if (off.is(Items.TOTEM_OF_UNDYING)) count += off.getCount();
                g.renderItem(TOTEM, x, y);
                g.drawString(mc.font, "x" + count, x + 20, y + 4, 0xFFD700, true);
                y += 18;
            }

            // Potion effects
            if (SnakeConfig.isOn("inv_potion")) {
                for (MobEffectInstance e : mc.player.getActiveEffects()) {
                    String name = e.getEffect().value().getDisplayName().getString();
                    String time;
                    if (e.isInfiniteDuration()) {
                        time = "inf";
                    } else {
                        int sec = e.getDuration() / 20;
                        time = (sec / 60) + ":" + String.format("%02d", sec % 60);
                    }
                    g.drawString(mc.font, name + " " + (e.getAmplifier() + 1) + "  " + time,
                            x, y, 0xFFFFFF, true);
                    y += 10;
                }
                y += 4;
            }

            // Inventory ka saman
            if (SnakeConfig.isOn("inv_items")) {
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 9; c++) {
                        ItemStack s = inv.getItem(9 + r * 9 + c);
                        if (s.isEmpty()) continue;
                        g.renderItem(s, x + c * 18, y + r * 18);
                        g.renderItemDecorations(mc.font, s, x + c * 18, y + r * 18);
                    }
                }
            }
        });
    }
                      }
