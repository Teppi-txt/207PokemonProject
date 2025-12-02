package app.battle;

import entities.battle.Battle;
import entities.user.User;
import frameworks_and_drivers.BattlePlayerDataAccessObject;
import frameworks_and_drivers.BattlePlayerView;
import frameworks_and_drivers.BattleSetupDeckView;
import frameworks_and_drivers.BattleSetupViewIntegrated;
import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerPresenter;
import interface_adapters.battle_player.BattlePlayerState;
import interface_adapters.battle_player.BattlePlayerViewModel;
import use_case.battle_player.BattlePlayerInteractor;

import javax.swing.JFrame;

/**
 * Factory that wires the player-vs-player battle flow following Clean Architecture.
 * It centralizes dependency creation for both the setup screens and the battle view.
 */
public class BattlePlayerFactory {

    private BattlePlayerFactory() {
        // utility
    }

    /**
     * Creates the appropriate setup view (deck-based or manual) for PvP battles.
     *
     * @param user the current user (whose collection/decks are used)
     * @param returnCallback callback to run when the setup closes
     * @return a configured setup JFrame
     */
    public static JFrame createSetupView(User user, Runnable returnCallback) {
        boolean hasValidDecks = user.getDecks().values().stream()
                .anyMatch(deck -> deck.getPokemons() != null && deck.getPokemons().size() >= 3);

        if (hasValidDecks) {
            return new BattleSetupDeckView(user, returnCallback);
        }
        return new BattleSetupViewIntegrated(user, returnCallback);
    }

    /**
     * Creates a fully-wired battle window for the given battle.
     *
     * @param battle the battle domain object
     * @param playAgainHandler callback executed when the user chooses to play again/exit
     * @return configured BattlePlayerView ready to display
     */
    public static BattlePlayerView createBattleView(Battle battle, Runnable playAgainHandler) {
        BattlePlayerDataAccessObject dataAccess = new BattlePlayerDataAccessObject();
        dataAccess.saveBattle(battle);
        if (battle.getPlayer1() != null) {
            dataAccess.saveUser(battle.getPlayer1());
        }
        if (battle.getPlayer2() != null) {
            dataAccess.saveUser(battle.getPlayer2());
        }

        BattlePlayerViewModel viewModel = new BattlePlayerViewModel();
        BattlePlayerPresenter presenter = new BattlePlayerPresenter(viewModel);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(dataAccess, presenter);
        BattlePlayerController controller = new BattlePlayerController(interactor);

        BattlePlayerView battleView = new BattlePlayerView(controller, viewModel, dataAccess, playAgainHandler);

        BattlePlayerState initialState = new BattlePlayerState();
        initialState.setBattle(battle);
        initialState.setBattleStatus(battle.getBattleStatus());
        initialState.setBattleEnded(false);
        initialState.setTurnResult("Battle started! Select a move or switch Pokemon.");
        viewModel.setState(initialState);

        return battleView;
    }
}
