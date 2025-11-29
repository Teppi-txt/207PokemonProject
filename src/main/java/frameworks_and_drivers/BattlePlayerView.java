package frameworks_and_drivers;

import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerState;
import interface_adapters.battle_player.BattlePlayerViewModel;
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
    private JProgressBar player1HPBar;
    private JLabel player1HPLabel;
    private int player1CurrentHP = 100;
    private int player1MaxHP = 100;
    private JPanel player1MovesPanel;
    private JButton[] player1MoveButtons = new JButton[4];
    private JPanel player1TeamPanel;

    // UI Components - Player 2
    private JLabel player2PokemonNameLabel;
    private JLabel player2PokemonImageLabel;
    private JProgressBar player2HPBar;
    private JLabel player2HPLabel;
    private int player2CurrentHP = 100;
    private int player2MaxHP = 100;
    private JPanel player2MovesPanel;
    private JButton[] player2MoveButtons = new JButton[4];
    private JPanel player2TeamPanel;

    // Battle
    private JTextArea messageArea;
    private JLabel turnIndicatorLabel;

    // Current battle state
    private Battle currentBattle;
    private User currentUser;
    private UserPlayerAdapter currentUserAdapter;
    private Player opponentPlayer;
    private boolean processingTurn = false;
    private boolean player1Turn = true;
    private int turnCounter = 1;

    private Map<Pokemon, Integer> maxHPMap = new HashMap<>();
    private int currentPlayer1PokemonId = -1;
    private int currentPlayer2PokemonId = -1;
    private Map<String, ImageIcon> imageCache = new HashMap<>();

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
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with turn indicator
        turnIndicatorLabel = new JLabel("PLAYER 1's TURN", SwingConstants.CENTER);
        turnIndicatorLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        turnIndicatorLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(turnIndicatorLabel, BorderLayout.NORTH);

        // Battle arena - two Pokemon side by side
        JPanel arenaPanel = createArenaPanel();
        mainPanel.add(arenaPanel, BorderLayout.CENTER);

        // Bottom controls
        JPanel controlsPanel = createControlsPanel();
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createArenaPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 40, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Player 1 side
        JPanel player1Panel = createPokemonPanel(true);
        panel.add(player1Panel);

        // Player 2 side
        JPanel player2Panel = createPokemonPanel(false);
        panel.add(player2Panel);

        return panel;
    }

    private JPanel createPokemonPanel(boolean isPlayer1) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Player label
        JLabel playerLabel = new JLabel(isPlayer1 ? "PLAYER 1" : "PLAYER 2");
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(playerLabel);

        panel.add(Box.createVerticalStrut(10));

        // Pokemon name
        JLabel nameLabel = new JLabel("---");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer1) player1PokemonNameLabel = nameLabel;
        else player2PokemonNameLabel = nameLabel;
        panel.add(nameLabel);

        panel.add(Box.createVerticalStrut(15));

        // Pokemon image with circle
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 150));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer1) player1PokemonImageLabel = imageLabel;
        else player2PokemonImageLabel = imageLabel;
        panel.add(imageLabel);

        // Circle below Pokemon
        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 200, 200));
                int w = getWidth();
                g2d.fillOval(w/2 - 60, 0, 120, 25);
            }
        };
        circlePanel.setPreferredSize(new Dimension(150, 30));
        circlePanel.setMaximumSize(new Dimension(150, 30));
        circlePanel.setOpaque(false);
        circlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(circlePanel);

        panel.add(Box.createVerticalStrut(15));

        // HP Bar
        JPanel hpPanel = new JPanel();
        hpPanel.setLayout(new BoxLayout(hpPanel, BoxLayout.Y_AXIS));
        hpPanel.setBackground(Color.WHITE);
        hpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hpPanel.setMaximumSize(new Dimension(200, 50));

        JLabel hpText = new JLabel("HP");
        hpText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hpText.setAlignmentX(Component.CENTER_ALIGNMENT);
        hpPanel.add(hpText);

        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setStringPainted(false);
        hpBar.setPreferredSize(new Dimension(180, 12));
        hpBar.setMaximumSize(new Dimension(180, 12));
        hpBar.setForeground(new Color(76, 175, 80));
        hpBar.setBackground(new Color(230, 230, 230));
        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer1) player1HPBar = hpBar;
        else player2HPBar = hpBar;
        hpPanel.add(hpBar);

        JLabel hpLabel = new JLabel("100 / 100");
        hpLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hpLabel.setForeground(Color.GRAY);
        hpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer1) player1HPLabel = hpLabel;
        else player2HPLabel = hpLabel;
        hpPanel.add(hpLabel);

        panel.add(hpPanel);

        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Message area
        messageArea = new JTextArea(2, 40);
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("Battle started! Player 1, choose your move.");
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.add(messageArea, BorderLayout.NORTH);

        // Two player controls side by side
        JPanel playersPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        playersPanel.setBackground(Color.WHITE);

        playersPanel.add(createPlayerControlsPanel(true));
        playersPanel.add(createPlayerControlsPanel(false));

        panel.add(playersPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPlayerControlsPanel(boolean isPlayer1) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel label = new JLabel(isPlayer1 ? "PLAYER 1 MOVES" : "PLAYER 2 MOVES");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(Box.createVerticalStrut(10));

        // Moves grid
        JPanel movesPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        movesPanel.setOpaque(false);

        JButton[] buttons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            buttons[i] = new JButton("-");
            buttons[i].setFont(new Font("SansSerif", Font.BOLD, 12));
            buttons[i].setEnabled(false);
            buttons[i].setFocusPainted(false);
            final int moveIndex = i;
            buttons[i].addActionListener(e -> executeMoveForPlayer(isPlayer1, moveIndex));
            movesPanel.add(buttons[i]);
        }

        if (isPlayer1) {
            player1MoveButtons = buttons;
            player1MovesPanel = movesPanel;
        } else {
            player2MoveButtons = buttons;
            player2MovesPanel = movesPanel;
        }
        panel.add(movesPanel);

        panel.add(Box.createVerticalStrut(10));

        // Team panel
        JPanel teamPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        teamPanel.setOpaque(false);
        JLabel teamLabel = new JLabel("Team:");
        teamLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        teamPanel.add(teamLabel);

        if (isPlayer1) player1TeamPanel = teamPanel;
        else player2TeamPanel = teamPanel;
        panel.add(teamPanel);

        return panel;
    }

    private void loadPokemonImage(JLabel imageLabel, Pokemon pokemon, boolean isPlayer1) {
        if (pokemon == null) {
            imageLabel.setIcon(null);
            if (isPlayer1) currentPlayer1PokemonId = -1;
            else currentPlayer2PokemonId = -1;
            return;
        }

        int pokemonId = pokemon.getId();
        if (isPlayer1 && pokemonId == currentPlayer1PokemonId) return;
        if (!isPlayer1 && pokemonId == currentPlayer2PokemonId) return;

        if (isPlayer1) currentPlayer1PokemonId = pokemonId;
        else currentPlayer2PokemonId = pokemonId;

        int size = 140;
        String cacheKey = pokemonId + "_" + (isPlayer1 ? "back" : "front") + "_" + size;

        if (imageCache.containsKey(cacheKey)) {
            imageLabel.setIcon(imageCache.get(cacheKey));
            return;
        }

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    String animatedUrl = isPlayer1 ? pokemon.getBackGIF() : pokemon.getFrontGIF();
                    if (animatedUrl.endsWith(".gif")) {
                        URL url = new URL(animatedUrl);
                        ImageIcon gifIcon = new ImageIcon(url);
                        if (gifIcon.getIconWidth() > 0) {
                            Image scaledImage = gifIcon.getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
                            return new ImageIcon(scaledImage);
                        }
                    }

                    String imageUrl;
                    if (isPlayer1) {
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + pokemon.getId() + ".png";
                    } else {
                        imageUrl = pokemon.getSpriteUrl();
                    }
                    URL url = new URL(imageUrl);
                    Image image = ImageIO.read(url);
                    if (image != null) {
                        Image scaledImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
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
                        imageCache.put(cacheKey, icon);
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

        if (actingPlayer == null || actingPlayer.getActivePokemon() == null) return;
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) return;
        if (processingTurn || isPlayer1 != player1Turn) return;

        Pokemon activePokemon = actingPlayer.getActivePokemon();
        List<String> moves = activePokemon.getMoves();
        if (moves == null || moveIndex >= moves.size()) return;

        processingTurn = true;

        try {
            String moveName = moves.get(moveIndex);
            Move move = loadMove(moveName);

            int turnNumber = nextTurnNumber();
            MoveTurn turn = new MoveTurn(turnNumber, actingPlayer, turnNumber, move, targetPlayer);
            battlePlayerController.battle(turn);

            BattlePlayerState latestState = battlePlayerViewModel.getState();
            if (latestState.isBattleEnded() || !"IN_PROGRESS".equals(latestState.getBattleStatus())) return;

            setPlayerTurn(!player1Turn);
        } finally {
            processingTurn = false;
        }
    }

    private void executeSwitchForPlayer(boolean isPlayer1, Pokemon newPokemon) {
        Player actingPlayer = isPlayer1 ? currentUserAdapter : opponentPlayer;
        Player targetPlayer = isPlayer1 ? opponentPlayer : currentUserAdapter;

        if (actingPlayer == null) return;
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) return;
        if (processingTurn || isPlayer1 != player1Turn) return;

        Pokemon previousPokemon = actingPlayer.getActivePokemon();
        int turnNumber = nextTurnNumber();
        SwitchTurn turn = new SwitchTurn(turnNumber, actingPlayer, turnNumber, previousPokemon, newPokemon);
        actingPlayer.switchPokemon(newPokemon);
        battlePlayerController.battle(turn);

        BattlePlayerState latestState = battlePlayerViewModel.getState();
        if (latestState.isBattleEnded() || !"IN_PROGRESS".equals(latestState.getBattleStatus())) return;

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

            updatePlayerInfo(user1, currentUserAdapter, true);
            updatePlayerInfo(user2, opponentPlayer, false);
        }

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
        JProgressBar hpBar = isPlayer1 ? player1HPBar : player2HPBar;

        if (activePokemon != null) {
            nameLabel.setText(activePokemon.getName().toUpperCase());
            loadPokemonImage(imageLabel, activePokemon, isPlayer1);

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
            int hpPercent = maxHP > 0 ? (currentHP * 100 / maxHP) : 0;
            hpBar.setValue(hpPercent);

            // Update HP bar color
            if (hpPercent > 50) {
                hpBar.setForeground(new Color(76, 175, 80)); // Green
            } else if (hpPercent > 25) {
                hpBar.setForeground(new Color(255, 193, 7)); // Yellow
            } else {
                hpBar.setForeground(new Color(244, 67, 54)); // Red
            }

            updateMovesDisplay(activePokemon, isPlayer1);
            updateTeamDisplay(user.getOwnedPokemon(), activePokemon, isPlayer1);
        } else {
            nameLabel.setText("---");
            imageLabel.setIcon(null);
            hpLabel.setText("0 / 0");
        }
    }

    private void updateMovesDisplay(Pokemon pokemon, boolean isPlayer1) {
        List<String> moves = pokemon.getMoves();
        JButton[] buttons = isPlayer1 ? player1MoveButtons : player2MoveButtons;
        boolean canAct = isPlayer1 == player1Turn;

        for (int i = 0; i < 4; i++) {
            if (moves != null && i < moves.size()) {
                String moveName = moves.get(i);
                buttons[i].setText(moveName.toUpperCase());
                buttons[i].setEnabled(canAct && currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus()));
            } else {
                buttons[i].setText("-");
                buttons[i].setEnabled(false);
            }
        }
    }

    private void updateTeamDisplay(List<Pokemon> team, Pokemon activePokemon, boolean isPlayer1) {
        JPanel teamPanel = isPlayer1 ? player1TeamPanel : player2TeamPanel;
        teamPanel.removeAll();

        JLabel label = new JLabel("Team:");
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        teamPanel.add(label);

        if (team != null) {
            boolean canAct = isPlayer1 == player1Turn && currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus());

            for (Pokemon pokemon : team) {
                JButton btn = new JButton(pokemon.getName());
                btn.setFont(new Font("SansSerif", Font.PLAIN, 10));
                btn.setMargin(new Insets(2, 6, 2, 6));
                btn.setFocusPainted(false);

                if (pokemon == activePokemon) {
                    btn.setEnabled(false);
                    btn.setBackground(new Color(200, 230, 200));
                } else if (pokemon.isFainted()) {
                    btn.setEnabled(false);
                    btn.setBackground(Color.LIGHT_GRAY);
                } else {
                    btn.setEnabled(canAct);
                    btn.addActionListener(e -> executeSwitchForPlayer(isPlayer1, pokemon));
                }

                teamPanel.add(btn);
            }
        }

        teamPanel.revalidate();
        teamPanel.repaint();
    }

    private void updateControlsEnabled() {
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            disableAllControls();
            return;
        }

        for (JButton btn : player1MoveButtons) {
            btn.setEnabled(player1Turn);
        }
        for (JButton btn : player2MoveButtons) {
            btn.setEnabled(!player1Turn);
        }
    }

    private void disableAllControls() {
        for (JButton btn : player1MoveButtons) {
            btn.setEnabled(false);
        }
        for (JButton btn : player2MoveButtons) {
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

        JDialog dialog = new JDialog(this, "Battle Result", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel winLabel = new JLabel(winnerText);
        winLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        winLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(winLabel);

        content.add(Box.createVerticalStrut(25));

        JButton playAgainBtn = new JButton("Play Again");
        playAgainBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        playAgainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainBtn.addActionListener(e -> {
            dialog.dispose();
            dispose();
            if (playAgainHandler != null) playAgainHandler.run();
        });
        content.add(playAgainBtn);

        content.add(Box.createVerticalStrut(10));

        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> {
            dialog.dispose();
            dispose();
        });
        content.add(exitBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
