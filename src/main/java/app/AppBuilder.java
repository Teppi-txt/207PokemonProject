package app;

import entities.Pokemon;
import entities.User;
import interface_adapters.NavigationController;
import interface_adapters.ViewManagerModel;
import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.battle_ai.BattleAIDataAccessObject;
import interface_adapters.battle_ai.BattleAIPresenter;
import interface_adapters.battle_ai.BattleAIViewModel;
import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerPresenter;
import interface_adapters.battle_player.BattlePlayerViewModel;
import interface_adapters.collection.ViewCollectionController;
import interface_adapters.collection.ViewCollectionPresenter;
import interface_adapters.collection.ViewCollectionViewModel;
import frameworks_and_drivers.BattlePlayerDataAccessObject;
import pokeapi.JSONLoader;
import use_case.battle_ai.BattleAIInteractor;
import use_case.battle_player.BattlePlayerInteractor;
import use_case.collection.ViewCollectionInteractor;
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

    // ViewModels
    private ViewCollectionViewModel collectionViewModel;
    private BattleAIViewModel battleAIViewModel;
    private BattlePlayerViewModel battlePlayerViewModel;

    // Controllers
    private ViewCollectionController collectionController;
    private BattleAIController battleAIController;
    private BattlePlayerController battlePlayerController;

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
        user = new User(1, "Trainer", "trainer@pokemon.com", 10000);

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

        // Open Pack is disabled for now (placeholder)
        mainMenuView.setOnOpenPackClick(() -> {
            JOptionPane.showMessageDialog(cardPanel,
                "Open Pack feature coming soon!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
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
        frameworks_and_drivers.DeckSelectionView deckSelectionView =
            BattleAIFactory.createWithCallback(user, returnToMenu);
        deckSelectionView.setVisible(true);
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

        // Open the battle setup view
        frameworks_and_drivers.BattleSetupViewIntegrated setupView =
            new frameworks_and_drivers.BattleSetupViewIntegrated(user, returnToMenu);
        setupView.setVisible(true);
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
