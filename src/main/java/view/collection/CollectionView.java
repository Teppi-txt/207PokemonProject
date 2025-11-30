package view.collection;

import static view.collection.CollectionViewHelpers.*;

import entities.Pokemon;
import interface_adapters.collection.ViewCollectionController;
import interface_adapters.collection.ViewCollectionState;
import interface_adapters.collection.ViewCollectionViewModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class CollectionView extends JPanel implements PropertyChangeListener {
    private final PokemonInfoPanel pokemonInfoPanel;
    private final PokemonCollectionPanel pokemonCollectionPanel;
    private ViewCollectionController controller;
    private Runnable navigationCallback;

    private List<Pokemon> pokemonOnPage;
    private List<Pokemon> ownedPokemon;
    private String filter = "all";
    private int currentPage = 0;

    public CollectionView(final ViewCollectionViewModel collectionViewModel) {
        collectionViewModel.addPropertyChangeListener(this);
        this.setMinimumSize(COLLECTION_VIEW_WINDOW_SIZE);
        this.setBorder(SMALL_BORDER);

        final JLabel title = new JLabel("My Collection");
        title.setFont(TITLE_FONT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(SMALL_BORDER);

        final JButton returnButton = new JButton("Back to Menu:");
        returnButton.setFont(mainFont(18));
        returnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnButton.setBorder(SMALL_BORDER);
        returnButton.addActionListener(e -> {
            if (navigationCallback != null) {
                navigationCallback.run();
            }
        });

        final JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));
        body.setBorder(SMALL_BORDER);

        pokemonInfoPanel = new PokemonInfoPanel();
        pokemonCollectionPanel = new PokemonCollectionPanel();
        pokemonCollectionPanel.setBorder(SMALL_BORDER);

        body.add(pokemonInfoPanel);
        body.add(pokemonCollectionPanel);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(returnButton);
        this.add(body);
    }

    public void onPokemonSelection(final ActionEvent e) {
        JButton clicked = (JButton) e.getSource();
        int index = (int) clicked.getClientProperty("id");
        pokemonInfoPanel.setPokemon(pokemonOnPage.get(index),
                !pokemonIsInList(pokemonOnPage.get(index), ownedPokemon));
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final ViewCollectionState state = (ViewCollectionState) evt.getNewValue();
        this.pokemonOnPage = state.getPokemonOnPage();
        this.ownedPokemon = state.getOwnedPokemon();
        updatePanel(state);
    }

    private void updatePanel(ViewCollectionState state) {
        if (state.getPokemonOnPage() != null) {
            this.pokemonCollectionPanel.loadPage(state.getPokemonOnPage(), state.getOwnedPokemon());
            this.pokemonInfoPanel.setPokemon(state.getSelectedPokemon(),
                    !pokemonIsInList(state.getSelectedPokemon(), ownedPokemon));
        } else {
            this.pokemonCollectionPanel.loadEmptyPage(state.getErrorMessage());
            this.pokemonInfoPanel.setPokemonAsNull();
        }
    }

    public ViewCollectionController getController() {
        return controller;
    }

    public void setController(ViewCollectionController controller) {
        this.controller = controller;
    }

    public void setNavigationCallback(Runnable callback) {
        this.navigationCallback = callback;
    }

    public class PokemonFilterPanel extends JPanel {

        private void onFilterChanged(ActionEvent e) {
            filter = e.getActionCommand().toLowerCase();
            currentPage = 0;
            controller.execute(pokemonOnPage, currentPage, filter);
        }

        public PokemonFilterPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));

            JToggleButton allButton = new JToggleButton("All");
            JToggleButton ownedButton = new JToggleButton("Owned");
            JToggleButton shinyButton = new JToggleButton("Shiny");

            ButtonGroup group = new ButtonGroup();
            group.add(allButton);
            group.add(ownedButton);
            group.add(shinyButton);
            allButton.setSelected(true);

            Enumeration<AbstractButton> buttons = group.getElements();

            while (buttons.hasMoreElements()) {
                AbstractButton button = buttons.nextElement();
                button.setActionCommand(button.getText().toUpperCase());
                button.addActionListener(this::onFilterChanged);
                add(button);
            }
        }
    }

    public class PokemonCollectionPanel extends JPanel {
        private JPanel pokemonPanel;
        private JPanel filterPanel;
        private JPanel pageButtonPanel;
        private JButton backButton;
        private Label pageLabel;

        public PokemonCollectionPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            createPanels();
            createPageButtons();

            add(filterPanel);
            add(pokemonPanel);
            add(pageButtonPanel);
        }

        private void createPanels() {
            pokemonPanel = new JPanel();
            pokemonPanel.setMinimumSize(POKEMON_PANEL_SIZE);
            filterPanel = new PokemonFilterPanel();
            pageButtonPanel = new JPanel();
        }

        private void createPageButtons() {
            backButton = createButton("Prev", false, e -> {
                currentPage--;
                updatePageNumbers();
                controller.execute(pokemonOnPage, currentPage, filter);
            });
            pageButtonPanel.add(backButton);

            pageLabel = new Label(String.valueOf(currentPage));
            pageLabel.setAlignment(Label.CENTER);
            pageButtonPanel.add(pageLabel);

            JButton nextButton = createButton("Next", true, e -> {
                currentPage++;
                updatePageNumbers();
                controller.execute(pokemonOnPage, currentPage, filter);
            });
            pageButtonPanel.add(nextButton);
        }

        private JButton createButton(String text, boolean enabled, ActionListener listener) {
            JButton button = new JButton(text);
            button.setEnabled(enabled);
            button.addActionListener(listener);
            return button;
        }

        private void updatePageNumbers() {
            backButton.setEnabled(currentPage != 0);
            pageLabel.setText(String.valueOf(currentPage));
        }

        private void loadPage(List<Pokemon> pokemons, List<Pokemon> ownedPokemon) {
            pageButtonPanel.setVisible(true);
            pokemonPanel.removeAll();
            pokemonPanel.setLayout(new GridLayout(0, 5));

            // Add buttons to the frame
            for (int i = 0; i < pokemons.size(); i++) {
                ImageIcon pokeIcon;
                String flavorText = "";
                JButton button = createPokemonButton();

                try {
                    pokeIcon = new ImageIcon(new URL(pokemons.get(i).getSpriteUrl()));

                    if (!pokemonIsInList(pokemons.get(i), ownedPokemon)) {
                        pokeIcon.setImage(GrayFilter.createDisabledImage(pokeIcon.getImage()));
                    } else if (getPokemonInList(pokemons.get(i), ownedPokemon).isShiny()) {
                        flavorText += "⋆｡° ✮ - ";
                        button.setBackground(Color.YELLOW);
                    }

                    flavorText += ("#" + pokemons.get(i).getID());
                } catch (MalformedURLException e) {
                    pokeIcon = new ImageIcon("src/assets/sprites/no_image_icon.png");
                }

                button.putClientProperty("id", i);  // store index as command
                button.setIcon(pokeIcon);
                button.setText(flavorText);
                pokemonPanel.add(button);
            }

            updatePageNumbers();
        }

        @NotNull
        private JButton createPokemonButton() {
            JButton button = new JButton();
            button.setContentAreaFilled(false);
            button.setFocusable(false);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.addActionListener(CollectionView.this::onPokemonSelection);
            return button;
        }

        public void loadEmptyPage(final String errorMessage) {
            pageButtonPanel.setVisible(false);
            pokemonPanel.removeAll();
            pokemonPanel.setLayout(new BoxLayout(pokemonPanel, BoxLayout.Y_AXIS));
            pokemonPanel.setAlignmentX(CENTER_ALIGNMENT);
            JLabel label = new JLabel(errorMessage);
            label.setFont(new Font(label.getFont().getName(), Font.ITALIC, 30));
            pokemonPanel.add(label);
        }
    }
}