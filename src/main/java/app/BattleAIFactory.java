package app;

import entities.User;
import frameworks_and_drivers.DeckSelectionForBattleView;
import frameworks_and_drivers.DeckSelectionView;
import interface_adapters.battle_ai.*;
import use_case.battle_ai.BattleAIInteractor;
import use_case.battle_player.BattlePlayerInteractor;

import javax.swing.JFrame;

/**
 * Factory for creating and wiring Battle AI components with dependency injection.
 * Follows Clean Architecture principles.
 */
public class BattleAIFactory {

    /**
     * Creates a fully-wired selection view ready to be displayed.
     *
     * @param user The user who will be battling
     * @return Configured selection view
     */
    public static JFrame create(User user) {
        return createWithCallback(user, null);
    }

    /**
     * Creates a fully-wired DeckSelectionView with a callback for returning to menu.
     * Uses deck-based selection if user has decks, otherwise falls back to manual selection.
     *
     * @param user The user who will be battling
     * @param returnCallback Callback to execute when returning to menu
     * @return Configured selection view (either DeckSelectionForBattleView or DeckSelectionView)
     */
    public static JFrame createWithCallback(User user, Runnable returnCallback) {
        // Data Access Layer
        BattleAIDataAccessObject dataAccess = new BattleAIDataAccessObject();

        // Presenter & ViewModel
        BattleAIViewModel viewModel = new BattleAIViewModel();
        BattleAIPresenter presenter = new BattleAIPresenter(viewModel);

        // Use Case Interactors
        BattleAIInteractor aiInteractor = new BattleAIInteractor(dataAccess, presenter);
        BattlePlayerInteractor playerInteractor = new BattlePlayerInteractor(dataAccess, presenter);

        // Controller
        BattleAIController controller = new BattleAIController(
                playerInteractor, aiInteractor, dataAccess, presenter, viewModel);

        // Check if user has any decks with enough Pokemon
        boolean hasValidDecks = user.getDecks().values().stream()
                .anyMatch(deck -> deck.getPokemons() != null && deck.getPokemons().size() >= 3);

        if (hasValidDecks) {
            // Use deck-based selection
            return new DeckSelectionForBattleView(controller, user, returnCallback);
        } else {
            // Fall back to manual selection (user has no valid decks yet)
            return new DeckSelectionView(controller, user, returnCallback);
        }
    }

    /**
     * Creates a deck-based selection view for Battle AI.
     *
     * @param user The user who will be battling
     * @param returnCallback Callback to execute when returning to menu
     * @return Configured DeckSelectionForBattleView
     */
    public static DeckSelectionForBattleView createDeckBasedView(User user, Runnable returnCallback) {
        // Data Access Layer
        BattleAIDataAccessObject dataAccess = new BattleAIDataAccessObject();

        // Presenter & ViewModel
        BattleAIViewModel viewModel = new BattleAIViewModel();
        BattleAIPresenter presenter = new BattleAIPresenter(viewModel);

        // Use Case Interactors
        BattleAIInteractor aiInteractor = new BattleAIInteractor(dataAccess, presenter);
        BattlePlayerInteractor playerInteractor = new BattlePlayerInteractor(dataAccess, presenter);

        // Controller
        BattleAIController controller = new BattleAIController(
                playerInteractor, aiInteractor, dataAccess, presenter, viewModel);

        return new DeckSelectionForBattleView(controller, user, returnCallback);
    }
}
