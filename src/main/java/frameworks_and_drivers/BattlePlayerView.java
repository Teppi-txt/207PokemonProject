package frameworks_and_drivers;

import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerState;
import interface_adapters.battle_player.BattlePlayerViewModel;
import entities.*;
import use_case.battle_player.BattlePlayerUserDataAccessInterface;
import pokeapi.JSONLoader;
import pokeapi.PokeAPIFetcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import java.awt.image.BufferedImage;

public class BattlePlayerView extends JFrame implements PropertyChangeListener {
    private final BattlePlayerController battlePlayerController;
    private final BattlePlayerViewModel battlePlayerViewModel;
    private final BattlePlayerUserDataAccessInterface dataAccess;
    private final Runnable playAgainHandler;
    
    // UI Components - Status
    private JLabel battleStatusLabel;
    private JButton playAgainButton;
    
    // UI Components - Player 1
    private JPanel player1Panel;
    private JLabel player1NameLabel;
    private JLabel player1PokemonImageLabel;
    private JLabel player1PokemonNameLabel;
    private JLabel player1HPLabel;
    private JProgressBar player1HPBar;
    private JPanel player1MovesPanel;
    private JPanel player1TeamPanel;
    private JButton player1QuitButton;
    
    // UI Components - Player 2
    private JPanel player2Panel;
    private JLabel player2NameLabel;
    private JLabel player2PokemonImageLabel;
    private JLabel player2PokemonNameLabel;
    private JLabel player2HPLabel;
    private JProgressBar player2HPBar;
    private JPanel player2MovesPanel;
    private JPanel player2TeamPanel;
    private JButton player2QuitButton;
    
    // UI Components - Battle Info
    private JTextArea turnResultArea;
    private JLabel errorLabel;
    private JPanel battleEndedPanel;
    private JLabel winnerLabel;
    
    // UI Components - Main
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    
    // Current battle state
    private Battle currentBattle;
    private User currentUser; // The user playing (not AI)
    private UserPlayerAdapter currentUserAdapter;
    private Player opponentPlayer;
    private boolean processingTurn = false;
    private boolean player1Turn = true;
    
    // Turn counter
    private int turnCounter = 1;
    
    // Track max HP for each Pokemon (by Pokemon object reference)
    private Map<Pokemon, Integer> maxHPMap = new HashMap<>();

    public BattlePlayerView(BattlePlayerController battlePlayerController,
                           BattlePlayerViewModel battlePlayerViewModel,
                           BattlePlayerUserDataAccessInterface dataAccess,
                           Runnable playAgainHandler) {
        this.battlePlayerController = battlePlayerController;
        this.battlePlayerViewModel = battlePlayerViewModel;
        this.dataAccess = dataAccess;
        this.playAgainHandler = playAgainHandler;
        
        // Register as listener for ViewModel changes
        battlePlayerViewModel.addPropertyChangeListener(this);
        
        initializeGUI();
    }

    // build the gui
    private void initializeGUI() {
        setTitle("Pokemon Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Battle Status Panel
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel);
        
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Battle Arena Panel (Players and Pokemon)
        JPanel arenaPanel = createArenaPanel();
        mainPanel.add(arenaPanel);
        
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Turn Result Panel
        JPanel resultPanel = createResultPanel();
        mainPanel.add(resultPanel);
        
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Error Panel
        JPanel errorPanel = createErrorPanel();
        mainPanel.add(errorPanel);
        
        // Battle Ended Panel
        battleEndedPanel = createBattleEndedPanel();
        battleEndedPanel.setVisible(false);
        mainPanel.add(battleEndedPanel);
        
        // Add scroll pane
        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Set window properties
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // battle status panel
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("Battle Status"));
        panel.setBackground(Color.WHITE);
        
        battleStatusLabel = new JLabel("Status: Waiting for battle to start...");
        battleStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(battleStatusLabel);
        
        return panel;
    }

    // arena panel with both players
    private JPanel createArenaPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(new TitledBorder("Battle Arena"));
        panel.setBackground(Color.WHITE);
        
        // Player 1 Panel (Left side - User)
        player1Panel = createPlayerPanel(true);
        panel.add(player1Panel);
        
        // Player 2 Panel (Right side - Opponent)
        player2Panel = createPlayerPanel(false);
        panel.add(player2Panel);
        
        return panel;
    }

    // player panel with image/info/controls
    private JPanel createPlayerPanel(boolean isUser) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(isUser ? "You" : "Opponent"));
        panel.setBackground(isUser ? new Color(255, 240, 240) : new Color(240, 240, 255));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Player name
        JLabel nameLabel = new JLabel("Player: -");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        if (isUser) {
            player1NameLabel = nameLabel;
        } else {
            player2NameLabel = nameLabel;
        }

        panel.add(Box.createVerticalStrut(5));

        JButton quitButton = new JButton(isUser ? "Quit Battle (Player 1)" : "Quit Battle (Player 2)");
        quitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        quitButton.addActionListener(e -> handleQuit(isUser));
        panel.add(quitButton);
        if (isUser) {
            player1QuitButton = quitButton;
        } else {
            player2QuitButton = quitButton;
        }
        
        panel.add(Box.createVerticalStrut(10));
        
        // Pokemon image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(200, 200));
        imageLabel.setMinimumSize(new Dimension(200, 200));
        imageLabel.setMaximumSize(new Dimension(200, 200));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageLabel.setIcon(createPlaceholderIcon());
        panel.add(imageLabel);
        if (isUser) {
            player1PokemonImageLabel = imageLabel;
        } else {
            player2PokemonImageLabel = imageLabel;
        }
        
        panel.add(Box.createVerticalStrut(5));
        
        // Pokemon name
        JLabel pokemonNameLabel = new JLabel("Pokemon: -");
        pokemonNameLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        pokemonNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(pokemonNameLabel);
        if (isUser) {
            player1PokemonNameLabel = pokemonNameLabel;
        } else {
            player2PokemonNameLabel = pokemonNameLabel;
        }
        
        panel.add(Box.createVerticalStrut(5));
        
        // HP Label
        JLabel hpLabel = new JLabel("HP: - / -");
        hpLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        hpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hpLabel);
        if (isUser) {
            player1HPLabel = hpLabel;
        } else {
            player2HPLabel = hpLabel;
        }
        
        panel.add(Box.createVerticalStrut(5));
        
        // HP Bar
        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(true);
        hpBar.setPreferredSize(new Dimension(180, 20));
        hpBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hpBar);
        if (isUser) {
            player1HPBar = hpBar;
        } else {
            player2HPBar = hpBar;
        }
        
        // Moves panel (only for user)
        if (isUser) {
            panel.add(Box.createVerticalStrut(10));
            JPanel movesPanel = createMovesPanel();
            panel.add(movesPanel);
            player1MovesPanel = movesPanel;
            
            panel.add(Box.createVerticalStrut(10));
            JPanel teamPanel = createTeamPanel();
            panel.add(teamPanel);
            player1TeamPanel = teamPanel;
        } else {
            panel.add(Box.createVerticalStrut(10));
            JPanel movesPanel = createMovesPanel();
            panel.add(movesPanel);
            player2MovesPanel = movesPanel;
            
            panel.add(Box.createVerticalStrut(10));
            JPanel teamPanel = createTeamPanel();
            panel.add(teamPanel);
            player2TeamPanel = teamPanel;
        }
        
        return panel;
    }

    // moves list container
    private JPanel createMovesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Moves"));
        panel.setBackground(new Color(255, 255, 200));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Moves will be added dynamically
        JLabel placeholder = new JLabel("Select a Pokemon to see moves");
        placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(placeholder);
        
        return panel;
    }

    // team list container
    private JPanel createTeamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Your Team"));
        panel.setBackground(new Color(200, 255, 200));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Team will be added dynamically
        JLabel placeholder = new JLabel("No Pokemon in team");
        placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(placeholder);
        
        return panel;
    }

    // simple placeholder sprite
    private ImageIcon createPlaceholderIcon() {
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, 200, 200);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(0, 0, 199, 199);
        g2.dispose();
        return new ImageIcon(img);
    }

    // load a pokemon sprite from url
    private void loadPokemonImage(JLabel imageLabel, Pokemon pokemon) {
        if (pokemon == null) {
            imageLabel.setIcon(createPlaceholderIcon());
            return;
        }

        if (Boolean.getBoolean("offline")) {
            imageLabel.setIcon(createPlaceholderIcon());
            return;
        }

        ImageIcon placeholder = createPlaceholderIcon();
        imageLabel.setIcon(placeholder);

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    String imageUrl = pokemon.getSpriteUrl();
                    URL url = new URL(imageUrl);
                    Image image = ImageIO.read(url);
                    if (image != null) {
                        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading Pokemon image: " + e.getMessage());
                }
                return placeholder;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    imageLabel.setIcon(icon);
                } catch (Exception e) {
                    imageLabel.setIcon(placeholder);
                }
            }
        };
        worker.execute();
    }

    // turn result panel
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Turn Results"));
        panel.setBackground(Color.WHITE);
        
        turnResultArea = new JTextArea(5, 30);
        turnResultArea.setEditable(false);
        turnResultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        turnResultArea.setBackground(new Color(250, 250, 250));
        turnResultArea.setText("No turn executed yet.");
        
        JScrollPane scrollPane = new JScrollPane(turnResultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // error banner
    private JPanel createErrorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 12));
        errorLabel.setVisible(false);
        panel.add(errorLabel);
        
        return panel;
    }

    // winner banner
    private JPanel createBattleEndedPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Battle Result"));
        panel.setBackground(new Color(255, 255, 200));
        
        winnerLabel = new JLabel("");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        winnerLabel.setForeground(new Color(0, 100, 0));
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(winnerLabel);

        panel.add(Box.createVerticalStrut(10));

        playAgainButton = new JButton("Play Again");
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainButton.addActionListener(e -> handlePlayAgain());
        panel.add(playAgainButton);
        
        return panel;
    }

    // refresh move buttons for a pokemon
    private void updateMovesPanel(JPanel targetPanel, Pokemon activePokemon, boolean enableButtons, boolean isPlayer1Panel) {
        targetPanel.removeAll();
        
        if (activePokemon == null || activePokemon.getMoves() == null || activePokemon.getMoves().isEmpty()) {
            JLabel noMoves = new JLabel("No moves available");
            noMoves.setAlignmentX(Component.LEFT_ALIGNMENT);
            targetPanel.add(noMoves);
        } else {
            PokeAPIFetcher fetcher = new PokeAPIFetcher();
            for (String moveName : activePokemon.getMoves()) {
                JButton moveButton = new JButton(moveName);
                moveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                moveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                moveButton.setEnabled(enableButtons);
                moveButton.addActionListener(e -> {
                    if (isPlayer1Panel) {
                        executeMoveForPlayer(currentUserAdapter, opponentPlayer, true, moveName);
                    } else {
                        executeMoveForPlayer(opponentPlayer, currentUserAdapter, false, moveName);
                    }
                });
                targetPanel.add(moveButton);
                targetPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    // refresh team list for switches
    private void updateTeamPanel(JPanel targetPanel, User user, Pokemon activePokemon, boolean enableButtons, boolean isPlayer1Panel) {
        targetPanel.removeAll();
        
        if (user == null || user.getOwnedPokemon() == null || user.getOwnedPokemon().isEmpty()) {
            JLabel noTeam = new JLabel("No Pokemon in team");
            noTeam.setAlignmentX(Component.LEFT_ALIGNMENT);
            targetPanel.add(noTeam);
        } else {
            for (Pokemon pokemon : user.getOwnedPokemon()) {
                JPanel pokemonCard = new JPanel(new BorderLayout());
                pokemonCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                pokemonCard.setBackground(pokemon.isFainted() ? Color.LIGHT_GRAY : Color.WHITE);
                
                JLabel pokemonInfo = new JLabel(pokemon.getName() + 
                    (pokemon.isShiny() ? " ✨" : "") + 
                    (pokemon == activePokemon ? " (Active)" : "") +
                    (pokemon.isFainted() ? " (Fainted)" : ""));
                pokemonInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                if (pokemon != activePokemon && !pokemon.isFainted()) {
                    JButton switchButton = new JButton("Switch");
                    switchButton.setEnabled(enableButtons);
                    switchButton.addActionListener(e -> {
                        if (isPlayer1Panel) {
                            executeSwitchForPlayer(currentUserAdapter, opponentPlayer, true, pokemon);
                        } else {
                            executeSwitchForPlayer(opponentPlayer, currentUserAdapter, false, pokemon);
                        }
                    });
                    pokemonCard.add(pokemonInfo, BorderLayout.CENTER);
                    pokemonCard.add(switchButton, BorderLayout.EAST);
                } else {
                    pokemonCard.add(pokemonInfo, BorderLayout.CENTER);
                }
                
                pokemonCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                pokemonCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                targetPanel.add(pokemonCard);
                targetPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    // run a move for the acting player
    private void executeMoveForPlayer(Player actingPlayer, Player targetPlayer, boolean isPlayer1, String moveName) {
        if (actingPlayer == null || actingPlayer.getActivePokemon() == null) {
            displayError("No active Pokemon!");
            return;
        }
        
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            displayError("Battle is not in progress!");
            return;
        }
        
        if (processingTurn || isPlayer1 != player1Turn) {
            return;
        }
        processingTurn = true;

        try {
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

    // switch active pokemon for the acting player
    private void executeSwitchForPlayer(Player actingPlayer, Player targetPlayer, boolean isPlayer1, Pokemon newPokemon) {
        if (actingPlayer == null) {
            displayError("No active player!");
            return;
        }
        
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            displayError("Battle is not in progress!");
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
        if (JSONLoader.allMoves.isEmpty()) {
            try {
                JSONLoader.loadMoves();
            } catch (Exception ignored) {
                // ignore and fall through to default handling
            }
        }

        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                return move;
            }
        }

        try {
            return PokeAPIFetcher.getMove(moveName);
        } catch (Exception e) {
            // Fallback so the battle can continue even if network/API fails
            return new Move()
                    .setName(moveName)
                    .setType("normal")
                    .setPower(40)
                    .setAccuracy(100)
                    .setPriority(0);
        }
    }

    private void handleQuit(boolean quittingPlayer1) {
        if (currentBattle == null || "COMPLETED".equals(currentBattle.getBattleStatus())) {
            return;
        }
        User quitter = quittingPlayer1 ? currentBattle.getPlayer1() : currentBattle.getPlayer2();
        User winner = quittingPlayer1 ? currentBattle.getPlayer2() : currentBattle.getPlayer1();
        if (winner == null) {
            return;
        }

        currentBattle.endBattle(winner);
        winner.addCurrency(500);
        if (quitter != null) {
            quitter.addCurrency(100);
        }
        if (dataAccess != null) {
            dataAccess.saveBattle(currentBattle);
            dataAccess.saveUser(winner);
            if (quitter != null) {
                dataAccess.saveUser(quitter);
            }
        }

        player1Turn = false;

        BattlePlayerState state = new BattlePlayerState();
        state.setBattle(currentBattle);
        state.setBattleStatus(currentBattle.getBattleStatus());
        state.setBattleEnded(true);
        String quitterName = quitter != null ? quitter.getName() : "Player";
        String winnerName = winner.getName();
        state.setTurnResult(quitterName + " forfeited. " + winnerName + " wins!");
        battlePlayerViewModel.setState(state);
    }

    private void handlePlayAgain() {
        dispose();
        if (playAgainHandler != null) {
            playAgainHandler.run();
        }
    }

    private int nextTurnNumber() {
        return turnCounter++;
    }

    private void setPlayerTurn(boolean player1TurnNow) {
        this.player1Turn = player1TurnNow;
        updateControlsEnabled();
    }

    // listen for view model changes
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BattlePlayerViewModel.STATE_PROPERTY)) {
        updateView();
        }
    }

    // push state to the ui
    public void updateView() {
        BattlePlayerState state = battlePlayerViewModel.getState();
        
        // Clear error first
        errorLabel.setVisible(false);
        errorLabel.setText("");
        
        if (state.getErrorMessage() != null) {
            displayError(state.getErrorMessage());
            return;
        }

        if (state.isBattleEnded()) {
            displayBattleEnded(state);
        } else {
            displayBattleStatus(state);
        }
    }

    // show current battle state
    private void displayBattleStatus(BattlePlayerState state) {
        battleEndedPanel.setVisible(false);
        
        // Update battle status
        if (state.getBattle() != null) {
            currentBattle = state.getBattle();
            battleStatusLabel.setText("Battle ID: " + currentBattle.getId() + " | Status: " + state.getBattleStatus());
            
            // Determine which player is the current user
            User user1 = currentBattle.getPlayer1();
            User user2 = currentBattle.getPlayer2();
            
            if (currentUserAdapter == null) {
                currentUser = user1;
                currentUserAdapter = new UserPlayerAdapter(currentUser);
            }
            if (opponentPlayer == null) {
                opponentPlayer = new UserPlayerAdapter(user2);
            }
            
            // Update player 1 (user) information
            updatePlayerPanel(user1, currentUserAdapter, true);
            
            // Update player 2 (opponent) information
            updatePlayerPanel(user2, opponentPlayer, false);
        } else {
            battleStatusLabel.setText("Status: " + (state.getBattleStatus() != null ? state.getBattleStatus() : "Unknown"));
        }
        
        // Update turn result
        if (state.getTurnResult() != null && !state.getTurnResult().isEmpty()) {
            turnResultArea.setText(state.getTurnResult());
        } else if (state.getTurn() != null) {
            turnResultArea.setText(state.getTurn().getTurnDetails());
        }
        updateControlsEnabled();
        // Refresh the UI
        revalidate();
        repaint();
    }

    // update labels, hp, moves, and team for one player
    private void updatePlayerPanel(User user, Player playerAdapter, boolean isUser) {
        if (user == null) {
            return;
        }
        
        // Update player name
        if (isUser) {
            player1NameLabel.setText("Player: " + user.getName());
        } else {
            player2NameLabel.setText("Player: " + user.getName());
        }
        
        // Get active Pokemon
        Pokemon activePokemon = null;
        if (playerAdapter != null) {
            activePokemon = playerAdapter.getActivePokemon();
        }
        
        // If no active Pokemon, find first available
        if (activePokemon == null && user.getOwnedPokemon() != null) {
            for (Pokemon p : user.getOwnedPokemon()) {
                if (!p.isFainted()) {
                    activePokemon = p;
                    if (playerAdapter != null && playerAdapter instanceof UserPlayerAdapter) {
                        ((UserPlayerAdapter) playerAdapter).switchPokemon(p);
                    }
                    break;
                }
            }
        }
        
        if (activePokemon != null) {
            // Update Pokemon image
            if (isUser) {
                loadPokemonImage(player1PokemonImageLabel, activePokemon);
                player1PokemonNameLabel.setText("Pokemon: " + activePokemon.getName() + 
                    (activePokemon.isShiny() ? " ✨" : ""));
            } else {
                loadPokemonImage(player2PokemonImageLabel, activePokemon);
                player2PokemonNameLabel.setText("Pokemon: " + activePokemon.getName() + 
                    (activePokemon.isShiny() ? " ✨" : ""));
            }
            
            // Update HP
            Stats stats = activePokemon.getStats();
            int currentHP = Math.max(0, stats.getHp());
            
            // Track max HP - use current HP as max if we haven't seen this Pokemon before
            // or if current HP is higher than our tracked max
            int maxHP = maxHPMap.getOrDefault(activePokemon, currentHP);
            if (currentHP > maxHP) {
                maxHP = currentHP;
                maxHPMap.put(activePokemon, maxHP);
            } else if (!maxHPMap.containsKey(activePokemon)) {
                // First time seeing this Pokemon - assume current HP is max
                maxHPMap.put(activePokemon, currentHP);
                maxHP = currentHP;
            }
            
            if (isUser) {
                player1HPLabel.setText(String.format("HP: %d / %d", currentHP, maxHP));
                int hpPercentage = maxHP > 0 ? (int) ((currentHP * 100.0) / maxHP) : 0;
                player1HPBar.setValue(hpPercentage);
                player1HPBar.setString(String.format("%d/%d", currentHP, maxHP));
                
                // Update color based on HP
                if (hpPercentage > 50) {
                    player1HPBar.setForeground(Color.GREEN);
                } else if (hpPercentage > 25) {
                    player1HPBar.setForeground(Color.YELLOW);
                } else {
                    player1HPBar.setForeground(Color.RED);
                }
                
                // Update moves and team panels
                updateMovesPanel(player1MovesPanel, activePokemon, player1Turn, true);
                updateTeamPanel(player1TeamPanel, user, activePokemon, player1Turn, true);
            } else {
                player2HPLabel.setText(String.format("HP: %d / %d", currentHP, maxHP));
                int hpPercentage = maxHP > 0 ? (int) ((currentHP * 100.0) / maxHP) : 0;
                player2HPBar.setValue(hpPercentage);
                player2HPBar.setString(String.format("%d/%d", currentHP, maxHP));
                
                // Update color based on HP
                if (hpPercentage > 50) {
                    player2HPBar.setForeground(Color.GREEN);
                } else if (hpPercentage > 25) {
                    player2HPBar.setForeground(Color.YELLOW);
                } else {
                    player2HPBar.setForeground(Color.RED);
                }

                updateMovesPanel(player2MovesPanel, activePokemon, !player1Turn, false);
                updateTeamPanel(player2TeamPanel, user, activePokemon, !player1Turn, false);
            }
        } else {
            // No active Pokemon
            if (isUser) {
                loadPokemonImage(player1PokemonImageLabel, null);
                player1PokemonNameLabel.setText("Pokemon: None (All Fainted)");
                player1HPLabel.setText("HP: 0 / 0");
                player1HPBar.setValue(0);
                player1HPBar.setString("0/0");
            } else {
                loadPokemonImage(player2PokemonImageLabel, null);
                player2PokemonNameLabel.setText("Pokemon: None (All Fainted)");
                player2HPLabel.setText("HP: 0 / 0");
                player2HPBar.setValue(0);
                player2HPBar.setString("0/0");
            }
        }
    }

    // show final winner banner
    private void displayBattleEnded(BattlePlayerState state) {
        displayBattleStatus(state); // Show final state first
        
        battleEndedPanel.setVisible(true);
        if (playAgainButton != null) {
            playAgainButton.setEnabled(true);
        }
        
        User winner = resolveWinner(state);
        if (winner != null) {
            winnerLabel.setText("Winner: " + winner.getName());
        } else {
            winnerLabel.setText("Battle Ended");
        }
        
        if (state.getTurnResult() != null && !state.getTurnResult().isEmpty()) {
            turnResultArea.setText("FINAL RESULT:\n" + state.getTurnResult());
        }
        
        // Disable move buttons when battle ends
        disableUserControls();
        
        // Refresh the UI
        revalidate();
        repaint();
    }

    private User resolveWinner(BattlePlayerState state) {
        if (state != null && state.getBattle() != null && state.getBattle().getWinner() != null) {
            return state.getBattle().getWinner();
        }
        if (currentBattle != null && currentBattle.getWinner() != null) {
            return currentBattle.getWinner();
        }
        if (dataAccess != null && dataAccess.getBattle() != null) {
            return dataAccess.getBattle().getWinner();
        }
        return null;
    }

    // show an error in the banner and result area
    private void displayError(String errorMessage) {
        errorLabel.setText("Error: " + errorMessage);
        errorLabel.setVisible(true);
        
        // Also show in turn result area
        turnResultArea.setText("ERROR: " + errorMessage);
        
        // Refresh the UI
        revalidate();
        repaint();
    }

    private void updateControlsEnabled() {
        boolean player1Enable = player1Turn && currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus());
        boolean player2Enable = !player1Turn && currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus());
        toggleButtons(player1MovesPanel, player1Enable);
        toggleButtons(player1TeamPanel, player1Enable);
        toggleButtons(player2MovesPanel, player2Enable);
        toggleButtons(player2TeamPanel, player2Enable);
        if (player1QuitButton != null) {
            player1QuitButton.setEnabled(currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus()));
        }
        if (player2QuitButton != null) {
            player2QuitButton.setEnabled(currentBattle != null && "IN_PROGRESS".equals(currentBattle.getBattleStatus()));
        }
    }

    private void disableUserControls() {
        this.player1Turn = false;
        updateControlsEnabled();
    }

    private void toggleButtons(JPanel panel, boolean enable) {
        if (panel == null) {
            return;
        }
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(enable);
            } else if (comp instanceof JPanel) {
                for (Component child : ((JPanel) comp).getComponents()) {
                    if (child instanceof JButton) {
                        child.setEnabled(enable);
                    }
                }
            }
        }
    }
}
