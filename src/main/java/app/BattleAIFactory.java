package app;

import entities.User;
import frameworks_and_drivers.DeckSelectionView;
import interface_adapters.battle_ai.*;
import use_case.battle_ai.BattleAIInteractor;
import use_case.battle_player.BattlePlayerInteractor;

/**
 * Factory for creating and wiring Battle AI components with dependency injection.
 * Follows Clean Architecture principles.
 */
public class BattleAIFactory {

    /**
     * Creates a fully-wired DeckSelectionView ready to be displayed.
     *
     * @param user The user who will be battling
     * @return Configured DeckSelectionView
     */
    public static DeckSelectionView create(User user) {
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

        // View
        DeckSelectionView view = new DeckSelectionView(controller, user);

        return view;
    }
}
