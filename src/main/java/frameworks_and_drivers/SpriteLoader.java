package frameworks_and_drivers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Utility class for downloading and caching Pokemon sprites from URLs.
 * Provides synchronous and asynchronous loading with in-memory caching.
 */
public class SpriteLoader {
    private static final Map<String, BufferedImage> cache = new ConcurrentHashMap<>();
    private static final int PLACEHOLDER_SIZE = 96;

    /**
     * Loads a sprite synchronously from the given URL.
     * Uses cached version if available.
     *
     * @param spriteUrl The URL of the sprite to load
     * @return The loaded BufferedImage, or a placeholder if loading fails
     */
    public static BufferedImage loadSprite(String spriteUrl) {
        if (spriteUrl == null || spriteUrl.isEmpty()) {
            return getPlaceholderImage();
        }

        // Check cache first
        if (cache.containsKey(spriteUrl)) {
            return cache.get(spriteUrl);
        }

        // Load from URL
        try {
            URL url = new URL(spriteUrl);
            BufferedImage image = ImageIO.read(url);
            if (image != null) {
                cache.put(spriteUrl, image);
                return image;
            }
        } catch (IOException e) {
            System.err.println("Failed to load sprite from: " + spriteUrl);
        }

        return getPlaceholderImage();
    }

    /**
     * Loads a sprite asynchronously using SwingWorker.
     *
     * @param spriteUrl The URL of the sprite to load
     * @param callback Consumer that receives the loaded image when ready
     */
    public static void loadSpriteAsync(String spriteUrl, Consumer<BufferedImage> callback) {
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() {
                return loadSprite(spriteUrl);
            }

            @Override
            protected void done() {
                try {
                    BufferedImage sprite = get();
                    callback.accept(sprite);
                } catch (Exception e) {
                    callback.accept(getPlaceholderImage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Clears the sprite cache.
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * Returns the number of cached sprites.
     */
    public static int getCacheSize() {
        return cache.size();
    }

    /**
     * Creates a placeholder image for when sprite loading fails.
     *
     * @return A gray placeholder BufferedImage
     */
    private static BufferedImage getPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(
                PLACEHOLDER_SIZE, PLACEHOLDER_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw gray background
        g2.setColor(new Color(200, 200, 200));
        g2.fillRect(0, 0, PLACEHOLDER_SIZE, PLACEHOLDER_SIZE);

        // Draw border
        g2.setColor(new Color(150, 150, 150));
        g2.drawRect(0, 0, PLACEHOLDER_SIZE - 1, PLACEHOLDER_SIZE - 1);

        // Draw question mark
        g2.setColor(new Color(100, 100, 100));
        g2.setFont(new Font("SansSerif", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String text = "?";
        int x = (PLACEHOLDER_SIZE - fm.stringWidth(text)) / 2;
        int y = ((PLACEHOLDER_SIZE - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);

        g2.dispose();
        return placeholder;
    }
}
