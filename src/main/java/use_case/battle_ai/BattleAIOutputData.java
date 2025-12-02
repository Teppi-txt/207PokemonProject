package use_case.battle_ai;

import entities.battle.Battle;
import entities.Pokemon;
import entities.battle.Turn;

/**
 * Output data from executing an AI player's turn.
 */
public class BattleAIOutputData {

    private final Turn executedTurn;
    private final Battle battle;
    private final String turnResult;
    private final boolean battleEnded;
    private final Pokemon playerSwitchedTo;
    private final Pokemon aiSwitchedTo;

    public BattleAIOutputData(Turn executedTurn, Battle battle, String turnResult, boolean battleEnded) {
        this(executedTurn, battle, turnResult, battleEnded, null, null);
    }

    public BattleAIOutputData(Turn executedTurn, Battle battle, String turnResult, boolean battleEnded,
                              Pokemon playerSwitchedTo, Pokemon aiSwitchedTo) {
        this.executedTurn = executedTurn;
        this.battle = battle;
        this.turnResult = turnResult;
        this.battleEnded = battleEnded;
        this.playerSwitchedTo = playerSwitchedTo;
        this.aiSwitchedTo = aiSwitchedTo;
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

    public Pokemon getPlayerSwitchedTo() {
        return playerSwitchedTo;
    }

    public Pokemon getAiSwitchedTo() {
        return aiSwitchedTo;
    }
}
