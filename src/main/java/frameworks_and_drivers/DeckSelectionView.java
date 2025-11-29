package frameworks_and_drivers;

import entities.Pokemon;
import entities.User;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View for selecting Pokemon deck before battle.
 * Displays user's owned Pokemon as cards with sprites.
 */
public class DeckSelectionView extends JFrame {

    private final BattleAIController controller;
    private final User user;
    private final List<Pokemon> selectedDeck;
    private final JPanel selectedPanel;
    private final JButton startButton;
    private final JComboBox<String> difficultyCombo;
    private Runnable returnCallback;

    public DeckSelectionView(BattleAIController controller, User user) {
        this(controller, user, null);
    }

    public DeckSelectionView(BattleAIController controller, User user, Runnable returnCallback) {
        this.controller = controller;
        this.user = user;
        this.selectedDeck = new ArrayList<>();
        this.returnCallback = returnCallback;

        setTitle("Select Your Battle Team");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Main Content - Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);

        // Left: Pokemon Collection
        JPanel collectionPanel = createCollectionPanel();
        splitPane.setLeftComponent(collectionPanel);

        // Right: Selected Deck
        selectedPanel = createSelectedPanel();
        splitPane.setRightComponent(selectedPanel);

        add(splitPane, BorderLayout.CENTER);

        // Bottom Panel - Back, Difficulty and Start Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(UIStyleConstants.BACKGROUND);

        // Back to Menu button
        StyledButton backButton = new StyledButton("Back to Menu");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> {
            dispose();
            if (returnCallback != null) {
                returnCallback.run();
            }
        });
        bottomPanel.add(backButton);

        bottomPanel.add(new JLabel("Difficulty:"));
        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        bottomPanel.add(difficultyCombo);

        startButton = new StyledButton("START BATTLE", UIStyleConstants.SECONDARY_COLOR);
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startBattle());
        bottomPanel.add(startButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyleConstants.PRIMARY_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("⚔ SELECT YOUR BATTLE TEAM");
        title.setFont(UIStyleConstants.TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        JLabel subtitle = new JLabel(user.getName() + " | Currency: " + user.getCurrency());
        subtitle.setFont(UIStyleConstants.BODY_FONT);
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        return panel;
    }

    private JPanel createCollectionPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.BACKGROUND);

        JLabel header = new JLabel("Your Pokemon");
        header.setFont(UIStyleConstants.HEADING_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        container.add(header, BorderLayout.NORTH);

        // Grid of Pokemon cards
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        gridPanel.setBackground(UIStyleConstants.BACKGROUND);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add Pokemon cards
        List<Pokemon> ownedPokemon = user.getOwnedPokemon();
        if (ownedPokemon.isEmpty()) {
            JLabel emptyLabel = new JLabel("No Pokemon! Open a pack first.");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.add(emptyLabel);
        } else {
            for (Pokemon pokemon : ownedPokemon) {
                PokemonCard card = new PokemonCard(pokemon);
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        addToDeck(pokemon);
                    }
                });
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                gridPanel.add(card);
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createSelectedPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.BACKGROUND);

        JLabel header = new JLabel("Battle Deck (0/3)");
        header.setFont(UIStyleConstants.HEADING_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        container.add(header, BorderLayout.NORTH);

        JPanel deckPanel = new JPanel();
        deckPanel.setLayout(new BoxLayout(deckPanel, BoxLayout.Y_AXIS));
        deckPanel.setBackground(UIStyleConstants.BACKGROUND);
        deckPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        container.add(deckPanel, BorderLayout.CENTER);

        return container;
    }

    private void addToDeck(Pokemon pokemon) {
        if (selectedDeck.size() >= 3) {
            JOptionPane.showMessageDialog(this, "Deck is full! (Maximum 3 Pokemon)",
                    "Deck Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedDeck.contains(pokemon)) {
            JOptionPane.showMessageDialog(this, "This Pokemon is already in your deck!",
                    "Already Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedDeck.add(pokemon);
        updateSelectedPanel();
    }

    private void updateSelectedPanel() {
        // Update header
        Component[] components = selectedPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText("Battle Deck (" + selectedDeck.size() + "/3)");
                break;
            }
        }

        // Update deck display
        JPanel deckPanel = (JPanel) selectedPanel.getComponent(1);
        deckPanel.removeAll();

        for (int i = 0; i < selectedDeck.size(); i++) {
            Pokemon pokemon = selectedDeck.get(i);
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(300, 80));

            JLabel nameLabel = new JLabel((i + 1) + ". " + capitalize(pokemon.getName()));
            nameLabel.setFont(UIStyleConstants.BODY_FONT);

            StyledButton removeBtn = new StyledButton("Remove");
            removeBtn.setPreferredSize(new Dimension(80, 30));
            int index = i;
            removeBtn.addActionListener(e -> {
                selectedDeck.remove(index);
                updateSelectedPanel();
            });

            row.add(nameLabel, BorderLayout.CENTER);
            row.add(removeBtn, BorderLayout.EAST);
            deckPanel.add(row);
            deckPanel.add(Box.createVerticalStrut(10));
        }

        deckPanel.revalidate();
        deckPanel.repaint();

        // Enable/disable start button
        startButton.setEnabled(selectedDeck.size() == 3);
    }

    private void startBattle() {
        String difficulty = ((String) difficultyCombo.getSelectedItem()).toLowerCase();
        controller.startBattle(user, selectedDeck, difficulty);

        // Open battle view with return callback
        BattleAIView battleView = new BattleAIView(controller, returnCallback);
        battleView.setViewModel(controller.getViewModel());
        battleView.setVisible(true);
        dispose();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
