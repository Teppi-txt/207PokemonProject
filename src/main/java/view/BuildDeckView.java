package view;

import entities.Deck;
import entities.Pokemon;
import entities.User;
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
import java.util.List;

public class BuildDeckView extends JPanel implements PropertyChangeListener {
    private final BuildDeckViewModel viewModel;
    private BuildDeckController controller;

    // Components
    private final JTextField deckNameField;
    private final JLabel deckIdLabel;
    private final DeckDisplayPanel deckDisplayPanel;
    private final OwnedPokemonSelectionPanel selectionPanel;
    private final JLabel errorMessageLabel;
    private final JComboBox<Deck> deckSelector;

    // Local state for the view
    private Deck currentDeck;
    private final List<Pokemon> ownedPokemon;
    private final ActionListener deckSelectListener = this::onDeckSelected;


    public BuildDeckView(BuildDeckViewModel viewModel, User user) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.ownedPokemon = user.getOwnedPokemon();

        // Initialize components
        this.deckNameField = new JTextField(20);
        this.deckIdLabel = new JLabel("ID: --");
        this.deckDisplayPanel = new DeckDisplayPanel();
        this.selectionPanel = new OwnedPokemonSelectionPanel();
        this.errorMessageLabel = new JLabel();
        this.deckSelector = new JComboBox<>();
        this.deckSelector.addActionListener(deckSelectListener);

        // Setup Layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JLabel title = new JLabel("Build Your Deck");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 46));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JButton returnButton = new JButton("Back to menu:");
        returnButton.setFont(new Font(title.getFont().getFontName(), Font.PLAIN, 18));
        returnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnButton.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Name and Controls Panel
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
        controlsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel nameIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameIdPanel.add(new JLabel("Deck Name: "));
        nameIdPanel.add(deckNameField);
        nameIdPanel.add(Box.createHorizontalStrut(15));
        nameIdPanel.add(deckIdLabel); // < Add ID label

        // Deck Management Panel
        JPanel deckManagementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deckManagementPanel.add(new JLabel("Select/New Deck:"));
        deckManagementPanel.add(deckSelector); // < Add selector

        // Action Buttons Panel
        JButton saveButton = new JButton("Save Deck");
        saveButton.addActionListener(this::onSaveDeck);

        JButton newDeckButton = new JButton("New Deck");
        newDeckButton.addActionListener(this::onNewDeck);

        JButton randomButton = new JButton("Randomize Deck");
        randomButton.addActionListener(this::onRandomizeDeck);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(newDeckButton); // < Add new deck button
        actionPanel.add(randomButton);
        actionPanel.add(saveButton);

        controlsPanel.add(deckManagementPanel); // Add Deck Selector/New
        controlsPanel.add(nameIdPanel); // Add Name/ID
        controlsPanel.add(Box.createHorizontalGlue());
        controlsPanel.add(actionPanel);

        // Main Content Panel (Deck Display and Selection)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(deckDisplayPanel);
        contentPanel.add(Box.createHorizontalStrut(20));
        contentPanel.add(selectionPanel);

        // Add all to view
        this.add(title);
        this.add(returnButton);
        this.add(errorMessageLabel);
        this.add(controlsPanel);
        this.add(contentPanel);

        //  Initial Load 
        Deck initialDeck = viewModel.getState().getDeck();
        if (initialDeck == null) {
            // Mock initial deck if the state is empty
            initialDeck = new Deck(-1, "New Deck");
        }
        updateView(initialDeck, viewModel.getState().getAllDecks(), null);
    }

    //  Controller Setter 
    public void setController(BuildDeckController controller) {
        this.controller = controller;
    }

    // Action Listeners
    private void onSaveDeck(ActionEvent e) {
        // Collect current deck info
        // Use the ID of the currently loaded deck (which is an existing ID)
        int deckId = (currentDeck == null) ? -1 : currentDeck.getId();
        String deckName = deckNameField.getText();
        List<Pokemon> pokemons = currentDeck.getPokemons();

        // Execute controller call to save/update the current deck (not random)
        controller.buildDeck(deckId, deckName, pokemons, false);
        // If deckId is 101, the interactor saves Deck 101, it does not call getNextDeckId().
    }

    private void onNewDeck(ActionEvent e) {
        // Clear the current state and trigger a new deck creation in the interactor
        // Use sentinel deckId = -1 and null name/pokémon
        controller.buildDeck(-1, null, null, false);
    }

    private void onRandomizeDeck(ActionEvent e) {
        // Execute controller call to randomly generate a deck
        int deckId = (currentDeck == null) ? -1 : currentDeck.getId();
        String currentName = deckNameField.getText();
        // Pass null for Pokémon list and set isRandom = true
        controller.buildDeck(deckId, currentName, null, true);
    }

    private void onDeckSelected(ActionEvent e) {
        // Get the selected item and cast it back to a Deck object
        Deck selectedDeck = (Deck) deckSelector.getSelectedItem();
        // Prevent triggering unnecessary loads if the selection didn't actually change
        // or if the deck is null/unselectable
        if (selectedDeck != null && (currentDeck == null || selectedDeck.getId() != currentDeck.getId())) {
            // Trigger the controller to load the deck.
            System.out.println("Loading deck ID: " + selectedDeck.getId() + " - " + selectedDeck.getName());
            // Execute the controller call
            controller.buildDeck(selectedDeck.getId(), selectedDeck.getName(), null, false);
        }
    }

    public void onPokemonSelection(ActionEvent e) {
        // Determine the source of the click (JButton from selection panel or JLabel from deck slot)
        Component source = (Component)e.getSource();
        Pokemon selected;
        // Use a conditional check to safely get the client property from the correct component type
        if (source instanceof JButton button) {
            selected = (Pokemon) button.getClientProperty("pokemon");
        } else if (source instanceof JLabel label) {
            selected = (Pokemon) label.getClientProperty("pokemon");
        } else {
            // If the source is neither, ignore the action
            return;
        }
        // Ensure a Pokémon was actually retrieved before proceeding
        if (selected == null) {
            return;
        }
        // Deck modification logic
        if (currentDeck.getPokemons().contains(selected)) {
            // Remove from deck if it's already there
            currentDeck.removePokemon(selected);
        } else {
            // Add to deck (Deck class handles the limit)
            currentDeck.addPokemon(selected);
            System.out.println("Deck size after attempt to add: " + currentDeck.getPokemons().size());
            System.out.println("Pokemon list contents: " + currentDeck.getPokemons());
        }
        // NO controller call here: only update the view state locally and allow the Save button to persist the change.
        // call updateView directly with the locally modified deck
        updateView(currentDeck, viewModel.getState().getAllDecks(), null);
    }

    // PropertyChangeListener implementation

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BuildDeckState state = (BuildDeckState) evt.getNewValue();
        updateView(state.getDeck(), state.getAllDecks(), state.getErrorMessage());
    }

    private void updateView(Deck deck, List<Deck> allDecks, String errorMessage) {
        this.currentDeck = deck;

        // Update Error Message
        if (errorMessage != null) {
            errorMessageLabel.setText("Error: " + errorMessage);
        } else {
            errorMessageLabel.setText("");
        }
        // Update Deck Selector
        updateDeckSelector(allDecks, deck);
        // Update Deck Name Field and ID Label
        if (deck != null) {
            deckNameField.setText(deck.getName()); // <-- Autofill Name
            deckIdLabel.setText("ID: " + deck.getId()); // <-- Update ID
        } else {
            deckNameField.setText("New Deck");
            deckIdLabel.setText("ID: --");
        }
        // Update Deck Display Panel (shows slots)
        deckDisplayPanel.updateDeck(deck);
        // Update Selection Panel (highlights owned Pokémon)
        selectionPanel.updateSelectionStatus(deck);
        this.revalidate();
        this.repaint();
    }

    private void updateDeckSelector(List<Deck> allDecks, Deck currentDeck) {
        // Temporarily remove listener to avoid triggering onDeckSelected during population
        deckSelector.removeActionListener(deckSelectListener);
        deckSelector.removeAllItems();
        if (allDecks != null) {
            for (Deck d : allDecks) {
                deckSelector.addItem(d);
            }
        }
        // Select the current working deck
        if (currentDeck != null) {
            // Iterate over items to find a match by ID (since they are Deck objects, equals/hashCode would be better, but this is a simple check)
            for (int i = 0; i < deckSelector.getItemCount(); i++) {
                if (deckSelector.getItemAt(i).getId() == currentDeck.getId()) {
                    deckSelector.setSelectedIndex(i);
                    break;
                }
            }
            // If the current deck is new and not saved, it won't be in the selector.
        }
        deckSelector.addActionListener(deckSelectListener);
    }
    
    // Components

    /**
     * Panel to display the 5 slots for the currently building deck.
     */
    public class DeckDisplayPanel extends JPanel {

        private final JLabel[] slotLabels;
        private static final int DECK_LIMIT = entities.Deck.DECK_LIMIT;

        public DeckDisplayPanel() {
            this.setLayout(new GridLayout(1, DECK_LIMIT, 10, 0)); // 1 row, 5 columns
            this.setPreferredSize(new Dimension(350, 150));
            this.setBorder(BorderFactory.createTitledBorder("Current Deck (Max " + DECK_LIMIT + ")"));
            this.slotLabels = new JLabel[DECK_LIMIT];

            for (int i = 0; i < DECK_LIMIT; i++) {
                JLabel slot = new JLabel("[Empty Slot " + (i + 1) + "]", SwingConstants.CENTER);
                slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                slot.setOpaque(true);
                slot.setBackground(new Color(240, 240, 240));
                slotLabels[i] = slot;
                this.add(slot);
            }
        }

        public void updateDeck(Deck deck) {
            List<Pokemon> pokemons = deck.getPokemons();
            final int DECK_LIMIT = 5;

            for (int i = 0; i < DECK_LIMIT; i++) {
                JLabel slot = slotLabels[i];
                for(java.awt.event.MouseListener ml : slot.getListeners(java.awt.event.MouseListener.class)) {
                    slot.removeMouseListener(ml);
                }
                if (i < pokemons.size()) {
                    Pokemon p = pokemons.get(i);
                    slot.setText(p.getName()); // set text first
                    try {
                        // fetch and scale the sprite image
                        ImageIcon sprite = getScaledSprite(p.getSpriteUrl(), 60, 60);
                        slot.setIcon(sprite);
                    } catch (Exception e) {
                        // handle image loading failure
                        slot.setIcon(null);
                    }
                    slot.setToolTipText(p.getName() + " (Click to remove)");
                    slot.setOpaque(true);
                    slot.setBackground(new Color(200, 255, 200)); // light green for filled slot
                    slot.putClientProperty("pokemon", p); // store Pokémon for removal logic

                    slot.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            JLabel clickedSlot = (JLabel) e.getSource();
                            Pokemon removedPokemon = (Pokemon) clickedSlot.getClientProperty("pokemon");
                            if (removedPokemon != null) {
                                BuildDeckView.this.onPokemonSelection(new ActionEvent(clickedSlot, ActionEvent.ACTION_PERFORMED, "remove"));
                            }
                        }
                    });

                } else {
                    // empty slot logic
                    slot.setIcon(null);
                    slot.setText("[Empty Slot " + (i + 1) + "]");
                    slot.setToolTipText(null);
                    slot.setBackground(new Color(240, 240, 240));
                    slot.putClientProperty("pokemon", null);
                }
            }
            this.revalidate();
            this.repaint();
        }
    }

    /**
     * Panel to display owned Pokémon for selection.
     */
    public class OwnedPokemonSelectionPanel extends JPanel {

        private final JPanel pokemonGrid;

        public OwnedPokemonSelectionPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createTitledBorder("Owned Pokémon (Click to Add/Remove)"));

            // Use 0 rows for flexible vertical size, 5 columns
            pokemonGrid = new JPanel(new GridLayout(0, 1, 5, 5));

            // Populate initial buttons
            for (Pokemon pokemon : ownedPokemon) {
                pokemonGrid.add(createPokemonButton(pokemon));
            }

            JScrollPane scrollPane = new JScrollPane(pokemonGrid);
            scrollPane.setPreferredSize(new Dimension(350, 300));
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            this.add(scrollPane);
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
            for (Component component : pokemonGrid.getComponents()) {
                if (component instanceof JButton button) {
                    Pokemon pokemon = (Pokemon) button.getClientProperty("pokemon");
                    if (deck.getPokemons().contains(pokemon)) {
                        // highlight pokémon button if in deck
                        button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                        button.setBackground(new Color(170, 200, 255));
                        button.setToolTipText("In Deck - Click to Remove");
                    } else {
                        // normal appearance if not in deck
                        button.setBorder(UIManager.getBorder("Button.border"));
                        // reset background to default
                        button.setBackground(UIManager.getColor("Button.background"));
                        button.setToolTipText("Not In Deck - Click to Add");
                    }
                }
            }
            pokemonGrid.revalidate();
            pokemonGrid.repaint();
        }
    }

    private ImageIcon getScaledSprite(String urlString, int width, int height) throws MalformedURLException {
        ImageIcon sprite = new ImageIcon(new URL(urlString));
        sprite.setImage(sprite.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        return sprite;
    }
}