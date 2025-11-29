package use_case.battle_ai;

import entities.Battle;
import entities.Turn;
import ai.graph.Decision;

/**
 * Output data for the battle AI use case.
 * This class encapsulates the results of an AI player's turn execution.
 */
public class BattleAIOutputData {
    private final Turn executedTurn;
    private final Battle battle;
    private final String turnResult;
    private final boolean battleEnded;
    private final String battleStatus;
    private final Decision decision;

    /**
     * Constructs output data for the battle AI use case.
     * @param executedTurn the turn that was executed
     * @param battle the current battle state
     * @param turnResult the result message from turn execution
     * @param battleEnded true if the battle has ended, false otherwise
     * @param battleStatus the current status of the battle
     * @param decision the AI's decision object (for transparency/debugging)
     */
    public BattleAIOutputData(Turn executedTurn, Battle battle, String turnResult,
                              boolean battleEnded, String battleStatus, Decision decision) {
        this.executedTurn = executedTurn;
        this.battle = battle;
        this.turnResult = turnResult;
        this.battleEnded = battleEnded;
        this.battleStatus = battleStatus;
        this.decision = decision;
    }

    /**
     * Gets the executed turn.
     * @return the turn that was executed
     */
    public Turn getExecutedTurn() {
        return executedTurn;
    }

    /**
     * Gets the battle.
     * @return the current battle state
     */
    public Battle getBattle() {
        return battle;
    }

    /**
     * Gets the turn result message.
     * @return the result message
     */
    public String getTurnResult() {
        return turnResult;
    }

    /**
     * Checks if the battle has ended.
     * @return true if battle ended, false otherwise
     */
    public boolean isBattleEnded() {
        return battleEnded;
    }

    /**
     * Gets the battle status.
     * @return the battle status
     */
    public String getBattleStatus() {
        return battleStatus;
    }

    /**
     * Gets the AI's decision object.
     * @return the decision made by the AI
     */
    public Decision getDecision() {
        return decision;
    }
}
