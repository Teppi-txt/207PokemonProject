package frameworks_and_drivers.collection;

import entities.Pokemon;
import entities.battle.Stats;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class representing a JPanel displaying a specific Pokemon instance
 */

public class PokemonInfoPanel extends JPanel {
    public PokemonInfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 350));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 4, true));
    }

    public void setPokemon(Pokemon pokemon, boolean highlighted) {
        JLabel spriteLabel = getSpriteLabel(pokemon);
        JLabel nameLabel = getNameLabel(pokemon.getName());
        JPanel statsPanel = getStatsPanel(pokemon.getStats());
        this.removeAll();

        if (highlighted) {
            spriteLabel.setIcon(spriteLabel.getDisabledIcon());
        }

        add(Box.createVerticalStrut(10));
        add(spriteLabel);
        add(nameLabel);
        add(statsPanel);
        add(Box.createVerticalGlue());
        this.revalidate();
        this.repaint();
    }

    public void setPokemonAsNull() {
        JLabel spriteLabel = getSpriteLabel(new ImageIcon("src/assets/sprites/no_image_icon.png"));
        JLabel nameLabel = getNameLabel("No Pokemon :(");
        this.removeAll();
        add(Box.createVerticalStrut(10));
        add(spriteLabel);
        add(nameLabel);
        add(Box.createVerticalGlue());
        this.revalidate();
        this.repaint();
    }

    public JLabel getNameLabel(String name) {
        JLabel nameLabel = new JLabel(capitaliseFirstLetter(name));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 32));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return nameLabel;
    }

    public JLabel getSpriteLabel(Pokemon pokemon) {

        List<String> spriteUrls = new ArrayList<>();

        if (pokemon.isShiny()) {
            spriteUrls.add(pokemon.getShinyFrontGIF());
            spriteUrls.add(pokemon.getShinySpriteURL());
        } else {
            spriteUrls.add(pokemon.getRegularFrontGIF());
            spriteUrls.add(pokemon.getRegularSpriteURL());
        }

        for (String url : spriteUrls) {
            if (url == null || url.isEmpty()) continue;

            try {
                ImageIcon icon = new ImageIcon(new URL(url));

                if (icon.getIconWidth() < 32) {
                    continue;
                }

                icon.setImage(icon.getImage().getScaledInstance(160, 160, Image.SCALE_DEFAULT));

                JLabel spriteLabel = new JLabel(icon);
                spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                spriteLabel.setPreferredSize(new Dimension(300, 300));
                return spriteLabel;

            } catch (Exception ignored) { }
        }

        ImageIcon fallback = new ImageIcon("src/assets/sprites/no_image_icon.png");
        fallback.setImage(fallback.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH));

        JLabel spriteLabel = new JLabel(fallback);
        spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spriteLabel.setPreferredSize(new Dimension(300, 300));
        return spriteLabel;
    }


    public JLabel getSpriteLabel(ImageIcon sprite) {
        try {
            sprite.setImage(sprite.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));

            JLabel spriteLabel = new JLabel(sprite);
            spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            spriteLabel.setMaximumSize(new Dimension(300, 100));
            return spriteLabel;
        } catch (Exception e) {
            return new JLabel();
        }
    }

    public JPanel getStatsPanel(Stats stats) {
        JPanel statsPanel = new JPanel();
        Map<String, Integer> map = stats.getStatMap();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (String stat : Stats.STAT_NAMES) {
            JLabel statLabel = new JLabel(stat + ": " + map.get(stat));
            statLabel.setFont(new Font("Arial", Font.BOLD, 24));
            statsPanel.add(statLabel);
        }
        return statsPanel;
    }

    public String capitaliseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}