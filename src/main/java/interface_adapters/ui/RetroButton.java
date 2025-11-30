package interface_adapters.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Retro Pokemon-style button with pixel art aesthetic.
 * Features raised 3D effect and hover animations like classic Pokemon games.
 */
public class RetroButton extends JButton {

    private boolean isHovered = false;
    private boolean isPressed = false;
    private Color baseColor;
    private Color hoverColor;
    private Color pressedColor;

    public RetroButton(String text) {
        this(text, UIStyleConstants.PRIMARY_COLOR);
    }

    public RetroButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();

        setFont(UIStyleConstants.MENU_FONT);
        setForeground(UIStyleConstants.TEXT_LIGHT);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        int width = getWidth();
        int height = getHeight();
        int borderThickness = 4;

        // Determine current color
        Color currentColor;
        if (!isEnabled()) {
            currentColor = Color.GRAY;
        } else if (isPressed) {
            currentColor = pressedColor;
        } else if (isHovered) {
            currentColor = hoverColor;
        } else {
            currentColor = baseColor;
        }

        // Draw shadow (bottom-right)
        g2d.setColor(UIStyleConstants.SHADOW_COLOR);
        g2d.fillRect(borderThickness, borderThickness, width - borderThickness, height - borderThickness);

        // Draw dark border (3D effect - bottom and right)
        g2d.setColor(currentColor.darker().darker());
        g2d.fillRect(0, height - borderThickness, width, borderThickness);
        g2d.fillRect(width - borderThickness, 0, borderThickness, height);

        // Draw light border (3D effect - top and left)
        g2d.setColor(isPressed ? currentColor.darker() : currentColor.brighter());
        g2d.fillRect(0, 0, width - borderThickness, borderThickness);
        g2d.fillRect(0, 0, borderThickness, height - borderThickness);

        // Draw main button face
        int offset = isPressed ? 2 : 0;
        g2d.setColor(currentColor);
        g2d.fillRect(borderThickness + offset, borderThickness + offset,
                     width - 2 * borderThickness - offset, height - 2 * borderThickness - offset);

        // Draw text
        g2d.setFont(getFont());
        g2d.setColor(isEnabled() ? getForeground() : Color.DARK_GRAY);

        FontMetrics fm = g2d.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2 + offset;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent() + offset;

        // Text shadow for depth
        if (isEnabled()) {
            g2d.setColor(Color.BLACK);
            g2d.drawString(getText(), textX + 2, textY + 2);
            g2d.setColor(getForeground());
        }
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(Math.max(size.width + 40, 200), Math.max(size.height + 20, 50));
    }

    public void setButtonColor(Color color) {
        this.baseColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        repaint();
    }
}
