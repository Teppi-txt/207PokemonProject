package frameworks_and_drivers;

import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerState;
import interface_adapters.battle_player.BattlePlayerViewModel;
import entities.*;
import pokeapi.PokeAPIFetcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class BattlePlayerView extends JFrame implements PropertyChangeListener {
    private final BattlePlayerController battlePlayerController;
    private final BattlePlayerViewModel battlePlayerViewModel;
    
    // UI Components - Status
    private JLabel battleStatusLabel;
    
    // UI Components - Player 1
    private JPanel player1Panel;
    private JLabel player1NameLabel;
    private JLabel player1PokemonImageLabel;
    private JLabel player1PokemonNameLabel;
    private JLabel player1HPLabel;
    private JProgressBar player1HPBar;
    private JPanel player1MovesPanel;
    private JPanel player1TeamPanel;
    
    // UI Components - Player 2
    private JPanel player2Panel;
    private JLabel player2NameLabel;
    private JLabel player2PokemonImageLabel;
    private JLabel player2PokemonNameLabel;
    private JLabel player2HPLabel;
    private JProgressBar player2HPBar;
    
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
    
    // Turn counter
    private int turnCounter = 1;
    
    // Track max HP for each Pokemon (by Pokemon object reference)
    private Map<Pokemon, Integer> maxHPMap = new HashMap<>();

    public BattlePlayerView(BattlePlayerController battlePlayerController, 
                           BattlePlayerViewModel battlePlayerViewModel) {
        this.battlePlayerController = battlePlayerController;
        this.battlePlayerViewModel = battlePlayerViewModel;
        
        // Register as listener for ViewModel changes
        battlePlayerViewModel.addPropertyChangeListener(this);
        
        initializeGUI();
    }

    /**
     * Initializes the GUI components
     */
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

    /**
     * Creates the battle status panel
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("Battle Status"));
        panel.setBackground(Color.WHITE);
        
        battleStatusLabel = new JLabel("Status: Waiting for battle to start...");
        battleStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(battleStatusLabel);
        
        return panel;
    }

    /**
     * Creates the arena panel showing both players and their Pokemon
     */
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

    /**
     * Creates a player panel with Pokemon image, info, and controls
     */
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
        }
        
        return panel;
    }

    /**
     * Creates the moves selection panel for the user
     */
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

    /**
     * Creates the team panel for switching Pokemon
     */
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

    /**
     * Creates a placeholder icon for Pokemon images
     */
    private ImageIcon createPlaceholderIcon() {
        ImageIcon icon = new ImageIcon();
        Image img = new ImageIcon().getImage();
        Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    /**
     * Loads and displays a Pokemon image from URL
     */
    private void loadPokemonImage(JLabel imageLabel, Pokemon pokemon) {
        if (pokemon == null) {
            imageLabel.setIcon(createPlaceholderIcon());
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                String imageUrl = pokemon.getSpriteUrl();
                URL url = new URL(imageUrl);
                Image image = ImageIO.read(url);
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    imageLabel.setIcon(createPlaceholderIcon());
                }
            } catch (IOException e) {
                System.err.println("Error loading Pokemon image: " + e.getMessage());
                imageLabel.setIcon(createPlaceholderIcon());
            }
        });
    }

    /**
     * Creates the turn result panel
     */
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

    /**
     * Creates the error panel
     */
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

    /**
     * Creates the battle ended panel
     */
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
        
        return panel;
    }

    /**
     * Updates the moves panel with available moves for the active Pokemon
     */
    private void updateMovesPanel(Pokemon activePokemon) {
        player1MovesPanel.removeAll();
        
        if (activePokemon == null || activePokemon.getMoves() == null || activePokemon.getMoves().isEmpty()) {
            JLabel noMoves = new JLabel("No moves available");
            noMoves.setAlignmentX(Component.LEFT_ALIGNMENT);
            player1MovesPanel.add(noMoves);
        } else {
            // Try to get Move objects from PokeAPIFetcher
            PokeAPIFetcher fetcher = new PokeAPIFetcher();
            for (String moveName : activePokemon.getMoves()) {
                JButton moveButton = new JButton(moveName);
                moveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                moveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                moveButton.addActionListener(e -> executeMove(moveName));
                player1MovesPanel.add(moveButton);
                player1MovesPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        player1MovesPanel.revalidate();
        player1MovesPanel.repaint();
    }

    /**
     * Updates the team panel with available Pokemon for switching
     */
    private void updateTeamPanel(User user, Pokemon activePokemon) {
        player1TeamPanel.removeAll();
        
        if (user == null || user.getOwnedPokemon() == null || user.getOwnedPokemon().isEmpty()) {
            JLabel noTeam = new JLabel("No Pokemon in team");
            noTeam.setAlignmentX(Component.LEFT_ALIGNMENT);
            player1TeamPanel.add(noTeam);
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
                    switchButton.addActionListener(e -> executeSwitch(pokemon));
                    pokemonCard.add(pokemonInfo, BorderLayout.CENTER);
                    pokemonCard.add(switchButton, BorderLayout.EAST);
                } else {
                    pokemonCard.add(pokemonInfo, BorderLayout.CENTER);
                }
                
                pokemonCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                pokemonCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                player1TeamPanel.add(pokemonCard);
                player1TeamPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        player1TeamPanel.revalidate();
        player1TeamPanel.repaint();
    }

    /**
     * Executes a move turn
     */
    private void executeMove(String moveName) {
        if (currentUserAdapter == null || currentUserAdapter.getActivePokemon() == null) {
            displayError("No active Pokemon!");
            return;
        }
        
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            displayError("Battle is not in progress!");
            return;
        }
        
        // Create a Move object
        Move move = new Move()
            .setName(moveName)
            .setType("normal") // Default type, could be improved
            .setPower(40)
            .setAccuracy(100)
            .setPriority(0);
        
        MoveTurn turn = new MoveTurn(turnCounter++, currentUserAdapter, turnCounter, move);
        battlePlayerController.battle(turn);
    }

    /**
     * Executes a switch turn
     */
    private void executeSwitch(Pokemon newPokemon) {
        if (currentUserAdapter == null) {
            displayError("No active player!");
            return;
        }
        
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            displayError("Battle is not in progress!");
            return;
        }
        
        Pokemon previousPokemon = currentUserAdapter.getActivePokemon();
        SwitchTurn turn = new SwitchTurn(turnCounter++, currentUserAdapter, turnCounter, previousPokemon, newPokemon);
        currentUserAdapter.switchPokemon(newPokemon);
        battlePlayerController.battle(turn);
    }

    /**
     * PropertyChangeListener implementation - called when ViewModel state changes
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BattlePlayerViewModel.STATE_PROPERTY)) {
        updateView();
        }
    }

    /**
     * Updates the view based on the current state in the ViewModel
     */
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

    /**
     * Displays the current battle status
     */
    private void displayBattleStatus(BattlePlayerState state) {
        battleEndedPanel.setVisible(false);
        
        // Update battle status
        if (state.getBattle() != null) {
            currentBattle = state.getBattle();
            battleStatusLabel.setText("Battle ID: " + currentBattle.getId() + " | Status: " + state.getBattleStatus());
            
            // Determine which player is the current user
            User user1 = currentBattle.getPlayer1();
            User user2 = currentBattle.getPlayer2();
            
            // For now, assume player1 is the user (could be made configurable)
            if (currentUser == null) {
                currentUser = user1;
                currentUserAdapter = new UserPlayerAdapter(currentUser);
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
        
        // Refresh the UI
        revalidate();
        repaint();
    }

    /**
     * Updates a player panel with Pokemon information
     */
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
                updateMovesPanel(activePokemon);
                updateTeamPanel(user, activePokemon);
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

    /**
     * Displays the battle ended state
     */
    private void displayBattleEnded(BattlePlayerState state) {
        displayBattleStatus(state); // Show final state first
        
        battleEndedPanel.setVisible(true);
        
        if (state.getBattle() != null && state.getBattle().getWinner() != null) {
            User winner = state.getBattle().getWinner();
            winnerLabel.setText("🏆 Winner: " + winner.getName() + " 🏆");
        } else {
            winnerLabel.setText("🏆 Battle Ended 🏆");
        }
        
        if (state.getTurnResult() != null && !state.getTurnResult().isEmpty()) {
            turnResultArea.setText("FINAL RESULT:\n" + state.getTurnResult());
        }
        
        // Disable move buttons when battle ends
        for (Component comp : player1MovesPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(false);
            }
        }
        
        // Refresh the UI
        revalidate();
        repaint();
    }

    /**
     * Displays an error message
     */
    private void displayError(String errorMessage) {
        errorLabel.setText("Error: " + errorMessage);
        errorLabel.setVisible(true);
        
        // Also show in turn result area
        turnResultArea.setText("ERROR: " + errorMessage);
        
        // Refresh the UI
        revalidate();
        repaint();
    }
}
