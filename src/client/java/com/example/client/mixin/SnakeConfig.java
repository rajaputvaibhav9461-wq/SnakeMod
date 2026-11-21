package com.snakemod;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/** Saari settings yahan. Naya feature = TOGGLES me ek line. */
public class SnakeConfig {
    public record Toggle(String id, String label, String page, boolean def) { }

    public static final List<String> PAGES = List.of("Apple", "Display", "HUD");

    public static final List<Toggle> TOGGLES = List.of(
            new Toggle("apple_hunger", "Hunger preview", "Apple", true),
            new Toggle("apple_sat", "Saturation bar", "Apple", true),
            new Toggle("apple_health", "Health preview", "Apple", true),
            new Toggle("apple_exh", "Exhaustion", "Apple", true),
            new Toggle("apple_tooltip", "Tooltip", "Apple", true),

            new Toggle("fullbright", "Fullbright", "Display", false),
            new Toggle("fps", "FPS", "Display", true),
            new Toggle("coords", "Coords", "Display", true),
            new Toggle("ping", "Ping", "Display", true),

            new Toggle("player_hud", "Player HUD", "HUD", true),
            new Toggle("inv_armor", "Armor", "HUD", true),
            new Toggle("inv_items", "Inventory", "HUD", true),
            new Toggle("inv_potion", "Potion effects", "HUD", true),
            new Toggle("inv_totem", "Totem counter", "HUD", true)
    );

    private static final Map<String, Boolean> bools = new HashMap<>();
    private static final Map<String, Integer> ints = new HashMap<>();

    public static boolean isOn(String id) {
        Boolean b = bools.get(id);
        if (b != null) return b;
        for (Toggle t : TOGGLES) if (t.id().equals(id)) return t.def();
        return false;
    }

    public static void setOn(String id, boolean v) {
        bools.put(id, v);
        save();
    }

    public static int getInt(String id, int def) {
        return ints.getOrDefault(id, def);
    }

    public static void setInt(String id, int v) {
        ints.put(id, v);
        save();
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("snakemod.properties");
    }

    public static void load() {
        bools.clear();
        ints.clear();
        Path f = file();
        if (!Files.exists(f)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(f)) {
            p.load(in);
        } catch (IOException e) {
            return;
        }
        for (String k : p.stringPropertyNames()) {
            String v = p.getProperty(k);
            if (k.startsWith("b.")) {
                bools.put(k.substring(2), Boolean.parseBoolean(v.trim()));
            } else if (k.startsWith("i.")) {
                try {
                    ints.put(k.substring(2), Integer.parseInt(v.trim()));
                } catch (NumberFormatException ignored) { }
            }
        }
    }

    public static void save() {
        Properties p = new Properties();
        bools.forEach((k, v) -> p.setProperty("b." + k, String.valueOf(v)));
        ints.forEach((k, v) -> p.setProperty("i." + k, String.valueOf(v)));
        try (OutputStream out = Files.newOutputStream(file())) {
            p.store(out, "SnakeMod settings");
        } catch (IOException ignored) { }
    }
      }
