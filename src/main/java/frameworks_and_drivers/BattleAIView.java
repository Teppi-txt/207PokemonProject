package frameworks_and_drivers;

import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIViewModel;
import entities.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import pokeapi.JSONLoader;

public class BattleAIView extends JFrame implements BattleAIViewModel.ViewModelListener {
    private final BattleAIController controller;
    private BattleAIViewModel viewModel;
    private Runnable returnCallback;

    // UI Components - Status
    private JLabel battleStatusLabel;

    // UI Components - Player (User)
    private JPanel playerPanel;
    private JLabel playerNameLabel;
    private JLabel playerPokemonImageLabel;
    private JLabel playerPokemonNameLabel;
    private JLabel playerHPLabel;
    private JProgressBar playerHPBar;
    private JPanel playerMovesPanel;
    private JPanel playerTeamPanel;

    // UI Components - AI Opponent
    private JPanel aiPanel;
    private JLabel aiNameLabel;
    private JLabel aiPokemonImageLabel;
    private JLabel aiPokemonNameLabel;
    private JLabel aiHPLabel;
    private JProgressBar aiHPBar;
    private JPanel aiTeamPanel;

    // UI Components - Battle Info
    private JTextArea turnResultArea;
    private JLabel errorLabel;
    private JPanel battleEndedPanel;
    private JLabel winnerLabel;

    // UI Components - Main
    private JPanel mainPanel;
    private JScrollPane scrollPane;

    // Track max HP for each Pokemon
    private Map<Pokemon, Integer> maxHPMap = new HashMap<>();

    public BattleAIView(BattleAIController controller) {
        this(controller, null);
    }

    public BattleAIView(BattleAIController controller, Runnable returnCallback) {
        this.controller = controller;
        this.returnCallback = returnCallback;
        initializeGUI();
    }

    public void setViewModel(BattleAIViewModel viewModel) {
        this.viewModel = viewModel;
        if (viewModel != null) {
            viewModel.addListener(this);
            updateDisplay();
        }
    }

    @Override
    public void onViewModelChanged() {
        SwingUtilities.invokeLater(this::updateDisplay);
    }

    private void initializeGUI() {
        setTitle("Pokemon Battle - AI Opponent");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // Battle Arena Panel (Player and AI)
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
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("Battle Status"));
        panel.setBackground(Color.WHITE);

        battleStatusLabel = new JLabel("Status: Waiting for battle to start...");
        battleStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(battleStatusLabel);

        return panel;
    }

    private JPanel createArenaPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(new TitledBorder("Battle Arena"));
        panel.setBackground(Color.WHITE);

        // Player Panel (Left side - User with controls)
        playerPanel = createPlayerPanel();
        panel.add(playerPanel);

        // AI Panel (Right side - AI Opponent, no controls)
        aiPanel = createAIPanel();
        panel.add(aiPanel);

        return panel;
    }

    private JPanel createPlayerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("You"));
        panel.setBackground(new Color(255, 240, 240));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Player name
        playerNameLabel = new JLabel("Player: -");
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        playerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(playerNameLabel);

        panel.add(Box.createVerticalStrut(10));

        // Pokemon image
        playerPokemonImageLabel = new JLabel();
        playerPokemonImageLabel.setPreferredSize(new Dimension(200, 200));
        playerPokemonImageLabel.setMinimumSize(new Dimension(200, 200));
        playerPokemonImageLabel.setMaximumSize(new Dimension(200, 200));
        playerPokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerPokemonImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        playerPokemonImageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        playerPokemonImageLabel.setIcon(createPlaceholderIcon());
        panel.add(playerPokemonImageLabel);

        panel.add(Box.createVerticalStrut(5));

        // Pokemon name
        playerPokemonNameLabel = new JLabel("Pokemon: -");
        playerPokemonNameLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        playerPokemonNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(playerPokemonNameLabel);

        panel.add(Box.createVerticalStrut(5));

        // HP Label
        playerHPLabel = new JLabel("HP: - / -");
        playerHPLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        playerHPLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(playerHPLabel);

        panel.add(Box.createVerticalStrut(5));

        // HP Bar
        playerHPBar = new JProgressBar(0, 100);
        playerHPBar.setStringPainted(true);
        playerHPBar.setPreferredSize(new Dimension(180, 20));
        playerHPBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(playerHPBar);

        // Moves panel
        panel.add(Box.createVerticalStrut(10));
        playerMovesPanel = createMovesPanel();
        panel.add(playerMovesPanel);

        // Team panel
        panel.add(Box.createVerticalStrut(10));
        playerTeamPanel = createTeamPanel("Your Team");
        panel.add(playerTeamPanel);

        return panel;
    }

    private JPanel createAIPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("AI Opponent"));
        panel.setBackground(new Color(240, 240, 255));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // AI name
        aiNameLabel = new JLabel("AI: -");
        aiNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        aiNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(aiNameLabel);

        panel.add(Box.createVerticalStrut(10));

        // Pokemon image
        aiPokemonImageLabel = new JLabel();
        aiPokemonImageLabel.setPreferredSize(new Dimension(200, 200));
        aiPokemonImageLabel.setMinimumSize(new Dimension(200, 200));
        aiPokemonImageLabel.setMaximumSize(new Dimension(200, 200));
        aiPokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aiPokemonImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        aiPokemonImageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        aiPokemonImageLabel.setIcon(createPlaceholderIcon());
        panel.add(aiPokemonImageLabel);

        panel.add(Box.createVerticalStrut(5));

        // Pokemon name
        aiPokemonNameLabel = new JLabel("Pokemon: -");
        aiPokemonNameLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        aiPokemonNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(aiPokemonNameLabel);

        panel.add(Box.createVerticalStrut(5));

        // HP Label
        aiHPLabel = new JLabel("HP: - / -");
        aiHPLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        aiHPLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(aiHPLabel);

        panel.add(Box.createVerticalStrut(5));

        // HP Bar
        aiHPBar = new JProgressBar(0, 100);
        aiHPBar.setStringPainted(true);
        aiHPBar.setPreferredSize(new Dimension(180, 20));
        aiHPBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(aiHPBar);

        // Team panel for AI
        panel.add(Box.createVerticalStrut(10));
        aiTeamPanel = createTeamPanel("AI Team");
        panel.add(aiTeamPanel);

        return panel;
    }

    private JPanel createMovesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Moves"));
        panel.setBackground(new Color(255, 255, 200));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel placeholder = new JLabel("Select a Pokemon to see moves");
        placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(placeholder);

        return panel;
    }

    private JPanel createTeamPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(title));
        panel.setBackground(new Color(200, 255, 200));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel placeholder = new JLabel("No Pokemon in team");
        placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(placeholder);

        return panel;
    }

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

    private void loadPokemonImage(JLabel imageLabel, Pokemon pokemon) {
        if (pokemon == null) {
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

    private JPanel createBattleEndedPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Battle Ended"));
        panel.setBackground(new Color(255, 255, 200));

        winnerLabel = new JLabel("Winner: ");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(winnerLabel);

        panel.add(Box.createVerticalStrut(10));

        JButton backToMenuButton = new JButton("Back to Menu");
        backToMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToMenuButton.addActionListener(e -> {
            dispose();
            if (returnCallback != null) {
                returnCallback.run();
            }
        });
        panel.add(backToMenuButton);

        return panel;
    }

    private void updateDisplay() {
        if (viewModel == null) {
            return;
        }

        // Clear error first
        errorLabel.setVisible(false);
        errorLabel.setText("");

        Battle battle = controller.getCurrentBattle();
        if (battle == null) {
            battleStatusLabel.setText("Status: No battle in progress");
            return;
        }

        Pokemon playerPokemon = controller.getPlayerActivePokemon();
        AIPlayer aiPlayer = controller.getAiPlayer();
        List<Pokemon> playerTeam = controller.getPlayerTeam();

        // Update status
        String status = battle.getBattleStatus();
        battleStatusLabel.setText("Battle Status: " + status);

        // Update player panel
        if (playerPokemon != null && playerTeam != null) {
            updatePlayerInfo(playerPokemon, playerTeam);
        }

        // Update AI panel
        if (aiPlayer != null) {
            updateAIInfo(aiPlayer);
        }

        // Update turn result
        String lastTurn = controller.getLastTurnDescription();
        if (lastTurn != null && !lastTurn.isEmpty()) {
            turnResultArea.setText(lastTurn);
        }

        // Check if battle ended
        if ("COMPLETED".equals(status)) {
            displayBattleEnded(battle);
        } else {
            battleEndedPanel.setVisible(false);
        }

        revalidate();
        repaint();
    }

    private void updatePlayerInfo(Pokemon activePokemon, List<Pokemon> team) {
        // Update player name
        if (controller.getCurrentBattle() != null) {
            User player = controller.getCurrentBattle().getPlayer1();
            playerNameLabel.setText("Player: " + player.getName());
        }

        // Update active Pokemon
        playerPokemonNameLabel.setText("Pokemon: " + activePokemon.getName());
        loadPokemonImage(playerPokemonImageLabel, activePokemon);

        // Update HP
        int currentHP = activePokemon.getStats().getHp();
        Integer maxHP = maxHPMap.get(activePokemon);
        if (maxHP == null) {
            maxHP = currentHP;
            maxHPMap.put(activePokemon, maxHP);
        }

        playerHPLabel.setText("HP: " + currentHP + " / " + maxHP);
        int hpPercent = maxHP > 0 ? (currentHP * 100 / maxHP) : 0;
        playerHPBar.setValue(hpPercent);
        playerHPBar.setString(hpPercent + "%");

        // Set HP bar color based on percentage
        if (hpPercent > 50) {
            playerHPBar.setForeground(Color.GREEN);
        } else if (hpPercent > 25) {
            playerHPBar.setForeground(Color.ORANGE);
        } else {
            playerHPBar.setForeground(Color.RED);
        }

        // Update moves
        updateMovesPanel(activePokemon);

        // Update team
        updatePlayerTeamPanel(team, activePokemon);
    }

    private void updateAIInfo(AIPlayer aiPlayer) {
        // Update AI name
        aiNameLabel.setText("AI: " + aiPlayer.getName() + " (Difficulty: " + aiPlayer.getDifficulty() + ")");

        Pokemon activePokemon = aiPlayer.getActivePokemon();
        if (activePokemon == null) {
            return;
        }

        // Update active Pokemon
        aiPokemonNameLabel.setText("Pokemon: " + activePokemon.getName());
        loadPokemonImage(aiPokemonImageLabel, activePokemon);

        // Update HP
        int currentHP = activePokemon.getStats().getHp();
        Integer maxHP = maxHPMap.get(activePokemon);
        if (maxHP == null) {
            maxHP = currentHP;
            maxHPMap.put(activePokemon, maxHP);
        }

        aiHPLabel.setText("HP: " + currentHP + " / " + maxHP);
        int hpPercent = maxHP > 0 ? (currentHP * 100 / maxHP) : 0;
        aiHPBar.setValue(hpPercent);
        aiHPBar.setString(hpPercent + "%");

        // Set HP bar color
        if (hpPercent > 50) {
            aiHPBar.setForeground(Color.GREEN);
        } else if (hpPercent > 25) {
            aiHPBar.setForeground(Color.ORANGE);
        } else {
            aiHPBar.setForeground(Color.RED);
        }

        // Update AI team
        updateAITeamPanel(aiPlayer.getTeam());
    }

    private void updateMovesPanel(Pokemon pokemon) {
        playerMovesPanel.removeAll();

        if (pokemon == null || pokemon.getMoves() == null || pokemon.getMoves().isEmpty()) {
            JLabel noMoves = new JLabel("No moves available");
            noMoves.setAlignmentX(Component.LEFT_ALIGNMENT);
            playerMovesPanel.add(noMoves);
            playerMovesPanel.revalidate();
            playerMovesPanel.repaint();
            return;
        }

        for (String moveName : pokemon.getMoves()) {
            JButton moveButton = new JButton(moveName);
            moveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            moveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            moveButton.addActionListener(e -> executePlayerMove(moveName));
            playerMovesPanel.add(moveButton);
            playerMovesPanel.add(Box.createVerticalStrut(3));
        }

        playerMovesPanel.revalidate();
        playerMovesPanel.repaint();
    }

    private void updatePlayerTeamPanel(List<Pokemon> team, Pokemon activePokemon) {
        playerTeamPanel.removeAll();

        if (team == null || team.isEmpty()) {
            JLabel noTeam = new JLabel("No Pokemon in team");
            noTeam.setAlignmentX(Component.LEFT_ALIGNMENT);
            playerTeamPanel.add(noTeam);
            playerTeamPanel.revalidate();
            playerTeamPanel.repaint();
            return;
        }

        for (Pokemon pokemon : team) {
            JButton pokemonButton = new JButton(pokemon.getName() + " (HP: " + pokemon.getStats().getHp() + ")");
            pokemonButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            pokemonButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            if (pokemon == activePokemon) {
                pokemonButton.setEnabled(false);
                pokemonButton.setText(pokemonButton.getText() + " [ACTIVE]");
            } else if (pokemon.isFainted()) {
                pokemonButton.setEnabled(false);
                pokemonButton.setText(pokemonButton.getText() + " [FAINTED]");
            } else {
                pokemonButton.addActionListener(e -> executePlayerSwitch(pokemon));
            }

            playerTeamPanel.add(pokemonButton);
            playerTeamPanel.add(Box.createVerticalStrut(3));
        }

        playerTeamPanel.revalidate();
        playerTeamPanel.repaint();
    }

    private void updateAITeamPanel(List<Pokemon> team) {
        aiTeamPanel.removeAll();

        if (team == null || team.isEmpty()) {
            JLabel noTeam = new JLabel("No Pokemon in team");
            noTeam.setAlignmentX(Component.LEFT_ALIGNMENT);
            aiTeamPanel.add(noTeam);
            aiTeamPanel.revalidate();
            aiTeamPanel.repaint();
            return;
        }

        for (Pokemon pokemon : team) {
            String status = pokemon.isFainted() ? " [FAINTED]" : "";
            JLabel pokemonLabel = new JLabel(pokemon.getName() + " (HP: " + pokemon.getStats().getHp() + ")" + status);
            pokemonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            aiTeamPanel.add(pokemonLabel);
            aiTeamPanel.add(Box.createVerticalStrut(3));
        }

        aiTeamPanel.revalidate();
        aiTeamPanel.repaint();
    }

    private void executePlayerMove(String moveName) {
        // Look up the move
        Move selectedMove = null;
        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                selectedMove = move;
                break;
            }
        }

        if (selectedMove == null) {
            selectedMove = new Move().setName(moveName).setPower(40);
        }

        // Execute the move through controller
        controller.executePlayerMove(selectedMove);
    }

    private void executePlayerSwitch(Pokemon pokemon) {
        controller.executePlayerSwitch(pokemon);
    }

    private void displayBattleEnded(Battle battle) {
        battleEndedPanel.setVisible(true);

        User winner = battle.getWinner();
        if (winner != null) {
            winnerLabel.setText("Winner: " + winner.getName());
        } else {
            winnerLabel.setText("Battle ended with no winner");
        }
    }
}
