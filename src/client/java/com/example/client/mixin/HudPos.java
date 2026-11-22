package com.snakemod;

import java.util.List;

public class HudPos {
    public record Elem(String id, String label, int defX, int defY, int w, int h) { }

    public static final List<Elem> ELEMS = List.of(
            new Elem("fps", "FPS: 120", 5, 5, 50, 10),
            new Elem("coords", "XYZ: 0.0 64.0 0.0", 5, 16, 100, 10),
            new Elem("ping", "Ping: 50 ms", 5, 27, 60, 10),
            new Elem("shield_self", "Shield: OK", 5, 40, 70, 10),
            new Elem("inv_hud", "[Inventory HUD]", 5, 60, 100, 100)
    );

    private static Elem find(String id) {
        for (Elem e : ELEMS) if (e.id().equals(id)) return e;
        return null;
    }

    public static int x(String id) {
        Elem e = find(id);
        return SnakeConfig.getInt("x_" + id, e == null ? 5 : e.defX());
    }

    public static int y(String id) {
        Elem e = find(id);
        return SnakeConfig.getInt("y_" + id, e == null ? 5 : e.defY());
    }
      }
