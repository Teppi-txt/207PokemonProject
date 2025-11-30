package use_case.battle_ai;

import entities.Battle;
import entities.Turn;

/**
 * Output data from executing an AI player's turn.
 */
public class BattleAIOutputData {

    private final Turn executedTurn;
    private final Battle battle;
    private final String turnResult;
    private final boolean battleEnded;

    public BattleAIOutputData(Turn executedTurn, Battle battle, String turnResult, boolean battleEnded) {
        this.executedTurn = executedTurn;
        this.battle = battle;
        this.turnResult = turnResult;
        this.battleEnded = battleEnded;
    }

    public Turn getExecutedTurn() {
        return executedTurn;
    }

    public Battle getBattle() {
        return battle;
    }

    public String getTurnResult() {
        return turnResult;
    }

    public boolean isBattleEnded() {
        return battleEnded;
    }
}
