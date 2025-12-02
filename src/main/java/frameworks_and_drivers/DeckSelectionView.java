package frameworks_and_drivers;

import entities.Pokemon;
import entities.user.User;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIDataAccessObject;
import interface_adapters.battle_ai.BattleAIViewModel;
import view.BattleMovesetSelectionView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple view for selecting Pokemon deck before battle.
 */
public class DeckSelectionView extends JFrame {

    private final BattleAIController controller;
    private final BattleAIDataAccessObject dataAccess;
    private final BattleAIViewModel viewModel;
    private final User user;
    private final List<Pokemon> selectedDeck;
    private JPanel selectedPokemonPanel;
    private JLabel deckHeaderLabel;
    private JButton startButton;
    private final JComboBox<String> difficultyCombo;
    private final Runnable returnCallback;

    public DeckSelectionView(BattleAIController controller, BattleAIDataAccessObject dataAccess,
                             BattleAIViewModel viewModel, User user, Runnable returnCallback) {
        this.controller = controller;
        this.dataAccess = dataAccess;
        this.viewModel = viewModel;
        this.user = user;
        this.selectedDeck = new ArrayList<>();
        this.returnCallback = returnCallback;

        setTitle("Select Your Battle Team");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        mainPanel.add(createCollectionPanel(), BorderLayout.CENTER);
        mainPanel.add(createSelectedPanel(), BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel diffPanel = (JPanel) bottomPanel.getComponent(0);
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        diffPanel.add(diffLabel);
        diffPanel.add(difficultyCombo);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("SELECT YOUR BATTLE TEAM");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title);

        return panel;
    }

    private JPanel createCollectionPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("YOUR POKEMON");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        container.add(headerLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        List<Pokemon> ownedPokemon = user.getOwnedPokemon();
        if (ownedPokemon.isEmpty()) {
            JLabel emptyLabel = new JLabel("No Pokemon! Open a pack first.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            gridPanel.add(emptyLabel);
        } else {
            for (Pokemon pokemon : ownedPokemon) {
                JPanel card = createPokemonCard(pokemon);
                gridPanel.add(card);
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createPokemonCard(Pokemon pokemon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Pokemon sprite
        JLabel imgLabel = new JLabel();
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(80, 80));
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {
            imgLabel.setText("?");
            imgLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        }
        card.add(imgLabel);

        // Pokemon name
        JLabel nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        card.add(Box.createVerticalStrut(8));

        // Add button
        JButton addBtn = new JButton("ADD");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addToDeck(pokemon));
        card.add(addBtn);

        return card;
    }

    private JPanel createSelectedPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setPreferredSize(new Dimension(220, 0));
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        deckHeaderLabel = new JLabel("BATTLE DECK (0/3)");
        deckHeaderLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        container.add(deckHeaderLabel, BorderLayout.NORTH);

        selectedPokemonPanel = new JPanel();
        selectedPokemonPanel.setLayout(new BoxLayout(selectedPokemonPanel, BoxLayout.Y_AXIS));
        selectedPokemonPanel.setBackground(Color.WHITE);
        selectedPokemonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        container.add(selectedPokemonPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("BACK");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        backButton.addActionListener(e -> {
            dispose();
            if (returnCallback != null) returnCallback.run();
        });
        leftPanel.add(backButton);

        panel.add(leftPanel, BorderLayout.WEST);

        startButton = new JButton("START BATTLE");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startBattle());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(startButton);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void addToDeck(Pokemon pokemon) {
        if (selectedDeck.size() >= 3) {
            JOptionPane.showMessageDialog(this, "Deck is full! (Maximum 3 Pokemon)",
                    "Deck Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedDeck.contains(pokemon)) {
            JOptionPane.showMessageDialog(this, "This Pokemon is already in your deck!",
                    "Already Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedDeck.add(pokemon);
        updateSelectedPanel();
    }

    private void updateSelectedPanel() {
        deckHeaderLabel.setText("BATTLE DECK (" + selectedDeck.size() + "/3)");
        selectedPokemonPanel.removeAll();

        for (int i = 0; i < selectedDeck.size(); i++) {
            Pokemon pokemon = selectedDeck.get(i);
            JPanel row = createSelectedPokemonRow(pokemon, i);
            selectedPokemonPanel.add(row);
            selectedPokemonPanel.add(Box.createVerticalStrut(5));
        }

        selectedPokemonPanel.revalidate();
        selectedPokemonPanel.repaint();
        startButton.setEnabled(selectedDeck.size() == 3);
    }

    private JPanel createSelectedPokemonRow(Pokemon pokemon, int index) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(new Color(248, 248, 248));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        row.setMaximumSize(new Dimension(200, 35));

        JLabel nameLabel = new JLabel((index + 1) + ". " + capitalize(pokemon.getName()));
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        row.add(nameLabel);
        row.add(Box.createHorizontalGlue());

        JButton removeBtn = new JButton("X");
        removeBtn.setFont(new Font("SansSerif", Font.BOLD, 9));
        removeBtn.setMargin(new Insets(0, 0, 0, 0));
        removeBtn.setPreferredSize(new Dimension(22, 20));
        removeBtn.setMinimumSize(new Dimension(22, 20));
        removeBtn.setMaximumSize(new Dimension(22, 20));
        removeBtn.setFocusPainted(false);
        removeBtn.addActionListener(e -> {
            selectedDeck.remove(index);
            updateSelectedPanel();
        });
        row.add(removeBtn);

        return row;
    }

    private void startBattle() {
        String difficulty = ((String) difficultyCombo.getSelectedItem()).toLowerCase();

        List<Pokemon> battleDeck = new ArrayList<>();
        for (Pokemon p : selectedDeck) {
            battleDeck.add(p.copy());
        }

        Runnable onMovesetComplete = () -> {
            // Set up battle via controller (proper Clean Architecture)
            controller.setupBattle(user, battleDeck, difficulty);

            // Create battle view
            BattleAIView battleView = new BattleAIView(controller, viewModel, returnCallback);
            battleView.setVisible(true);
        };

        Runnable onMovesetCancel = () -> this.setVisible(true);

        BattleMovesetSelectionView movesetView = new BattleMovesetSelectionView(
            battleDeck, onMovesetComplete, onMovesetCancel
        );
        movesetView.setVisible(true);
        this.setVisible(false);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
