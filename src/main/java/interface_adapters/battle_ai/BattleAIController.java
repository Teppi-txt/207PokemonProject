package interface_adapters.battle_ai;

import entities.AIPlayer;
import entities.Battle;
import entities.Pokemon;
import entities.Turn;
import entities.User;
import use_case.battle_ai.BattleAIInputBoundary;
import use_case.battle_ai.BattleAIInputData;

import java.util.List;

/**
 * Controller for Battle AI use case.
 * Handles battle setup, player turns, and AI turns.
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
     * Executes a player's turn in the battle.
     */
    public void executePlayerTurn(Turn turn) {
        BattleAIInputData inputData = new BattleAIInputData(turn);
        interactor.execute(inputData);
    }

    /**
     * Executes the AI player's turn in an ongoing battle.
     */
    public void executeAITurn(Battle battle, AIPlayer aiPlayer, boolean forcedSwitch) {
        BattleAIInputData inputData = new BattleAIInputData(battle, aiPlayer, forcedSwitch);
        interactor.execute(inputData);
    }

    /**
     * Executes a player's switch to a different Pokemon.
     */
    public void executePlayerSwitch(Pokemon switchTarget) {
        BattleAIInputData inputData = new BattleAIInputData(switchTarget);
        interactor.execute(inputData);
    }
}
