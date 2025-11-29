package frameworks_and_drivers;

import entities.*;
import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerPresenter;
import interface_adapters.battle_player.BattlePlayerViewModel;
import interface_adapters.ui.*;
import use_case.battle_player.BattlePlayerInteractor;
import view.BattleMovesetSelectionView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Retro-styled battle setup view for Player vs Player battles.
 * Allows two players to select Pokemon from the user's collection for battle.
 */
public class BattleSetupViewIntegrated extends JFrame {

    private final User user;
    private final Runnable returnCallback;

    // Player 1 selection
    private final List<Pokemon> player1Deck = new ArrayList<>();
    private JPanel player1DeckPanel;
    private JLabel player1DeckHeader;
    private JTextField player1NameField;

    // Player 2 selection
    private final List<Pokemon> player2Deck = new ArrayList<>();
    private JPanel player2DeckPanel;
    private JLabel player2DeckHeader;
    private JTextField player2NameField;

    // Currently selecting for which player
    private boolean selectingForPlayer1 = true;
    private JLabel selectionIndicator;

    private RetroButton startButton;

    public BattleSetupViewIntegrated(User user, Runnable returnCallback) {
        this.user = user;
        this.returnCallback = returnCallback;

        setTitle("Battle Setup - Player vs Player");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyleConstants.DARK_BG);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, UIStyleConstants.PRIMARY_COLOR,
                    getWidth(), 0, UIStyleConstants.POKEMON_BLUE
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Bottom accent
                g2d.setColor(UIStyleConstants.SECONDARY_COLOR);
                g2d.fillRect(0, getHeight() - 4, getWidth(), 4);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 100));

        JLabel title = new JLabel("PLAYER VS PLAYER BATTLE SETUP");
        title.setFont(UIStyleConstants.TITLE_FONT);
        title.setForeground(UIStyleConstants.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(8));

        selectionIndicator = new JLabel("Click Pokemon to add to PLAYER 1's deck");
        selectionIndicator.setFont(UIStyleConstants.BODY_FONT);
        selectionIndicator.setForeground(UIStyleConstants.SECONDARY_COLOR);
        selectionIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(selectionIndicator);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(UIStyleConstants.DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Left: Player 1 deck
        mainPanel.add(createPlayerPanel(true), BorderLayout.WEST);

        // Center: Pokemon collection
        mainPanel.add(createPokemonCollectionPanel(), BorderLayout.CENTER);

        // Right: Player 2 deck
        mainPanel.add(createPlayerPanel(false), BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createPlayerPanel(boolean isPlayer1) {
        Color playerColor = isPlayer1 ? UIStyleConstants.PRIMARY_COLOR : UIStyleConstants.POKEMON_BLUE;

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.DARK_BG);
        container.setPreferredSize(new Dimension(220, 0));

        // Header with player name
        JPanel headerBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(playerColor);
                g2d.fillRect(0, 0, w, h);

                // 3D effect
                g2d.setColor(playerColor.brighter());
                g2d.drawLine(0, 0, w, 0);
                g2d.drawLine(0, 0, 0, h);
                g2d.setColor(playerColor.darker());
                g2d.drawLine(w - 1, 0, w - 1, h);
                g2d.drawLine(0, h - 1, w, h - 1);
            }
        };
        headerBox.setLayout(new BoxLayout(headerBox, BoxLayout.Y_AXIS));
        headerBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel playerLabel = new JLabel(isPlayer1 ? "PLAYER 1" : "PLAYER 2");
        playerLabel.setFont(UIStyleConstants.HEADING_FONT);
        playerLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerBox.add(playerLabel);

        container.add(headerBox, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(new Color(50, 50, 60));
                g2d.fillRect(3, 3, w - 6, h - 6);
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));

        // Name field
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        namePanel.setOpaque(false);
        namePanel.setMaximumSize(new Dimension(200, 35));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(UIStyleConstants.SMALL_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
        namePanel.add(nameLabel);

        JTextField nameField = new JTextField(isPlayer1 ? "Player 1" : "Player 2", 10);
        nameField.setFont(UIStyleConstants.BODY_FONT);
        nameField.setBackground(UIStyleConstants.MENU_BG);
        namePanel.add(nameField);

        if (isPlayer1) {
            player1NameField = nameField;
        } else {
            player2NameField = nameField;
        }

        contentPanel.add(namePanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Select button
        RetroButton selectBtn = new RetroButton("SELECT FOR " + (isPlayer1 ? "P1" : "P2"));
        selectBtn.setButtonColor(playerColor);
        selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectBtn.setMaximumSize(new Dimension(180, 35));
        selectBtn.addActionListener(e -> {
            selectingForPlayer1 = isPlayer1;
            selectionIndicator.setText("Click Pokemon to add to " + (isPlayer1 ? "PLAYER 1" : "PLAYER 2") + "'s deck");
        });
        contentPanel.add(selectBtn);
        contentPanel.add(Box.createVerticalStrut(15));

        // Deck header
        JLabel deckHeader = new JLabel("DECK (0/3)");
        deckHeader.setFont(UIStyleConstants.BODY_FONT);
        deckHeader.setForeground(UIStyleConstants.SECONDARY_COLOR);
        deckHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(deckHeader);

        if (isPlayer1) {
            player1DeckHeader = deckHeader;
        } else {
            player2DeckHeader = deckHeader;
        }

        contentPanel.add(Box.createVerticalStrut(5));

        // Deck panel
        JPanel deckPanel = new JPanel();
        deckPanel.setLayout(new BoxLayout(deckPanel, BoxLayout.Y_AXIS));
        deckPanel.setOpaque(false);
        deckPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(deckPanel);

        if (isPlayer1) {
            player1DeckPanel = deckPanel;
        } else {
            player2DeckPanel = deckPanel;
        }

        contentPanel.add(Box.createVerticalGlue());

        // Clear button
        RetroButton clearBtn = new RetroButton("CLEAR");
        clearBtn.setButtonColor(UIStyleConstants.BORDER_DARK);
        clearBtn.setFont(UIStyleConstants.SMALL_FONT);
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.setMaximumSize(new Dimension(100, 30));
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
        container.setBackground(UIStyleConstants.DARK_BG);

        // Header
        JPanel headerBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.HP_HIGH);
                g2d.fillRect(0, 0, w, h);

                g2d.setColor(UIStyleConstants.HP_HIGH.brighter());
                g2d.drawLine(0, 0, w, 0);
                g2d.drawLine(0, 0, 0, h);
                g2d.setColor(UIStyleConstants.HP_HIGH.darker());
                g2d.drawLine(w - 1, 0, w - 1, h);
                g2d.drawLine(0, h - 1, w, h - 1);
            }
        };
        headerBox.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerBox.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel headerLabel = new JLabel("YOUR POKEMON (Click to Add)");
        headerLabel.setFont(UIStyleConstants.HEADING_FONT);
        headerLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
        headerBox.add(headerLabel);
        container.add(headerBox, BorderLayout.NORTH);

        // Grid of Pokemon
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        gridPanel.setBackground(UIStyleConstants.DARK_BG);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Pokemon> ownedPokemon = user.getOwnedPokemon();
        if (ownedPokemon.isEmpty()) {
            JLabel emptyLabel = new JLabel("No Pokemon available!");
            emptyLabel.setFont(UIStyleConstants.BODY_FONT);
            emptyLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.add(emptyLabel);
        } else {
            for (Pokemon pokemon : ownedPokemon) {
                JPanel card = createPokemonCard(pokemon);
                gridPanel.add(card);
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_DARK, 2));
        scrollPane.getViewport().setBackground(UIStyleConstants.DARK_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(3, 3, w - 6, h - 6);

                // Highlight
                g2d.setColor(Color.WHITE);
                g2d.drawLine(3, 3, w - 3, 3);
                g2d.drawLine(3, 3, 3, h - 3);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        card.setPreferredSize(new Dimension(140, 160));
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
            imgLabel.setFont(UIStyleConstants.HEADING_FONT);
        }
        card.add(imgLabel);

        // Pokemon name
        JLabel nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(UIStyleConstants.BODY_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        // Types
        String types = String.join("/", pokemon.getTypes()).toUpperCase();
        JLabel typeLabel = new JLabel(types);
        typeLabel.setFont(UIStyleConstants.SMALL_FONT);
        typeLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(typeLabel);

        card.add(Box.createVerticalStrut(5));

        // Add button
        RetroButton addBtn = new RetroButton("ADD");
        addBtn.setButtonColor(UIStyleConstants.HP_HIGH);
        addBtn.setFont(UIStyleConstants.SMALL_FONT);
        addBtn.setPreferredSize(new Dimension(70, 25));
        addBtn.setMaximumSize(new Dimension(70, 25));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> addPokemonToCurrentPlayer(pokemon));
        card.add(addBtn);

        return card;
    }

    private void addPokemonToCurrentPlayer(Pokemon pokemon) {
        List<Pokemon> targetDeck = selectingForPlayer1 ? player1Deck : player2Deck;
        JPanel targetPanel = selectingForPlayer1 ? player1DeckPanel : player2DeckPanel;
        JLabel targetHeader = selectingForPlayer1 ? player1DeckHeader : player2DeckHeader;

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

        // Create a copy for this battle
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
        JPanel row = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.drawRect(0, 0, w - 1, h - 1);
            }
        };
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 5));
        row.setMaximumSize(new Dimension(190, 35));
        row.setPreferredSize(new Dimension(190, 35));

        JLabel nameLabel = new JLabel((index + 1) + ". " + capitalize(pokemon.getName()));
        nameLabel.setFont(UIStyleConstants.SMALL_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        row.add(nameLabel);
        row.add(Box.createHorizontalGlue());

        JButton removeBtn = new JButton("X");
        removeBtn.setBackground(UIStyleConstants.HP_LOW);
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Courier New", Font.BOLD, 10));
        removeBtn.setPreferredSize(new Dimension(24, 20));
        removeBtn.setMaximumSize(new Dimension(24, 20));
        removeBtn.setMinimumSize(new Dimension(24, 20));
        removeBtn.setMargin(new Insets(0, 0, 0, 0));
        removeBtn.setFocusPainted(false);
        removeBtn.setBorderPainted(false);
        removeBtn.setOpaque(true);
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
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Back button on left
        RetroButton backBtn = new RetroButton("BACK");
        backBtn.setButtonColor(UIStyleConstants.BORDER_DARK);
        backBtn.addActionListener(e -> {
            dispose();
            if (returnCallback != null) {
                returnCallback.run();
            }
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backBtn);
        panel.add(leftPanel, BorderLayout.WEST);

        // Start button on right
        startButton = new RetroButton("START BATTLE");
        startButton.setButtonColor(UIStyleConstants.SECONDARY_COLOR);
        startButton.setPreferredSize(new Dimension(180, 50));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startBattle());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
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

        // Show moveset selection for Player 1 first
        Runnable onPlayer1Complete = () -> {
            // Then show moveset selection for Player 2
            Runnable onPlayer2Complete = () -> {
                startActualBattle(p1Name, p2Name);
            };

            Runnable onPlayer2Cancel = () -> {
                this.setVisible(true);
            };

            BattleMovesetSelectionView player2MovesetView = new BattleMovesetSelectionView(
                player2Deck, onPlayer2Complete, onPlayer2Cancel
            );
            player2MovesetView.setTitle(p2Name + " - Select Moves");
            player2MovesetView.setVisible(true);
        };

        Runnable onPlayer1Cancel = () -> {
            this.setVisible(true);
        };

        BattleMovesetSelectionView player1MovesetView = new BattleMovesetSelectionView(
            player1Deck, onPlayer1Complete, onPlayer1Cancel
        );
        player1MovesetView.setTitle(p1Name + " - Select Moves");
        player1MovesetView.setVisible(true);
        this.setVisible(false);
    }

    private void startActualBattle(String player1Name, String player2Name) {
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
