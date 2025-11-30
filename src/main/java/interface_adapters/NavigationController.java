package interface_adapters;

import view.MainMenuView;

/**
 * Controller for coordinating navigation between views.
 * Updates the ViewManagerModel to switch views in the CardLayout.
 */
public class NavigationController {

    private final ViewManagerModel viewManagerModel;

    public NavigationController(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Navigate to a specific view by name.
     * @param viewName The name of the view to navigate to
     */
    public void navigateTo(String viewName) {
        viewManagerModel.setState(viewName);
        viewManagerModel.firePropertyChanged();
    }

    /**
     * Navigate to the main menu.
     */
    public void navigateToMainMenu() {
        navigateTo(MainMenuView.VIEW_NAME);
    }

    /**
     * Navigate to the collection view.
     */
    public void navigateToCollection() {
        navigateTo("collection");
    }

    /**
     * Navigate to the Battle AI deck selection view.
     */
    public void navigateToBattleAI() {
        navigateTo("deck_selection_ai");
    }

    /**
     * Navigate to the Battle Player setup view.
     */
    public void navigateToBattlePlayer() {
        navigateTo("battle_setup_player");
    }

    /**
     * Navigate to the actual AI battle view.
     */
    public void navigateToBattleAIView() {
        navigateTo("battle_ai");
    }

    /**
     * Navigate to the actual player battle view.
     */
    public void navigateToBattlePlayerView() {
        navigateTo("battle_player");
    }

    /**
     * Navigate to the build deck view.
     */
    public void navigateToBuildDeck() {
        navigateTo("build_deck");
    }

    public void navigateToOpenPackPreView() {
        viewManagerModel.setState("pre_open_pack");
        viewManagerModel.firePropertyChanged();
    }
}
