package interface_adapters.battle_ai;

import entities.Pokemon;
import entities.User;
import use_case.battle_ai.BattleAIInputBoundary;
import use_case.battle_ai.BattleAIInputData;

import java.util.List;

/**
 * Controller for Battle AI use case.
 * Handles battle setup, player moves, and player switches.
 */
public class BattleAIController {

    private final BattleAIInputBoundary interactor;

    public BattleAIController(BattleAIInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Sets up a new battle with AI opponent.
     */
    public void setupBattle(User user, List<Pokemon> playerTeam, String difficulty) {
        BattleAIInputData inputData = new BattleAIInputData(user, playerTeam, difficulty);
        interactor.execute(inputData);
    }

    /**
     * Executes a player's switch to a different Pokemon by ID.
     * @param pokemonId the ID of the Pokemon to switch to
     */
    public void executePlayerSwitch(int pokemonId) {
        BattleAIInputData inputData = BattleAIInputData.forSwitchById(pokemonId);
        interactor.execute(inputData);
    }

    /**
     * Executes a player's move by index.
     * @param moveIndex the index of the move in the active Pokemon's move list
     */
    public void executePlayerMove(int moveIndex) {
        BattleAIInputData inputData = new BattleAIInputData(moveIndex);
        interactor.execute(inputData);
    }
}