package interface_adapters.ui;

import entities.Pokemon;
import frameworks_and_drivers.SpriteLoader;

import javax.swing.*;
import java.awt.*;

/**
 * Card component displaying Pokemon sprite, name, HP bar, and type badges.
 * Loads sprites asynchronously to avoid blocking the UI.
 */
public class PokemonCard extends JPanel {
    private final JLabel spriteLabel;
    private final JLabel nameLabel;
    private final HPBar hpBar;
    private final JPanel typesPanel;
    private final Pokemon pokemon;

    public PokemonCard(Pokemon pokemon) {
        this(pokemon, true);
    }

    public PokemonCard(Pokemon pokemon, boolean showHPBar) {
        this.pokemon = pokemon;

        setLayout(new BorderLayout(UIStyleConstants.SMALL_PADDING, UIStyleConstants.SMALL_PADDING));
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(
                        UIStyleConstants.MEDIUM_PADDING,
                        UIStyleConstants.MEDIUM_PADDING,
                        UIStyleConstants.MEDIUM_PADDING,
                        UIStyleConstants.MEDIUM_PADDING)
        ));
        setPreferredSize(new Dimension(200, 280));

        // Sprite panel
        JPanel spritePanel = new JPanel();
        spritePanel.setOpaque(false);
        spriteLabel = new JLabel("Loading...", SwingConstants.CENTER);
        spriteLabel.setPreferredSize(new Dimension(
                UIStyleConstants.SPRITE_SIZE, UIStyleConstants.SPRITE_SIZE));
        spritePanel.add(spriteLabel);
        add(spritePanel, BorderLayout.CENTER);

        // Load sprite asynchronously
        loadSpriteAsync();

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Name
        nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(UIStyleConstants.HEADING_FONT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        infoPanel.add(nameLabel);

        infoPanel.add(Box.createVerticalStrut(UIStyleConstants.SMALL_PADDING));

        // HP Bar
        if (showHPBar) {
            hpBar = new HPBar(pokemon.getStats().getHp(), pokemon.getStats().getHp(), false);
            hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            hpBar.setPreferredSize(new Dimension(180, 16));
            hpBar.setMaximumSize(new Dimension(180, 16));
            infoPanel.add(hpBar);

            infoPanel.add(Box.createVerticalStrut(UIStyleConstants.SMALL_PADDING));
        } else {
            hpBar = null;
        }

        // Types panel
        typesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        typesPanel.setOpaque(false);
        for (String type : pokemon.getTypes()) {
            typesPanel.add(new TypeBadge(type));
        }
        infoPanel.add(typesPanel);

        add(infoPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads the Pokemon sprite asynchronously.
     */
    private void loadSpriteAsync() {
        SpriteLoader.loadSpriteAsync(pokemon.getSpriteUrl(), sprite -> {
            if (sprite != null) {
                spriteLabel.setIcon(new ImageIcon(sprite));
                spriteLabel.setText(null);
            } else {
                spriteLabel.setText("No Image");
            }
        });
    }

    /**
     * Updates the HP bar (if present) with new values.
     */
    public void updateHP(int currentHP) {
        if (hpBar != null) {
            hpBar.updateHP(currentHP, pokemon.getStats().getHp());
        }
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded background
        g2.setColor(UIStyleConstants.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                UIStyleConstants.BORDER_RADIUS, UIStyleConstants.BORDER_RADIUS);

        // Draw subtle shadow
        g2.setColor(new Color(0, 0, 0, 20));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3,
                UIStyleConstants.BORDER_RADIUS, UIStyleConstants.BORDER_RADIUS);

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Capitalizes the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
