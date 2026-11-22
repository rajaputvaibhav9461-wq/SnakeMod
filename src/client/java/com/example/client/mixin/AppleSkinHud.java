package com.snakemod;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;

public class AppleSkinHud {
    public static void register() {
        HudRenderCallback.EVENT.register((g, delta) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui || mc.player.isSpectator()
                    || mc.player.getAbilities().instabuild) return;

            FoodData fd = mc.player.getFoodData();
            int w = mc.getWindow().getGuiScaledWidth();
            int h = mc.getWindow().getGuiScaledHeight();
            int right = w / 2 + 91;
            int y = h - 39;
            int food = fd.getFoodLevel();

            if (SnakeConfig.isOn("apple_sat")) {
                int len = (int) (Math.min(fd.getSaturationLevel(), 20f) * 4);
                g.fill(right - len, y - 2, right, y - 1, 0xFFFFD700);
            }
            if (SnakeConfig.isOn("apple_exh")) {
                int len = (int) (Math.min(fd.getExhaustionLevel(), 4f) / 4f * 80);
                g.fill(right - len, y + 9, right, y + 10, 0xFFAAAAAA);
            }

            FoodProperties fp = mc.player.getMainHandItem().get(DataComponents.FOOD);
            if (fp == null) return;
            int newFood = Math.min(20, food + fp.nutrition());

            if (SnakeConfig.isOn("apple_hunger")) {
                for (int p = food; p < newFood; p++) {
                    int icon = p / 2;
                    int x = right - 9 - icon * 8;
                    int off = (p % 2 == 0) ? 0 : 4;
                    g.fill(x + off, y, x + off + 4, y + 9, 0x6600FF00);
                }
            }
            if (SnakeConfig.isOn("apple_health") && newFood >= 18
                    && mc.player.getHealth() < mc.player.getMaxHealth()) {
                g.drawString(mc.font, "Regen", w / 2 - 91, y - 12, 0xFF5555, true);
            }
        });

        ItemTooltipCallback.EVENT.register((stack, ctx, flag, lines) -> {
            if (!SnakeConfig.isOn("apple_tooltip")) return;
            FoodProperties fp = stack.get(DataComponents.FOOD);
            if (fp == null) return;
            lines.add(Component.literal("Hunger +" + fp.nutrition()
                    + "  Saturation +" + String.format("%.1f", fp.saturation()))
                    .withStyle(ChatFormatting.GOLD));
        });
    }
}
