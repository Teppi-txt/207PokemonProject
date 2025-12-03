package interface_adapters.battle_player;

import entities.battle.Turn;
import use_case.battle_player.BattlePlayerInputData;
import use_case.battle_player.BattlePlayerInputBoundary;

/**
* the controller for the battle player use case.
 */

public class BattlePlayerController {

    private final BattlePlayerInputBoundary battlePlayerUseCaseInteractor;

    public BattlePlayerController(BattlePlayerInputBoundary battlePlayerUseCaseInteractor) {
        this.battlePlayerUseCaseInteractor = battlePlayerUseCaseInteractor;
    }

    /**
     * Runs a battle turn.
     * @param turn turn data
     */
    public void battle(Turn turn) {
        final BattlePlayerInputData battlePlayerInputData = new BattlePlayerInputData(turn);
        battlePlayerUseCaseInteractor.execute(battlePlayerInputData);
    }
}
