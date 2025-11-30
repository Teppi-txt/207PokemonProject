package app;

import entities.Deck;
import entities.Pack;
import entities.Pokemon;
import entities.User;
import frameworks_and_drivers.BattlePlayerDataAccessObject;
import frameworks_and_drivers.ViewManagerFrame;
import interface_adapters.NavigationController;
import interface_adapters.ViewManagerModel;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIDataAccessObject;
import interface_adapters.battle_ai.BattleAIPresenter;
import interface_adapters.battle_ai.BattleAIViewModel;
import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerPresenter;
import interface_adapters.battle_player.BattlePlayerViewModel;
import interface_adapters.build_deck.BuildDeckController;
import interface_adapters.build_deck.BuildDeckPresenter;
import interface_adapters.build_deck.BuildDeckViewModel;
import interface_adapters.collection.ViewCollectionController;
import interface_adapters.collection.ViewCollectionPresenter;
import interface_adapters.collection.ViewCollectionViewModel;
import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackPresenter;
import interface_adapters.open_pack.OpenPackViewModel;
import pokeapi.JSONLoader;
import use_case.build_deck.BuildDeckInteractor;
import use_case.battle_ai.BattleAIInteractor;
import use_case.battle_player.BattlePlayerInteractor;
import use_case.collection.ViewCollectionInteractor;
import use_case.open_pack.OpenPackInputBoundary;
import use_case.open_pack.OpenPackInteractor;
import use_case.open_pack.OpenPackOutputBoundary;
import view.BuildDeckView;
import view.CollectionView;
import view.MainMenuView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

/**
 * Builder for constructing the complete Pokemon application with all views.
 */
public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final ViewManager viewManager;
    private final NavigationController navigationController;

    private User user;

    // Views
    private MainMenuView mainMenuView;
    private CollectionView collectionView;
    private BuildDeckView buildDeckView;

    // ViewModels
    private ViewCollectionViewModel collectionViewModel;
    private BattleAIViewModel battleAIViewModel;
    private BattlePlayerViewModel battlePlayerViewModel;
    private BuildDeckViewModel buildDeckViewModel;

    // Controllers
    private ViewCollectionController collectionController;
    private BattleAIController battleAIController;
    private BattlePlayerController battlePlayerController;
    private BuildDeckController buildDeckController;

    // Data Access
    private BattleAIDataAccessObject battleAIDataAccess;
    private BattlePlayerDataAccessObject battlePlayerDataAccess;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
        viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);
        navigationController = new NavigationController(viewManagerModel);
    }

    /**
     * Sets the user for the application.
     * If not called, a default user will be created.
     */
    public AppBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    /**
     * Creates a default user with starter Pokemon and currency.
     */
    public AppBuilder createDefaultUser() {
        user = new User(1, "Trainer", "trainer@pokemon.com", 5000);

        // Add starter Pokemon from the loaded data
        int starterCount = Math.min(6, JSONLoader.allPokemon.size());
        for (int i = 0; i < starterCount; i++) {
            Pokemon pokemon = JSONLoader.allPokemon.get(i).copy();
            user.addPokemon(pokemon);
        }

        // Add some additional Pokemon for variety
        int additionalCount = Math.min(20, JSONLoader.allPokemon.size());
        for (int i = 6; i < additionalCount; i++) {
            Pokemon pokemon = JSONLoader.allPokemon.get(i).copy();
            // Make some shiny for fun
            if (i % 5 == 0) {
                pokemon.setShiny(true);
            }
            user.addPokemon(pokemon);
        }

        // Create a starter deck with first 5 Pokemon
        Deck starterDeck = new Deck(1, "Starter Deck");
        int deckSize = Math.min(5, user.getOwnedPokemon().size());
        for (int i = 0; i < deckSize; i++) {
            starterDeck.addPokemon(user.getOwnedPokemon().get(i));
        }
        user.addDeck(starterDeck);

        return this;
    }

    /**
     * Adds the main menu view to the application.
     */
    public AppBuilder addMainMenuView() {
        if (user == null) {
            createDefaultUser();
        }

        mainMenuView = new MainMenuView(viewManagerModel, user);

        // Set up navigation callbacks
        mainMenuView.setOnCollectionClick(() -> {
            // Refresh collection data before navigating
            if (collectionController != null) {
                collectionController.execute(user.getOwnedPokemon(), 0, "all");
            }
            navigationController.navigateToCollection();
        });

        mainMenuView.setOnBattleAIClick(() -> {
            openBattleAIFlow();
        });

        mainMenuView.setOnBattlePlayerClick(() -> {
            openBattlePlayerFlow();
        });

        mainMenuView.setOnOpenPackClick(() -> {
            openOpenPackFlow();
        });

        mainMenuView.setOnBuildDeckClick(() -> {
            if (buildDeckController != null) {
                // Reload decks when entering build deck view
                buildDeckController.buildDeck(-1, null, null, false, false);
            }
            navigationController.navigateToBuildDeck();
        });

        cardPanel.add(mainMenuView, MainMenuView.VIEW_NAME);
        return this;
    }

    /**
     * Adds the collection view to the application.
     */
    public AppBuilder addCollectionView() {
        if (user == null) {
            createDefaultUser();
        }

        collectionViewModel = new ViewCollectionViewModel();
        collectionView = new CollectionView(collectionViewModel);

        ViewCollectionPresenter presenter = new ViewCollectionPresenter(collectionViewModel);
        ViewCollectionInteractor interactor = new ViewCollectionInteractor(presenter, user);
        collectionController = new ViewCollectionController(interactor);

        collectionView.setController(collectionController);
        collectionView.setNavigationCallback(() -> {
            mainMenuView.refreshUserInfo();
            navigationController.navigateToMainMenu();
        });

        // Initial load
        collectionController.execute(user.getOwnedPokemon(), 0, "all");

        cardPanel.add(collectionView, collectionViewModel.getViewName());
        return this;
    }

    /**
     * Adds the build deck view to the application.
     */
    public AppBuilder addBuildDeckView() {
        if (user == null) {
            createDefaultUser();
        }

        buildDeckViewModel = new BuildDeckViewModel();
        buildDeckView = new BuildDeckView(buildDeckViewModel, user);

        InMemoryBuildDeckDataAccess dataAccess = new InMemoryBuildDeckDataAccess(user);
        BuildDeckPresenter presenter = new BuildDeckPresenter(buildDeckViewModel);
        BuildDeckInteractor interactor = new BuildDeckInteractor(dataAccess, presenter);
        buildDeckController = new BuildDeckController(interactor);

        buildDeckView.setController(buildDeckController);
        buildDeckView.setNavigationCallback(() -> {
            mainMenuView.refreshUserInfo();
            navigationController.navigateToMainMenu();
        });

        // Initialize with first deck or create new one
        buildDeckController.buildDeck(-1, null, null, false, false);

        cardPanel.add(buildDeckView, BuildDeckView.VIEW_NAME);
        return this;
    }

    /**
     * Opens the Battle AI flow in a separate window.
     * Uses the DeckSelectionView -> BattleAIView flow.
     */
    private void openBattleAIFlow() {
        if (user == null || user.getOwnedPokemon().isEmpty()) {
            JOptionPane.showMessageDialog(cardPanel,
                "You need Pokemon to battle! Open some packs first.",
                "No Pokemon",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (user.getOwnedPokemon().size() < 3) {
            JOptionPane.showMessageDialog(cardPanel,
                "You need at least 3 Pokemon to battle!",
                "Not Enough Pokemon",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create the Battle AI components using the factory pattern
        // But pass a callback to return to main menu when done
        Runnable returnToMenu = () -> {
            // Reset Pokemon HP after battle
            resetPokemonHP();
            mainMenuView.refreshUserInfo();
            navigationController.navigateToMainMenu();
        };

        // Use the BattleAIFactory but with our user and callback
        javax.swing.JFrame selectionView = BattleAIFactory.createWithCallback(user, returnToMenu);
        selectionView.setVisible(true);
    }

    /**
     * Opens the Battle Player flow in a separate window.
     */
    private void openBattlePlayerFlow() {
        if (user == null || user.getOwnedPokemon().isEmpty()) {
            JOptionPane.showMessageDialog(cardPanel,
                "You need Pokemon to battle! Open some packs first.",
                "No Pokemon",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (user.getOwnedPokemon().size() < 3) {
            JOptionPane.showMessageDialog(cardPanel,
                "You need at least 3 Pokemon to battle!",
                "Not Enough Pokemon",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create return to menu callback
        Runnable returnToMenu = () -> {
            // Reset Pokemon HP after battle
            resetPokemonHP();
            mainMenuView.refreshUserInfo();
            navigationController.navigateToMainMenu();
        };

        // Check if user has any decks with enough Pokemon
        boolean hasValidDecks = user.getDecks().values().stream()
                .anyMatch(deck -> deck.getPokemons() != null && deck.getPokemons().size() >= 3);

        if (hasValidDecks) {
            // Use deck-based selection
            frameworks_and_drivers.BattleSetupDeckView setupView =
                new frameworks_and_drivers.BattleSetupDeckView(user, returnToMenu);
            setupView.setVisible(true);
        } else {
            // Fall back to manual selection
            frameworks_and_drivers.BattleSetupViewIntegrated setupView =
                new frameworks_and_drivers.BattleSetupViewIntegrated(user, returnToMenu);
            setupView.setVisible(true);
        }
    }

    /**
     * Opens the Open Pack flow in a separate window.
     */
    private void openOpenPackFlow() {
        if (user == null) {
            JOptionPane.showMessageDialog(cardPanel,
                "No user found!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (user.getCurrency() < 1000) {
            JOptionPane.showMessageDialog(cardPanel,
                "Not enough currency! You need 1000 to open a pack.\nWin battles to earn more currency!",
                "Insufficient Currency",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create the pack with the card pool
        Pack pack = new Pack(1, "Pokemon Pack", JSONLoader.allPokemon);

        // Create data access with shared user
        InMemoryOpenPackDataAccess dataAccess = new InMemoryOpenPackDataAccess(user);

        // Create view model
        OpenPackViewModel viewModel = new OpenPackViewModel();
        viewModel.getState().setRemainingCurrency(user.getCurrency());

        // Create callback to refresh main menu when pack window closes
        Runnable onClose = () -> {
            mainMenuView.refreshUserInfo();
        };

        // Create the frame with callback support
        ViewManagerFrame frame = new ViewManagerFrame(viewModel, (OpenPackController) null, onClose);

        // Create presenter and interactor
        OpenPackOutputBoundary presenter = new OpenPackPresenter(viewModel, frame);
        OpenPackInputBoundary interactor = new OpenPackInteractor(dataAccess, presenter, pack);
        OpenPackController controller = new OpenPackController(interactor);

        // Wire the controller
        frame.setController(controller);
    }

    /**
     * Resets all Pokemon HP to their max values after a battle.
     */
    private void resetPokemonHP() {
        if (user != null) {
            for (Pokemon pokemon : user.getOwnedPokemon()) {
                pokemon.getStats().resetHp();
            }
        }
    }

    /**
     * Builds and returns the application JFrame.
     */
    public JFrame build() {
        final JFrame application = new JFrame("Pokemon Battle Game");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setPreferredSize(new Dimension(1100, 800));

        application.add(cardPanel);

        // Start at main menu
        viewManagerModel.setState(MainMenuView.VIEW_NAME);
        viewManagerModel.firePropertyChanged();

        return application;
    }

    // Getters for testing/access
    public User getUser() {
        return user;
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public ViewManagerModel getViewManagerModel() {
        return viewManagerModel;
    }
}
