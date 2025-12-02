package frameworks_and_drivers.battle;

import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIViewModel;
import interface_adapters.ui.RetroButton;
import interface_adapters.ui.UIStyleConstants;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class BattleAIView extends JFrame implements BattleAIViewModel.ViewModelListener {
    private final BattleAIController controller;
    private final BattleAIViewModel viewModel;
    private final Runnable returnCallback;

    // UI Components - Player
    private JLabel playerPokemonNameLabel;
    private JLabel playerPokemonImageLabel;
    private JProgressBar playerHPBar;
    private JLabel playerHPLabel;

    // UI Components - AI
    private JLabel aiPokemonNameLabel;
    private JLabel aiPokemonImageLabel;
    private JProgressBar aiHPBar;
    private JLabel aiHPLabel;

    // UI Components - Battle Controls
    private JPanel movesPanel;
    private JButton[] moveButtons = new JButton[4];
    private JPanel teamPanel;
    private JTextArea messageArea;
    private JPanel battleEndedPanel;
    private JLabel winnerLabel;

    // Track currently displayed Pokemon to avoid reloading same image (prevents flickering)
    private int currentPlayerPokemonId = -1;
    private int currentAIPokemonId = -1;
    private Map<String, ImageIcon> imageCache = new HashMap<>();

    public BattleAIView(BattleAIController controller, BattleAIViewModel viewModel, Runnable returnCallback) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.returnCallback = returnCallback;

        if (viewModel != null) {
            viewModel.addListener(this);
        }

        initializeGUI();
        updateDisplay();
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

        // Pokemon name
        JLabel nameLabel = new JLabel(isPlayer ? "Your Pokemon" : "Enemy Pokemon");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(nameLabel);

        if (isPlayer) {
            playerPokemonNameLabel = nameLabel;
        } else {
            aiPokemonNameLabel = nameLabel;
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

        if (isPlayer) {
            playerHPBar = hpBar;
        } else {
            aiHPBar = hpBar;
        }

        box.add(hpRow);

        box.add(Box.createVerticalStrut(3));

        // HP numbers
        JLabel hpNumLabel = new JLabel("100 / 100");
        hpNumLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hpNumLabel.setForeground(Color.GRAY);
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
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        outerPanel.setPreferredSize(new Dimension(400, 140));

        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setBackground(Color.WHITE);

        for (int i = 0; i < 4; i++) {
            moveButtons[i] = new JButton("-");
            moveButtons[i].setFont(new Font("SansSerif", Font.BOLD, 12));
            moveButtons[i].setEnabled(false);
            moveButtons[i].setFocusPainted(false);
            final int moveIndex = i;
            moveButtons[i].addActionListener(e -> executeMove(moveIndex));
            grid.add(moveButtons[i]);
        }

        outerPanel.add(grid, BorderLayout.CENTER);

        return outerPanel;
    }

    private JPanel createTeamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(420, 70));

        JLabel label = new JLabel("SWITCH:");
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
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

    private void loadPokemonImage(JLabel imageLabel, BattleAIViewModel.PokemonViewModel pokemon, boolean isPlayer) {
        if (pokemon == null) {
            imageLabel.setIcon(null);
            if (isPlayer) currentPlayerPokemonId = -1;
            else currentAIPokemonId = -1;
            return;
        }

        // Skip reloading if same Pokemon is already displayed (prevents flickering)
        int pokemonId = pokemon.getId();
        if (isPlayer && pokemonId == currentPlayerPokemonId) {
            return; // Same Pokemon, skip reload
        }
        if (!isPlayer && pokemonId == currentAIPokemonId) {
            return; // Same Pokemon, skip reload
        }

        // Update tracking
        if (isPlayer) currentPlayerPokemonId = pokemonId;
        else currentAIPokemonId = pokemonId;

        int size = isPlayer ? 180 : 120;
        String cacheKey = pokemonId + "_" + (isPlayer ? "back" : "front") + "_" + size;

        // Check cache first
        if (imageCache.containsKey(cacheKey)) {
            imageLabel.setIcon(imageCache.get(cacheKey));
            return;
        }

        // Get URLs from ViewModel
        String animatedUrl = isPlayer ? pokemon.getBackGifUrl() : pokemon.getFrontGifUrl();
        String staticUrl = pokemon.getSpriteUrl();
        String backPngUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + pokemonId + ".png";

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    // Try animated GIF first
                    if (animatedUrl != null && animatedUrl.endsWith(".gif")) {
                        URL url = new URL(animatedUrl);
                        ImageIcon gifIcon = new ImageIcon(url);
                        if (gifIcon.getIconWidth() > 0) {
                            Image scaledImage = gifIcon.getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
                            return new ImageIcon(scaledImage);
                        }
                    }

                    // Fallback to static PNG
                    String imageUrl = isPlayer ? backPngUrl : staticUrl;
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

    private void updateDisplay() {
        // Read state from ViewModel (not dataAccess)
        String battleStatus = viewModel.getBattleStatus();
        if (!"IN_PROGRESS".equals(battleStatus) && !"COMPLETED".equals(battleStatus)) {
            messageArea.setText("Waiting for battle to start...");
            return;
        }

        BattleAIViewModel.PokemonViewModel playerPokemon = viewModel.getPlayer1Active();
        BattleAIViewModel.PokemonViewModel aiPokemon = viewModel.getPlayer2Active();
        List<BattleAIViewModel.PokemonViewModel> playerTeam = viewModel.getPlayer1Team();

        // Update player Pokemon
        if (playerPokemon != null) {
            updatePlayerPokemon(playerPokemon);
        }

        // Update AI Pokemon
        if (aiPokemon != null) {
            updateAIPokemon(aiPokemon);
        }

        // Update team panel
        if (playerTeam != null) {
            updateTeamDisplay(playerTeam, playerPokemon);
        }

        // Check if battle ended
        if ("COMPLETED".equals(battleStatus)) {
            displayBattleEnded();
        }

        revalidate();
        repaint();
    }

    private void updatePlayerPokemon(BattleAIViewModel.PokemonViewModel pokemon) {
        playerPokemonNameLabel.setText(pokemon.getName().toUpperCase());
        loadPokemonImage(playerPokemonImageLabel, pokemon, true);

        int currentHP = pokemon.getCurrentHP();
        int maxHP = pokemon.getMaxHP();

        playerHPLabel.setText(currentHP + " / " + maxHP);
        int hpPercent = maxHP > 0 ? (currentHP * 100 / maxHP) : 0;
        playerHPBar.setValue(hpPercent);

        // Update HP bar color based on percentage
        if (hpPercent > 50) {
            playerHPBar.setForeground(new Color(76, 175, 80)); // Green
        } else if (hpPercent > 25) {
            playerHPBar.setForeground(new Color(255, 193, 7)); // Yellow
        } else {
            playerHPBar.setForeground(new Color(244, 67, 54)); // Red
        }

        // Update moves
        updateMovesDisplay(pokemon);
    }

    private void updateAIPokemon(BattleAIViewModel.PokemonViewModel pokemon) {
        aiPokemonNameLabel.setText(pokemon.getName().toUpperCase() + " (Lv.50)");
        loadPokemonImage(aiPokemonImageLabel, pokemon, false);

        int currentHP = pokemon.getCurrentHP();
        int maxHP = pokemon.getMaxHP();

        aiHPLabel.setText(currentHP + " / " + maxHP);
        int hpPercent = maxHP > 0 ? (currentHP * 100 / maxHP) : 0;
        aiHPBar.setValue(hpPercent);

        // Update HP bar color based on percentage
        if (hpPercent > 50) {
            aiHPBar.setForeground(new Color(76, 175, 80)); // Green
        } else if (hpPercent > 25) {
            aiHPBar.setForeground(new Color(255, 193, 7)); // Yellow
        } else {
            aiHPBar.setForeground(new Color(244, 67, 54)); // Red
        }
    }

    private void updateMovesDisplay(BattleAIViewModel.PokemonViewModel pokemon) {
        List<String> moves = pokemon.getMoveNames();

        for (int i = 0; i < 4; i++) {
            if (moves != null && i < moves.size()) {
                String moveName = moves.get(i);
                moveButtons[i].setText(moveName.toUpperCase());
                moveButtons[i].setEnabled(true);
            } else {
                moveButtons[i].setText("-");
                moveButtons[i].setEnabled(false);
            }
        }
    }

    private void updateTeamDisplay(List<BattleAIViewModel.PokemonViewModel> team,
                                   BattleAIViewModel.PokemonViewModel activePokemon) {
        teamPanel.removeAll();

        JLabel label = new JLabel("SWITCH:");
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        teamPanel.add(label);

        if (team == null || team.isEmpty()) {
            teamPanel.revalidate();
            teamPanel.repaint();
            return;
        }

        for (BattleAIViewModel.PokemonViewModel pokemon : team) {
            if (pokemon == null) continue;

            String pokemonName = pokemon.getName();
            if (pokemonName == null || pokemonName.isEmpty()) {
                pokemonName = "PKM" + pokemon.getId();
            }

            String btnText = pokemonName;
            boolean isActive = (activePokemon != null && pokemon.getId() == activePokemon.getId());
            boolean isFainted = pokemon.isFainted();

            if (isActive) {
                btnText += " *";
            } else if (isFainted) {
                btnText += " X";
            }

            JButton pokemonBtn = new JButton(btnText);
            pokemonBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            pokemonBtn.setFocusPainted(false);

            if (isActive || isFainted) {
                pokemonBtn.setEnabled(false);
            } else {
                final int pokemonId = pokemon.getId();
                pokemonBtn.addActionListener(e -> executeSwitch(pokemonId));
            }

            teamPanel.add(pokemonBtn);
        }

        teamPanel.revalidate();
        teamPanel.repaint();
    }

    private void executeMove(int moveIndex) {
        // Execute move via controller - all logic is in Interactor
        controller.executePlayerMove(moveIndex);

        // Get result from ViewModel
        String result = viewModel.getCurrentTurnDescription();
        if (result == null) result = "";

        messageArea.setText(result);
        updateDisplay();
    }

    private void executeSwitch(int pokemonId) {
        // Execute switch via controller - all logic is in Interactor
        controller.executePlayerSwitch(pokemonId);

        // Get result from ViewModel
        String result = viewModel.getCurrentTurnDescription();
        if (result == null) result = "";

        messageArea.setText(result);
        updateDisplay();
    }

    private void displayBattleEnded() {
        String winnerName = viewModel.getWinnerName();
        if (winnerName != null) {
            winnerLabel.setText(winnerName + " wins!");
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