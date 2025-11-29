package frameworks_and_drivers;

import entities.Pokemon;
import entities.User;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.ui.*;
import view.BattleMovesetSelectionView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View for selecting Pokemon deck before battle.
 * Retro Pokemon-themed with dark background and pixel-style aesthetics.
 */
public class DeckSelectionView extends JFrame {

    private final BattleAIController controller;
    private final User user;
    private final List<Pokemon> selectedDeck;
    private JPanel selectedPokemonPanel;
    private JLabel deckHeaderLabel;
    private RetroButton startButton;
    private final JComboBox<String> difficultyCombo;
    private Runnable returnCallback;

    public DeckSelectionView(BattleAIController controller, User user) {
        this(controller, user, null);
    }

    public DeckSelectionView(BattleAIController controller, User user, Runnable returnCallback) {
        this.controller = controller;
        this.user = user;
        this.selectedDeck = new ArrayList<>();
        this.returnCallback = returnCallback;

        setTitle("Select Your Battle Team");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyleConstants.DARK_BG);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Content
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(UIStyleConstants.DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Left: Pokemon Collection
        mainPanel.add(createCollectionPanel(), BorderLayout.CENTER);

        // Right: Selected Deck
        mainPanel.add(createSelectedPanel(), BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialize difficulty combo
        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyCombo.setFont(UIStyleConstants.BODY_FONT);
        difficultyCombo.setBackground(UIStyleConstants.MENU_BG);

        // Add to bottom panel
        JPanel diffPanel = (JPanel) bottomPanel.getComponent(0);
        diffPanel.add(new JLabel("Difficulty:") {{
            setForeground(UIStyleConstants.TEXT_LIGHT);
            setFont(UIStyleConstants.BODY_FONT);
        }});
        diffPanel.add(difficultyCombo);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, UIStyleConstants.PRIMARY_COLOR,
                    getWidth(), 0, UIStyleConstants.PRIMARY_DARK
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Bottom border
                g2d.setColor(UIStyleConstants.SECONDARY_COLOR);
                g2d.fillRect(0, getHeight() - 4, getWidth(), 4);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 100));

        JLabel title = new JLabel("SELECT YOUR BATTLE TEAM");
        title.setFont(UIStyleConstants.TITLE_FONT);
        title.setForeground(UIStyleConstants.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Trainer: " + user.getName() + " | Currency: $" + user.getCurrency());
        subtitle.setFont(UIStyleConstants.BODY_FONT);
        subtitle.setForeground(UIStyleConstants.SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        return panel;
    }

    private JPanel createCollectionPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.DARK_BG);

        // Header box
        JPanel headerBox = createRetroBox("YOUR POKEMON", UIStyleConstants.POKEMON_BLUE);
        container.add(headerBox, BorderLayout.NORTH);

        // Grid of Pokemon cards
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        gridPanel.setBackground(UIStyleConstants.DARK_BG);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        List<Pokemon> ownedPokemon = user.getOwnedPokemon();
        if (ownedPokemon.isEmpty()) {
            JLabel emptyLabel = new JLabel("No Pokemon! Open a pack first.");
            emptyLabel.setFont(UIStyleConstants.BODY_FONT);
            emptyLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.add(emptyLabel);
        } else {
            for (Pokemon pokemon : ownedPokemon) {
                JPanel card = createPokemonCard(pokemon);
                gridPanel.add(card);
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_DARK, 2));
        scrollPane.getViewport().setBackground(UIStyleConstants.DARK_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
                g2d.fillRect(3, 3, w - 6, h - 6);

                // Highlight effect
                g2d.setColor(Color.WHITE);
                g2d.drawLine(3, 3, w - 3, 3);
                g2d.drawLine(3, 3, 3, h - 3);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        card.setPreferredSize(new Dimension(160, 180));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
            imgLabel.setFont(UIStyleConstants.TITLE_FONT);
        }
        card.add(imgLabel);

        // Pokemon name
        JLabel nameLabel = new JLabel(capitalize(pokemon.getName()));
        nameLabel.setFont(UIStyleConstants.HEADING_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        // Pokemon types
        String types = String.join("/", pokemon.getTypes()).toUpperCase();
        JLabel typeLabel = new JLabel(types);
        typeLabel.setFont(UIStyleConstants.SMALL_FONT);
        typeLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(typeLabel);

        card.add(Box.createVerticalStrut(5));

        // Add button
        RetroButton addBtn = new RetroButton("ADD");
        addBtn.setButtonColor(UIStyleConstants.HP_HIGH);
        addBtn.setFont(UIStyleConstants.SMALL_FONT);
        addBtn.setPreferredSize(new Dimension(80, 28));
        addBtn.setMaximumSize(new Dimension(80, 28));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> addToDeck(pokemon));
        card.add(addBtn);

        return card;
    }

    private JPanel createSelectedPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.DARK_BG);
        container.setPreferredSize(new Dimension(280, 0));

        // Header
        deckHeaderLabel = new JLabel("BATTLE DECK (0/3)");
        JPanel headerBox = createRetroBox(deckHeaderLabel, UIStyleConstants.PRIMARY_COLOR);
        container.add(headerBox, BorderLayout.NORTH);

        // Selected Pokemon panel
        selectedPokemonPanel = new JPanel();
        selectedPokemonPanel.setLayout(new BoxLayout(selectedPokemonPanel, BoxLayout.Y_AXIS));
        selectedPokemonPanel.setBackground(UIStyleConstants.DARK_BG);
        selectedPokemonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(UIStyleConstants.DARK_BG);
        wrapperPanel.setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_DARK, 2));
        wrapperPanel.add(selectedPokemonPanel, BorderLayout.NORTH);

        container.add(wrapperPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel createRetroBox(String text, Color accentColor) {
        JLabel label = new JLabel(text);
        return createRetroBox(label, accentColor);
    }

    private JPanel createRetroBox(JLabel label, Color accentColor) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // Background
                g2d.setColor(accentColor);
                g2d.fillRect(0, 0, w, h);

                // 3D border effect
                g2d.setColor(accentColor.brighter());
                g2d.drawLine(0, 0, w, 0);
                g2d.drawLine(0, 0, 0, h);
                g2d.setColor(accentColor.darker());
                g2d.drawLine(w - 1, 0, w - 1, h);
                g2d.drawLine(0, h - 1, w, h - 1);
            }
        };
        box.setLayout(new FlowLayout(FlowLayout.CENTER));
        box.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        label.setFont(UIStyleConstants.HEADING_FONT);
        label.setForeground(UIStyleConstants.TEXT_LIGHT);
        box.add(label);

        return box;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Left side - back and difficulty
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        RetroButton backButton = new RetroButton("BACK");
        backButton.setButtonColor(UIStyleConstants.BORDER_DARK);
        backButton.addActionListener(e -> {
            dispose();
            if (returnCallback != null) {
                returnCallback.run();
            }
        });
        leftPanel.add(backButton);

        panel.add(leftPanel, BorderLayout.WEST);

        // Right side - start button
        startButton = new RetroButton("START BATTLE");
        startButton.setButtonColor(UIStyleConstants.SECONDARY_COLOR);
        startButton.setPreferredSize(new Dimension(180, 50));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startBattle());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
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
            selectedPokemonPanel.add(Box.createVerticalStrut(8));
        }

        selectedPokemonPanel.revalidate();
        selectedPokemonPanel.repaint();
        startButton.setEnabled(selectedDeck.size() == 3);
    }

    private JPanel createSelectedPokemonRow(Pokemon pokemon, int index) {
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
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 8));
        row.setMaximumSize(new Dimension(260, 50));
        row.setPreferredSize(new Dimension(260, 50));

        // Number and name
        JLabel nameLabel = new JLabel((index + 1) + ". " + capitalize(pokemon.getName()));
        nameLabel.setFont(UIStyleConstants.BODY_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        row.add(nameLabel);
        row.add(Box.createHorizontalGlue());

        // Remove button
        JButton removeBtn = new JButton("X");
        removeBtn.setBackground(UIStyleConstants.HP_LOW);
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Courier New", Font.BOLD, 10));
        removeBtn.setPreferredSize(new Dimension(24, 20));
        removeBtn.setMaximumSize(new Dimension(24, 20));
        removeBtn.setMinimumSize(new Dimension(24, 20));
        removeBtn.setMargin(new Insets(0, 0, 0, 0));
        removeBtn.setFocusPainted(false);
        removeBtn.setBorderPainted(false);
        removeBtn.setOpaque(true);
        removeBtn.addActionListener(e -> {
            selectedDeck.remove(index);
            updateSelectedPanel();
        });
        row.add(removeBtn);

        return row;
    }

    private void startBattle() {
        String difficulty = ((String) difficultyCombo.getSelectedItem()).toLowerCase();

        // Create copies of selected Pokemon for battle
        List<Pokemon> battleDeck = new ArrayList<>();
        for (Pokemon p : selectedDeck) {
            battleDeck.add(p.copy());
        }

        // Show moveset selection view before battle
        Runnable onMovesetComplete = () -> {
            controller.startBattle(user, battleDeck, difficulty);
            BattleAIView battleView = new BattleAIView(controller, returnCallback);
            battleView.setViewModel(controller.getViewModel());
            battleView.setVisible(true);
        };

        Runnable onMovesetCancel = () -> {
            this.setVisible(true);
        };

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
