package view;

import entities.Pokemon;
import entities.Stats;
import interface_adapter.collection.ViewCollectionController;
import interface_adapter.collection.ViewCollectionState;
import interface_adapter.collection.ViewCollectionViewModel;
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
import java.util.List;
import java.util.Map;

public class CollectionView extends JPanel implements PropertyChangeListener, ActionListener {
    static final JFrame application  = new JFrame("Pokemon Collection Example");

    final PokemonInfoPanel pokemonInfoPanel;
    final PokemonCollectionPanel pokemonCollectionPanel;
    private final ViewCollectionViewModel collectionViewModel;
    private ViewCollectionController controller;

    private Pokemon selectedPokemon;
    private List<Pokemon> pokemonOnPage;
    private List<Pokemon> ownedPokemon;
    private String filter = "all";
    private int currentPage = 0;

    public CollectionView(ViewCollectionViewModel collectionViewModel) {
        this.collectionViewModel = collectionViewModel;
        this.collectionViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("My Collection");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 46));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));

        pokemonInfoPanel = new PokemonInfoPanel(JSONLoader.allPokemon.get(1));
        pokemonCollectionPanel = new PokemonCollectionPanel();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        body.add(pokemonInfoPanel);
        body.add(pokemonCollectionPanel);
        this.add(body);
    }

    public void onPokemonSelection(ActionEvent e) {
        JButton clicked = (JButton)e.getSource();
        int index = (int) clicked.getClientProperty("id");
        pokemonInfoPanel.updatePokemon(JSONLoader.allPokemon.get(index));
        application.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final ViewCollectionState state = (ViewCollectionState) evt.getNewValue();
        this.pokemonOnPage = state.getPokemonOnPage();
        this.selectedPokemon = state.getSelectedPokemon();
        this.ownedPokemon = state.getOwnedPokemon();
        updatePanel(state);
    }

    private void updatePanel(ViewCollectionState state) {
        this.pokemonInfoPanel.updatePokemon(state.getSelectedPokemon());
        this.pokemonCollectionPanel.loadPage(state.getPokemonOnPage(), state.getOwnedPokemon());
    }

    public ViewCollectionController getController() {
        return controller;
    }

    public void setController(ViewCollectionController controller) {
        this.controller = controller;
    }


    public static class PokemonInfoPanel extends JPanel {
        public PokemonInfoPanel(Pokemon pokemon) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(300, 350));
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            updatePokemon(pokemon);
        }

        public void updatePokemon(Pokemon pokemon) {
            JPanel statsPanel;
            JLabel spriteLabel;
            JLabel nameLabel;
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

        private void onFilterChanged(ActionEvent e) {
            filter = e.getActionCommand().toLowerCase();
            currentPage = 0;
            controller.execute(pokemonOnPage, currentPage, filter);
        }

        public PokemonFilterPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));

            allButton   = new JToggleButton("All");
            ownedButton = new JToggleButton("Owned");
            shinyButton = new JToggleButton("Shiny");

            ButtonGroup group = new ButtonGroup();
            group.add(allButton);
            group.add(ownedButton);
            group.add(shinyButton);
            allButton.setSelected(true);

            allButton.setActionCommand("ALL");
            ownedButton.setActionCommand("OWNED");
            shinyButton.setActionCommand("SHINY");

            allButton.addActionListener(this::onFilterChanged);
            ownedButton.addActionListener(this::onFilterChanged);
            shinyButton.addActionListener(this::onFilterChanged);

            add(allButton);
            add(ownedButton);
            add(shinyButton);
        }
    }

    public class PokemonCollectionPanel extends JPanel {
        private final JPanel pokemonPanel;
        private final JPanel filterPanel;

        public PokemonCollectionPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            pokemonPanel = new JPanel();
            pokemonPanel.setLayout(new GridLayout(5, 5));
            pokemonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            pokemonPanel.setMinimumSize(new Dimension(600, 600));

            filterPanel = new PokemonFilterPanel();
            this.add(filterPanel);
            this.add(pokemonPanel);
            JButton backButton = new JButton("Prev");
            backButton.addActionListener(e -> {
                currentPage--;
                controller.execute(pokemonOnPage, currentPage, filter);
            });
            this.add(backButton);

            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> {
                currentPage++;
                controller.execute(pokemonOnPage, currentPage, filter);
            });
            this.add(nextButton);
        }

        private void loadPage(List<Pokemon> pokemons, List<Pokemon> ownedPokemon) {
            pokemonPanel.removeAll();

            // Add buttons to the frame
            for (int i = 0; i < pokemons.size(); i++) {
                ImageIcon pokeIcon = new ImageIcon();
                try {
                    pokeIcon = new ImageIcon(new URL(pokemons.get(i).getSpriteUrl()));
                    if (!ownedPokemon.contains(pokemons.get(i))) {
                        pokeIcon.setImage(GrayFilter.createDisabledImage(pokeIcon.getImage()));
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                JButton button = new JButton(pokeIcon);
                button.setContentAreaFilled(false);
                button.setFocusable(false);
                button.putClientProperty("id", i);  // store index as command
                button.addActionListener(CollectionView.this::onPokemonSelection);
                pokemonPanel.add(button);
            }
        }


    }
}
