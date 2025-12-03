package app.battle;

import entities.user.User;
import frameworks_and_drivers.deck.DeckSelectionForBattleView;
import frameworks_and_drivers.deck.DeckSelectionView;
import interface_adapters.battle_ai.*;
import pokeapi.JSONLoader;
import use_case.battle_ai.BattleAIInteractor;

import javax.swing.JFrame;

/**
 * Factory for creating and wiring Battle AI components
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
     * Uses deck-based selection if user has decks, otherwise falls back to manual selection.
     *
     * @param user The user who will be battling
     * @param returnCallback Callback to execute when returning to menu
     * @return Configured selection view (either DeckSelectionForBattleView or DeckSelectionView)
     */
    public static JFrame createWithCallback(User user, Runnable returnCallback) {
        // Data Access Layer
        BattleAIDataAccessObject dataAccess = new BattleAIDataAccessObject(
                JSONLoader.getInstance().getAllPokemon(), JSONLoader.getInstance().getAllMoves());

        // Presenter & ViewModel
        BattleAIViewModel viewModel = new BattleAIViewModel();
        BattleAIPresenter presenter = new BattleAIPresenter(viewModel);

        // Use Case Interactor
        BattleAIInteractor aiInteractor = new BattleAIInteractor(dataAccess, presenter);

        // Controller
        BattleAIController controller = new BattleAIController(aiInteractor);

        // Check if user has any decks with enough Pokemon
        boolean hasValidDecks = user.getDecks().values().stream()
                .anyMatch(deck -> deck.getPokemons() != null && deck.getPokemons().size() >= 3);

        if (hasValidDecks) {
            // Use deck-based selection
            return new DeckSelectionForBattleView(controller, dataAccess, viewModel, user, returnCallback);
        } else {
            // Fuser has no valid decks yet
            return new DeckSelectionView(controller, dataAccess, viewModel, user, returnCallback);
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
        BattleAIDataAccessObject dataAccess = new BattleAIDataAccessObject(
                JSONLoader.getInstance().getAllPokemon(), JSONLoader.getInstance().getAllMoves());

        // Presenter & ViewModel
        BattleAIViewModel viewModel = new BattleAIViewModel();
        BattleAIPresenter presenter = new BattleAIPresenter(viewModel);

        // Use Case Interactor
        BattleAIInteractor aiInteractor = new BattleAIInteractor(dataAccess, presenter);

        // Controller 
        BattleAIController controller = new BattleAIController(aiInteractor);

        return new DeckSelectionForBattleView(controller, dataAccess, viewModel, user, returnCallback);
    }
}
