package frameworks_and_drivers;

import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIViewModel;
import interface_adapters.ui.RetroButton;
import interface_adapters.ui.UIStyleConstants;
import entities.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import pokeapi.JSONLoader;

/**
 * Retro Pokemon-styled battle view for AI opponent battles.
 * Features classic Pokemon battle UI with proper move buttons and HP bars.
 */
public class BattleAIView extends JFrame implements BattleAIViewModel.ViewModelListener {
    private final BattleAIController controller;
    private BattleAIViewModel viewModel;
    private Runnable returnCallback;

    // UI Components - Player
    private JLabel playerPokemonNameLabel;
    private JLabel playerPokemonImageLabel;
    private JPanel playerHPBarPanel;
    private JLabel playerHPLabel;
    private int playerCurrentHP = 100;
    private int playerMaxHP = 100;

    // UI Components - AI
    private JLabel aiPokemonNameLabel;
    private JLabel aiPokemonImageLabel;
    private JPanel aiHPBarPanel;
    private JLabel aiHPLabel;
    private int aiCurrentHP = 100;
    private int aiMaxHP = 100;

    // UI Components - Battle Controls
    private JPanel movesPanel;
    private RetroButton[] moveButtons = new RetroButton[4];
    private JPanel teamPanel;
    private JTextArea messageArea;
    private JPanel battleEndedPanel;
    private JLabel winnerLabel;

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
        getContentPane().setBackground(UIStyleConstants.DARK_BG);

        // Main battle panel
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(UIStyleConstants.DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Top: Battle Arena
        JPanel arenaPanel = createArenaPanel();
        mainPanel.add(arenaPanel, BorderLayout.CENTER);

        // Bottom: Controls (moves and team)
        JPanel controlsPanel = createControlsPanel();
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setSize(900, 650);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
    }

    private JPanel createArenaPanel() {
        // Store references for repositioning
        JPanel aiInfoBox = createPokemonInfoBox(false);
        JPanel playerInfoBox = createPokemonInfoBox(true);

        aiPokemonImageLabel = new JLabel();
        aiPokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        playerPokemonImageLabel = new JLabel();
        playerPokemonImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

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

                // Calculate platform positions relative to panel size
                int playerPlatformX = (int)(w * 0.08);
                int playerPlatformY = h - 70;
                int playerPlatformW = 220;
                int playerPlatformH = 55;

                int aiPlatformX = (int)(w * 0.65);
                int aiPlatformY = (int)(h * 0.42);
                int aiPlatformW = 180;
                int aiPlatformH = 45;

                // Player platform (bottom left)
                g2d.setColor(new Color(139, 90, 43));
                g2d.fillOval(playerPlatformX, playerPlatformY, playerPlatformW, playerPlatformH);
                g2d.setColor(new Color(160, 120, 80));
                g2d.fillOval(playerPlatformX + 5, playerPlatformY + 5, playerPlatformW - 10, playerPlatformH - 10);

                // AI platform (top right)
                g2d.setColor(new Color(139, 90, 43));
                g2d.fillOval(aiPlatformX, aiPlatformY, aiPlatformW, aiPlatformH);
                g2d.setColor(new Color(160, 120, 80));
                g2d.fillOval(aiPlatformX + 5, aiPlatformY + 5, aiPlatformW - 10, aiPlatformH - 10);
            }

            @Override
            public void doLayout() {
                super.doLayout();
                int w = getWidth();
                int h = getHeight();

                // Position AI info box (top left)
                aiInfoBox.setBounds(15, 10, 260, 75);

                // Position AI Pokemon (above AI platform)
                int aiPlatformX = (int)(w * 0.65);
                int aiPlatformY = (int)(h * 0.42);
                aiPokemonImageLabel.setBounds(aiPlatformX + 15, aiPlatformY - 130, 150, 150);

                // Position Player info box (bottom right area)
                playerInfoBox.setBounds(w - 275, h - 95, 260, 75);

                // Position Player Pokemon (above player platform)
                int playerPlatformX = (int)(w * 0.08);
                int playerPlatformY = h - 70;
                playerPokemonImageLabel.setBounds(playerPlatformX + 10, playerPlatformY - 180, 200, 200);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(880, 300));

        panel.add(aiPokemonImageLabel);
        panel.add(aiInfoBox);
        panel.add(playerPokemonImageLabel);
        panel.add(playerInfoBox);

        return panel;
    }

    private JPanel createPokemonInfoBox(boolean isPlayer) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // Outer border
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(0, 0, w, h);

                // Inner background
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(4, 4, w - 8, h - 8);

                // 3D effect
                g2d.setColor(Color.WHITE);
                g2d.drawLine(4, 4, w - 4, 4);
                g2d.drawLine(4, 4, 4, h - 4);
                g2d.setColor(UIStyleConstants.SHADOW_COLOR);
                g2d.drawLine(w - 4, 4, w - 4, h - 4);
                g2d.drawLine(4, h - 4, w - 4, h - 4);
            }
        };
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        box.setOpaque(false);

        // Pokemon name
        JLabel nameLabel = new JLabel(isPlayer ? "Your Pokemon" : "Enemy Pokemon");
        nameLabel.setFont(UIStyleConstants.HEADING_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(nameLabel);

        if (isPlayer) {
            playerPokemonNameLabel = nameLabel;
        } else {
            aiPokemonNameLabel = nameLabel;
        }

        box.add(Box.createVerticalStrut(5));

        // HP label
        JPanel hpRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hpRow.setOpaque(false);
        hpRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hpText = new JLabel("HP:");
        hpText.setFont(UIStyleConstants.BODY_FONT);
        hpText.setForeground(UIStyleConstants.TEXT_PRIMARY);
        hpRow.add(hpText);

        // HP Bar container
        JPanel hpBarContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // HP bar background
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

                int currentHP = isPlayer ? playerCurrentHP : aiCurrentHP;
                int maxHP = isPlayer ? playerMaxHP : aiMaxHP;
                float hpPercent = maxHP > 0 ? (float) currentHP / maxHP : 0;

                int barWidth = (int) ((getWidth() - 4) * hpPercent);

                // HP color
                Color hpColor = UIStyleConstants.getHPColor(hpPercent);
                g2d.setColor(hpColor);
                g2d.fillRect(2, 2, barWidth, getHeight() - 4);
            }
        };
        hpBar.setOpaque(false);
        hpBarContainer.add(hpBar, BorderLayout.CENTER);
        hpRow.add(hpBarContainer);

        if (isPlayer) {
            playerHPBarPanel = hpBar;
        } else {
            aiHPBarPanel = hpBar;
        }

        box.add(hpRow);

        box.add(Box.createVerticalStrut(3));

        // HP numbers
        JLabel hpNumLabel = new JLabel("100 / 100");
        hpNumLabel.setFont(UIStyleConstants.SMALL_FONT);
        hpNumLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        hpNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(hpNumLabel);

        if (isPlayer) {
            playerHPLabel = hpNumLabel;
        } else {
            aiHPLabel = hpNumLabel;
        }

        return box;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setPreferredSize(new Dimension(880, 240));

        // Left side: Message box
        JPanel messageBox = createMessageBox();
        panel.add(messageBox, BorderLayout.CENTER);

        // Right side: Action panel (moves + team)
        JPanel actionPanel = new JPanel(new BorderLayout(5, 5));
        actionPanel.setBackground(UIStyleConstants.DARK_BG);
        actionPanel.setPreferredSize(new Dimension(420, 230));

        // Moves panel (2x2 grid)
        movesPanel = createMovesPanel();
        actionPanel.add(movesPanel, BorderLayout.CENTER);

        // Team panel (below moves)
        teamPanel = createTeamPanel();
        actionPanel.add(teamPanel, BorderLayout.SOUTH);

        panel.add(actionPanel, BorderLayout.EAST);

        // Battle ended panel (hidden initially)
        battleEndedPanel = createBattleEndedPanel();
        battleEndedPanel.setVisible(false);

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

                // Outer white border
                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                g2d.fillRect(0, 0, w, h);

                // Dark border
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(4, 4, w - 8, h - 8);

                // Inner cream background
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(8, 8, w - 16, h - 16);

                // 3D effect
                g2d.setColor(Color.WHITE);
                g2d.drawLine(8, 8, w - 8, 8);
                g2d.drawLine(8, 8, 8, h - 8);
                g2d.setColor(UIStyleConstants.SHADOW_COLOR);
                g2d.drawLine(w - 8, 8, w - 8, h - 8);
                g2d.drawLine(8, h - 8, w - 8, h - 8);
            }
        };
        box.setLayout(new BorderLayout());
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        messageArea = new JTextArea();
        messageArea.setFont(UIStyleConstants.MENU_FONT);
        messageArea.setForeground(UIStyleConstants.TEXT_PRIMARY);
        messageArea.setBackground(UIStyleConstants.MENU_BG);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText("What will you do?");
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        box.add(messageArea, BorderLayout.CENTER);

        return box;
    }

    private JPanel createMovesPanel() {
        JPanel outerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // Border
                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(4, 4, w - 8, h - 8);
                g2d.setColor(new Color(48, 80, 120));
                g2d.fillRect(8, 8, w - 16, h - 16);
            }
        };
        outerPanel.setLayout(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        outerPanel.setPreferredSize(new Dimension(400, 140));

        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setOpaque(false);

        // Create 4 move buttons
        Color[] moveColors = {
            UIStyleConstants.PRIMARY_COLOR,
            UIStyleConstants.POKEMON_BLUE,
            new Color(120, 200, 80),
            UIStyleConstants.SECONDARY_COLOR
        };

        for (int i = 0; i < 4; i++) {
            moveButtons[i] = new RetroButton("-");
            moveButtons[i].setButtonColor(moveColors[i]);
            moveButtons[i].setFont(UIStyleConstants.BODY_FONT);
            moveButtons[i].setEnabled(false);
            final int moveIndex = i;
            moveButtons[i].addActionListener(e -> executeMove(moveIndex));
            grid.add(moveButtons[i]);
        }

        outerPanel.add(grid, BorderLayout.CENTER);

        return outerPanel;
    }

    private JPanel createTeamPanel() {
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
                g2d.fillRect(3, 3, w - 6, h - 6);
                g2d.setColor(new Color(60, 80, 60));
                g2d.fillRect(6, 6, w - 12, h - 12);
            }
        };
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        panel.setPreferredSize(new Dimension(400, 75));

        JLabel label = new JLabel("TEAM");
        label.setFont(UIStyleConstants.BODY_FONT);
        label.setForeground(UIStyleConstants.TEXT_LIGHT);
        panel.add(label);

        return panel;
    }

    private JPanel createBattleEndedPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // Semi-transparent overlay
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRect(0, 0, w, h);

                // Result box
                int boxW = 400;
                int boxH = 200;
                int boxX = (w - boxW) / 2;
                int boxY = (h - boxH) / 2;

                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                g2d.fillRect(boxX, boxY, boxW, boxH);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(boxX + 4, boxY + 4, boxW - 8, boxH - 8);
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(boxX + 8, boxY + 8, boxW - 16, boxH - 16);
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        winnerLabel = new JLabel("Battle Over!");
        winnerLabel.setFont(UIStyleConstants.TITLE_FONT);
        winnerLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(winnerLabel);

        content.add(Box.createVerticalStrut(20));

        RetroButton backButton = new RetroButton("Back to Menu");
        backButton.setButtonColor(UIStyleConstants.POKEMON_BLUE);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            dispose();
            if (returnCallback != null) {
                returnCallback.run();
            }
        });
        content.add(backButton);

        panel.add(content);

        return panel;
    }

    private void loadPokemonImage(JLabel imageLabel, Pokemon pokemon, boolean isPlayer) {
        if (pokemon == null) {
            imageLabel.setIcon(null);
            return;
        }

        int size = isPlayer ? 180 : 120;

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    // Try animated GIF first
                    // Use back sprite for player's Pokemon, front sprite for AI
                    String animatedUrl = isPlayer ? pokemon.getAnimatedBackSpriteUrl() : pokemon.getAnimatedSpriteUrl();
                    if (animatedUrl.endsWith(".gif")) {
                        URL url = new URL(animatedUrl);
                        // For GIFs, load directly as ImageIcon to preserve animation
                        ImageIcon gifIcon = new ImageIcon(url);
                        if (gifIcon.getIconWidth() > 0) {
                            // Scale the GIF
                            Image scaledImage = gifIcon.getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
                            return new ImageIcon(scaledImage);
                        }
                    }

                    // Fallback to static PNG (back sprite for player)
                    String imageUrl;
                    if (isPlayer) {
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
                        imageLabel.setIcon(icon);
                    }
                } catch (Exception e) {
                    // Keep current icon
                }
            }
        };
        worker.execute();
    }

    private void updateDisplay() {
        if (viewModel == null) {
            return;
        }

        Battle battle = controller.getCurrentBattle();
        if (battle == null) {
            messageArea.setText("Waiting for battle to start...");
            return;
        }

        Pokemon playerPokemon = controller.getPlayerActivePokemon();
        AIPlayer aiPlayer = controller.getAiPlayer();
        List<Pokemon> playerTeam = controller.getPlayerTeam();

        // Update player Pokemon
        if (playerPokemon != null) {
            updatePlayerPokemon(playerPokemon);
        }

        // Update AI Pokemon
        if (aiPlayer != null && aiPlayer.getActivePokemon() != null) {
            updateAIPokemon(aiPlayer.getActivePokemon(), aiPlayer.getDifficulty());
        }

        // Update team panel
        if (playerTeam != null) {
            updateTeamDisplay(playerTeam, playerPokemon);
        }

        // Update turn result / message
        String lastTurn = controller.getLastTurnDescription();
        if (lastTurn != null && !lastTurn.isEmpty()) {
            messageArea.setText(lastTurn);
        }

        // Check if battle ended
        String status = battle.getBattleStatus();
        if ("COMPLETED".equals(status)) {
            displayBattleEnded(battle);
        }

        revalidate();
        repaint();
    }

    private void updatePlayerPokemon(Pokemon pokemon) {
        playerPokemonNameLabel.setText(pokemon.getName().toUpperCase());
        loadPokemonImage(playerPokemonImageLabel, pokemon, true);

        int currentHP = pokemon.getStats().getHp();
        Integer maxHP = maxHPMap.get(pokemon);
        if (maxHP == null) {
            maxHP = currentHP;
            maxHPMap.put(pokemon, maxHP);
        }

        playerCurrentHP = currentHP;
        playerMaxHP = maxHP;
        playerHPLabel.setText(currentHP + " / " + maxHP);
        playerHPBarPanel.repaint();

        // Update moves
        updateMovesDisplay(pokemon);
    }

    private void updateAIPokemon(Pokemon pokemon, String difficulty) {
        aiPokemonNameLabel.setText(pokemon.getName().toUpperCase() + " (Lv.50)");
        loadPokemonImage(aiPokemonImageLabel, pokemon, false);

        int currentHP = pokemon.getStats().getHp();
        Integer maxHP = maxHPMap.get(pokemon);
        if (maxHP == null) {
            maxHP = currentHP;
            maxHPMap.put(pokemon, maxHP);
        }

        aiCurrentHP = currentHP;
        aiMaxHP = maxHP;
        aiHPLabel.setText(currentHP + " / " + maxHP);
        aiHPBarPanel.repaint();
    }

    private void updateMovesDisplay(Pokemon pokemon) {
        List<String> moves = pokemon.getMoves();

        for (int i = 0; i < 4; i++) {
            if (moves != null && i < moves.size()) {
                String moveName = moves.get(i);
                moveButtons[i].setText(moveName.toUpperCase());
                moveButtons[i].setEnabled(true);

                // Set color based on move type
                Move move = findMove(moveName);
                if (move != null && move.getType() != null) {
                    Color typeColor = UIStyleConstants.getTypeColor(move.getType());
                    moveButtons[i].setButtonColor(typeColor);
                }
            } else {
                moveButtons[i].setText("-");
                moveButtons[i].setEnabled(false);
            }
        }
    }

    private void updateTeamDisplay(List<Pokemon> team, Pokemon activePokemon) {
        teamPanel.removeAll();

        JLabel label = new JLabel("SWITCH: ");
        label.setFont(UIStyleConstants.BODY_FONT);
        label.setForeground(UIStyleConstants.TEXT_LIGHT);
        teamPanel.add(label);

        if (team == null || team.isEmpty()) {
            teamPanel.revalidate();
            teamPanel.repaint();
            return;
        }

        for (Pokemon pokemon : team) {
            String btnText = pokemon.getName();
            if (pokemon == activePokemon) {
                btnText += " *";
            } else if (pokemon.isFainted()) {
                btnText += " X";
            }

            RetroButton pokemonBtn = new RetroButton(btnText);
            pokemonBtn.setFont(new Font("Courier New", Font.BOLD, 10));
            pokemonBtn.setPreferredSize(new Dimension(100, 38));
            pokemonBtn.setMinimumSize(new Dimension(100, 38));
            pokemonBtn.setMaximumSize(new Dimension(100, 38));

            if (pokemon == activePokemon) {
                pokemonBtn.setButtonColor(UIStyleConstants.HP_HIGH);
                pokemonBtn.setEnabled(false);
            } else if (pokemon.isFainted()) {
                pokemonBtn.setButtonColor(Color.GRAY);
                pokemonBtn.setEnabled(false);
            } else {
                pokemonBtn.setButtonColor(UIStyleConstants.POKEMON_BLUE);
                pokemonBtn.setEnabled(true);
                final Pokemon switchTarget = pokemon;
                pokemonBtn.addActionListener(e -> {
                    System.out.println("Switch button clicked for: " + switchTarget.getName());
                    executeSwitch(switchTarget);
                });
            }

            teamPanel.add(pokemonBtn);
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

    private void executeMove(int moveIndex) {
        Pokemon playerPokemon = controller.getPlayerActivePokemon();
        if (playerPokemon == null || playerPokemon.getMoves() == null) {
            return;
        }

        List<String> moves = playerPokemon.getMoves();
        if (moveIndex >= moves.size()) {
            return;
        }

        String moveName = moves.get(moveIndex);
        Move selectedMove = findMove(moveName);

        if (selectedMove == null) {
            selectedMove = new Move().setName(moveName).setPower(40);
        }

        controller.executePlayerMove(selectedMove);
    }

    private void executeSwitch(Pokemon pokemon) {
        controller.executePlayerSwitch(pokemon);
    }

    private void displayBattleEnded(Battle battle) {
        User winner = battle.getWinner();
        if (winner != null) {
            winnerLabel.setText(winner.getName() + " wins!");
        } else {
            winnerLabel.setText("Battle Over!");
        }

        // Show overlay
        battleEndedPanel.setVisible(true);

        // Add overlay to glass pane
        JPanel glassPane = new JPanel(new BorderLayout());
        glassPane.setOpaque(false);
        glassPane.add(battleEndedPanel, BorderLayout.CENTER);
        setGlassPane(glassPane);
        glassPane.setVisible(true);
    }
}
