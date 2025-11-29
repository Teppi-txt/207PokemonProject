package interface_adapters.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Custom button component with rounded corners and modern styling.
 * Provides hover effects and anti-aliased rendering.
 */
public class StyledButton extends JButton {

    private Color backgroundColor;
    private Color hoverColor;

    public StyledButton(String text) {
        this(text, UIStyleConstants.PRIMARY_COLOR);
    }

    public StyledButton(String text, Color backgroundColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.hoverColor = backgroundColor.brighter();

        setFont(UIStyleConstants.BODY_FONT);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                repaint();
            }
        });
    }

    @Override
    public void setBackground(Color bg) {
        this.backgroundColor = bg;
        this.hoverColor = bg.brighter();
        super.setBackground(bg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine color based on hover state
        Color currentColor = getModel().isRollover() ? hoverColor : backgroundColor;

        // Disable state
        if (!isEnabled()) {
            currentColor = Color.LIGHT_GRAY;
        }

        // Draw rounded background
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                UIStyleConstants.BORDER_RADIUS, UIStyleConstants.BORDER_RADIUS);

        // Draw text
        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width + 32, Math.max(size.height + 16, 40));
    }
}
