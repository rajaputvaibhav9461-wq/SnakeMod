package com.snakemod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class SnakeMod implements ClientModInitializer {
    static KeyMapping menuKey;

    @Override
    public void onInitializeClient() {
        SnakeConfig.load();

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.snakemod.menu", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M, "category.snakemod"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.consumeClick()) {
                if (mc.screen == null && mc.player != null) {
                    mc.setScreen(new MenuScreen());
                }
            }
        });
    }
}
