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
import java.util.ArrayList;
import java.util.Map;

public class CollectionView extends JPanel implements PropertyChangeListener, ActionListener {
    static final JFrame application  = new JFrame("Pokemon Collection Example");

    final PokemonInfoPanel pokemonInfoPanel;
    final PokemonCollectionPanel pokemonCollectionPanel;

    public CollectionView() {
        final JLabel title = new JLabel("My Collection");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 46));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));

        pokemonInfoPanel = new PokemonInfoPanel(JSONLoader.allPokemon.get(1));
        pokemonCollectionPanel = new PokemonCollectionPanel(JSONLoader.allPokemon);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        body.add(pokemonInfoPanel);
        body.add(pokemonCollectionPanel);
        this.add(body);
    }

    public void onPokemonSelection(ActionEvent e) {
        JButton clicked = (JButton)e.getSource();
        int index = (int) clicked.getClientProperty("id");
        System.out.println(index);
        pokemonInfoPanel.updatePokemon(JSONLoader.allPokemon.get(index));
        application.pack();
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
        application.setMinimumSize(new Dimension(700, 600));
        application.pack();
        application.setVisible(true);
    }

    public class PokemonInfoPanel extends JPanel {

        private JLabel spriteLabel;
        private JLabel nameLabel;
        private JPanel statsPanel;

        public PokemonInfoPanel(Pokemon pokemon) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(300, 350));
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            updatePokemon(pokemon);
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
            nameLabel.setFont(new Font("Arial", Font.BOLD, 32));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            return nameLabel;
        }

        public JLabel getSpriteLabel(Pokemon pokemon) {
            try {
                ImageIcon sprite = new ImageIcon(new URL(pokemon.getSpriteUrl()));
                sprite.setImage(sprite.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));

                JLabel spriteLabel = new JLabel(sprite);
                spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                spriteLabel.setMaximumSize(new Dimension(300, 100));
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

            for (String stat : Stats.STAT_NAMES) {
                JLabel statLabel = new JLabel(stat + ": " + String.valueOf(map.get(stat)));
                statLabel.setFont(new Font("Arial", Font.BOLD, 24));
                statsPanel.add(statLabel);
            }
            return statsPanel;
        }
    }

    public class PokemonFilterPanel extends JPanel {

        private final JToggleButton allButton;
        private final JToggleButton ownedButton;
        private final JToggleButton shinyButton;

        public PokemonFilterPanel(ActionListener filterListener) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));

            allButton   = new JToggleButton("All");
            ownedButton = new JToggleButton("Owned");
            shinyButton = new JToggleButton("Shiny");

            ButtonGroup group = new ButtonGroup();
            group.add(allButton);
            group.add(ownedButton);
            group.add(shinyButton);
            allButton.setSelected(true);

            if (filterListener != null) {
                allButton.setActionCommand("ALL");
                ownedButton.setActionCommand("OWNED");
                shinyButton.setActionCommand("SHINY");

                allButton.addActionListener(filterListener);
                ownedButton.addActionListener(filterListener);
                shinyButton.addActionListener(filterListener);
            }

            add(allButton);
            add(ownedButton);
            add(shinyButton);
        }

        public String getSelectedFilter() {
            if (ownedButton.isSelected()) return "OWNED";
            if (shinyButton.isSelected()) return "SHINY";
            return "ALL";
        }
    }

    public class PokemonCollectionPanel extends JPanel {
        private final JPanel pokemonPanel;
        private final JPanel filterPanel;
        private int currentPage = 0;

        public PokemonCollectionPanel(ArrayList<Pokemon> pokemons) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            pokemonPanel = new JPanel();
            pokemonPanel.setLayout(new GridLayout(5, 5));
            pokemonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            pokemonPanel.setMinimumSize(new Dimension(600, 600));

            loadPage(currentPage);


            filterPanel = new PokemonFilterPanel(null);
            this.add(filterPanel);
            this.add(pokemonPanel);
            JButton backButton = new JButton("Prev");
            backButton.addActionListener(e -> {
                currentPage--;
                loadPage(currentPage);
                application.pack();
            });
            this.add(backButton);

            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> {
                currentPage++;
                loadPage(currentPage);
                application.pack();
            });
            this.add(nextButton);
        }

        private void loadPage(int page) {
            pokemonPanel.removeAll();

            // Add 25 buttons to the frame
            for (int i = 25 * page; i < 25 * (page + 1); i++) {
                ImageIcon pokeIcon = new ImageIcon();
                try {
                    pokeIcon = new ImageIcon(new URL(JSONLoader.allPokemon.get(i).getSpriteUrl()));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                JButton button = new JButton(pokeIcon);
                button.setContentAreaFilled(false);
                button.putClientProperty("id", i);  // store index as command
                button.addActionListener(CollectionView.this::onPokemonSelection);
                pokemonPanel.add(button);
            }
        }
    }
}
