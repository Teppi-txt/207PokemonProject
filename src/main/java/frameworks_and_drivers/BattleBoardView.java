package frameworks_and_drivers;

import entities.Move;
import entities.Pokemon;
import pokeapi.JSONLoader;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIViewModel;
import interface_adapters.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main battle board view where battles take place.
 * Displays both Pokemon with HP bars, move buttons, and battle log.
 */
public class BattleBoardView extends JFrame implements BattleAIViewModel.ViewModelListener {

    private final BattleAIController controller;
    private BattleAIViewModel viewModel;

    private JLabel playerPokemonName;
    private JLabel playerPokemonSprite;
    private HPBar playerHPBar;
    private JPanel movesPanel;

    private JLabel aiPokemonName;
    private JLabel aiPokemonSprite;
    private HPBar aiHPBar;

    private JTextArea battleLog;

    public BattleBoardView(BattleAIController controller) {
        this.controller = controller;

        setTitle("Pokemon Battle");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top - AI Pokemon
        JPanel aiPanel = createAIPokemonPanel();
        add(aiPanel, BorderLayout.NORTH);

        // Center - Battle Log
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom - Player Pokemon and Moves
        JPanel playerPanel = createPlayerPanel();
        add(playerPanel, BorderLayout.SOUTH);

        // Initialize display
        updateDisplay();
    }

    public void setViewModel(BattleAIViewModel viewModel) {
        this.viewModel = viewModel;
        if (viewModel != null) {
            viewModel.addListener(this);
        }
    }

    @Override
    public void onViewModelChanged() {
        SwingUtilities.invokeLater(this::updateDisplay);
    }

    private JPanel createAIPokemonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        aiPokemonName = new JLabel("AI Pokemon");
        aiPokemonName.setFont(UIStyleConstants.HEADING_FONT);
        aiPokemonName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(aiPokemonName);

        panel.add(Box.createVerticalStrut(10));

        aiPokemonSprite = new JLabel("Loading...", SwingConstants.CENTER);
        aiPokemonSprite.setPreferredSize(new Dimension(128, 128));
        aiPokemonSprite.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(aiPokemonSprite);

        panel.add(Box.createVerticalStrut(10));

        aiHPBar = new HPBar();
        aiHPBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(aiHPBar);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyleConstants.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel logLabel = new JLabel("Battle Log");
        logLabel.setFont(UIStyleConstants.HEADING_FONT);
        panel.add(logLabel, BorderLayout.NORTH);

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setFont(UIStyleConstants.BODY_FONT);
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        battleLog.setText("Battle starting...\n");

        JScrollPane scrollPane = new JScrollPane(battleLog);
        scrollPane.setPreferredSize(new Dimension(900, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPlayerPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 250, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Pokemon info
        playerPokemonName = new JLabel("Your Pokemon");
        playerPokemonName.setFont(UIStyleConstants.HEADING_FONT);
        playerPokemonName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(playerPokemonName);

        panel.add(Box.createVerticalStrut(10));

        playerPokemonSprite = new JLabel("Loading...", SwingConstants.CENTER);
        playerPokemonSprite.setPreferredSize(new Dimension(128, 128));
        playerPokemonSprite.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(playerPokemonSprite);

        panel.add(Box.createVerticalStrut(10));

        playerHPBar = new HPBar();
        playerHPBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(playerHPBar);

        panel.add(Box.createVerticalStrut(20));

        // Moves
        JLabel movesLabel = new JLabel("Moves:");
        movesLabel.setFont(UIStyleConstants.BODY_FONT);
        movesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(movesLabel);

        panel.add(Box.createVerticalStrut(10));

        movesPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        movesPanel.setOpaque(false);
        movesPanel.setMaximumSize(new Dimension(600, 100));
        panel.add(movesPanel);

        return panel;
    }

    private void updateDisplay() {
        Pokemon playerPokemon = controller.getPlayerActivePokemon();
        Pokemon aiPokemon = controller.getAiPlayer() != null ?
                controller.getAiPlayer().getActivePokemon() : null;

        // Update player Pokemon
        if (playerPokemon != null) {
            playerPokemonName.setText(capitalize(playerPokemon.getName()));
            playerHPBar.updateHP(playerPokemon.getStats().getHp(), playerPokemon.getStats().getHp());

            // Load sprite
            SpriteLoader.loadSpriteAsync(playerPokemon.getSpriteUrl(), sprite -> {
                if (sprite != null) {
                    playerPokemonSprite.setIcon(new ImageIcon(sprite));
                    playerPokemonSprite.setText(null);
                }
            });

            // Update moves
            updateMoves(playerPokemon);
        }

        // Update AI Pokemon
        if (aiPokemon != null) {
            aiPokemonName.setText(capitalize(aiPokemon.getName()));
            aiHPBar.updateHP(aiPokemon.getStats().getHp(), aiPokemon.getStats().getHp());

            // Load sprite
            SpriteLoader.loadSpriteAsync(aiPokemon.getSpriteUrl(), sprite -> {
                if (sprite != null) {
                    aiPokemonSprite.setIcon(new ImageIcon(sprite));
                    aiPokemonSprite.setText(null);
                }
            });
        }

        // Check if battle ended
        if (controller.getCurrentBattle() != null &&
                "COMPLETED".equals(controller.getCurrentBattle().getBattleStatus())) {
            showBattleResults();
        }
    }

    private void updateMoves(Pokemon pokemon) {
        movesPanel.removeAll();

        List<String> moveNames = pokemon.getMoves();
        int moveCount = Math.min(4, moveNames.size());

        for (int i = 0; i < moveCount; i++) {
            String moveName = moveNames.get(i);

            // Look up move from JSONLoader
            Move move = findMoveByName(moveName);

            StyledButton moveBtn = new StyledButton(capitalize(moveName));
            moveBtn.addActionListener(e -> {
                if (move != null) {
                    executeMove(move);
                } else {
                    appendLog("Move not found: " + moveName + "\n");
                }
            });
            movesPanel.add(moveBtn);
        }

        // Fill empty slots
        for (int i = moveCount; i < 4; i++) {
            movesPanel.add(new JLabel());
        }

        movesPanel.revalidate();
        movesPanel.repaint();
    }

    private Move findMoveByName(String name) {
        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(name)) {
                return move;
            }
        }
        return null;
    }

    private void executeMove(Move move) {
        appendLog("You chose " + move.getName() + "!\n");

        // Disable moves during execution
        setMovesEnabled(false);

        // Execute player move
        controller.executePlayerMove(move);

        // Update display
        updateDisplay();

        // Re-enable moves
        SwingUtilities.invokeLater(() -> setMovesEnabled(true));
    }

    private void setMovesEnabled(boolean enabled) {
        for (Component comp : movesPanel.getComponents()) {
            comp.setEnabled(enabled);
        }
    }

    private void appendLog(String message) {
        battleLog.append(message);
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }

    private void showBattleResults() {
        String winner = controller.getCurrentBattle().getWinner() != null ?
                controller.getCurrentBattle().getWinner().getName() : "Unknown";

        BattleResultView resultView = new BattleResultView(winner, controller);
        resultView.setVisible(true);
        dispose();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
