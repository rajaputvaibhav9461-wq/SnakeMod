package com.snakemod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Fullbright {
    private static double oldGamma = -1;
    private static boolean added = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) {
                added = false;
                return;
            }
            if (SnakeConfig.isOn("fullbright")) {
                if (oldGamma < 0) oldGamma = mc.options.gamma().get();
                mc.options.gamma().set(1.0);
                if (!mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
                    mc.player.addEffect(new MobEffectInstance(
                            MobEffects.NIGHT_VISION, 1000000, 0, false, false, false));
                    added = true;
                }
            } else {
                if (oldGamma >= 0) {
                    mc.options.gamma().set(oldGamma);
                    oldGamma = -1;
                }
                if (added) {
                    mc.player.removeEffect(MobEffects.NIGHT_VISION);
                    added = false;
                }
            }
        });
    }
}
