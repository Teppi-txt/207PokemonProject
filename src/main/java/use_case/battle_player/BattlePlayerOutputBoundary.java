package use_case.battle_player;

/**
 * output boundary for the battle player use case.
 */

public interface BattlePlayerOutputBoundary {
    /**
     * prepares the success view for the battle player use case
     * @param outputData the output data
     */
    void prepareSuccessView(BattlePlayerOutputData outputData);

    /**
     * prepares the failure view for the battle player use case
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}

