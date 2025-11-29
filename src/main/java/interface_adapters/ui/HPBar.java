package interface_adapters.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Custom HP bar component that displays health with gradient colors.
 */
public class HPBar extends JPanel {
    private int currentHP;
    private int maxHP;
    private boolean showLabel;

    public HPBar() {
        this(100, 100, true);
    }

    public HPBar(int currentHP, int maxHP, boolean showLabel) {
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.showLabel = showLabel;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 24));
    }

    /**
     * Updates the HP values and repaints the bar.
     *
     * @param current Current HP
     * @param max Maximum HP
     */
    public void updateHP(int current, int max) {
        this.currentHP = Math.max(0, current);
        this.maxHP = Math.max(1, max);
        repaint();
    }

    /**
     * Sets whether to show the HP label text.
     */
    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        repaint();
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int barHeight = showLabel ? height - 14 : height;

        // Draw background (empty HP)
        g2.setColor(new Color(220, 220, 220));
        g2.fillRoundRect(0, 0, width, barHeight, 8, 8);

        // Draw border
        g2.setColor(new Color(180, 180, 180));
        g2.drawRoundRect(0, 0, width - 1, barHeight - 1, 8, 8);

        // Draw foreground (current HP) with color based on percentage
        if (maxHP > 0 && currentHP > 0) {
            float hpPercent = (float) currentHP / maxHP;
            Color hpColor = UIStyleConstants.getHPColor(hpPercent);

            g2.setColor(hpColor);
            int fillWidth = (int) (width * hpPercent);
            g2.fillRoundRect(1, 1, fillWidth - 2, barHeight - 2, 7, 7);
        }

        // Draw label
        if (showLabel) {
            g2.setColor(UIStyleConstants.TEXT_PRIMARY);
            g2.setFont(UIStyleConstants.SMALL_FONT);
            String label = currentHP + " / " + maxHP + " HP";
            FontMetrics fm = g2.getFontMetrics();
            int labelX = (width - fm.stringWidth(label)) / 2;
            int labelY = barHeight + fm.getAscent() + 2;
            g2.drawString(label, labelX, labelY);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if (showLabel) {
            return new Dimension(size.width, Math.max(size.height, 24));
        }
        return new Dimension(size.width, Math.max(size.height, 16));
    }
}
