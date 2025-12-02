package frameworks_and_drivers;

import app.battle.BattlePlayerFactory;
import entities.*;
import entities.battle.Battle;
import entities.battle.Deck;
import entities.user.User;
import view.BattleMovesetSelectionView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Deck-based battle setup view for Player vs Player battles.
 * Players select from pre-built decks instead of manually picking Pokemon.
 */
public class BattleSetupDeckView extends JFrame {

    private final User user;
    private final Runnable returnCallback;

    private Deck player1SelectedDeck;
    private Deck player2SelectedDeck;

    private JPanel player1DeckPanel;
    private JLabel player1DeckHeader;
    private JTextField player1NameField;

    private JPanel player2DeckPanel;
    private JLabel player2DeckHeader;
    private JTextField player2NameField;

    private JButton startButton;

    public BattleSetupDeckView(User user, Runnable returnCallback) {
        this.user = user;
        this.returnCallback = returnCallback;

        setTitle("Battle Setup - Player vs Player");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 15, 20)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PLAYER VS PLAYER BATTLE SETUP");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Each player selects a deck for battle");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        mainPanel.add(createPlayerPanel(true));
        mainPanel.add(createDeckListPanel());
        mainPanel.add(createPlayerPanel(false));

        return mainPanel;
    }

    private JPanel createPlayerPanel(boolean isPlayer1) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header
        JLabel playerLabel = new JLabel(isPlayer1 ? "PLAYER 1" : "PLAYER 2");
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        container.add(playerLabel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameLabel);

        contentPanel.add(Box.createVerticalStrut(3));

        JTextField nameField = new JTextField(isPlayer1 ? "Player 1" : "Player 2");
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isPlayer1) player1NameField = nameField;
        else player2NameField = nameField;

        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(15));

        // Deck header
        JLabel deckHeader = new JLabel("SELECTED DECK: None");
        deckHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        deckHeader.setForeground(new Color(100, 100, 100));
        deckHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(deckHeader);

        if (isPlayer1) player1DeckHeader = deckHeader;
        else player2DeckHeader = deckHeader;

        contentPanel.add(Box.createVerticalStrut(5));

        // Deck preview panel
        JPanel deckPanel = new JPanel();
        deckPanel.setLayout(new BoxLayout(deckPanel, BoxLayout.Y_AXIS));
        deckPanel.setBackground(new Color(248, 248, 248));
        deckPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        deckPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emptyLabel = new JLabel("No deck selected");
        emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        emptyLabel.setForeground(new Color(128, 128, 128));
        deckPanel.add(emptyLabel);

        if (isPlayer1) player1DeckPanel = deckPanel;
        else player2DeckPanel = deckPanel;

        contentPanel.add(deckPanel);
        contentPanel.add(Box.createVerticalGlue());

        container.add(contentPanel, BorderLayout.CENTER);
        return container;
    }

    private JPanel createDeckListPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel headerLabel = new JLabel("AVAILABLE DECKS");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        container.add(headerLabel, BorderLayout.NORTH);

        JPanel deckListPanel = new JPanel();
        deckListPanel.setLayout(new BoxLayout(deckListPanel, BoxLayout.Y_AXIS));
        deckListPanel.setBackground(Color.WHITE);

        List<Deck> decks = new ArrayList<>(user.getDecks().values());

        if (decks.isEmpty()) {
            JLabel emptyLabel = new JLabel("No decks available!");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            deckListPanel.add(emptyLabel);

            JLabel helpLabel = new JLabel("Build a deck from the main menu first.");
            helpLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            helpLabel.setForeground(new Color(100, 100, 100));
            helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            deckListPanel.add(Box.createVerticalStrut(5));
            deckListPanel.add(helpLabel);
        } else {
            for (Deck deck : decks) {
                JPanel deckCard = createDeckCard(deck);
                deckListPanel.add(deckCard);
                deckListPanel.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scrollPane = new JScrollPane(deckListPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createDeckCard(Deck deck) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Left side: deck info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(deck.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        infoPanel.add(nameLabel);

        int pokemonCount = deck.getPokemons() != null ? deck.getPokemons().size() : 0;
        JLabel countLabel = new JLabel(pokemonCount + " Pokemon");
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        countLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(countLabel);

        card.add(infoPanel, BorderLayout.WEST);

        // Right side: selection buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        boolean hasEnoughPokemon = deck.getPokemons() != null && deck.getPokemons().size() >= 3;

        JButton p1Button = new JButton("P1");
        p1Button.setFont(new Font("SansSerif", Font.BOLD, 10));
        p1Button.setFocusPainted(false);
        p1Button.setEnabled(hasEnoughPokemon);
        p1Button.setToolTipText(hasEnoughPokemon ? "Select for Player 1" : "Deck needs at least 3 Pokemon");
        p1Button.addActionListener(e -> selectDeckForPlayer(deck, true));
        buttonPanel.add(p1Button);

        JButton p2Button = new JButton("P2");
        p2Button.setFont(new Font("SansSerif", Font.BOLD, 10));
        p2Button.setFocusPainted(false);
        p2Button.setEnabled(hasEnoughPokemon);
        p2Button.setToolTipText(hasEnoughPokemon ? "Select for Player 2" : "Deck needs at least 3 Pokemon");
        p2Button.addActionListener(e -> selectDeckForPlayer(deck, false));
        buttonPanel.add(p2Button);

        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void selectDeckForPlayer(Deck deck, boolean isPlayer1) {
        if (isPlayer1) {
            player1SelectedDeck = deck;
            updatePlayerDeckPreview(player1DeckHeader, player1DeckPanel, deck);
        } else {
            player2SelectedDeck = deck;
            updatePlayerDeckPreview(player2DeckHeader, player2DeckPanel, deck);
        }
        updateStartButton();
    }

    private void updatePlayerDeckPreview(JLabel header, JPanel panel, Deck deck) {
        header.setText("SELECTED: " + deck.getName());
        panel.removeAll();

        int displayCount = Math.min(3, deck.getPokemons().size());
        for (int i = 0; i < displayCount; i++) {
            Pokemon pokemon = deck.getPokemons().get(i);
            JPanel row = createPokemonPreviewRow(pokemon, i + 1);
            panel.add(row);
            if (i < displayCount - 1) {
                panel.add(Box.createVerticalStrut(5));
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createPokemonPreviewRow(Pokemon pokemon, int index) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sprite
        JLabel spriteLabel = new JLabel();
        spriteLabel.setPreferredSize(new Dimension(30, 30));
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            spriteLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {
            spriteLabel.setText("?");
        }
        row.add(spriteLabel);
        row.add(Box.createHorizontalStrut(5));

        // Name
        JLabel nameLabel = new JLabel(index + ". " + capitalize(pokemon.getName()));
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        row.add(nameLabel);

        return row;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JButton backBtn = new JButton("BACK");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        backBtn.addActionListener(e -> {
            dispose();
            if (returnCallback != null) returnCallback.run();
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(backBtn);
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

    private void updateStartButton() {
        boolean player1Ready = player1SelectedDeck != null &&
                player1SelectedDeck.getPokemons() != null &&
                player1SelectedDeck.getPokemons().size() >= 3;
        boolean player2Ready = player2SelectedDeck != null &&
                player2SelectedDeck.getPokemons() != null &&
                player2SelectedDeck.getPokemons().size() >= 3;
        startButton.setEnabled(player1Ready && player2Ready);
    }

    private void startBattle() {
        String player1Name = player1NameField.getText().trim();
        String player2Name = player2NameField.getText().trim();

        if (player1Name.isEmpty()) player1Name = "Player 1";
        if (player2Name.isEmpty()) player2Name = "Player 2";

        final String p1Name = player1Name;
        final String p2Name = player2Name;

        // Copy first 3 Pokemon from each deck
        List<Pokemon> player1Deck = new ArrayList<>();
        List<Pokemon> player2Deck = new ArrayList<>();

        for (int i = 0; i < 3 && i < player1SelectedDeck.getPokemons().size(); i++) {
            player1Deck.add(player1SelectedDeck.getPokemons().get(i).copy());
        }
        for (int i = 0; i < 3 && i < player2SelectedDeck.getPokemons().size(); i++) {
            player2Deck.add(player2SelectedDeck.getPokemons().get(i).copy());
        }

        Runnable onPlayer1Complete = () -> {
            Runnable onPlayer2Complete = () -> startActualBattle(p1Name, p2Name, player1Deck, player2Deck);
            Runnable onPlayer2Cancel = () -> this.setVisible(true);

            BattleMovesetSelectionView player2MovesetView = new BattleMovesetSelectionView(
                player2Deck, onPlayer2Complete, onPlayer2Cancel
            );
            player2MovesetView.setTitle(p2Name + " - Select Moves");
            player2MovesetView.setVisible(true);
        };

        Runnable onPlayer1Cancel = () -> this.setVisible(true);

        BattleMovesetSelectionView player1MovesetView = new BattleMovesetSelectionView(
            player1Deck, onPlayer1Complete, onPlayer1Cancel
        );
        player1MovesetView.setTitle(p1Name + " - Select Moves");
        player1MovesetView.setVisible(true);
        this.setVisible(false);
    }

    private void startActualBattle(String player1Name, String player2Name,
                                   List<Pokemon> player1Deck, List<Pokemon> player2Deck) {
        User user1 = new User(1, player1Name, "player1@battle.com", 0);
        User user2 = new User(2, player2Name, "player2@battle.com", 0);

        for (Pokemon p : player1Deck) user1.addPokemon(p);
        for (Pokemon p : player2Deck) user2.addPokemon(p);

        Battle battle = new Battle(1, user1, user2);
        battle.startBattle();

        Runnable playAgain = () -> {
            if (returnCallback != null) returnCallback.run();
        };

        BattlePlayerView battleView = BattlePlayerFactory.createBattleView(battle, playAgain);

        battleView.setVisible(true);
        dispose();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
