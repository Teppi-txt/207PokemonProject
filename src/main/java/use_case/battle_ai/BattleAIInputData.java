package use_case.battle_ai;

import entities.Battle;
import entities.AIPlayer;

/**
 * Input data for the battle AI use case.
 * This class encapsulates the data required to execute an AI player's turn.
 */
public class BattleAIInputData {
    private final Battle battle;
    private final AIPlayer aiPlayer;
    private final boolean forcedSwitch;

    /**
     * Constructs input data for the battle AI use case.
     * @param battle the current battle state
     * @param aiPlayer the AI player making the decision
     * @param forcedSwitch true if AI must switch (active Pokemon fainted), false otherwise
     */
    public BattleAIInputData(Battle battle, AIPlayer aiPlayer, boolean forcedSwitch) {
        this.battle = battle;
        this.aiPlayer = aiPlayer;
        this.forcedSwitch = forcedSwitch;
    }

    /**
     * Gets the battle.
     * @return the battle
     */
    public Battle getBattle() {
        return battle;
    }

    /**
     * Gets the AI player.
     * @return the AI player
     */
    public AIPlayer getAiPlayer() {
        return aiPlayer;
    }

    /**
     * Checks if this is a forced switch scenario.
     * @return true if AI must switch Pokemon, false otherwise
     */
    public boolean isForcedSwitch() {
        return forcedSwitch;
    }
}
