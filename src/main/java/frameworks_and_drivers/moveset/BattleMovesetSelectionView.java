package frameworks_and_drivers.moveset;

import entities.battle.Move;
import entities.Pokemon;
import pokeapi.JSONLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple view for selecting moves for each Pokemon before battle.
 */
public class BattleMovesetSelectionView extends JFrame {

    private final List<Pokemon> battlePokemon;
    private final Runnable onComplete;
    private final Runnable onCancel;
    private final Map<Pokemon, List<String>> selectedMoves = new HashMap<>();
    private final Map<Pokemon, JPanel> movesDisplayPanels = new HashMap<>();
    private final Map<Pokemon, JLabel> movesCountLabels = new HashMap<>();
    private JButton proceedButton;

    public BattleMovesetSelectionView(List<Pokemon> battlePokemon, Runnable onComplete, Runnable onCancel) {
        this.battlePokemon = battlePokemon;
        this.onComplete = onComplete;
        this.onCancel = onCancel;

        for (Pokemon p : battlePokemon) {
            selectedMoves.put(p, new ArrayList<>());
        }

        setTitle("Select Moves For Battle");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("SELECT MOVES FOR BATTLE");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        JLabel instructions = new JLabel("Click SELECT MOVES to choose 1-4 moves for each Pokemon");
        instructions.setFont(new Font("SansSerif", Font.PLAIN, 14));
        instructions.setForeground(new Color(100, 100, 100));
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(5));
        panel.add(instructions);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel gridPanel = new JPanel(new GridLayout(1, battlePokemon.size(), 20, 0));
        gridPanel.setBackground(Color.WHITE);

        for (Pokemon pokemon : battlePokemon) {
            JPanel pokemonCard = createPokemonCard(pokemon);
            gridPanel.add(pokemonCard);
        }

        container.add(gridPanel, BorderLayout.CENTER);
        return container;
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

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
            imgLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        }
        card.add(imgLabel);

        // Pokemon name
        JLabel nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        card.add(Box.createVerticalStrut(15));

        // Selected moves panel
        JPanel movesBox = new JPanel();
        movesBox.setLayout(new BoxLayout(movesBox, BoxLayout.Y_AXIS));
        movesBox.setBackground(new Color(248, 248, 248));
        movesBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        movesBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        movesBox.setMaximumSize(new Dimension(220, 110));

        JLabel movesHeader = new JLabel("SELECTED MOVES (0/4)");
        movesHeader.setFont(new Font("SansSerif", Font.BOLD, 11));
        movesHeader.setForeground(new Color(100, 100, 100));
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
        JButton selectBtn = new JButton("SELECT MOVES");
        selectBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectBtn.setFocusPainted(false);
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

        JDialog dialog = new JDialog(this, capitalize(pokemon.getName()) + " - Select Moves", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel headerLabel = new JLabel("Select 1-4 moves for " + capitalize(pokemon.getName()));
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Moves list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
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
        scrollPane.getViewport().setBackground(Color.WHITE);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        cancelBtn.addActionListener(e -> dialog.dispose());
        bottomPanel.add(cancelBtn);

        JButton saveBtn = new JButton("SAVE SELECTION");
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
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
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(new Color(248, 248, 248));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JCheckBox cb = new JCheckBox(capitalize(moveName));
        cb.setFont(new Font("SansSerif", Font.BOLD, 13));
        cb.setOpaque(false);
        cb.setSelected(currentSelection.contains(moveName));
        checkBoxes.add(cb);

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

        if (moveDetail != null) {
            JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            detailsPanel.setOpaque(false);

            String power = moveDetail.getPower() != null ? String.valueOf(moveDetail.getPower()) : "-";
            String accuracy = moveDetail.getAccuracy() != null ? String.valueOf(moveDetail.getAccuracy()) : "-";
            String type = moveDetail.getType() != null ? moveDetail.getType().toUpperCase() : "";

            JLabel statsLabel = new JLabel(type + " | PWR: " + power + " | ACC: " + accuracy);
            statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            statsLabel.setForeground(new Color(100, 100, 100));
            detailsPanel.add(statsLabel);

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
            moveLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
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
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JButton backBtn = new JButton("BACK");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        backBtn.addActionListener(e -> {
            dispose();
            if (onCancel != null) onCancel.run();
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(backBtn);
        panel.add(leftPanel, BorderLayout.WEST);

        proceedButton = new JButton("PROCEED TO BATTLE");
        proceedButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        proceedButton.setEnabled(false);
        proceedButton.addActionListener(e -> proceedToBattle());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(proceedButton);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void proceedToBattle() {
        for (Pokemon pokemon : battlePokemon) {
            List<String> selected = selectedMoves.get(pokemon);
            if (selected != null && !selected.isEmpty()) {
                pokemon.setMoves(new ArrayList<>(selected));
            }
        }

        dispose();
        if (onComplete != null) onComplete.run();
    }

    private Move findMoveDetail(String moveName) {
        if (JSONLoader.getInstance().getAllMoves() == null) return null;
        for (Move m : JSONLoader.getInstance().getAllMoves()) {
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
