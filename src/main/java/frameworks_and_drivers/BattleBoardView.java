package frameworks_and_drivers;

import entities.Move;
import entities.Pokemon;
import pokeapi.JSONLoader;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Simple, clean battle board view for AI battles.
 */
public class BattleBoardView extends JFrame implements BattleAIViewModel.ViewModelListener {

    private final BattleAIController controller;
    private BattleAIViewModel viewModel;

    private JLabel playerPokemonName;
    private JLabel playerPokemonSprite;
    private JProgressBar playerHPBar;
    private JLabel playerHPLabel;
    private JPanel movesPanel;

    private JLabel aiPokemonName;
    private JLabel aiPokemonSprite;
    private JProgressBar aiHPBar;
    private JLabel aiHPLabel;

    private JTextArea battleLog;

    private int playerMaxHP = 100;
    private int aiMaxHP = 100;

    public BattleBoardView(BattleAIController controller) {
        this.controller = controller;

        setTitle("Pokemon Battle");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("POKEMON BATTLE", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Battle arena
        JPanel arenaPanel = createArenaPanel();
        mainPanel.add(arenaPanel, BorderLayout.CENTER);

        // Bottom - Battle log and moves
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

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

    private JPanel createArenaPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 60, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Player Pokemon (left)
        JPanel playerPanel = createPokemonPanel(true);
        panel.add(playerPanel);

        // AI Pokemon (right)
        JPanel aiPanel = createPokemonPanel(false);
        panel.add(aiPanel);

        return panel;
    }

    private JPanel createPokemonPanel(boolean isPlayer) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Label
        JLabel label = new JLabel(isPlayer ? "YOUR POKEMON" : "OPPONENT");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(Box.createVerticalStrut(8));

        // Pokemon name
        JLabel nameLabel = new JLabel("---");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer) playerPokemonName = nameLabel;
        else aiPokemonName = nameLabel;
        panel.add(nameLabel);

        panel.add(Box.createVerticalStrut(15));

        // Pokemon sprite
        JLabel spriteLabel = new JLabel("Loading...", SwingConstants.CENTER);
        spriteLabel.setPreferredSize(new Dimension(150, 150));
        spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer) playerPokemonSprite = spriteLabel;
        else aiPokemonSprite = spriteLabel;
        panel.add(spriteLabel);

        // Circle/shadow below Pokemon
        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 200, 200));
                int w = getWidth();
                g2d.fillOval(w/2 - 50, 0, 100, 20);
            }
        };
        circlePanel.setPreferredSize(new Dimension(150, 25));
        circlePanel.setMaximumSize(new Dimension(150, 25));
        circlePanel.setOpaque(false);
        circlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(circlePanel);

        panel.add(Box.createVerticalStrut(15));

        // HP section
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
        hpBar.setPreferredSize(new Dimension(160, 12));
        hpBar.setMaximumSize(new Dimension(160, 12));
        hpBar.setForeground(new Color(76, 175, 80));
        hpBar.setBackground(new Color(230, 230, 230));
        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer) playerHPBar = hpBar;
        else aiHPBar = hpBar;
        hpPanel.add(hpBar);

        JLabel hpLabel = new JLabel("100 / 100");
        hpLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hpLabel.setForeground(Color.GRAY);
        hpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isPlayer) playerHPLabel = hpLabel;
        else aiHPLabel = hpLabel;
        hpPanel.add(hpLabel);

        panel.add(hpPanel);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBackground(Color.WHITE);

        // Battle log
        battleLog = new JTextArea(4, 40);
        battleLog.setEditable(false);
        battleLog.setFont(new Font("SansSerif", Font.PLAIN, 13));
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        battleLog.setText("Battle starting...\n");
        battleLog.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scrollPane = new JScrollPane(battleLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Moves panel
        JPanel movesContainer = new JPanel();
        movesContainer.setLayout(new BoxLayout(movesContainer, BoxLayout.Y_AXIS));
        movesContainer.setBackground(Color.WHITE);

        JLabel movesLabel = new JLabel("MOVES");
        movesLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        movesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        movesContainer.add(movesLabel);

        movesContainer.add(Box.createVerticalStrut(8));

        movesPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        movesPanel.setBackground(Color.WHITE);
        movesPanel.setPreferredSize(new Dimension(300, 100));
        movesContainer.add(movesPanel);

        panel.add(movesContainer, BorderLayout.EAST);

        return panel;
    }

    private void updateDisplay() {
        Pokemon playerPokemon = controller.getPlayerActivePokemon();
        Pokemon aiPokemon = controller.getAiPlayer() != null ?
                controller.getAiPlayer().getActivePokemon() : null;

        // Update player Pokemon
        if (playerPokemon != null) {
            String pokemonName = capitalize(playerPokemon.getName());

            if (!playerPokemonName.getText().equals(pokemonName)) {
                playerMaxHP = playerPokemon.getStats().getHp();
                playerPokemonName.setText(pokemonName);

                SpriteLoader.loadSpriteAsync(playerPokemon.getSpriteUrl(), sprite -> {
                    if (sprite != null) {
                        playerPokemonSprite.setIcon(new ImageIcon(sprite));
                        playerPokemonSprite.setText(null);
                    }
                });

                updateMoves(playerPokemon);
            }

            int currentHP = playerPokemon.getStats().getHp();
            int hpPercent = playerMaxHP > 0 ? (currentHP * 100 / playerMaxHP) : 0;
            playerHPBar.setValue(hpPercent);
            playerHPLabel.setText(currentHP + " / " + playerMaxHP);
            updateHPBarColor(playerHPBar, hpPercent);
        }

        // Update AI Pokemon
        if (aiPokemon != null) {
            String pokemonName = capitalize(aiPokemon.getName());

            if (!aiPokemonName.getText().equals(pokemonName)) {
                aiMaxHP = aiPokemon.getStats().getHp();
                aiPokemonName.setText(pokemonName);

                SpriteLoader.loadSpriteAsync(aiPokemon.getSpriteUrl(), sprite -> {
                    if (sprite != null) {
                        aiPokemonSprite.setIcon(new ImageIcon(sprite));
                        aiPokemonSprite.setText(null);
                    }
                });
            }

            int currentHP = aiPokemon.getStats().getHp();
            int hpPercent = aiMaxHP > 0 ? (currentHP * 100 / aiMaxHP) : 0;
            aiHPBar.setValue(hpPercent);
            aiHPLabel.setText(currentHP + " / " + aiMaxHP);
            updateHPBarColor(aiHPBar, hpPercent);
        }

        // Check if battle ended
        if (controller.getCurrentBattle() != null &&
                "COMPLETED".equals(controller.getCurrentBattle().getBattleStatus())) {
            showBattleResults();
        }
    }

    private void updateHPBarColor(JProgressBar bar, int percent) {
        if (percent > 50) {
            bar.setForeground(new Color(76, 175, 80)); // Green
        } else if (percent > 25) {
            bar.setForeground(new Color(255, 193, 7)); // Yellow
        } else {
            bar.setForeground(new Color(244, 67, 54)); // Red
        }
    }

    private void updateMoves(Pokemon pokemon) {
        movesPanel.removeAll();

        List<String> moveNames = pokemon.getMoves();
        int moveCount = Math.min(4, moveNames.size());

        for (int i = 0; i < moveCount; i++) {
            String moveName = moveNames.get(i);
            Move move = findMoveByName(moveName);

            JButton moveBtn = new JButton(capitalize(moveName));
            moveBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            moveBtn.setFocusPainted(false);
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
        setMovesEnabled(false);

        controller.executePlayerMove(move);

        String turnResult = controller.getLastTurnDescription();
        if (turnResult != null) {
            appendLog(turnResult + "\n");
        }

        updateDisplay();

        javax.swing.Timer updateTimer = new javax.swing.Timer(600, e -> {
            String aiTurnResult = controller.getLastTurnDescription();
            if (aiTurnResult != null && !aiTurnResult.equals(turnResult)) {
                appendLog(aiTurnResult + "\n");
            }

            updateDisplay();

            if (controller.getCurrentBattle() != null &&
                    "IN_PROGRESS".equals(controller.getCurrentBattle().getBattleStatus())) {
                setMovesEnabled(true);
            }
        });
        updateTimer.setRepeats(false);
        updateTimer.start();
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
