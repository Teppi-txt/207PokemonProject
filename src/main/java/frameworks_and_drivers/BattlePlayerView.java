package frameworks_and_drivers;

import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerState;
import interface_adapters.battle_player.BattlePlayerViewModel;
import entities.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattlePlayerView extends JFrame {
    private final BattlePlayerController battlePlayerController;
    private final BattlePlayerViewModel battlePlayerViewModel;
    
    // UI Components
    private JLabel battleStatusLabel;
    private JLabel player1NameLabel;
    private JLabel player2NameLabel;
    private JLabel player1PokemonLabel;
    private JLabel player2PokemonLabel;
    private JLabel player1HPLabel;
    private JLabel player2HPLabel;
    private JTextArea turnResultArea;
    private JLabel errorLabel;
    private JPanel battleEndedPanel;
    private JLabel winnerLabel;
    private JPanel mainPanel;
    private JScrollPane scrollPane;

    public BattlePlayerView(BattlePlayerController battlePlayerController, 
                           BattlePlayerViewModel battlePlayerViewModel) {
        this.battlePlayerController = battlePlayerController;
        this.battlePlayerViewModel = battlePlayerViewModel;
        
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
        setSize(800, 600);
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
        
        // Player 1 Panel
        JPanel player1Panel = new JPanel();
        player1Panel.setLayout(new BoxLayout(player1Panel, BoxLayout.Y_AXIS));
        player1Panel.setBorder(new TitledBorder("Player 1"));
        player1Panel.setBackground(new Color(255, 240, 240));
        player1Panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        player1NameLabel = new JLabel("Player: -");
        player1NameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        player1Panel.add(player1NameLabel);
        
        player1Panel.add(Box.createVerticalStrut(5));
        
        player1PokemonLabel = new JLabel("Pokemon: -");
        player1PokemonLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        player1Panel.add(player1PokemonLabel);
        
        player1Panel.add(Box.createVerticalStrut(5));
        
        player1HPLabel = new JLabel("HP: - / -");
        player1HPLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        player1Panel.add(player1HPLabel);
        
        // Player 2 Panel
        JPanel player2Panel = new JPanel();
        player2Panel.setLayout(new BoxLayout(player2Panel, BoxLayout.Y_AXIS));
        player2Panel.setBorder(new TitledBorder("Player 2"));
        player2Panel.setBackground(new Color(240, 240, 255));
        player2Panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        player2NameLabel = new JLabel("Player: -");
        player2NameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        player2Panel.add(player2NameLabel);
        
        player2Panel.add(Box.createVerticalStrut(5));
        
        player2PokemonLabel = new JLabel("Pokemon: -");
        player2PokemonLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        player2Panel.add(player2PokemonLabel);
        
        player2Panel.add(Box.createVerticalStrut(5));
        
        player2HPLabel = new JLabel("HP: - / -");
        player2HPLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        player2Panel.add(player2HPLabel);
        
        panel.add(player1Panel);
        panel.add(player2Panel);
        
        return panel;
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
     * Executes a turn in the battle
     * @param turn The turn to execute
     */
    public void executeTurn(Turn turn) {
        battlePlayerController.battle(turn);
        updateView();
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
            Battle battle = state.getBattle();
            battleStatusLabel.setText("Battle ID: " + battle.getId() + " | Status: " + state.getBattleStatus());
            
            // Update player 1 information
            if (battle.getPlayer1() != null) {
                User user1 = battle.getPlayer1();
                player1NameLabel.setText("Player: " + user1.getName());
                
                // Display first available Pokemon from user's owned Pokemon
                if (user1.getOwnedPokemon() != null && !user1.getOwnedPokemon().isEmpty()) {
                    Pokemon pokemon1 = null;
                    for (Pokemon p : user1.getOwnedPokemon()) {
                        if (!p.isFainted()) {
                            pokemon1 = p;
                            break;
                        }
                    }
                    if (pokemon1 != null) {
                        Stats stats = pokemon1.getStats();
                        player1PokemonLabel.setText("Pokemon: " + pokemon1.getName() + 
                            (pokemon1.isShiny() ? " ✨" : ""));
                        int currentHP = Math.max(0, stats.getHp());
                        int maxHP = stats.getHp(); // Assuming max HP equals current HP initially
                        player1HPLabel.setText(String.format("HP: %d / %d", currentHP, maxHP));
                    } else {
                        player1PokemonLabel.setText("Pokemon: None (All Fainted)");
                        player1HPLabel.setText("HP: 0 / 0");
                    }
                } else {
                    player1PokemonLabel.setText("Pokemon: None");
                    player1HPLabel.setText("HP: - / -");
                }
            }
            
            // Update player 2 information
            if (battle.getPlayer2() != null) {
                User user2 = battle.getPlayer2();
                player2NameLabel.setText("Player: " + user2.getName());
                
                // Display first available Pokemon from user's owned Pokemon
                if (user2.getOwnedPokemon() != null && !user2.getOwnedPokemon().isEmpty()) {
                    Pokemon pokemon2 = null;
                    for (Pokemon p : user2.getOwnedPokemon()) {
                        if (!p.isFainted()) {
                            pokemon2 = p;
                            break;
                        }
                    }
                    if (pokemon2 != null) {
                        Stats stats = pokemon2.getStats();
                        player2PokemonLabel.setText("Pokemon: " + pokemon2.getName() + 
                            (pokemon2.isShiny() ? " ✨" : ""));
                        int currentHP = Math.max(0, stats.getHp());
                        int maxHP = stats.getHp();
                        player2HPLabel.setText(String.format("HP: %d / %d", currentHP, maxHP));
                    } else {
                        player2PokemonLabel.setText("Pokemon: None (All Fainted)");
                        player2HPLabel.setText("HP: 0 / 0");
                    }
                } else {
                    player2PokemonLabel.setText("Pokemon: None");
                    player2HPLabel.setText("HP: - / -");
                }
            }
        } else {
            battleStatusLabel.setText("Status: " + (state.getBattleStatus() != null ? state.getBattleStatus() : "Unknown"));
        }
        
        // Update turn result
        if (state.getTurnResult() != null && !state.getTurnResult().isEmpty()) {
            turnResultArea.setText(state.getTurnResult());
        } else if (state.getTurn() != null) {
            // Show turn details if available
            turnResultArea.setText(state.getTurn().getTurnDetails());
        }
        
        // Refresh the UI
        revalidate();
        repaint();
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

