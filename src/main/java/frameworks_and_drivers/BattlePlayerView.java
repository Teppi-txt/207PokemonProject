package frameworks_and_drivers;

import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerState;
import interface_adapters.battle_player.BattlePlayerViewModel;
import interface_adapters.ui.RetroButton;
import interface_adapters.ui.UIStyleConstants;
import entities.*;
import use_case.battle_player.BattlePlayerUserDataAccessInterface;
import pokeapi.JSONLoader;
import pokeapi.PokeAPIFetcher;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;


public class BattlePlayerView extends JFrame implements PropertyChangeListener {
    private final BattlePlayerController battlePlayerController;
    private final BattlePlayerViewModel battlePlayerViewModel;
    private final BattlePlayerUserDataAccessInterface dataAccess;
    private final Runnable playAgainHandler;

    // UI Components - Player 1
    private JLabel player1PokemonNameLabel;
    private JLabel player1PokemonImageLabel;
    private JPanel player1HPBarPanel;
    private JLabel player1HPLabel;
    private int player1CurrentHP = 100;
    private int player1MaxHP = 100;
    private JPanel player1MovesPanel;
    private RetroButton[] player1MoveButtons = new RetroButton[4];
    private JPanel player1TeamPanel;

    // UI Components - Player 2
    private JLabel player2PokemonNameLabel;
    private JLabel player2PokemonImageLabel;
    private JPanel player2HPBarPanel;
    private JLabel player2HPLabel;
    private int player2CurrentHP = 100;
    private int player2MaxHP = 100;
    private JPanel player2MovesPanel;
    private RetroButton[] player2MoveButtons = new RetroButton[4];
    private JPanel player2TeamPanel;

    // UI Components - Battle
    private JTextArea messageArea;
    private JPanel battleEndedPanel;
    private JLabel winnerLabel;
    private JLabel turnIndicatorLabel;

    // Current battle state
    private Battle currentBattle;
    private User currentUser;
    private UserPlayerAdapter currentUserAdapter;
    private Player opponentPlayer;
    private boolean processingTurn = false;
    private boolean player1Turn = true;
    private int turnCounter = 1;

    // Track max HP for each Pokemon
    private Map<Pokemon, Integer> maxHPMap = new HashMap<>();

    public BattlePlayerView(BattlePlayerController battlePlayerController,
                           BattlePlayerViewModel battlePlayerViewModel,
                           BattlePlayerUserDataAccessInterface dataAccess,
                           Runnable playAgainHandler) {
        this.battlePlayerController = battlePlayerController;
        this.battlePlayerViewModel = battlePlayerViewModel;
        this.dataAccess = dataAccess;
        this.playAgainHandler = playAgainHandler;

        battlePlayerViewModel.addPropertyChangeListener(this);
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Pokemon Battle - Player vs Player");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyleConstants.DARK_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UIStyleConstants.DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: Turn indicator
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center: Battle Arena
        JPanel arenaPanel = createArenaPanel();
        mainPanel.add(arenaPanel, BorderLayout.CENTER);

        // Bottom: Controls (both players side by side)
        JPanel controlsPanel = createControlsPanel();
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setSize(1100, 800);
        setLocationRelativeTo(null);
        setVisible(true);
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
            }
        };
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        turnIndicatorLabel = new JLabel("PLAYER 1's TURN");
        turnIndicatorLabel.setFont(UIStyleConstants.TITLE_FONT);
        turnIndicatorLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
        panel.add(turnIndicatorLabel);

        return panel;
    }

    private JPanel createArenaPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Draw battle background gradient
                GradientPaint skyGradient = new GradientPaint(
                    0, 0, new Color(135, 206, 235),
                    0, getHeight() / 2, new Color(176, 224, 230)
                );
                g2d.setPaint(skyGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight() / 2);

                // Draw ground
                GradientPaint groundGradient = new GradientPaint(
                    0, getHeight() / 2, new Color(144, 238, 144),
                    0, getHeight(), new Color(34, 139, 34)
                );
                g2d.setPaint(groundGradient);
                g2d.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);

                // Draw VS text
                g2d.setFont(UIStyleConstants.EXTRA_LARGE_FONT);
                g2d.setColor(new Color(255, 255, 255, 150));
                String vs = "VS";
                FontMetrics fm = g2d.getFontMetrics();
                int vsX = (getWidth() - fm.stringWidth(vs)) / 2;
                int vsY = getHeight() / 2;
                g2d.drawString(vs, vsX, vsY);

                // Draw battle platforms
                g2d.setColor(new Color(139, 90, 43));
                // Player 1 platform (left)
                g2d.fillOval(100, getHeight() - 120, 200, 50);
                g2d.setColor(new Color(160, 120, 80));
                g2d.fillOval(105, getHeight() - 115, 190, 40);

                // Player 2 platform (right)
                g2d.setColor(new Color(139, 90, 43));
                g2d.fillOval(getWidth() - 300, getHeight() - 120, 200, 50);
                g2d.setColor(new Color(160, 120, 80));
                g2d.fillOval(getWidth() - 295, getHeight() - 115, 190, 40);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(1080, 300));

        // Player 1 Pokemon (left side)
        player1PokemonImageLabel = new JLabel();
        player1PokemonImageLabel.setBounds(130, 80, 150, 150);
        player1PokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(player1PokemonImageLabel);

        // Player 1 HP Box (top left)
        JPanel player1InfoBox = createPokemonInfoBox(true);
        player1InfoBox.setBounds(20, 20, 280, 80);
        panel.add(player1InfoBox);

        // Player 2 Pokemon (right side)
        player2PokemonImageLabel = new JLabel();
        player2PokemonImageLabel.setBounds(800, 80, 150, 150);
        player2PokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(player2PokemonImageLabel);

        // Player 2 HP Box (top right)
        JPanel player2InfoBox = createPokemonInfoBox(false);
        player2InfoBox.setBounds(780, 20, 280, 80);
        panel.add(player2InfoBox);

        return panel;
    }

    private JPanel createPokemonInfoBox(boolean isPlayer1) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(4, 4, w - 8, h - 8);

                // Border color based on player
                g2d.setColor(isPlayer1 ? UIStyleConstants.PRIMARY_COLOR : UIStyleConstants.POKEMON_BLUE);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(2, 2, w - 4, h - 4);
            }
        };
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        box.setOpaque(false);

        JLabel nameLabel = new JLabel(isPlayer1 ? "PLAYER 1" : "PLAYER 2");
        nameLabel.setFont(UIStyleConstants.HEADING_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(nameLabel);

        if (isPlayer1) {
            player1PokemonNameLabel = nameLabel;
        } else {
            player2PokemonNameLabel = nameLabel;
        }

        box.add(Box.createVerticalStrut(5));

        JPanel hpRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hpRow.setOpaque(false);
        hpRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hpText = new JLabel("HP:");
        hpText.setFont(UIStyleConstants.BODY_FONT);
        hpText.setForeground(UIStyleConstants.TEXT_PRIMARY);
        hpRow.add(hpText);

        JPanel hpBarContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(new Color(48, 48, 48));
                g2d.fillRect(2, 2, w - 4, h - 4);
            }
        };
        hpBarContainer.setPreferredSize(new Dimension(150, 16));
        hpBarContainer.setLayout(new BorderLayout());

        JPanel hpBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int currentHP = isPlayer1 ? player1CurrentHP : player2CurrentHP;
                int maxHP = isPlayer1 ? player1MaxHP : player2MaxHP;
                float hpPercent = maxHP > 0 ? (float) currentHP / maxHP : 0;

                int barWidth = (int) ((getWidth() - 4) * hpPercent);

                Color hpColor = UIStyleConstants.getHPColor(hpPercent);
                g2d.setColor(hpColor);
                g2d.fillRect(2, 2, barWidth, getHeight() - 4);
            }
        };
        hpBar.setOpaque(false);
        hpBarContainer.add(hpBar, BorderLayout.CENTER);
        hpRow.add(hpBarContainer);

        if (isPlayer1) {
            player1HPBarPanel = hpBar;
        } else {
            player2HPBarPanel = hpBar;
        }

        box.add(hpRow);
        box.add(Box.createVerticalStrut(3));

        JLabel hpNumLabel = new JLabel("100 / 100");
        hpNumLabel.setFont(UIStyleConstants.SMALL_FONT);
        hpNumLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        hpNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(hpNumLabel);

        if (isPlayer1) {
            player1HPLabel = hpNumLabel;
        } else {
            player2HPLabel = hpNumLabel;
        }

        return box;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setPreferredSize(new Dimension(1080, 320));

        // Message box at top
        JPanel messageBox = createMessageBox();
        panel.add(messageBox, BorderLayout.NORTH);

        // Two player control panels side by side
        JPanel playersPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        playersPanel.setBackground(UIStyleConstants.DARK_BG);

        // Player 1 controls (left)
        JPanel player1Controls = createPlayerControlsPanel(true);
        playersPanel.add(player1Controls);

        // Player 2 controls (right)
        JPanel player2Controls = createPlayerControlsPanel(false);
        playersPanel.add(player2Controls);

        panel.add(playersPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMessageBox() {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(4, 4, w - 8, h - 8);
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(8, 8, w - 16, h - 16);
            }
        };
        box.setLayout(new BorderLayout());
        box.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        box.setPreferredSize(new Dimension(0, 80));

        messageArea = new JTextArea();
        messageArea.setFont(UIStyleConstants.MENU_FONT);
        messageArea.setForeground(UIStyleConstants.TEXT_PRIMARY);
        messageArea.setBackground(UIStyleConstants.MENU_BG);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Battle started! Player 1, choose your action!");
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        box.add(messageArea, BorderLayout.CENTER);

        return box;
    }

    private JPanel createPlayerControlsPanel(boolean isPlayer1) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(4, 4, w - 8, h - 8);
                g2d.setColor(isPlayer1 ? new Color(80, 40, 40) : new Color(40, 40, 80));
                g2d.fillRect(8, 8, w - 16, h - 16);

                // Player label
                g2d.setFont(UIStyleConstants.HEADING_FONT);
                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                String label = isPlayer1 ? "PLAYER 1" : "PLAYER 2";
                g2d.drawString(label, 15, 30);
            }
        };
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 12, 12, 12));

        // Moves panel
        JPanel movesPanel = createMovesPanel(isPlayer1);
        if (isPlayer1) {
            player1MovesPanel = movesPanel;
        } else {
            player2MovesPanel = movesPanel;
        }
        panel.add(movesPanel, BorderLayout.CENTER);

        // Team panel
        JPanel teamPanel = createTeamPanel(isPlayer1);
        if (isPlayer1) {
            player1TeamPanel = teamPanel;
        } else {
            player2TeamPanel = teamPanel;
        }
        panel.add(teamPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMovesPanel(boolean isPlayer1) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setOpaque(false);

        Color[] moveColors = {
            UIStyleConstants.PRIMARY_COLOR,
            UIStyleConstants.POKEMON_BLUE,
            new Color(120, 200, 80),
            UIStyleConstants.SECONDARY_COLOR
        };

        RetroButton[] buttons = new RetroButton[4];
        for (int i = 0; i < 4; i++) {
            buttons[i] = new RetroButton("-");
            buttons[i].setButtonColor(moveColors[i]);
            buttons[i].setFont(UIStyleConstants.SMALL_FONT);
            buttons[i].setEnabled(false);
            final int moveIndex = i;
            buttons[i].addActionListener(e -> executeMoveForPlayer(isPlayer1, moveIndex));
            panel.add(buttons[i]);
        }

        if (isPlayer1) {
            player1MoveButtons = buttons;
        } else {
            player2MoveButtons = buttons;
        }

        return panel;
    }

    private JPanel createTeamPanel(boolean isPlayer1) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 50));

        JLabel label = new JLabel("TEAM:");
        label.setFont(UIStyleConstants.SMALL_FONT);
        label.setForeground(UIStyleConstants.TEXT_LIGHT);
        panel.add(label);

        return panel;
    }

    private void loadPokemonImage(JLabel imageLabel, Pokemon pokemon) {
        if (pokemon == null) {
            imageLabel.setIcon(null);
            return;
        }

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    String imageUrl = pokemon.getSpriteUrl();
                    URL url = new URL(imageUrl);
                    Image image = ImageIO.read(url);
                    if (image != null) {
                        Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading Pokemon image: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imageLabel.setIcon(icon);
                    }
                } catch (Exception e) {
                    // Keep current icon
                }
            }
        };
        worker.execute();
    }

    private void executeMoveForPlayer(boolean isPlayer1, int moveIndex) {
        Player actingPlayer = isPlayer1 ? currentUserAdapter : opponentPlayer;
        Player targetPlayer = isPlayer1 ? opponentPlayer : currentUserAdapter;

        if (actingPlayer == null || actingPlayer.getActivePokemon() == null) {
            return;
        }

        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            return;
        }

        if (processingTurn || isPlayer1 != player1Turn) {
            return;
        }

        Pokemon activePokemon = actingPlayer.getActivePokemon();
        List<String> moves = activePokemon.getMoves();
        if (moves == null || moveIndex >= moves.size()) {
            return;
        }

        processingTurn = true;

        try {
            String moveName = moves.get(moveIndex);
            Move move = loadMove(moveName);

            int turnNumber = nextTurnNumber();
            MoveTurn turn = new MoveTurn(turnNumber, actingPlayer, turnNumber, move, targetPlayer);
            battlePlayerController.battle(turn);

            BattlePlayerState latestState = battlePlayerViewModel.getState();
            if (latestState.isBattleEnded() || !"IN_PROGRESS".equals(latestState.getBattleStatus())) {
                return;
            }

            setPlayerTurn(!player1Turn);
        } finally {
            processingTurn = false;
        }
    }

    private void executeSwitchForPlayer(boolean isPlayer1, Pokemon newPokemon) {
        Player actingPlayer = isPlayer1 ? currentUserAdapter : opponentPlayer;
        Player targetPlayer = isPlayer1 ? opponentPlayer : currentUserAdapter;

        if (actingPlayer == null) {
            return;
        }

        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            return;
        }

        if (processingTurn || isPlayer1 != player1Turn) {
            return;
        }

        Pokemon previousPokemon = actingPlayer.getActivePokemon();
        int turnNumber = nextTurnNumber();
        SwitchTurn turn = new SwitchTurn(turnNumber, actingPlayer, turnNumber, previousPokemon, newPokemon);
        actingPlayer.switchPokemon(newPokemon);
        battlePlayerController.battle(turn);

        BattlePlayerState latestState = battlePlayerViewModel.getState();
        if (latestState.isBattleEnded() || !"IN_PROGRESS".equals(latestState.getBattleStatus())) {
            return;
        }

        setPlayerTurn(!player1Turn);
    }

    private Move loadMove(String moveName) {
        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                return move;
            }
        }

        try {
            return PokeAPIFetcher.getMove(moveName);
        } catch (Exception e) {
            return new Move()
                    .setName(moveName)
                    .setType("normal")
                    .setPower(40)
                    .setAccuracy(100)
                    .setPriority(0);
        }
    }

    private int nextTurnNumber() {
        return turnCounter++;
    }

    private void setPlayerTurn(boolean player1TurnNow) {
        this.player1Turn = player1TurnNow;
        turnIndicatorLabel.setText(player1TurnNow ? "PLAYER 1's TURN" : "PLAYER 2's TURN");
        updateControlsEnabled();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BattlePlayerViewModel.STATE_PROPERTY)) {
            SwingUtilities.invokeLater(this::updateView);
        }
    }

    public void updateView() {
        BattlePlayerState state = battlePlayerViewModel.getState();

        if (state.getErrorMessage() != null) {
            messageArea.setText("Error: " + state.getErrorMessage());
            return;
        }

        if (state.isBattleEnded()) {
            displayBattleEnded(state);
        } else {
            displayBattleStatus(state);
        }
    }

    private void displayBattleStatus(BattlePlayerState state) {
        if (state.getBattle() != null) {
            currentBattle = state.getBattle();

            User user1 = currentBattle.getPlayer1();
            User user2 = currentBattle.getPlayer2();

            if (currentUserAdapter == null) {
                currentUser = user1;
                currentUserAdapter = new UserPlayerAdapter(currentUser);
            }
            if (opponentPlayer == null) {
                opponentPlayer = new UserPlayerAdapter(user2);
            }

            // Update player 1 info
            updatePlayerInfo(user1, currentUserAdapter, true);

            // Update player 2 info
            updatePlayerInfo(user2, opponentPlayer, false);
        }

        // Update turn result
        if (state.getTurnResult() != null && !state.getTurnResult().isEmpty()) {
            messageArea.setText(state.getTurnResult());
        } else if (state.getTurn() != null) {
            messageArea.setText(state.getTurn().getTurnDetails());
        }

        updateControlsEnabled();
        revalidate();
        repaint();
    }

    private void updatePlayerInfo(User user, Player playerAdapter, boolean isPlayer1) {
        if (user == null) return;

        Pokemon activePokemon = playerAdapter != null ? playerAdapter.getActivePokemon() : null;

        if (activePokemon == null && user.getOwnedPokemon() != null) {
            for (Pokemon p : user.getOwnedPokemon()) {
                if (!p.isFainted()) {
                    activePokemon = p;
                    if (playerAdapter instanceof UserPlayerAdapter) {
                        ((UserPlayerAdapter) playerAdapter).switchPokemon(p);
                    }
                    break;
                }
            }
        }

        JLabel nameLabel = isPlayer1 ? player1PokemonNameLabel : player2PokemonNameLabel;
        JLabel imageLabel = isPlayer1 ? player1PokemonImageLabel : player2PokemonImageLabel;
        JLabel hpLabel = isPlayer1 ? player1HPLabel : player2HPLabel;
        JPanel hpBar = isPlayer1 ? player1HPBarPanel : player2HPBarPanel;

        if (activePokemon != null) {
            nameLabel.setText(activePokemon.getName().toUpperCase());
            loadPokemonImage(imageLabel, activePokemon);

            int currentHP = activePokemon.getStats().getHp();
            Integer maxHP = maxHPMap.get(activePokemon);
            if (maxHP == null || currentHP > maxHP) {
                maxHP = currentHP;
                maxHPMap.put(activePokemon, maxHP);
            }

            if (isPlayer1) {
                player1CurrentHP = currentHP;
                player1MaxHP = maxHP;
            } else {
                player2CurrentHP = currentHP;
                player2MaxHP = maxHP;
            }

            hpLabel.setText(currentHP + " / " + maxHP);
            hpBar.repaint();

            // Update moves
            updateMovesDisplay(activePokemon, isPlayer1);

            // Update team
            updateTeamDisplay(user.getOwnedPokemon(), activePokemon, isPlayer1);
        } else {
            nameLabel.setText("NO POKEMON");
            imageLabel.setIcon(null);
            hpLabel.setText("0 / 0");
        }
    }

    private void updateMovesDisplay(Pokemon pokemon, boolean isPlayer1) {
        List<String> moves = pokemon.getMoves();
        RetroButton[] buttons = isPlayer1 ? player1MoveButtons : player2MoveButtons;
        boolean canAct = isPlayer1 == player1Turn;

        for (int i = 0; i < 4; i++) {
            if (moves != null && i < moves.size()) {
                String moveName = moves.get(i);
                buttons[i].setText(moveName.toUpperCase());
                buttons[i].setEnabled(canAct && currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus()));

                Move move = findMove(moveName);
                if (move != null && move.getType() != null) {
                    buttons[i].setButtonColor(UIStyleConstants.getTypeColor(move.getType()));
                }
            } else {
                buttons[i].setText("-");
                buttons[i].setEnabled(false);
            }
        }
    }

    private void updateTeamDisplay(List<Pokemon> team, Pokemon activePokemon, boolean isPlayer1) {
        JPanel teamPanel = isPlayer1 ? player1TeamPanel : player2TeamPanel;
        teamPanel.removeAll();

        JLabel label = new JLabel("TEAM:");
        label.setFont(UIStyleConstants.SMALL_FONT);
        label.setForeground(UIStyleConstants.TEXT_LIGHT);
        teamPanel.add(label);

        if (team != null) {
            boolean canAct = isPlayer1 == player1Turn && currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus());

            for (Pokemon pokemon : team) {
                RetroButton btn = new RetroButton(pokemon.getName());
                btn.setFont(new Font("Courier New", Font.BOLD, 10));
                btn.setPreferredSize(new Dimension(80, 30));

                if (pokemon == activePokemon) {
                    btn.setButtonColor(UIStyleConstants.HP_HIGH);
                    btn.setEnabled(false);
                } else if (pokemon.isFainted()) {
                    btn.setButtonColor(Color.GRAY);
                    btn.setEnabled(false);
                } else {
                    btn.setButtonColor(UIStyleConstants.POKEMON_BLUE);
                    btn.setEnabled(canAct);
                    btn.addActionListener(e -> executeSwitchForPlayer(isPlayer1, pokemon));
                }

                teamPanel.add(btn);
            }
        }

        teamPanel.revalidate();
        teamPanel.repaint();
    }

    private Move findMove(String moveName) {
        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                return move;
            }
        }
        return null;
    }

    private void updateControlsEnabled() {
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            disableAllControls();
            return;
        }

        // Player 1 controls
        for (RetroButton btn : player1MoveButtons) {
            btn.setEnabled(player1Turn);
        }

        // Player 2 controls
        for (RetroButton btn : player2MoveButtons) {
            btn.setEnabled(!player1Turn);
        }
    }

    private void disableAllControls() {
        for (RetroButton btn : player1MoveButtons) {
            btn.setEnabled(false);
        }
        for (RetroButton btn : player2MoveButtons) {
            btn.setEnabled(false);
        }
    }

    private void displayBattleEnded(BattlePlayerState state) {
        displayBattleStatus(state);
        disableAllControls();

        User winner = null;
        if (state.getBattle() != null && state.getBattle().getWinner() != null) {
            winner = state.getBattle().getWinner();
        } else if (currentBattle != null && currentBattle.getWinner() != null) {
            winner = currentBattle.getWinner();
        }

        String winnerText = winner != null ? winner.getName() + " WINS!" : "BATTLE OVER!";

        // Show winner dialog
        JDialog dialog = new JDialog(this, "Battle Result", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UIStyleConstants.DARK_BG);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(6, 6, w - 12, h - 12);
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(12, 12, w - 24, h - 24);
            }
        };
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel winLabel = new JLabel(winnerText);
        winLabel.setFont(UIStyleConstants.TITLE_FONT);
        winLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        winLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(winLabel);

        content.add(Box.createVerticalStrut(20));

        RetroButton playAgainBtn = new RetroButton("Play Again");
        playAgainBtn.setButtonColor(UIStyleConstants.HP_HIGH);
        playAgainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainBtn.addActionListener(e -> {
            dialog.dispose();
            dispose();
            if (playAgainHandler != null) {
                playAgainHandler.run();
            }
        });
        content.add(playAgainBtn);

        content.add(Box.createVerticalStrut(10));

        RetroButton exitBtn = new RetroButton("Exit");
        exitBtn.setButtonColor(UIStyleConstants.PRIMARY_COLOR);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> {
            dialog.dispose();
            dispose();
        });
        content.add(exitBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
