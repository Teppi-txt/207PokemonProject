package view;

import entities.Deck;
import entities.Move;
import entities.Pokemon;
import entities.User;
import interface_adapters.pick_moveset.PickMovesetController;
import interface_adapters.pick_moveset.PickMovesetState;
import interface_adapters.pick_moveset.PickMovesetViewModel;


import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class MovesetSelectionView extends JFrame implements PropertyChangeListener {

    private final Deck deck;
    private final PickMovesetViewModel viewModel;
    private PickMovesetController controller;
    private SwingWorker<Void, Void> loader;
    private final Map<String, PokemonCard> cardMap = new HashMap<>();
    private JPanel pokemonPanel;
    private final User user;

    public MovesetSelectionView(User user, Deck deck, PickMovesetViewModel viewModel) {
        this.user = user;
        this.deck = deck;
        this.viewModel = viewModel;

        this.viewModel.addPropertyChangeListener(this);

        setTitle("Pick Moveset For Your Pokémon");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel northWrapper = new JPanel();
        northWrapper.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        JButton teamBtn = new JButton("Select Team →");
        teamBtn.addActionListener(e -> {
            Collection<Deck> decks = user.getDecks().values();
            Deck chosen = DeckPickerDialog.pickDeck(decks);
            if (chosen == null) return;

            // refresh Pokémon
            this.deck.getPokemons().clear();
            this.deck.getPokemons().addAll(chosen.getPokemons());

            JPanel newPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
            Map<String, PokemonCard> newMap = new HashMap<>();

            for (Pokemon p : chosen.getPokemons()) {
                PokemonCard card = new PokemonCard(p);
                newMap.put(p.getName(), card);
                newPanel.add(card);
            }

            this.cardMap.clear();
            this.cardMap.putAll(newMap);
            this.pokemonPanel = newPanel;

            JScrollPane scroll = (JScrollPane) ((JViewport) pokemonPanel.getParent()).getParent();
            scroll.setViewportView(newPanel);
            // refresh UI
            newPanel.revalidate();
            newPanel.repaint();
            scroll.revalidate();
            scroll.repaint();
            // reload moves
            controller.loadMoves(chosen);
        });

        teamBtn.setFont(new Font("Arial", Font.BOLD, 18));
        topBar.add(teamBtn, BorderLayout.WEST);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton clearBtn = new JButton("Clear Selected");
        JButton exitBtn = new JButton("Exit Without Saving");
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        rightBtns.add(clearBtn);
        rightBtns.add(exitBtn);
        topBar.add(rightBtns, BorderLayout.EAST);

        northWrapper.add(topBar, BorderLayout.NORTH);
        JLabel title = new JLabel("Pick Moveset For Your Pokémon", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        northWrapper.add(title, BorderLayout.SOUTH);

        add(northWrapper, BorderLayout.NORTH);

        pokemonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
        for (Pokemon p : deck.getPokemons()) {
            PokemonCard card = new PokemonCard(p);
            cardMap.put(p.getName(), card);
            pokemonPanel.add(card);
        }

        add(new JScrollPane(pokemonPanel), BorderLayout.CENTER);

        JButton battleBtn = new JButton("Save and Back To Menu →");
        battleBtn.setFont(new Font("Arial", Font.BOLD, 20));
        battleBtn.addActionListener(e -> toMenu());

        JPanel bottom = new JPanel();
        bottom.add(battleBtn);
        add(bottom, BorderLayout.SOUTH);

        this.loader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                if (controller != null) controller.loadMoves(deck);
                return null;
            }

            @Override
            protected void done() {
                System.out.println("Moves loaded (async)");
            }
        };

        clearBtn.addActionListener(e -> {
            for (PokemonCard card : cardMap.values()) {
                card.setSelectedMoves(new ArrayList<Move>());
            }
            JOptionPane.showMessageDialog(this, "All selections cleared.");
        });

        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Exited without saving.");
            dispose();
        });
    }

    public void setController(PickMovesetController controller) {
        this.controller = controller;
        if (loader != null) loader.execute();
    }

    public boolean allMovesSelected() {
        for (PokemonCard c : cardMap.values()) {
            if (c.getSelectedMoveCount() == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PickMovesetState state = viewModel.getState();
        if (!state.getError().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (!state.getMessage().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getMessage());
        }
        if (!state.getAvailableMoves().isEmpty()) {
            loadMovesIntoUI(state.getAvailableMoves());
        }
    }

    private void loadMovesIntoUI(Map<Pokemon, List<String>> map) {
        for (Map.Entry<Pokemon, List<String>> entry : map.entrySet()) {
            Pokemon p = entry.getKey();
            List<String> moveNames = entry.getValue();
            PokemonCard card = cardMap.get(p.getName());
            if (card != null) {
                card.setAvailableMoves(moveNames);
            }
        }
    }

    private void toMenu() {
        if (!allMovesSelected()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Some Pokémon do not have moves selected!",
                    "Incomplete Movesets",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "All moves selected! Proceeding to Battle...",
                "Battle Start",
                JOptionPane.INFORMATION_MESSAGE);
    }

    class PokemonCard extends JPanel {

        private final Pokemon pokemon;
        private List<String> availableMoves = new ArrayList<>();
        private List<Move> selectedMoves = new ArrayList<>();

        private final JPanel selectedMovesPanel;

        public PokemonCard(Pokemon pokemon) {
            this.pokemon = pokemon;

            setPreferredSize(new Dimension(250, 340));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            JLabel imgLabel = new JLabel();
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception ignored) {}

            JLabel nameLabel = new JLabel(pokemon.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

            JButton movesBtn = new JButton("Moves →");
            movesBtn.addActionListener(e -> openDialog());

            selectedMovesPanel = new JPanel();
            selectedMovesPanel.setLayout(new BoxLayout(selectedMovesPanel, BoxLayout.Y_AXIS));
            selectedMovesPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(6, 6, 6, 6)
            ));
            selectedMovesPanel.setBackground(new Color(245, 245, 245));

            JPanel bottomWrapper = new JPanel(new BorderLayout());
            bottomWrapper.add(movesBtn, BorderLayout.NORTH);
            bottomWrapper.add(selectedMovesPanel, BorderLayout.CENTER);

            add(imgLabel, BorderLayout.NORTH);
            add(nameLabel, BorderLayout.CENTER);
            add(bottomWrapper, BorderLayout.SOUTH);

            refreshSelectedMovesUI();
        }

        void setAvailableMoves(List<String> moves) {
            this.availableMoves = moves;
        }

        void openDialog() {
            if (availableMoves.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Moves not loaded yet.");
                return;
            }
            new MovesDialog(this, pokemon, availableMoves);
        }

        void saveMoveSelection(List<Move> chosen) {
            this.selectedMoves = chosen;
            controller.saveMoves(pokemon, chosen);
            refreshSelectedMovesUI();
        }

        public int getSelectedMoveCount() {
            return selectedMoves.size();
        }

        public void setSelectedMoves(List<Move> moves) {
            this.selectedMoves = moves;
            refreshSelectedMovesUI();
        }

        private void refreshSelectedMovesUI() {
            selectedMovesPanel.removeAll();

            if (selectedMoves.isEmpty()) {
                JLabel empty = new JLabel("No moves selected");
                empty.setForeground(Color.GRAY);
                selectedMovesPanel.add(empty);
            } else {
                for (Move m : selectedMoves) {
                    JPanel row = new JPanel(new BorderLayout());
                    row.setOpaque(false);

                    JLabel label = new JLabel("• " + m.getName());
                    label.setToolTipText(
                            "<html>Power: " + m.getPower() +
                                    "<br>Accuracy: " + m.getAccuracy() +
                                    "<br><i>" + m.getEffect() + "</i></html>"
                    );

                    JButton removeBtn = new JButton("✕");
                    removeBtn.setMargin(new Insets(0, 0, 0, 0));
                    removeBtn.setBorder(null);
                    removeBtn.setForeground(Color.RED);
                    removeBtn.setFont(new Font("Arial", Font.BOLD, 14));

                    removeBtn.addActionListener(e -> {
                        selectedMoves.remove(m);
                        refreshSelectedMovesUI();
                    });

                    row.add(label, BorderLayout.WEST);
                    row.add(removeBtn, BorderLayout.EAST);

                    selectedMovesPanel.add(row);
                }
            }

            selectedMovesPanel.revalidate();
            selectedMovesPanel.repaint();
        }
    }

    class MovesDialog extends JDialog {

        private final PokemonCard parentCard;
        private final Pokemon pokemon;

        private List<JCheckBox> allBoxes = new ArrayList<>();

        private List<Move> allMoves = new ArrayList<>();

        private JPanel selectedPanel;
        private JTextField searchField;

        MovesDialog(PokemonCard parentCard, Pokemon pokemon, List<String> moves) {
            super(MovesetSelectionView.this, pokemon.getName() + " - Select Moves", true);

            this.parentCard = parentCard;
            this.pokemon = pokemon;

            setSize(520, 700);
            setLayout(new BorderLayout());

            // Load all moves with details
            for (String mv : moves) {
                allMoves.add(controller.fetchMoveDetail(mv));
            }

            // search bar
            JPanel searchPanel = new JPanel(new BorderLayout());
            JLabel searchLabel = new JLabel("Search: ");
            searchField = new JTextField();
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshTabs(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshTabs(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshTabs(); }
            });
            searchPanel.add(searchLabel, BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            add(searchPanel, BorderLayout.NORTH);

            // TABS
            JTabbedPane tabs = new JTabbedPane();
            JPanel statusPanel = new JPanel();
            JPanel physicalPanel = new JPanel();
            JPanel specialPanel = new JPanel();
            statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
            physicalPanel.setLayout(new BoxLayout(physicalPanel, BoxLayout.Y_AXIS));
            specialPanel.setLayout(new BoxLayout(specialPanel, BoxLayout.Y_AXIS));

            tabs.add("Status", new JScrollPane(statusPanel));
            tabs.add("Physical", new JScrollPane(physicalPanel));
            tabs.add("Special", new JScrollPane(specialPanel));

            add(tabs, BorderLayout.CENTER);

            // Selected Panel
            selectedPanel = new JPanel();
            selectedPanel.setLayout(new BoxLayout(selectedPanel, BoxLayout.Y_AXIS));
            selectedPanel.setBorder(BorderFactory.createTitledBorder("Selected Moves"));
            JScrollPane selectedScroll = new JScrollPane(selectedPanel);
            selectedScroll.setPreferredSize(new Dimension(0, 120));

            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.add(selectedScroll, BorderLayout.CENTER);

            JButton saveBtn = new JButton("Save");
            JPanel saveWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            saveWrapper.add(saveBtn);
            southPanel.add(saveWrapper, BorderLayout.SOUTH);

            add(southPanel, BorderLayout.SOUTH);

            saveBtn.addActionListener(e -> save());

            // Build checkboxes in tabs
            buildTabs(statusPanel, physicalPanel, specialPanel);

            updateSelectedPanel();

            setVisible(true);
        }

        private void buildTabs(JPanel statusPanel, JPanel physicalPanel, JPanel specialPanel) {
            statusPanel.removeAll();
            physicalPanel.removeAll();
            specialPanel.removeAll();
            allBoxes.clear();

            String filter = searchField.getText().toLowerCase();

            for (Move m : allMoves) {

                if (!m.getName().toLowerCase().contains(filter)) continue;

                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
                row.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

                JLabel typeLabel = new JLabel(m.getType().toUpperCase());
                typeLabel.setOpaque(true);
                typeLabel.setForeground(Color.WHITE);
                typeLabel.setBackground(getTypeColor(m.getType()));
                typeLabel.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

                JCheckBox cb = new JCheckBox(m.getName());
                cb.addActionListener(e -> updateSelectedPanel());
                allBoxes.add(cb);

                JLabel statsLabel = new JLabel(
                        "Power: " + m.getPower() + "   Accuracy: " + m.getAccuracy());

                JLabel descLabel = new JLabel("<html><i>" + m.getEffect() + "</i></html>");

                row.add(typeLabel);
                row.add(cb);
                row.add(statsLabel);
                row.add(descLabel);

                String dmg = m.getDamageClass().toLowerCase();

                if (dmg.equals("status")) {
                    statusPanel.add(row);
                } else if (dmg.equals("physical")) {
                    physicalPanel.add(row);
                } else if (dmg.equals("special")) {
                    specialPanel.add(row);
                }
            }

            statusPanel.revalidate(); statusPanel.repaint();
            physicalPanel.revalidate(); physicalPanel.repaint();
            specialPanel.revalidate(); specialPanel.repaint();
        }

        private void refreshTabs() {
            JTabbedPane tabs = (JTabbedPane) getContentPane().getComponent(1);
            JScrollPane statusPane = (JScrollPane) tabs.getComponentAt(0);
            JScrollPane physicalPane = (JScrollPane) tabs.getComponentAt(1);
            JScrollPane specialPane = (JScrollPane) tabs.getComponentAt(2);

            buildTabs(
                    (JPanel) statusPane.getViewport().getView(),
                    (JPanel) physicalPane.getViewport().getView(),
                    (JPanel) specialPane.getViewport().getView()
            );
        }

        private void updateSelectedPanel() {
            selectedPanel.removeAll();

            for (JCheckBox cb : allBoxes) {
                if (cb.isSelected()) {
                    selectedPanel.add(new JLabel("• " + cb.getText()));
                }
            }
            selectedPanel.revalidate();
            selectedPanel.repaint();
        }

        private void save() {
            List<Move> chosen = new ArrayList<>();
            for (JCheckBox cb : allBoxes) {
                if (cb.isSelected()) {
                    chosen.add(controller.fetchMoveDetail(cb.getText()));
                }
            }
            parentCard.saveMoveSelection(chosen);
            dispose();
        }

        private Color getTypeColor(String type) {
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
                default: return Color.GRAY;
            }
        }
    }

    static class DeckPickerDialog {

        public static Deck pickDeck(Collection<Deck> decks) {

            if (decks == null || decks.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No decks available.");
                return null;
            }

            // Convert collection → array for easier handling
            Deck[] deckArray = decks.toArray(new Deck[0]);
            String[] names = new String[deckArray.length];

            // Display format: "Team Name (ID: 3)"
            for (int i = 0; i < deckArray.length; i++) {
                names[i] = deckArray[i].getName() + " (ID: " + deckArray[i].getId() + ")";
            }

            // JOptionPane selection
            String choice = (String) JOptionPane.showInputDialog(
                    null,
                    "Select a team:",
                    "Choose Deck",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    names,
                    names[0]
            );

            // If user clicked cancel
            if (choice == null) return null;

            // Match choice back to actual Deck
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(choice)) {
                    return deckArray[i];
                }
            }
            return null; // should not happen
        }
    }

}