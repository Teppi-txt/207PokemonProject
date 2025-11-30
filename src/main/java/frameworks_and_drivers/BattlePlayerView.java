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
import java.awt.GradientPaint;
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
    private final Runnable playAgainHandler;

    // UI Components - Player 1
    private JLabel player1PokemonNameLabel;
    private JLabel player1PokemonImageLabel;
    private JProgressBar player1HPBar;
    private JLabel player1HPLabel;
    private JButton[] player1MoveButtons = new JButton[4];
    private JPanel player1TeamPanel;

    // UI Components - Player 2
    private JLabel player2PokemonNameLabel;
    private JLabel player2PokemonImageLabel;
    private JProgressBar player2HPBar;
    private JLabel player2HPLabel;
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
        // Create info boxes for both players
        JPanel player1InfoBox = createPokemonInfoBox(true);
        JPanel player2InfoBox = createPokemonInfoBox(false);

        player1PokemonImageLabel = new JLabel();
        player1PokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        player2PokemonImageLabel = new JLabel();
        player2PokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // Draw battle background gradient (sky)
                GradientPaint skyGradient = new GradientPaint(
                    0, 0, new Color(135, 206, 235),
                    0, h / 2, new Color(176, 224, 230)
                );
                g2d.setPaint(skyGradient);
                g2d.fillRect(0, 0, w, h / 2);

                // Draw ground
                GradientPaint groundGradient = new GradientPaint(
                    0, h / 2, new Color(144, 238, 144),
                    0, h, new Color(34, 139, 34)
                );
                g2d.setPaint(groundGradient);
                g2d.fillRect(0, h / 2, w, h / 2);

                // Player 1 platform (bottom left)
                int player1PlatformX = (int)(w * 0.08);
                int player1PlatformY = h - 70;
                int player1PlatformW = 220;
                int player1PlatformH = 55;

                g2d.setColor(new Color(139, 90, 43));
                g2d.fillOval(player1PlatformX, player1PlatformY, player1PlatformW, player1PlatformH);
                g2d.setColor(new Color(160, 120, 80));
                g2d.fillOval(player1PlatformX + 5, player1PlatformY + 5, player1PlatformW - 10, player1PlatformH - 10);

                // Player 2 platform (top right)
                int player2PlatformX = (int)(w * 0.65);
                int player2PlatformY = (int)(h * 0.42);
                int player2PlatformW = 180;
                int player2PlatformH = 45;

                g2d.setColor(new Color(139, 90, 43));
                g2d.fillOval(player2PlatformX, player2PlatformY, player2PlatformW, player2PlatformH);
                g2d.setColor(new Color(160, 120, 80));
                g2d.fillOval(player2PlatformX + 5, player2PlatformY + 5, player2PlatformW - 10, player2PlatformH - 10);
            }

            @Override
            public void doLayout() {
                super.doLayout();
                int w = getWidth();
                int h = getHeight();

                // Position Player 2 info box (top left - opponent)
                player2InfoBox.setBounds(15, 10, 260, 75);

                // Position Player 2 Pokemon (above Player 2 platform - opponent)
                int player2PlatformX = (int)(w * 0.65);
                int player2PlatformY = (int)(h * 0.42);
                player2PokemonImageLabel.setBounds(player2PlatformX + 15, player2PlatformY - 130, 150, 150);

                // Position Player 1 info box (bottom right)
                player1InfoBox.setBounds(w - 275, h - 95, 260, 75);

                // Position Player 1 Pokemon (above Player 1 platform)
                int player1PlatformX = (int)(w * 0.08);
                int player1PlatformY = h - 70;
                player1PokemonImageLabel.setBounds(player1PlatformX + 10, player1PlatformY - 180, 200, 200);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(880, 300));

        panel.add(player1PokemonImageLabel);
        panel.add(player1InfoBox);
        panel.add(player2PokemonImageLabel);
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

                // Outer border
                g2d.setColor(new Color(64, 64, 64));
                g2d.fillRect(0, 0, w, h);

                // Inner background
                g2d.setColor(new Color(248, 248, 248));
                g2d.fillRect(4, 4, w - 8, h - 8);

                // 3D effect
                g2d.setColor(Color.WHITE);
                g2d.drawLine(4, 4, w - 4, 4);
                g2d.drawLine(4, 4, 4, h - 4);
                g2d.setColor(new Color(180, 180, 180));
                g2d.drawLine(w - 4, 4, w - 4, h - 4);
                g2d.drawLine(4, h - 4, w - 4, h - 4);
            }
        };
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        box.setOpaque(false);

        // Pokemon name with player indicator
        JLabel nameLabel = new JLabel(isPlayer1 ? "PLAYER 1" : "PLAYER 2");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(nameLabel);

        if (isPlayer1) {
            player1PokemonNameLabel = nameLabel;
        } else {
            player2PokemonNameLabel = nameLabel;
        }

        box.add(Box.createVerticalStrut(5));

        // HP label row
        JPanel hpRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hpRow.setOpaque(false);
        hpRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hpText = new JLabel("HP:");
        hpText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hpText.setForeground(Color.BLACK);
        hpRow.add(hpText);

        // HP Bar
        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setStringPainted(false);
        hpBar.setPreferredSize(new Dimension(150, 14));
        hpBar.setForeground(new Color(76, 175, 80));
        hpBar.setBackground(new Color(48, 48, 48));
        hpRow.add(hpBar);

        if (isPlayer1) {
            player1HPBar = hpBar;
        } else {
            player2HPBar = hpBar;
        }

        box.add(hpRow);

        box.add(Box.createVerticalStrut(3));

        // HP numbers
        JLabel hpLabel = new JLabel("100 / 100");
        hpLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hpLabel.setForeground(Color.GRAY);
        hpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(hpLabel);

        if (isPlayer1) {
            player1HPLabel = hpLabel;
        } else {
            player2HPLabel = hpLabel;
        }

        return box;
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
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel label = new JLabel(isPlayer1 ? "PLAYER 1 MOVES" : "PLAYER 2 MOVES");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(Box.createVerticalStrut(10));

        // Moves grid
        JPanel movesPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        movesPanel.setBackground(Color.WHITE);

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
        } else {
            player2MoveButtons = buttons;
        }
        panel.add(movesPanel);

        panel.add(Box.createVerticalStrut(10));

        // Team panel
        JPanel teamPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        teamPanel.setBackground(Color.WHITE);
        JLabel teamLabel = new JLabel("Team:");
        teamLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
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
            } else {
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
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        teamPanel.add(label);

        if (team != null) {
            boolean canAct = isPlayer1 == player1Turn && currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus());

            for (Pokemon pokemon : team) {
                String btnText = pokemon.getName();
                if (pokemon == activePokemon) {
                    btnText += " *";
                } else if (pokemon.isFainted()) {
                    btnText += " X";
                }

                JButton btn = new JButton(btnText);
                btn.setFont(new Font("SansSerif", Font.PLAIN, 10));
                btn.setFocusPainted(false);

                if (pokemon == activePokemon || pokemon.isFainted()) {
                    btn.setEnabled(false);
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
