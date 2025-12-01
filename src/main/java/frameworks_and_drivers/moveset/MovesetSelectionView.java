package frameworks_and_drivers.moveset;

import entities.Deck;
import entities.Move;
import entities.Pokemon;
import interface_adapters.pick_moveset.PickMovesetController;
import interface_adapters.pick_moveset.PickMovesetPresenter;
import interface_adapters.pick_moveset.PickMovesetState;
import interface_adapters.pick_moveset.PickMovesetViewModel;
import pokeapi.JSONLoader;
import use_case.pick_moveset.PickMovesetInteractor;

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

    public MovesetSelectionView(Deck deck, PickMovesetViewModel viewModel) {
        this.deck = deck;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setTitle("Pick Moveset For Your Pokémon");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Pick Moveset For Your Pokémon", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        pokemonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
        for (Pokemon p : deck.getPokemons()) {
            PokemonCard card = new PokemonCard(p);
            cardMap.put(p.getName(), card);
            pokemonPanel.add(card);
        }

        add(new JScrollPane(pokemonPanel), BorderLayout.CENTER);

        JButton battleBtn = new JButton("Back To Menu →");
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

        public PokemonCard(Pokemon pokemon) {
            this.pokemon = pokemon;

            setPreferredSize(new Dimension(200, 260));
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

            add(imgLabel, BorderLayout.NORTH);
            add(nameLabel, BorderLayout.CENTER);
            add(movesBtn, BorderLayout.SOUTH);
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
            controller.saveMoves(pokemon, chosen);
        }

        public int getSelectedMoveCount() {
            return selectedMoves.size();
        }

        public void setSelectedMoves(List<Move> moves) {
            this.selectedMoves = moves;
        }

    }


    class MovesDialog extends JDialog {

        private final PokemonCard parentCard;
        private final Pokemon pokemon;
        private final List<JCheckBox> boxes = new ArrayList<>();

        MovesDialog(PokemonCard parentCard, Pokemon pokemon, List<String> moves) {
            super(MovesetSelectionView.this, pokemon.getName() + " - Select Moves", true);
            this.parentCard = parentCard;
            this.pokemon = pokemon;

            setSize(450, 600);
            setLayout(new BorderLayout());

            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            for (String mv : moves) {
                Move detail = controller.fetchMoveDetail(mv);   // get detail

                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
                row.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
                //Type label (colored box)
                JLabel typeLabel = new JLabel(detail.getType().toUpperCase());
                typeLabel.setOpaque(true);
                typeLabel.setForeground(Color.WHITE);
                typeLabel.setBackground(getTypeColor(detail.getType()));
                typeLabel.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
                JCheckBox cb = new JCheckBox(detail.getName());
                cb.addActionListener(e -> limit());
                boxes.add(cb);
                //description panel
                JLabel statsLabel = new JLabel(
                        "Power: " + detail.getPower() +
                                "   Accuracy: " + detail.getAccuracy()
                );

                JLabel descLabel = new JLabel("<html><i>" + detail.getEffect() + "</i></html>");
                row.add(typeLabel);
                row.add(cb);
                row.add(statsLabel);
                row.add(descLabel);
                listPanel.add(row);
            }

            add(new JScrollPane(listPanel), BorderLayout.CENTER);

            JButton saveBtn = new JButton("Save");
            saveBtn.addActionListener(e -> save());
            add(saveBtn, BorderLayout.SOUTH);

            setVisible(true);
        }

        void limit() {
            long count = boxes.stream().filter(JCheckBox::isSelected).count();
            if (count > 4) {
                JOptionPane.showMessageDialog(this, "You may only select 4 moves");
                boxes.stream().filter(JCheckBox::isSelected).reduce((a, b) -> b).ifPresent(cb -> cb.setSelected(false));
            }
        }

        void save() {
            List<Move> chosen = new ArrayList<>();
            for (JCheckBox cb : boxes) {
                if (cb.isSelected()) {
                    String mvName = cb.getText();
                    chosen.add(controller.fetchMoveDetail(mvName));
                }
            }
            parentCard.saveMoveSelection(chosen);
            dispose();
        }
        private Color getTypeColor(String type) {
            String t = type.toLowerCase();

            switch (t) {
                case "fire":
                    return new Color(255, 80, 50);
                case "water":
                    return new Color(80, 150, 255);
                case "grass":
                    return new Color(80, 200, 80);
                case "electric":
                    return new Color(255, 220, 50);
                case "ice":
                    return new Color(120, 220, 255);
                case "fighting":
                    return new Color(200, 80, 60);
                case "poison":
                    return new Color(180, 60, 180);
                case "ground":
                    return new Color(220, 180, 90);
                case "flying":
                    return new Color(150, 180, 255);
                case "psychic":
                    return new Color(255, 100, 180);
                case "bug":
                    return new Color(170, 200, 50);
                case "rock":
                    return new Color(200, 180, 60);
                case "ghost":
                    return new Color(120, 110, 180);
                case "dragon":
                    return new Color(90, 110, 255);
                case "dark":
                    return new Color(90, 70, 60);
                case "steel":
                    return new Color(150, 150, 170);
                default:
                    return Color.GRAY;
            }
        }

    }

    // test ui
    public static void main(String[] args) {
        JSONLoader.getInstance().loadPokemon();
        JSONLoader.getInstance().loadMoves();
        ArrayList<Pokemon> team = new ArrayList<>();
        try {
            team.add(pokeapi.PokeAPIFetcher.getPokemon("pikachu"));
            team.add(pokeapi.PokeAPIFetcher.getPokemon("magikarp"));
            team.add(pokeapi.PokeAPIFetcher.getPokemon("meowth"));
            team.add(pokeapi.PokeAPIFetcher.getPokemon("aegislash-shield"));
            team.add(pokeapi.PokeAPIFetcher.getPokemon("ditto"));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load Pokémon.");
            return;
        }
        Deck deck = new Deck(1, "Demo Deck", team);
        PickMovesetViewModel vm = new PickMovesetViewModel();
        MovesetSelectionView view = new MovesetSelectionView(deck, vm);
        PickMovesetPresenter presenter = new PickMovesetPresenter(vm);
        PickMovesetInteractor interactor = new PickMovesetInteractor(presenter);
        PickMovesetController controller = new PickMovesetController(interactor);
        view.setController(controller);
        view.setVisible(true);
        view.setVisible(true);
    }

}
