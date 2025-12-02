package frameworks_and_drivers;

import app.battle.BattlePlayerFactory;
import entities.*;
import entities.battle.Battle;
import entities.user.User;
import view.BattleMovesetSelectionView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple battle setup view for Player vs Player battles.
 */
public class BattleSetupViewIntegrated extends JFrame {

    private final User user;
    private final Runnable returnCallback;

    private final List<Pokemon> player1Deck = new ArrayList<>();
    private JPanel player1DeckPanel;
    private JLabel player1DeckHeader;
    private JTextField player1NameField;

    private final List<Pokemon> player2Deck = new ArrayList<>();
    private JPanel player2DeckPanel;
    private JLabel player2DeckHeader;
    private JTextField player2NameField;

    private boolean selectingForPlayer1 = true;
    private JLabel selectionIndicator;
    private JButton startButton;

    public BattleSetupViewIntegrated(User user, Runnable returnCallback) {
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

        selectionIndicator = new JLabel("Click Pokemon to add to PLAYER 1's deck");
        selectionIndicator.setFont(new Font("SansSerif", Font.PLAIN, 14));
        selectionIndicator.setForeground(new Color(100, 100, 100));
        selectionIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(selectionIndicator);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        mainPanel.add(createPlayerPanel(true), BorderLayout.WEST);
        mainPanel.add(createPokemonCollectionPanel(), BorderLayout.CENTER);
        mainPanel.add(createPlayerPanel(false), BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createPlayerPanel(boolean isPlayer1) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setPreferredSize(new Dimension(200, 0));
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

        // Name field - stacked vertically
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameLabel);

        contentPanel.add(Box.createVerticalStrut(3));

        JTextField nameField = new JTextField(isPlayer1 ? "Player 1" : "Player 2");
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameField.setMaximumSize(new Dimension(170, 25));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isPlayer1) player1NameField = nameField;
        else player2NameField = nameField;

        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(10));

        // Select button
        JButton selectBtn = new JButton("SELECT FOR " + (isPlayer1 ? "P1" : "P2"));
        selectBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        selectBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectBtn.setFocusPainted(false);
        selectBtn.addActionListener(e -> {
            selectingForPlayer1 = isPlayer1;
            selectionIndicator.setText("Click Pokemon to add to " + (isPlayer1 ? "PLAYER 1" : "PLAYER 2") + "'s deck");
        });
        contentPanel.add(selectBtn);
        contentPanel.add(Box.createVerticalStrut(15));

        // Deck header
        JLabel deckHeader = new JLabel("DECK (0/3)");
        deckHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        deckHeader.setForeground(new Color(100, 100, 100));
        contentPanel.add(deckHeader);

        if (isPlayer1) player1DeckHeader = deckHeader;
        else player2DeckHeader = deckHeader;

        contentPanel.add(Box.createVerticalStrut(5));

        // Deck panel
        JPanel deckPanel = new JPanel();
        deckPanel.setLayout(new BoxLayout(deckPanel, BoxLayout.Y_AXIS));
        deckPanel.setBackground(Color.WHITE);
        contentPanel.add(deckPanel);

        if (isPlayer1) player1DeckPanel = deckPanel;
        else player2DeckPanel = deckPanel;

        contentPanel.add(Box.createVerticalGlue());

        // Clear button
        JButton clearBtn = new JButton("CLEAR");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> {
            if (isPlayer1) {
                player1Deck.clear();
                updateDeckPanel(player1DeckPanel, player1Deck, player1DeckHeader, true);
            } else {
                player2Deck.clear();
                updateDeckPanel(player2DeckPanel, player2Deck, player2DeckHeader, false);
            }
            updateStartButton();
        });
        contentPanel.add(clearBtn);

        container.add(contentPanel, BorderLayout.CENTER);
        return container;
    }

    private JPanel createPokemonCollectionPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("YOUR POKEMON (Click to Add)");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        container.add(headerLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Pokemon> ownedPokemon = user.getOwnedPokemon();
        if (ownedPokemon.isEmpty()) {
            JLabel emptyLabel = new JLabel("No Pokemon available!");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            gridPanel.add(emptyLabel);
        } else {
            for (Pokemon pokemon : ownedPokemon) {
                JPanel card = createPokemonCard(pokemon);
                gridPanel.add(card);
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Pokemon sprite
        JLabel imgLabel = new JLabel();
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(70, 70));
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {
            imgLabel.setText("?");
            imgLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        }
        card.add(imgLabel);

        // Pokemon name
        JLabel nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        card.add(Box.createVerticalStrut(5));

        // Add button
        JButton addBtn = new JButton("ADD");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 10));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addPokemonToCurrentPlayer(pokemon));
        card.add(addBtn);

        return card;
    }

    private void addPokemonToCurrentPlayer(Pokemon pokemon) {
        List<Pokemon> targetDeck = selectingForPlayer1 ? player1Deck : player2Deck;
        JPanel targetPanel = selectingForPlayer1 ? player1DeckPanel : player2DeckPanel;
        JLabel targetHeader = selectingForPlayer1 ? player1DeckHeader : player2DeckHeader;

        if (targetDeck.size() >= 3) {
            JOptionPane.showMessageDialog(this, "Deck is full! (Maximum 3 Pokemon)",
                    "Deck Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (player1Deck.contains(pokemon) || player2Deck.contains(pokemon)) {
            JOptionPane.showMessageDialog(this, "This Pokemon is already selected!",
                    "Already Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pokemon pokemonCopy = pokemon.copy();
        targetDeck.add(pokemonCopy);
        updateDeckPanel(targetPanel, targetDeck, targetHeader, selectingForPlayer1);
        updateStartButton();
    }

    private void updateDeckPanel(JPanel panel, List<Pokemon> deck, JLabel header, boolean isPlayer1) {
        panel.removeAll();
        header.setText("DECK (" + deck.size() + "/3)");

        for (int i = 0; i < deck.size(); i++) {
            Pokemon pokemon = deck.get(i);
            JPanel row = createDeckRow(pokemon, i, deck, panel, header, isPlayer1);
            panel.add(row);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createDeckRow(Pokemon pokemon, int index, List<Pokemon> deck, JPanel panel, JLabel header, boolean isPlayer1) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(new Color(248, 248, 248));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 8, 5, 5)
        ));
        row.setMaximumSize(new Dimension(170, 30));

        JLabel nameLabel = new JLabel((index + 1) + ". " + capitalize(pokemon.getName()));
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        row.add(nameLabel);
        row.add(Box.createHorizontalGlue());

        JButton removeBtn = new JButton("X");
        removeBtn.setFont(new Font("SansSerif", Font.BOLD, 9));
        removeBtn.setMargin(new Insets(0, 0, 0, 0));
        removeBtn.setPreferredSize(new Dimension(22, 20));
        removeBtn.setMinimumSize(new Dimension(22, 20));
        removeBtn.setMaximumSize(new Dimension(22, 20));
        removeBtn.setFocusPainted(false);
        removeBtn.addActionListener(e -> {
            deck.remove(index);
            updateDeckPanel(panel, deck, header, isPlayer1);
            updateStartButton();
        });
        row.add(removeBtn);

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
        startButton.setEnabled(player1Deck.size() == 3 && player2Deck.size() == 3);
    }

    private void startBattle() {
        String player1Name = player1NameField.getText().trim();
        String player2Name = player2NameField.getText().trim();

        if (player1Name.isEmpty()) player1Name = "Player 1";
        if (player2Name.isEmpty()) player2Name = "Player 2";

        final String p1Name = player1Name;
        final String p2Name = player2Name;

        Runnable onPlayer1Complete = () -> {
            Runnable onPlayer2Complete = () -> startActualBattle(p1Name, p2Name);
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

    private void startActualBattle(String player1Name, String player2Name) {
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
