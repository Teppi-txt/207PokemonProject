package use_case.battle_player;

/**
 * Output boundary for the battle player use case.
 */

public interface BattlePlayerOutputBoundary {
    /**
     * Prepares the success view for the battle player use case.
     * @param outputData the output data.
     */
    void prepareSuccessView(BattlePlayerOutputData outputData);

    /**
     * Prepares the failure view for the battle player use case.
     * @param errorMessage the explanation of the failure.
     */
    void prepareFailView(String errorMessage);
}

