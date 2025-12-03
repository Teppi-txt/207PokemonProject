package frameworks_and_drivers.deck;

import entities.battle.Deck;
import entities.Pokemon;
import entities.user.User;
import frameworks_and_drivers.battle.BattleAIView;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIDataAccessObject;
import interface_adapters.battle_ai.BattleAIViewModel;
import frameworks_and_drivers.moveset.BattleMovesetSelectionView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View for selecting a pre-built deck before battle.
 * Replaces manual Pokemon selection with deck-based selection.
 */
public class DeckSelectionForBattleView extends JFrame {

    private final BattleAIController controller;
    private final BattleAIDataAccessObject dataAccess;
    private final BattleAIViewModel viewModel;
    private final User user;
    private final Runnable returnCallback;

    private JPanel deckListPanel;
    private JPanel deckPreviewPanel;
    private JLabel deckPreviewHeader;
    private JButton startButton;
    private final JComboBox<String> difficultyCombo;

    private Deck selectedDeck;

    public DeckSelectionForBattleView(BattleAIController controller, BattleAIDataAccessObject dataAccess,
                                       BattleAIViewModel viewModel, User user, Runnable returnCallback) {
        this.controller = controller;
        this.dataAccess = dataAccess;
        this.viewModel = viewModel;
        this.user = user;
        this.returnCallback = returnCallback;

        setTitle("Select Your Battle Deck");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        mainPanel.add(createDeckListPanel(), BorderLayout.CENTER);
        mainPanel.add(createPreviewPanel(), BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel diffPanel = (JPanel) bottomPanel.getComponent(0);
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        diffPanel.add(diffLabel);
        diffPanel.add(difficultyCombo);

        refreshDeckList();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("SELECT YOUR BATTLE DECK");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title);

        return panel;
    }

    private JPanel createDeckListPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("YOUR DECKS");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        container.add(headerLabel, BorderLayout.NORTH);

        deckListPanel = new JPanel();
        deckListPanel.setLayout(new BoxLayout(deckListPanel, BoxLayout.Y_AXIS));
        deckListPanel.setBackground(Color.WHITE);
        deckListPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JScrollPane scrollPane = new JScrollPane(deckListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createPreviewPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setPreferredSize(new Dimension(280, 0));
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        deckPreviewHeader = new JLabel("DECK PREVIEW");
        deckPreviewHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        container.add(deckPreviewHeader, BorderLayout.NORTH);

        deckPreviewPanel = new JPanel();
        deckPreviewPanel.setLayout(new BoxLayout(deckPreviewPanel, BoxLayout.Y_AXIS));
        deckPreviewPanel.setBackground(Color.WHITE);
        deckPreviewPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel emptyLabel = new JLabel("Select a deck to preview");
        emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        emptyLabel.setForeground(new Color(128, 128, 128));
        deckPreviewPanel.add(emptyLabel);

        container.add(deckPreviewPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("BACK");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        backButton.addActionListener(e -> {
            dispose();
            if (returnCallback != null) returnCallback.run();
        });
        leftPanel.add(backButton);

        panel.add(leftPanel, BorderLayout.WEST);

        startButton = new JButton("START BATTLE");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startBattle());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(startButton);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void refreshDeckList() {
        deckListPanel.removeAll();

        List<Deck> decks = new ArrayList<>(user.getDecks().values());

        if (decks.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));

            JLabel emptyLabel = new JLabel("No decks found!");
            emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyPanel.add(emptyLabel);

            emptyPanel.add(Box.createVerticalStrut(10));

            JLabel helpLabel = new JLabel("Build a deck first from the main menu.");
            helpLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            helpLabel.setForeground(new Color(100, 100, 100));
            helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyPanel.add(helpLabel);

            deckListPanel.add(emptyPanel);
        } else {
            for (Deck deck : decks) {
                JPanel deckCard = createDeckCard(deck);
                deckListPanel.add(deckCard);
                deckListPanel.add(Box.createVerticalStrut(10));
            }
        }

        deckListPanel.revalidate();
        deckListPanel.repaint();
    }

    private JPanel createDeckCard(Deck deck) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Left side: deck info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(deck.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        infoPanel.add(nameLabel);

        int pokemonCount = deck.getPokemons() != null ? deck.getPokemons().size() : 0;
        JLabel countLabel = new JLabel(pokemonCount + " Pokemon");
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        countLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(countLabel);

        card.add(infoPanel, BorderLayout.WEST);

        // Right side: Pokemon sprites
        JPanel spritesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        spritesPanel.setBackground(Color.WHITE);

        if (deck.getPokemons() != null) {
            int displayCount = Math.min(3, deck.getPokemons().size());
            for (int i = 0; i < displayCount; i++) {
                Pokemon pokemon = deck.getPokemons().get(i);
                JLabel spriteLabel = new JLabel();
                spriteLabel.setPreferredSize(new Dimension(50, 50));
                try {
                    ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
                    Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    spriteLabel.setIcon(new ImageIcon(scaled));
                } catch (Exception ignored) {
                    spriteLabel.setText("?");
                }
                spritesPanel.add(spriteLabel);
            }
        }

        card.add(spritesPanel, BorderLayout.EAST);

        // Selection button
        JButton selectBtn = new JButton("SELECT");
        selectBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        selectBtn.setFocusPainted(false);

        boolean hasEnoughPokemon = deck.getPokemons() != null && deck.getPokemons().size() >= 3;
        selectBtn.setEnabled(hasEnoughPokemon);

        if (!hasEnoughPokemon) {
            selectBtn.setToolTipText("Deck needs at least 3 Pokemon");
        }

        selectBtn.addActionListener(e -> selectDeck(deck));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(selectBtn);
        card.add(buttonPanel, BorderLayout.CENTER);

        return card;
    }

    private void selectDeck(Deck deck) {
        this.selectedDeck = deck;
        updatePreview();
        startButton.setEnabled(true);
    }

    private void updatePreview() {
        deckPreviewPanel.removeAll();

        if (selectedDeck == null) {
            JLabel emptyLabel = new JLabel("Select a deck to preview");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            emptyLabel.setForeground(new Color(128, 128, 128));
            deckPreviewPanel.add(emptyLabel);
        } else {
            deckPreviewHeader.setText("DECK: " + selectedDeck.getName());

            JLabel infoLabel = new JLabel("First 3 Pokemon will battle:");
            infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            infoLabel.setForeground(new Color(100, 100, 100));
            infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            deckPreviewPanel.add(infoLabel);
            deckPreviewPanel.add(Box.createVerticalStrut(10));

            int displayCount = Math.min(3, selectedDeck.getPokemons().size());
            for (int i = 0; i < displayCount; i++) {
                Pokemon pokemon = selectedDeck.getPokemons().get(i);
                JPanel row = createPreviewRow(pokemon, i + 1);
                deckPreviewPanel.add(row);
                deckPreviewPanel.add(Box.createVerticalStrut(8));
            }
        }

        deckPreviewPanel.revalidate();
        deckPreviewPanel.repaint();
    }

    private JPanel createPreviewRow(Pokemon pokemon, int index) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(new Color(248, 248, 248));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(250, 60));

        // Sprite
        JLabel spriteLabel = new JLabel();
        spriteLabel.setPreferredSize(new Dimension(40, 40));
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            spriteLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {
            spriteLabel.setText("?");
        }
        row.add(spriteLabel);
        row.add(Box.createHorizontalStrut(10));

        // Name and stats
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(index + ". " + capitalize(pokemon.getName()));
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        infoPanel.add(nameLabel);

        int totalStats = pokemon.getStats().getTotalStats();
        JLabel statsLabel = new JLabel("Total: " + totalStats);
        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        statsLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(statsLabel);

        row.add(infoPanel);

        return row;
    }

    private void startBattle() {
        if (selectedDeck == null || selectedDeck.getPokemons().size() < 3) {
            JOptionPane.showMessageDialog(this,
                "Please select a deck with at least 3 Pokemon!",
                "Invalid Deck",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String difficulty = ((String) difficultyCombo.getSelectedItem()).toLowerCase();

        // Use first 3 Pokemon from the deck
        List<Pokemon> battleDeck = new ArrayList<>();
        for (int i = 0; i < 3 && i < selectedDeck.getPokemons().size(); i++) {
            battleDeck.add(selectedDeck.getPokemons().get(i).copy());
        }

        Runnable onMovesetComplete = () -> {
            // Set up battle via controller (proper Clean Architecture)
            controller.setupBattle(user, battleDeck, difficulty);

            // Create battle view
            BattleAIView battleView = new BattleAIView(controller, viewModel, returnCallback);
            battleView.setVisible(true);
        };

        Runnable onMovesetCancel = () -> this.setVisible(true);

        BattleMovesetSelectionView movesetView = new BattleMovesetSelectionView(
            battleDeck, onMovesetComplete, onMovesetCancel
        );
        movesetView.setVisible(true);
        this.setVisible(false);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
