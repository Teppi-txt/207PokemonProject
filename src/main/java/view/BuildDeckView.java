package view;

import entities.battle.Deck;
import entities.Pokemon;
import entities.battle.Stats;
import entities.user.User;
import interface_adapters.build_deck.BuildDeckController;
import interface_adapters.build_deck.BuildDeckState;
import interface_adapters.build_deck.BuildDeckViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuildDeckView extends JPanel implements PropertyChangeListener {
    private final BuildDeckViewModel viewModel;
    private BuildDeckController controller;

    private final JTextField deckNameField;
    private final JLabel deckIdLabel;
    private final DeckDisplayPanel deckDisplayPanel;
    private final OwnedPokemonSelectionPanel selectionPanel;
    private final JLabel errorMessageLabel;
    private final JComboBox<Deck> deckSelector;

    private Deck currentDeck;
    private final List<Pokemon> ownedPokemon;
    private final ActionListener deckSelectListener = this::onDeckSelected;
    private Runnable navigationCallback;

    public static final String VIEW_NAME = "build_deck";


    public BuildDeckView(BuildDeckViewModel viewModel, User user) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.ownedPokemon = user.getOwnedPokemon();

        this.deckNameField = new JTextField(20);
        this.deckIdLabel = new JLabel("ID: --");
        this.deckDisplayPanel = new DeckDisplayPanel();
        this.selectionPanel = new OwnedPokemonSelectionPanel();
        this.errorMessageLabel = new JLabel();
        this.deckSelector = new JComboBox<>();
        this.deckSelector.addActionListener(deckSelectListener);

        this.setLayout(new BorderLayout());

        // Top panel with title and back button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        final JLabel title = new JLabel("Build Your Deck");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 46));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JButton returnButton = new JButton("Back to Menu");
        returnButton.setFont(new Font(title.getFont().getFontName(), Font.PLAIN, 18));
        returnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnButton.setBorder(new EmptyBorder(10, 10, 10, 10));
        returnButton.addActionListener(e -> {
            if (navigationCallback != null) {
                navigationCallback.run();
            }
        });

        // name and controls panel
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
        controlsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel nameIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameIdPanel.add(new JLabel("Deck Name: "));
        nameIdPanel.add(deckNameField);
        nameIdPanel.add(Box.createHorizontalStrut(15));
        nameIdPanel.add(deckIdLabel);

        // deck management panel
        JPanel deckManagementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deckManagementPanel.add(new JLabel("Select/New Deck:"));
        deckManagementPanel.add(deckSelector);

        // action buttons panel
        JButton saveButton = new JButton("Save Deck");
        saveButton.addActionListener(this::onSaveDeck);

        JButton deleteButton = new JButton("Delete Deck");
        deleteButton.addActionListener(e -> onDeleteDeck());

        JButton newDeckButton = new JButton("New Deck");
        newDeckButton.addActionListener(this::onNewDeck);

        JButton randomButton = new JButton("Randomize Deck");
        randomButton.addActionListener(this::onRandomizeDeck);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(newDeckButton);
        actionPanel.add(randomButton);
        actionPanel.add(saveButton);
        actionPanel.add(deleteButton);

        controlsPanel.add(deckManagementPanel);
        controlsPanel.add(nameIdPanel);
        controlsPanel.add(Box.createHorizontalGlue());
        controlsPanel.add(actionPanel);

        topPanel.add(title);
        topPanel.add(returnButton);
        topPanel.add(errorMessageLabel);
        topPanel.add(controlsPanel);

        // main content panel (deck display and selection)
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(deckDisplayPanel, BorderLayout.WEST);
        contentPanel.add(selectionPanel, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);

        //  initial load
        Deck initialDeck = viewModel.getState().getDeck();
        if (initialDeck == null) {
            // mock initial deck if the state is empty
            initialDeck = new Deck(-1, "New Deck");
        }
        updateView(initialDeck, viewModel.getState().getAllDecks(), null);
    }

    public void setController(BuildDeckController controller) {
        this.controller = controller;
    }

    public void setNavigationCallback(Runnable callback) {
        this.navigationCallback = callback;
    }

    // action listeners
    private void onSaveDeck(ActionEvent e) {
        int deckId = (currentDeck == null) ? -1 : currentDeck.getId();
        String deckName = deckNameField.getText();
        List<Pokemon> pokemons = currentDeck.getPokemons();

        controller.buildDeck(deckId, deckName, pokemons, false, false);
    }

    private void onDeleteDeck() {
        if (currentDeck == null || currentDeck.getId() == -1) {
            errorMessageLabel.setText("Cannot delete an unsaved deck.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this deck?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteDeck(currentDeck.getId());
        }
    }

    private void onNewDeck(ActionEvent e) {
        controller.buildDeck(-1, null, null, false, false);
    }

    private void onRandomizeDeck(ActionEvent e) {
        int deckId = (currentDeck == null) ? -1 : currentDeck.getId();
        String currentName = deckNameField.getText();
        controller.buildDeck(deckId, currentName, null, true, false);
    }

    private void onDeckSelected(ActionEvent e) {
        Deck selectedDeck = (Deck) deckSelector.getSelectedItem();
        // prevent triggering unnecessary loads if the selection didn't actually change
        // or if the deck is null/unselectable
        if (selectedDeck != null && (currentDeck == null || selectedDeck.getId() != currentDeck.getId())) {
            controller.buildDeck(selectedDeck.getId(), selectedDeck.getName(), null, false, false);
        }
    }

    public void onPokemonSelection(ActionEvent e) {
        Component source = (Component) e.getSource();
        Pokemon selected = null;

        // Pokémon button in OwnedPokemonSelectionPanel was clicked
        if (source instanceof JButton) {
            JButton button = (JButton) source;
            selected = (Pokemon) button.getClientProperty("pokemon");
        }
        // deck slot was clicked
        else if (source instanceof JPanel) {
            JPanel panel = (JPanel) source;
            selected = (Pokemon) panel.getClientProperty("pokemon");
        }
        if (selected == null) {
            return;
        }
        if (currentDeck.getPokemons().contains(selected)) {
            currentDeck.removePokemon(selected);
        } else {
            currentDeck.addPokemon(selected);
        }
        updateView(currentDeck, viewModel.getState().getAllDecks(), null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BuildDeckState state = (BuildDeckState) evt.getNewValue();
        updateView(state.getDeck(), state.getAllDecks(), state.getErrorMessage());
    }

    private void updateView(Deck deck, List<Deck> allDecks, String errorMessage) {
        this.currentDeck = deck;

        if (errorMessage != null) {
            errorMessageLabel.setText("Error: " + errorMessage);
        } else {
            errorMessageLabel.setText("");
        }
        updateDeckSelector(allDecks, deck);
        if (deck != null) {
            deckNameField.setText(deck.getName());
            deckIdLabel.setText("ID: " + deck.getId());
        } else {
            deckNameField.setText("New Deck");
            deckIdLabel.setText("ID: --");
        }
        deckDisplayPanel.updateDeck(deck);
        selectionPanel.updateSelectionStatus(deck);
        this.revalidate();
        this.repaint();
    }

    private void updateDeckSelector(List<Deck> allDecks, Deck currentDeck) {
        // temporarily remove listener to avoid triggering onDeckSelected during population
        deckSelector.removeActionListener(deckSelectListener);
        deckSelector.removeAllItems();
        if (allDecks != null) {
            for (Deck d : allDecks) {
                deckSelector.addItem(d);
            }
        }
        if (currentDeck != null) {
            for (int i = 0; i < deckSelector.getItemCount(); i++) {
                if (deckSelector.getItemAt(i).getId() == currentDeck.getId()) {
                    deckSelector.setSelectedIndex(i);
                    break;
                }
            }
        }
        deckSelector.addActionListener(deckSelectListener);
    }
    
    // components

    /**
     * Panel to display the 3 slots for the currently building deck.
     */
    public class DeckDisplayPanel extends JPanel {

        private final JPanel[] slotPanels;
        private static final int DECK_LIMIT = Deck.DECK_LIMIT;

        public DeckDisplayPanel() {
            this.setLayout(new GridLayout(1, DECK_LIMIT, 10, 0));
            this.setPreferredSize(new Dimension(550, 250));
            this.setBorder(BorderFactory.createTitledBorder("Current Deck (Max " + DECK_LIMIT + ")"));

            slotPanels = new JPanel[DECK_LIMIT];

            for (int i = 0; i < DECK_LIMIT; i++) {
                JPanel slot = createEmptySlotPanel(i);
                slotPanels[i] = slot;
                this.add(slot);
            }
        }

        private JPanel createEmptySlotPanel(int index) {
            JPanel slot = new JPanel();
            slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));
            slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            slot.setOpaque(true);
            slot.setBackground(new Color(240, 240, 240));

            JLabel empty = new JLabel("[Empty Slot " + (index + 1) + "]", SwingConstants.CENTER);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            slot.add(Box.createVerticalGlue());
            slot.add(empty);
            slot.add(Box.createVerticalGlue());

            return slot;
        }

        public void updateDeck(Deck deck) {
            List<Pokemon> pokemons = deck.getPokemons();

            for (int i = 0; i < DECK_LIMIT; i++) {
                JPanel slot = slotPanels[i];
                slot.removeAll();

                for (MouseListener ml : slot.getListeners(MouseListener.class)) {
                    slot.removeMouseListener(ml);
                }

                if (i < pokemons.size()) {
                    Pokemon p = pokemons.get(i);
                    fillSlotWithPokemon(slot, p);
                } else {
                    slot.removeAll();
                    slot.setBackground(new Color(240, 240, 240)); // reset to default
                    slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));

                    JLabel emptyLabel = new JLabel("[Empty Slot " + (i + 1) + "]", SwingConstants.CENTER);
                    emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    slot.add(Box.createVerticalGlue());
                    slot.add(emptyLabel);
                    slot.add(Box.createVerticalGlue());
                }
                slot.revalidate();
                slot.repaint();
            }
        }

        private void fillSlotWithPokemon(JPanel slot, Pokemon p) {
            Color slotColor = new Color(200, 255, 200);
            slot.setBackground(slotColor);
            slot.putClientProperty("pokemon", p);

            JLabel sprite = createSpriteLabel(p);
            JLabel name = createNameLabel(p);
            JPanel statsPanel = createStatsPanel(p);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            sprite.setAlignmentX(Component.CENTER_ALIGNMENT);
            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            centerPanel.add(sprite);
            centerPanel.add(name);
            centerPanel.add(statsPanel);

            slot.removeAll();
            slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));

            slot.add(Box.createVerticalGlue());
            slot.add(centerPanel);
            slot.add(Box.createVerticalGlue());

            slot.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BuildDeckView.this.onPokemonSelection(
                            new ActionEvent(slot, ActionEvent.ACTION_PERFORMED, "remove")
                    );
                }
            });
        }

        private JLabel createSpriteLabel(Pokemon pokemon) {
            try {
                ImageIcon sprite = new ImageIcon(new URL(pokemon.getSpriteUrl()));
                sprite.setImage(sprite.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));

                JLabel lbl = new JLabel(sprite);
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                return lbl;
            } catch (Exception e) {
                return new JLabel();
            }
        }

        private JLabel createNameLabel(Pokemon pokemon) {
            JLabel name = new JLabel(pokemon.getName());
            name.setFont(new Font("Arial", Font.BOLD, 16));
            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            name.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            return name;
        }

        private JPanel createStatsPanel(Pokemon pokemon) {
            JPanel statsPanel = new JPanel();
            statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
            statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPanel.setOpaque(true);

            // match the slot background color
            statsPanel.setBackground(new Color(200, 255, 200));

            Map<String, Integer> stats = pokemon.getStats().getStatMap();

            for (String stat : Stats.STAT_NAMES) {
                JLabel statLabel = new JLabel(stat + ": " + stats.get(stat));
                statLabel.setFont(new Font("Arial", Font.BOLD, 14));
                statLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                statLabel.setOpaque(false);
                statsPanel.add(statLabel);
            }
            statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            return statsPanel;
        }

    }

    /**
     * Panel to display owned Pokémon for selection.
     */
    public class OwnedPokemonSelectionPanel extends JPanel {

        private final JPanel pokemonGrid;

        public OwnedPokemonSelectionPanel() {
            this.setLayout(new BorderLayout());
            this.setBorder(BorderFactory.createTitledBorder("Owned Pokémon (Click to Add/Remove)"));
            this.setMinimumSize(new Dimension(400, 300));

            pokemonGrid = new JPanel(new GridLayout(0, 4, 8, 8));
            pokemonGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(pokemonGrid);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            this.add(scrollPane, BorderLayout.CENTER);
        }

        private JButton createPokemonButton(Pokemon pokemon) {
            JButton button = new JButton(pokemon.getName());
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.putClientProperty("pokemon", pokemon);
            button.addActionListener(BuildDeckView.this::onPokemonSelection);
            button.setOpaque(true);

            try {
                ImageIcon sprite = getScaledSprite(pokemon.getSpriteUrl(), 50, 50);
                button.setIcon(sprite);
            } catch (Exception e) {
                // if sprite fails to load, text remains
            }
            return button;
        }

        public void updateSelectionStatus(Deck deck) {
            pokemonGrid.removeAll();

            for (Pokemon pokemon : getUniqueOwnedPokemon()) {
                JButton button = createPokemonButton(pokemon);
                if (deck != null && deck.getPokemons().contains(pokemon)) {
                    button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                    button.setBackground(new Color(170, 200, 255));
                    button.setToolTipText("In Deck - Click to Remove");
                } else {
                    button.setBorder(UIManager.getBorder("Button.border"));
                    button.setBackground(UIManager.getColor("Button.background"));
                    button.setToolTipText("Not In Deck - Click to Add");
                }
                pokemonGrid.add(button);
            }
            pokemonGrid.revalidate();
            pokemonGrid.repaint();
        }

        private List<Pokemon> getUniqueOwnedPokemon() {
            Map<String, Pokemon> unique = new LinkedHashMap<>();
            for (Pokemon pokemon : ownedPokemon) {
                // dedupe by Pokémon ID and shiny status so variants still show separately
                String key = pokemon.getID() + "-" + pokemon.isShiny();
                unique.putIfAbsent(key, pokemon);
            }
            return new ArrayList<>(unique.values());
        }
    }

    private ImageIcon getScaledSprite(String urlString, int width, int height) throws MalformedURLException {
        ImageIcon sprite = new ImageIcon(new URL(urlString));
        sprite.setImage(sprite.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        return sprite;
    }
}
