package frameworks_and_drivers;

import entities.Pokemon;
import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;
import use_case.open_pack.OpenPackInputBoundary;
import use_case.open_pack.OpenPackInputData;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenPackView extends JPanel implements ActionListener{

    private final OpenPackController controller;
    private final OpenPackViewModel viewModel;

    // Panels
    private final JPanel topPanel = new JPanel(new FlowLayout());
    private final JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 10, 10));
    private final JPanel bottomPanel = new JPanel(new FlowLayout());

    // Components
    private final JLabel currencyLabel = new JLabel("Currency: 0");
    private final JButton openPackButton = new JButton("Open Pack");
    private final JLabel messageLabel = new JLabel("");
    private final JLabel addCollection = new JLabel("Add to Collection!");
    private final JButton backButton = new JButton("Back");

    public OpenPackView(OpenPackController controller, OpenPackViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        setLayout(new BorderLayout());

        // ===== TOP: currency + button =====
        topPanel.add(currencyLabel);
        topPanel.add(openPackButton);

        // ===== CENTER: cards =====
        // cardsPanel already set to 1x5 grid

        // ===== BOTTOM: message =====
        messageLabel.setForeground(Color.RED);
        bottomPanel.add(messageLabel);

        // ===== Assemble =====
        add(topPanel, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);



        // Button listener
        openPackButton.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == openPackButton){
            openPack();
        }
    }

    private void openPack(){
        controller.openPack();
    }

    public void updateView(OpenPackState state){
        if (state == null){
            return;
        }

        currencyLabel.setText("Currency: " + state.getRemainingCurrency());

        cardsPanel.removeAll();

        List<Pokemon> opened = state.getOpenedCards();
        if (opened != null){
            for (Pokemon pokemon : opened){
                cardsPanel.add(makeCardPanel(pokemon));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();

        if (state.getErrorMessage() != null) {
            messageLabel.setText(state.getErrorMessage());
        } else if (opened != null && !opened.isEmpty()) {
            messageLabel.setText("You opened a pack!");
        } else {
            messageLabel.setText("");
        }

    }

    private JPanel makeCardPanel(Pokemon pokemon){
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        cardPanel.setBackground(Color.WHITE);

        JLabel spriteLabel;

        try {
            // Uses your methods in Pokemon:
            // getRegularSpriteURL(), getShinySpriteURL(), getSpriteUrl()
            String spriteUrl = pokemon.getSpriteUrl();
            ImageIcon icon = new ImageIcon(new URL(spriteUrl));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            spriteLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            // Fallback if sprite can't be loaded
            spriteLabel = new JLabel("[no sprite]", SwingConstants.CENTER);
        }

        String labelText = pokemon.toString();
        if (pokemon.isShiny()) {
            labelText = "Shiny " + labelText;
        }

        JLabel nameLabel = new JLabel(labelText, SwingConstants.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 12f));

        cardPanel.add(spriteLabel, BorderLayout.CENTER);
        cardPanel.add(nameLabel, BorderLayout.SOUTH);

        return cardPanel;


    }

    public static void main(String[] args) {
        // 1. Fake use case (so controller has something to call)
        OpenPackInputBoundary fakeUseCase = new OpenPackInputBoundary() {
            @Override
            public void execute(OpenPackInputData inputData) {
                System.out.println("Fake OpenPack use case called.");
            }
        };

        OpenPackController fakeController = new OpenPackController(fakeUseCase);

        // 2. Fake ViewModel + State
        OpenPackViewModel fakeVM = new OpenPackViewModel();
        OpenPackState testState = new OpenPackState();

        List<Pokemon> fakeCards = new ArrayList<>();
        fakeCards.add(makeFakePokemon(1, "bulbasaur", false));
        fakeCards.add(makeFakePokemon(4, "charmander", true));
        fakeCards.add(makeFakePokemon(7, "squirtle", false));
        fakeCards.add(makeFakePokemon(25, "pikachu", true));
        fakeCards.add(makeFakePokemon(133, "eevee", false));

        testState.setOpenedCards(fakeCards);
        testState.setRemainingCurrency(2500);
        testState.setErrorMessage(null);

        fakeVM.setState(testState);

        // 3. Build and show the UI
        SwingUtilities.invokeLater(() -> {
            OpenPackView view = new OpenPackView(fakeController, fakeVM);
            view.updateView(fakeVM.getState());

            JFrame frame = new JFrame("Open Pack View TEST");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /** Helper: build minimal fake Pokémon without touching your real data layer. */
    private static Pokemon makeFakePokemon(int id, String name, boolean shiny) {
        // Use whichever constructor you actually have
        Pokemon p = new Pokemon(
                name,
                id,
                new ArrayList<>(), // empty types
                null,              // or a dummy Stats object if required
                new ArrayList<>()  // empty moves
        );
        p.setShiny(shiny);
        return p;
    }



}


