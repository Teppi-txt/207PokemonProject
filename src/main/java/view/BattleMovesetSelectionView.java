package view;

import entities.Move;
import entities.Pokemon;
import interface_adapters.ui.RetroButton;
import interface_adapters.ui.UIStyleConstants;
import pokeapi.JSONLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retro-styled view for selecting moves for each Pokemon before battle.
 * After selecting moves for all Pokemon, proceeds to battle via callback.
 */
public class BattleMovesetSelectionView extends JFrame {

    private final List<Pokemon> battlePokemon;
    private final Runnable onComplete;
    private final Runnable onCancel;
    private final Map<Pokemon, List<String>> selectedMoves = new HashMap<>();
    private final Map<Pokemon, JPanel> movesDisplayPanels = new HashMap<>();
    private final Map<Pokemon, JLabel> movesCountLabels = new HashMap<>();
    private RetroButton proceedButton;

    public BattleMovesetSelectionView(List<Pokemon> battlePokemon, Runnable onComplete, Runnable onCancel) {
        this.battlePokemon = battlePokemon;
        this.onComplete = onComplete;
        this.onCancel = onCancel;

        // Initialize with empty selections
        for (Pokemon p : battlePokemon) {
            selectedMoves.put(p, new ArrayList<>());
        }

        setTitle("Select Moves For Battle");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyleConstants.DARK_BG);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, UIStyleConstants.POKEMON_BLUE,
                    getWidth(), 0, UIStyleConstants.POKEMON_BLUE_LIGHT
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Bottom accent
                g2d.setColor(UIStyleConstants.SECONDARY_COLOR);
                g2d.fillRect(0, getHeight() - 4, getWidth(), 4);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 100));

        JLabel title = new JLabel("SELECT MOVES FOR BATTLE");
        title.setFont(UIStyleConstants.TITLE_FONT);
        title.setForeground(UIStyleConstants.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(8));

        JLabel instructions = new JLabel("Click SELECT MOVES to choose 1-4 moves for each Pokemon");
        instructions.setFont(UIStyleConstants.BODY_FONT);
        instructions.setForeground(UIStyleConstants.SECONDARY_COLOR);
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(instructions);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.DARK_BG);
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Pokemon cards grid
        JPanel gridPanel = new JPanel(new GridLayout(1, battlePokemon.size(), 20, 0));
        gridPanel.setBackground(UIStyleConstants.DARK_BG);

        for (Pokemon pokemon : battlePokemon) {
            JPanel pokemonCard = createPokemonCard(pokemon);
            gridPanel.add(pokemonCard);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIStyleConstants.DARK_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = new JPanel() {
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Pokemon image
        JLabel imgLabel = new JLabel();
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(100, 100));
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {
            imgLabel.setText("?");
            imgLabel.setFont(UIStyleConstants.TITLE_FONT);
        }
        card.add(imgLabel);

        // Pokemon name
        JLabel nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(UIStyleConstants.HEADING_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        // Pokemon types with colored background
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        typePanel.setOpaque(false);
        for (String type : pokemon.getTypes()) {
            JLabel typeLabel = new JLabel(type.toUpperCase());
            typeLabel.setFont(UIStyleConstants.SMALL_FONT);
            typeLabel.setForeground(Color.WHITE);
            typeLabel.setOpaque(true);
            typeLabel.setBackground(UIStyleConstants.getTypeColor(type));
            typeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            typePanel.add(typeLabel);
        }
        typePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(typePanel);

        card.add(Box.createVerticalStrut(15));

        // Selected moves panel
        JPanel movesBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(new Color(60, 60, 80));
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.drawRect(0, 0, w - 1, h - 1);
            }
        };
        movesBox.setLayout(new BoxLayout(movesBox, BoxLayout.Y_AXIS));
        movesBox.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        movesBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        movesBox.setMaximumSize(new Dimension(250, 120));
        movesBox.setPreferredSize(new Dimension(250, 120));

        JLabel movesHeader = new JLabel("SELECTED MOVES (0/4)");
        movesHeader.setFont(UIStyleConstants.SMALL_FONT);
        movesHeader.setForeground(UIStyleConstants.SECONDARY_COLOR);
        movesHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        movesBox.add(movesHeader);
        movesCountLabels.put(pokemon, movesHeader);

        movesBox.add(Box.createVerticalStrut(5));

        JPanel movesListPanel = new JPanel();
        movesListPanel.setLayout(new BoxLayout(movesListPanel, BoxLayout.Y_AXIS));
        movesListPanel.setOpaque(false);
        movesBox.add(movesListPanel);
        movesDisplayPanels.put(pokemon, movesListPanel);

        card.add(movesBox);

        card.add(Box.createVerticalStrut(15));

        // Select moves button
        RetroButton selectBtn = new RetroButton("SELECT MOVES");
        selectBtn.setButtonColor(UIStyleConstants.POKEMON_BLUE);
        selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectBtn.addActionListener(e -> openMoveSelectionDialog(pokemon));
        card.add(selectBtn);

        return card;
    }

    private void openMoveSelectionDialog(Pokemon pokemon) {
        List<String> availableMoveNames = pokemon.getMoves();
        if (availableMoveNames == null || availableMoveNames.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "This Pokemon has no moves available!",
                "No Moves", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create styled dialog
        JDialog dialog = new JDialog(this, capitalize(pokemon.getName()) + " - Select Moves", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyleConstants.DARK_BG);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(UIStyleConstants.POKEMON_BLUE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel headerLabel = new JLabel("SELECT 1-4 MOVES FOR " + capitalize(pokemon.getName()).toUpperCase());
        headerLabel.setFont(UIStyleConstants.HEADING_FONT);
        headerLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
        headerPanel.add(headerLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Moves list with checkboxes
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UIStyleConstants.DARK_BG);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<String> currentSelection = selectedMoves.get(pokemon);

        for (String moveName : availableMoveNames) {
            Move moveDetail = findMoveDetail(moveName);
            JPanel row = createMoveRow(moveName, moveDetail, checkBoxes, currentSelection, dialog);
            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIStyleConstants.DARK_BG);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(UIStyleConstants.DARK_BG);

        RetroButton cancelBtn = new RetroButton("CANCEL");
        cancelBtn.setButtonColor(UIStyleConstants.BORDER_DARK);
        cancelBtn.addActionListener(e -> dialog.dispose());
        bottomPanel.add(cancelBtn);

        RetroButton saveBtn = new RetroButton("SAVE SELECTION");
        saveBtn.setButtonColor(UIStyleConstants.HP_HIGH);
        saveBtn.addActionListener(e -> {
            List<String> selected = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selected.add(availableMoveNames.get(i));
                }
            }

            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please select at least 1 move!",
                    "No Moves Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            selectedMoves.put(pokemon, selected);
            updateMovesDisplay(pokemon, selected);
            updateProceedButton();
            dialog.dispose();
        });
        bottomPanel.add(saveBtn);

        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createMoveRow(String moveName, Move moveDetail, List<JCheckBox> checkBoxes,
                                  List<String> currentSelection, JDialog dialog) {
        JPanel row = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(0, 0, w, h);
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.drawRect(0, 0, w - 1, h - 1);
            }
        };
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Checkbox with move name
        JCheckBox cb = new JCheckBox(capitalize(moveName));
        cb.setFont(UIStyleConstants.BODY_FONT);
        cb.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cb.setOpaque(false);
        cb.setSelected(currentSelection.contains(moveName));
        checkBoxes.add(cb);

        // Limit to 4 moves
        cb.addActionListener(e -> {
            long count = checkBoxes.stream().filter(JCheckBox::isSelected).count();
            if (count > 4) {
                cb.setSelected(false);
                JOptionPane.showMessageDialog(dialog,
                    "You can only select up to 4 moves!",
                    "Limit Reached", JOptionPane.WARNING_MESSAGE);
            }
        });
        row.add(cb);

        // Move details
        if (moveDetail != null) {
            JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
            detailsPanel.setOpaque(false);

            // Type label
            if (moveDetail.getType() != null) {
                JLabel typeLabel = new JLabel(moveDetail.getType().toUpperCase());
                typeLabel.setFont(UIStyleConstants.SMALL_FONT);
                typeLabel.setForeground(Color.WHITE);
                typeLabel.setOpaque(true);
                typeLabel.setBackground(UIStyleConstants.getTypeColor(moveDetail.getType()));
                typeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                detailsPanel.add(typeLabel);
            }

            // Stats
            String power = moveDetail.getPower() != null ? String.valueOf(moveDetail.getPower()) : "-";
            String accuracy = moveDetail.getAccuracy() != null ? String.valueOf(moveDetail.getAccuracy()) : "-";
            JLabel statsLabel = new JLabel("PWR: " + power + " | ACC: " + accuracy);
            statsLabel.setFont(UIStyleConstants.SMALL_FONT);
            statsLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
            detailsPanel.add(statsLabel);

            if (moveDetail.getDamageClass() != null) {
                JLabel classLabel = new JLabel("(" + moveDetail.getDamageClass() + ")");
                classLabel.setFont(UIStyleConstants.SMALL_FONT);
                classLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
                detailsPanel.add(classLabel);
            }

            row.add(detailsPanel);
        }

        return row;
    }

    private void updateMovesDisplay(Pokemon pokemon, List<String> moves) {
        JPanel panel = movesDisplayPanels.get(pokemon);
        JLabel countLabel = movesCountLabels.get(pokemon);

        panel.removeAll();
        countLabel.setText("SELECTED MOVES (" + moves.size() + "/4)");

        for (String moveName : moves) {
            JLabel moveLabel = new JLabel("- " + capitalize(moveName));
            moveLabel.setFont(UIStyleConstants.SMALL_FONT);
            moveLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
            moveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(moveLabel);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void updateProceedButton() {
        boolean allHaveMoves = true;
        for (Pokemon p : battlePokemon) {
            List<String> moves = selectedMoves.get(p);
            if (moves == null || moves.isEmpty()) {
                allHaveMoves = false;
                break;
            }
        }
        proceedButton.setEnabled(allHaveMoves);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Back button on left
        RetroButton backBtn = new RetroButton("BACK");
        backBtn.setButtonColor(UIStyleConstants.BORDER_DARK);
        backBtn.addActionListener(e -> {
            dispose();
            if (onCancel != null) {
                onCancel.run();
            }
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backBtn);
        panel.add(leftPanel, BorderLayout.WEST);

        // Proceed button on right
        proceedButton = new RetroButton("PROCEED TO BATTLE");
        proceedButton.setButtonColor(UIStyleConstants.SECONDARY_COLOR);
        proceedButton.setPreferredSize(new Dimension(200, 50));
        proceedButton.setEnabled(false);
        proceedButton.addActionListener(e -> proceedToBattle());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(proceedButton);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void proceedToBattle() {
        // Update each Pokemon's moves to only contain the selected ones
        for (Pokemon pokemon : battlePokemon) {
            List<String> selected = selectedMoves.get(pokemon);
            if (selected != null && !selected.isEmpty()) {
                pokemon.setMoves(new ArrayList<>(selected));
            }
        }

        dispose();
        if (onComplete != null) {
            onComplete.run();
        }
    }

    private Move findMoveDetail(String moveName) {
        if (JSONLoader.allMoves == null) return null;
        for (Move m : JSONLoader.allMoves) {
            if (m.getName().equalsIgnoreCase(moveName)) {
                return m;
            }
        }
        return null;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
