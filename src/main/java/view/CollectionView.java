package view;

import entities.Pokemon;
import entities.Stats;
import pokeapi.JSONLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CollectionView extends JPanel implements PropertyChangeListener, ActionListener {
    static final JFrame application  = new JFrame("User Login Example");

    private final JTextField passwordInputField = new JTextField(15);

    public CollectionView() {
        final JTextField passwordInputField = new JTextField(15);
        final JLabel title = new JLabel("My Collection");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));

        final PokemonInfoPanel pokemonInfoPanel = new PokemonInfoPanel(JSONLoader.allPokemon.get(1));

        final JPanel pokemonPanel = new JPanel();
        pokemonPanel.setLayout(new GridLayout(5, 5));
        pokemonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        pokemonPanel.setMinimumSize(new Dimension(600, 600));
        // Add 25 buttons to the frame
        for (int i = 0; i < 25; i++) {
            ImageIcon pokeIcon = new ImageIcon();
            try {
                pokeIcon = new ImageIcon(new URL(JSONLoader.allPokemon.get(i).getSpriteUrl()));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            JButton button = new JButton(pokeIcon);
            button.setContentAreaFilled(false);
            button.putClientProperty("id", i);  // store index as command

            button.addActionListener(e -> {
                JButton clicked = (JButton)e.getSource();
                int index = (int) clicked.getClientProperty("id");
                System.out.println(index);
                pokemonInfoPanel.updatePokemon(JSONLoader.allPokemon.get(index));
                application.pack();
            });
            pokemonPanel.add(button);
        }


        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        body.add(pokemonInfoPanel);
        body.add(pokemonPanel);
        this.add(body);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public static void main(String[] args) {
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JSONLoader.loadPokemon();

        application.add(new CollectionView());
        application.setMinimumSize(new Dimension(600, 600));
        application.pack();
        application.setVisible(true);
    }

    public class PokemonInfoPanel extends JPanel {

        private JLabel spriteLabel;
        private JLabel nameLabel;
        private JPanel statsPanel;

        public PokemonInfoPanel(Pokemon pokemon) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(200, 350));
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            spriteLabel = getSpriteLabel(pokemon);
            nameLabel = getNameLabel(pokemon.getName());
            statsPanel = getStatsPanel(pokemon.getStats());

            add(Box.createVerticalStrut(10));
            add(spriteLabel);
            add(nameLabel);
            add(statsPanel);
            add(Box.createVerticalGlue());
        }

        public void updatePokemon(Pokemon pokemon) {
            this.removeAll();
            spriteLabel = getSpriteLabel(pokemon);
            nameLabel = getNameLabel(pokemon.getName());
            statsPanel = getStatsPanel(pokemon.getStats());

            add(Box.createVerticalStrut(10));
            add(spriteLabel);
            add(nameLabel);
            add(statsPanel);
            add(Box.createVerticalGlue());
            this.revalidate();
            this.repaint();
        }

        public JLabel getNameLabel(String name) {
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            return nameLabel;
        }

        public JLabel getSpriteLabel(Pokemon pokemon) {
            try {
                JLabel spriteLabel = new JLabel(new ImageIcon(new URL(pokemon.getSpriteUrl())));
                spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                return spriteLabel;

            } catch (Exception e) {
                System.out.println(e);
                return new  JLabel();
            }
        }

        public JPanel getStatsPanel(Stats stats) {
            JPanel statsPanel = new JPanel();
            Map<String, Integer> map = stats.getStatMap();
            statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
            statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPanel.setBackground(Color.WHITE);

            for (String stat : Stats.STAT_NAMES) {
                JLabel statLabel = new JLabel(stat + ": " + String.valueOf(map.get(stat)));
                statsPanel.add(statLabel);
            }
            return statsPanel;
        }
    }
}
