package use_case.battle_ai;

/**
 * Input boundary for the battle AI use case.
 * This interface defines the contract for executing an AI player's turn in a battle.
 */
public interface BattleAIInputBoundary {
    /**
     * Executes the battle AI use case.
     * The AI player will make a decision (move or switch) and execute it.
     * @param inputData the input data containing battle and AI player information
     */
    void execute(BattleAIInputData inputData);
}
