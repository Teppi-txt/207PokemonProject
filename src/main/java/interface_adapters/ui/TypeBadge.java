package interface_adapters.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Colored badge component for displaying Pokemon types.
 * Automatically colors itself based on the type name.
 */
public class TypeBadge extends JLabel {

    private final Color typeColor;

    public TypeBadge(String typeName) {
        super(typeName != null ? typeName.toUpperCase() : "UNKNOWN");

        this.typeColor = UIStyleConstants.getTypeColor(typeName);

        setFont(UIStyleConstants.SMALL_FONT);
        setForeground(Color.WHITE);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);

        // Add padding
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded background
        g2.setColor(typeColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                UIStyleConstants.SMALL_BORDER_RADIUS, UIStyleConstants.SMALL_BORDER_RADIUS);

        // Draw subtle shadow/border
        g2.setColor(typeColor.darker());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                UIStyleConstants.SMALL_BORDER_RADIUS, UIStyleConstants.SMALL_BORDER_RADIUS);

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width + 16, size.height + 8);
    }
}
