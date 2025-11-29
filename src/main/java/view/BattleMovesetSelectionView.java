package view;

import entities.Move;
import entities.Pokemon;
import interface_adapters.ui.StyledButton;
import interface_adapters.ui.UIStyleConstants;
import pokeapi.JSONLoader;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View for selecting moves for each Pokemon before battle.
 * After selecting moves for all Pokemon, proceeds to battle via callback.
 */
public class BattleMovesetSelectionView extends JFrame {

    private final List<Pokemon> battlePokemon;
    private final Runnable onComplete;
    private final Runnable onCancel;
    private final Map<Pokemon, List<String>> selectedMoves = new HashMap<>();
    private final Map<Pokemon, JPanel> pokemonPanels = new HashMap<>();
    private JButton proceedButton;

    public BattleMovesetSelectionView(List<Pokemon> battlePokemon, Runnable onComplete, Runnable onCancel) {
        this.battlePokemon = battlePokemon;
        this.onComplete = onComplete;
        this.onCancel = onCancel;

        // Initialize with empty selections
        for (Pokemon p : battlePokemon) {
            selectedMoves.put(p, new ArrayList<>());
        }

        setTitle("Select Moves For Battle");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title Panel
        add(createTitlePanel(), BorderLayout.NORTH);

        // Main content - Pokemon cards with move selection
        add(createMainPanel(), BorderLayout.CENTER);

        // Bottom Panel - Back and Proceed buttons
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyleConstants.PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("SELECT MOVES FOR EACH POKEMON");
        title.setFont(UIStyleConstants.TITLE_FONT);
        title.setForeground(Color.WHITE);
        panel.add(title);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.BACKGROUND);
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Instructions
        JLabel instructions = new JLabel("<html><center>Click on each Pokemon to select 1-4 moves for battle.<br/>All Pokemon must have at least 1 move selected.</center></html>");
        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        instructions.setFont(UIStyleConstants.BODY_FONT);
        instructions.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        container.add(instructions, BorderLayout.NORTH);

        // Pokemon grid
        JPanel gridPanel = new JPanel(new GridLayout(1, battlePokemon.size(), 20, 0));
        gridPanel.setBackground(UIStyleConstants.BACKGROUND);

        for (Pokemon pokemon : battlePokemon) {
            JPanel pokemonCard = createPokemonCard(pokemon);
            pokemonPanels.put(pokemon, pokemonCard);
            gridPanel.add(pokemonCard);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Pokemon image
        JLabel imgLabel = new JLabel();
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(120, 120));
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {
            imgLabel.setText("[Image]");
        }
        card.add(imgLabel);

        // Pokemon name
        JLabel nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(UIStyleConstants.HEADING_FONT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        // Pokemon types
        String types = String.join("/", pokemon.getTypes());
        JLabel typeLabel = new JLabel(types.toUpperCase());
        typeLabel.setFont(UIStyleConstants.BODY_FONT);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(typeLabel);

        card.add(Box.createVerticalStrut(10));

        // Selected moves display
        JPanel movesPanel = new JPanel();
        movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
        movesPanel.setBackground(new Color(240, 240, 240));
        movesPanel.setBorder(new TitledBorder("Selected Moves (0/4)"));
        movesPanel.setPreferredSize(new Dimension(200, 100));
        movesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(movesPanel);

        card.add(Box.createVerticalStrut(10));

        // Select moves button
        StyledButton selectBtn = new StyledButton("Select Moves");
        selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectBtn.addActionListener(e -> openMoveSelectionDialog(pokemon, movesPanel));
        card.add(selectBtn);

        return card;
    }

    private void openMoveSelectionDialog(Pokemon pokemon, JPanel movesPanel) {
        List<String> availableMoveNames = pokemon.getMoves();
        if (availableMoveNames == null || availableMoveNames.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "This Pokemon has no moves available!",
                "No Moves", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create dialog for move selection
        JDialog dialog = new JDialog(this, capitalize(pokemon.getName()) + " - Select Moves", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIStyleConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel headerLabel = new JLabel("Select 1-4 Moves for " + capitalize(pokemon.getName()));
        headerLabel.setFont(UIStyleConstants.HEADING_FONT);
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Moves list with checkboxes
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<String> currentSelection = selectedMoves.get(pokemon);

        for (String moveName : availableMoveNames) {
            Move moveDetail = findMoveDetail(moveName);

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            // Checkbox with move name
            JCheckBox cb = new JCheckBox(capitalize(moveName));
            cb.setFont(new Font("Arial", Font.BOLD, 14));
            cb.setBackground(Color.WHITE);
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
                // Type label
                JLabel typeLabel = new JLabel(moveDetail.getType() != null ? moveDetail.getType().toUpperCase() : "???");
                typeLabel.setOpaque(true);
                typeLabel.setForeground(Color.WHITE);
                typeLabel.setBackground(getTypeColor(moveDetail.getType()));
                typeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                typeLabel.setFont(new Font("Arial", Font.BOLD, 10));
                row.add(typeLabel);

                // Stats
                String power = moveDetail.getPower() != null ? String.valueOf(moveDetail.getPower()) : "-";
                String accuracy = moveDetail.getAccuracy() != null ? String.valueOf(moveDetail.getAccuracy()) : "-";
                JLabel statsLabel = new JLabel("Power: " + power + " | Accuracy: " + accuracy + " | " +
                    (moveDetail.getDamageClass() != null ? moveDetail.getDamageClass() : "unknown"));
                statsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                row.add(statsLabel);
            }

            listPanel.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(UIStyleConstants.BACKGROUND);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        bottomPanel.add(cancelBtn);

        JButton saveBtn = new StyledButton("Save Selection");
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
            updateMovesPanelDisplay(movesPanel, selected);
            updateProceedButton();
            dialog.dispose();
        });
        bottomPanel.add(saveBtn);

        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateMovesPanelDisplay(JPanel movesPanel, List<String> moves) {
        movesPanel.removeAll();
        ((TitledBorder) movesPanel.getBorder()).setTitle("Selected Moves (" + moves.size() + "/4)");

        for (String moveName : moves) {
            JLabel moveLabel = new JLabel("- " + capitalize(moveName));
            moveLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            movesPanel.add(moveLabel);
        }

        movesPanel.revalidate();
        movesPanel.repaint();
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(UIStyleConstants.BACKGROUND);

        // Back button
        StyledButton backBtn = new StyledButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            if (onCancel != null) {
                onCancel.run();
            }
        });
        panel.add(backBtn);

        // Proceed button
        proceedButton = new StyledButton("PROCEED TO BATTLE", UIStyleConstants.SECONDARY_COLOR);
        proceedButton.setPreferredSize(new Dimension(200, 50));
        proceedButton.setEnabled(false);
        proceedButton.addActionListener(e -> proceedToBattle());
        panel.add(proceedButton);

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

    private Color getTypeColor(String type) {
        if (type == null) return Color.GRAY;
        String t = type.toLowerCase();
        switch (t) {
            case "fire": return new Color(255, 80, 50);
            case "water": return new Color(80, 150, 255);
            case "grass": return new Color(80, 200, 80);
            case "electric": return new Color(255, 220, 50);
            case "ice": return new Color(120, 220, 255);
            case "fighting": return new Color(200, 80, 60);
            case "poison": return new Color(180, 60, 180);
            case "ground": return new Color(220, 180, 90);
            case "flying": return new Color(150, 180, 255);
            case "psychic": return new Color(255, 100, 180);
            case "bug": return new Color(170, 200, 50);
            case "rock": return new Color(200, 180, 60);
            case "ghost": return new Color(120, 110, 180);
            case "dragon": return new Color(90, 110, 255);
            case "dark": return new Color(90, 70, 60);
            case "steel": return new Color(150, 150, 170);
            case "fairy": return new Color(255, 150, 200);
            case "normal": return new Color(180, 180, 160);
            default: return Color.GRAY;
        }
    }
}
