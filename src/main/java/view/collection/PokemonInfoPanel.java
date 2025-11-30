package view.collection;

import entities.Pokemon;
import entities.Stats;
import entities.PriceCalculator;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        add(getBuyButtonsPanel(pokemon));
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
        try {
            ImageIcon sprite = new ImageIcon(new URL(pokemon.getFrontGIF()));
            sprite.setImage(sprite.getImage().getScaledInstance(160, 160, Image.SCALE_DEFAULT));

            JLabel spriteLabel = new JLabel(sprite);
            spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            spriteLabel.setPreferredSize(new Dimension(300, 300));
            return spriteLabel;
        } catch (Exception e) {
            return new JLabel();
        }
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

    public JPanel getBuyButtonsPanel(Pokemon pokemon) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int price = PriceCalculator.getPrice(pokemon);
        int shinyPrice = PriceCalculator.getShinyPrice(pokemon);

        JButton buyPokemonButton = new JButton("Buy (" + price + ")");
        JButton buyShinyButton = new JButton("Shiny (" + shinyPrice + ")");

        buyPokemonButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        buyShinyButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        panel.add(Box.createHorizontalGlue());
        panel.add(buyPokemonButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(buyShinyButton);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    public String capitaliseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}