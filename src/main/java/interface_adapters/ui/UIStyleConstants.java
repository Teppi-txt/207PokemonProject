package interface_adapters.ui;

import java.awt.*;
import java.util.Map;

/**
 * Centralized UI styling constants for consistent visual design across all views.
 * Retro Pokemon 64-bit theme inspired by classic Pokemon games.
 */
public class UIStyleConstants {

    // ============ RETRO POKEMON COLOR PALETTE ============
    // Classic Pokemon Red/Blue inspired colors
    public static final Color PRIMARY_COLOR = new Color(200, 56, 56);        // Pokemon Red
    public static final Color PRIMARY_DARK = new Color(168, 40, 40);         // Darker red for borders
    public static final Color SECONDARY_COLOR = new Color(255, 203, 5);      // Pokemon Yellow
    public static final Color POKEMON_BLUE = new Color(48, 80, 200);         // Pokemon Blue
    public static final Color POKEMON_BLUE_LIGHT = new Color(88, 128, 232);  // Lighter blue

    // Retro game-style backgrounds
    public static final Color BACKGROUND = new Color(248, 248, 248);         // Off-white like old games
    public static final Color MENU_BG = new Color(248, 248, 216);            // Cream/tan menu background
    public static final Color CARD_BG = new Color(255, 255, 255);            // White
    public static final Color DARK_BG = new Color(40, 40, 40);               // Dark background

    // HP Colors (classic Pokemon style)
    public static final Color HP_HIGH = new Color(56, 168, 72);              // Classic green
    public static final Color HP_MEDIUM = new Color(248, 176, 32);           // Classic yellow
    public static final Color HP_LOW = new Color(240, 64, 56);               // Classic red

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(40, 40, 40);          // Near black
    public static final Color TEXT_SECONDARY = new Color(96, 96, 96);        // Dark gray
    public static final Color TEXT_LIGHT = new Color(248, 248, 248);         // Off-white

    // Retro border colors
    public static final Color BORDER_DARK = new Color(64, 64, 64);           // Dark border
    public static final Color BORDER_LIGHT = new Color(192, 192, 192);       // Light border
    public static final Color SHADOW_COLOR = new Color(88, 88, 88);          // Shadow effect

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

    // ============ RETRO TYPOGRAPHY ============
    // Use monospace/pixel-style fonts for retro feel
    public static final Font TITLE_FONT = new Font("Courier New", Font.BOLD, 28);
    public static final Font HEADING_FONT = new Font("Courier New", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Courier New", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Courier New", Font.PLAIN, 12);
    public static final Font EXTRA_LARGE_FONT = new Font("Courier New", Font.BOLD, 36);
    public static final Font PIXEL_FONT = new Font("Monospaced", Font.BOLD, 16);
    public static final Font MENU_FONT = new Font("Courier New", Font.BOLD, 20);

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
