package interface_adapters.ui;

import java.awt.*;
import java.util.Map;

/**
 * Centralized UI styling constants for consistent visual design across all views.
 * Provides color palette, typography, spacing, and Pokemon type colors.
 */
public class UIStyleConstants {

    // ============ COLOR PALETTE ============
    public static final Color PRIMARY_COLOR = new Color(59, 76, 202);        // Pokemon blue
    public static final Color SECONDARY_COLOR = new Color(255, 203, 5);      // Pokemon yellow
    public static final Color BACKGROUND = new Color(245, 245, 245);         // Light gray
    public static final Color CARD_BG = Color.WHITE;                         // White
    public static final Color HP_HIGH = new Color(76, 175, 80);              // Green
    public static final Color HP_MEDIUM = new Color(255, 193, 7);            // Yellow/Orange
    public static final Color HP_LOW = new Color(244, 67, 54);               // Red
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);          // Dark gray
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);     // Medium gray

    // ============ POKEMON TYPE COLORS ============
    public static final Map<String, Color> TYPE_COLORS = Map.ofEntries(
            Map.entry("normal", new Color(168, 168, 120)),
            Map.entry("fighting", new Color(192, 48, 40)),
            Map.entry("flying", new Color(168, 144, 240)),
            Map.entry("poison", new Color(160, 64, 160)),
            Map.entry("ground", new Color(224, 192, 104)),
            Map.entry("rock", new Color(184, 160, 56)),
            Map.entry("bug", new Color(168, 184, 32)),
            Map.entry("ghost", new Color(112, 88, 152)),
            Map.entry("steel", new Color(184, 184, 208)),
            Map.entry("fire", new Color(240, 128, 48)),
            Map.entry("water", new Color(104, 144, 240)),
            Map.entry("grass", new Color(120, 200, 80)),
            Map.entry("electric", new Color(248, 208, 48)),
            Map.entry("psychic", new Color(248, 88, 136)),
            Map.entry("ice", new Color(152, 216, 216)),
            Map.entry("dragon", new Color(112, 56, 248)),
            Map.entry("dark", new Color(112, 88, 72)),
            Map.entry("fairy", new Color(238, 153, 172))
    );

    // ============ TYPOGRAPHY ============
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font EXTRA_LARGE_FONT = new Font("Segoe UI", Font.BOLD, 48);

    // ============ SPACING ============
    public static final int SMALL_PADDING = 8;
    public static final int MEDIUM_PADDING = 16;
    public static final int LARGE_PADDING = 24;
    public static final int XLARGE_PADDING = 32;

    // ============ BORDER & SHAPE ============
    public static final int BORDER_RADIUS = 10;
    public static final int SMALL_BORDER_RADIUS = 6;

    // ============ SIZES ============
    public static final int SPRITE_SIZE = 96;
    public static final int LARGE_SPRITE_SIZE = 128;
    public static final int SMALL_SPRITE_SIZE = 64;

    /**
     * Gets the color for a Pokemon type.
     *
     * @param typeName The type name (case-insensitive)
     * @return The color for that type, or gray if unknown
     */
    public static Color getTypeColor(String typeName) {
        if (typeName == null) {
            return Color.GRAY;
        }
        return TYPE_COLORS.getOrDefault(typeName.toLowerCase(), Color.GRAY);
    }

    /**
     * Gets HP bar color based on percentage.
     *
     * @param hpPercent HP percentage (0.0 to 1.0)
     * @return Appropriate color (green, yellow, or red)
     */
    public static Color getHPColor(float hpPercent) {
        if (hpPercent > 0.5f) {
            return HP_HIGH;
        } else if (hpPercent > 0.25f) {
            return HP_MEDIUM;
        } else {
            return HP_LOW;
        }
    }

    // Private constructor to prevent instantiation
    private UIStyleConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
