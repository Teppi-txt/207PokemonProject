package interface_adapters.ui;

import java.awt.*;
import java.util.Map;

/**
 * Simple UI styling constants.
 */
public class UIStyleConstants {

    // Simple color palette
    public static final Color PRIMARY_COLOR = new Color(50, 50, 50);
    public static final Color PRIMARY_DARK = new Color(30, 30, 30);
    public static final Color SECONDARY_COLOR = new Color(100, 100, 100);
    public static final Color POKEMON_BLUE = new Color(66, 133, 244);
    public static final Color POKEMON_BLUE_LIGHT = new Color(100, 160, 255);

    // Backgrounds
    public static final Color BACKGROUND = Color.WHITE;
    public static final Color MENU_BG = new Color(250, 250, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color DARK_BG = new Color(40, 40, 40);

    // HP Colors
    public static final Color HP_HIGH = new Color(76, 175, 80);
    public static final Color HP_MEDIUM = new Color(255, 193, 7);
    public static final Color HP_LOW = new Color(244, 67, 54);

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    public static final Color TEXT_LIGHT = Color.WHITE;

    // Borders
    public static final Color BORDER_DARK = new Color(200, 200, 200);
    public static final Color BORDER_LIGHT = new Color(230, 230, 230);
    public static final Color SHADOW_COLOR = new Color(150, 150, 150);

    // Pokemon type colors
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

    // Simple fonts
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font EXTRA_LARGE_FONT = new Font("SansSerif", Font.BOLD, 36);
    public static final Font PIXEL_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font MENU_FONT = new Font("SansSerif", Font.BOLD, 16);

    // Spacing
    public static final int SMALL_PADDING = 8;
    public static final int MEDIUM_PADDING = 16;
    public static final int LARGE_PADDING = 24;
    public static final int XLARGE_PADDING = 32;

    // Shape
    public static final int BORDER_RADIUS = 8;
    public static final int SMALL_BORDER_RADIUS = 4;

    // Sizes
    public static final int SPRITE_SIZE = 96;
    public static final int LARGE_SPRITE_SIZE = 128;
    public static final int SMALL_SPRITE_SIZE = 64;

    public static Color getTypeColor(String typeName) {
        if (typeName == null) {
            return Color.GRAY;
        }
        return TYPE_COLORS.getOrDefault(typeName.toLowerCase(), Color.GRAY);
    }

    public static Color getHPColor(float hpPercent) {
        if (hpPercent > 0.5f) {
            return HP_HIGH;
        } else if (hpPercent > 0.25f) {
            return HP_MEDIUM;
        } else {
            return HP_LOW;
        }
    }

    private UIStyleConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
