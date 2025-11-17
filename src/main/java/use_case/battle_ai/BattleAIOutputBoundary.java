package use_case.battle_ai;

/**
 * Output boundary for the battle AI use case.
 * This interface defines the contract for presenting the results of an AI turn.
 */
public interface BattleAIOutputBoundary {
    /**
     * Prepares the success view for the battle AI use case.
     * @param outputData the output data containing turn results and battle state
     */
    void prepareSuccessView(BattleAIOutputData outputData);

    /**
     * Prepares the failure view for the battle AI use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
