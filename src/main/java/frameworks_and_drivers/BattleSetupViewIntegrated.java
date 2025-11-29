package frameworks_and_drivers;

import entities.*;
import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerPresenter;
import interface_adapters.battle_player.BattlePlayerViewModel;
import interface_adapters.ui.*;
import use_case.battle_player.BattlePlayerInteractor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Battle setup view that uses the User's actual Pokemon instead of hardcoded decks.
 * Allows two players to select Pokemon from the user's collection for battle.
 */
public class BattleSetupViewIntegrated extends JFrame {

    private final User user;
    private final Runnable returnCallback;

    // Player 1 selection
    private final List<Pokemon> player1Deck = new ArrayList<>();
    private JPanel player1DeckPanel;
    private JTextField player1NameField;

    // Player 2 selection
    private final List<Pokemon> player2Deck = new ArrayList<>();
    private JPanel player2DeckPanel;
    private JTextField player2NameField;

    // Currently selecting for which player
    private boolean selectingForPlayer1 = true;

    private JButton startButton;

    public BattleSetupViewIntegrated(User user, Runnable returnCallback) {
        this.user = user;
        this.returnCallback = returnCallback;

        setTitle("Battle Setup - Player vs Player");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title Panel
        add(createTitlePanel(), BorderLayout.NORTH);

        // Main content - split into 3 columns
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(UIStyleConstants.BACKGROUND);

        // Left: Player 1 deck selection
        mainPanel.add(createPlayerPanel(true));

        // Center: Pokemon collection to choose from
        mainPanel.add(createPokemonCollectionPanel());

        // Right: Player 2 deck selection
        mainPanel.add(createPlayerPanel(false));

        add(mainPanel, BorderLayout.CENTER);

        // Bottom: Start Battle button
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyleConstants.PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("PLAYER VS PLAYER BATTLE SETUP");
        title.setFont(UIStyleConstants.TITLE_FONT);
        title.setForeground(Color.WHITE);
        panel.add(title);

        return panel;
    }

    private JPanel createPlayerPanel(boolean isPlayer1) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(isPlayer1 ? "Player 1" : "Player 2"));
        panel.setBackground(isPlayer1 ? new Color(255, 240, 240) : new Color(240, 240, 255));

        // Name field
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setOpaque(false);
        namePanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(isPlayer1 ? "Player 1" : "Player 2", 12);
        namePanel.add(nameField);
        panel.add(namePanel);

        if (isPlayer1) {
            player1NameField = nameField;
        } else {
            player2NameField = nameField;
        }

        // Select button
        JButton selectBtn = new StyledButton("Select for " + (isPlayer1 ? "Player 1" : "Player 2"));
        selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectBtn.addActionListener(e -> {
            selectingForPlayer1 = isPlayer1;
            JOptionPane.showMessageDialog(this,
                "Click on Pokemon in the center panel to add to " + (isPlayer1 ? "Player 1" : "Player 2") + "'s deck",
                "Selection Mode", JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(selectBtn);
        panel.add(Box.createVerticalStrut(10));

        // Deck display
        JPanel deckPanel = new JPanel();
        deckPanel.setLayout(new BoxLayout(deckPanel, BoxLayout.Y_AXIS));
        deckPanel.setBackground(Color.WHITE);
        deckPanel.setBorder(new TitledBorder("Deck (0/3)"));
        panel.add(deckPanel);

        if (isPlayer1) {
            player1DeckPanel = deckPanel;
        } else {
            player2DeckPanel = deckPanel;
        }

        // Clear button
        panel.add(Box.createVerticalStrut(10));
        JButton clearBtn = new StyledButton("Clear Deck");
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.addActionListener(e -> {
            if (isPlayer1) {
                player1Deck.clear();
                updateDeckPanel(player1DeckPanel, player1Deck, true);
            } else {
                player2Deck.clear();
                updateDeckPanel(player2DeckPanel, player2Deck, false);
            }
            updateStartButton();
        });
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createPokemonCollectionPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new TitledBorder("Your Pokemon (Click to Add)"));
        container.setBackground(UIStyleConstants.BACKGROUND);

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        gridPanel.setBackground(UIStyleConstants.BACKGROUND);

        List<Pokemon> ownedPokemon = user.getOwnedPokemon();
        if (ownedPokemon.isEmpty()) {
            JLabel emptyLabel = new JLabel("No Pokemon available!");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.add(emptyLabel);
        } else {
            for (Pokemon pokemon : ownedPokemon) {
                PokemonCard card = new PokemonCard(pokemon);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        addPokemonToCurrentPlayer(pokemon);
                    }
                });
                gridPanel.add(card);
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, BorderLayout.CENTER);

        // Instructions
        JLabel instructions = new JLabel("<html><center>Click Pokemon to add to selected player's deck.<br/>Each player needs 3 Pokemon.</center></html>");
        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        container.add(instructions, BorderLayout.SOUTH);

        return container;
    }

    private void addPokemonToCurrentPlayer(Pokemon pokemon) {
        List<Pokemon> targetDeck = selectingForPlayer1 ? player1Deck : player2Deck;
        JPanel targetPanel = selectingForPlayer1 ? player1DeckPanel : player2DeckPanel;

        if (targetDeck.size() >= 3) {
            JOptionPane.showMessageDialog(this,
                "Deck is full! (Maximum 3 Pokemon)",
                "Deck Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if Pokemon is already in either deck
        if (player1Deck.contains(pokemon) || player2Deck.contains(pokemon)) {
            JOptionPane.showMessageDialog(this,
                "This Pokemon is already selected!",
                "Already Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create a copy of the Pokemon for this battle
        Pokemon pokemonCopy = pokemon.copy();
        targetDeck.add(pokemonCopy);
        updateDeckPanel(targetPanel, targetDeck, selectingForPlayer1);
        updateStartButton();
    }

    private void updateDeckPanel(JPanel panel, List<Pokemon> deck, boolean isPlayer1) {
        panel.removeAll();
        ((TitledBorder) panel.getBorder()).setTitle("Deck (" + deck.size() + "/3)");

        for (int i = 0; i < deck.size(); i++) {
            Pokemon pokemon = deck.get(i);
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(250, 40));

            JLabel nameLabel = new JLabel((i + 1) + ". " + capitalize(pokemon.getName()));
            nameLabel.setFont(UIStyleConstants.BODY_FONT);

            JButton removeBtn = new JButton("X");
            removeBtn.setPreferredSize(new Dimension(40, 25));
            int index = i;
            removeBtn.addActionListener(e -> {
                deck.remove(index);
                updateDeckPanel(panel, deck, isPlayer1);
                updateStartButton();
            });

            row.add(nameLabel, BorderLayout.CENTER);
            row.add(removeBtn, BorderLayout.EAST);
            panel.add(row);
        }

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(UIStyleConstants.BACKGROUND);

        // Back button
        JButton backBtn = new StyledButton("Back to Menu");
        backBtn.addActionListener(e -> {
            dispose();
            if (returnCallback != null) {
                returnCallback.run();
            }
        });
        panel.add(backBtn);

        // Start button
        startButton = new StyledButton("START BATTLE", UIStyleConstants.SECONDARY_COLOR);
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startBattle());
        panel.add(startButton);

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

        // Create users for the battle
        User user1 = new User(1, player1Name, "player1@battle.com", 0);
        User user2 = new User(2, player2Name, "player2@battle.com", 0);

        // Add selected Pokemon to each user
        for (Pokemon p : player1Deck) {
            user1.addPokemon(p);
        }
        for (Pokemon p : player2Deck) {
            user2.addPokemon(p);
        }

        // Create battle
        Battle battle = new Battle(1, user1, user2);
        battle.startBattle();

        // Create battle components
        BattlePlayerDataAccessObject dataAccess = new BattlePlayerDataAccessObject();
        dataAccess.saveBattle(battle);
        dataAccess.saveUser(user1);
        dataAccess.saveUser(user2);

        BattlePlayerViewModel viewModel = new BattlePlayerViewModel();
        BattlePlayerPresenter presenter = new BattlePlayerPresenter(viewModel);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(dataAccess, presenter);
        BattlePlayerController controller = new BattlePlayerController(interactor);

        // Open battle view
        Runnable playAgain = () -> {
            if (returnCallback != null) {
                returnCallback.run();
            }
        };

        BattlePlayerView battleView = new BattlePlayerView(controller, viewModel, dataAccess, playAgain);

        // Initialize the battle state
        interface_adapters.battle_player.BattlePlayerState initialState =
            new interface_adapters.battle_player.BattlePlayerState();
        initialState.setBattle(battle);
        initialState.setBattleStatus(battle.getBattleStatus());
        initialState.setBattleEnded(false);
        initialState.setTurnResult("Battle started! Select a move or switch Pokemon.");
        viewModel.setState(initialState);

        battleView.setVisible(true);
        dispose();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
