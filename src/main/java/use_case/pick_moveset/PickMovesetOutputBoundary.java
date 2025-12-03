package use_case.pick_moveset;

public interface PickMovesetOutputBoundary {

    /**
     * Presents the data required to display the available moves
     * and the current selection state.
     * @param outputData the data to present to the user
     */
    void present(PickMovesetOutputData outputData);

    /**
     * Presents an error message when the move selection request fails.
     * @param errorMessage the explanation of the failure
     */
    void presentFailure(String errorMessage);

    /**
     * Presents a confirmation message when moves are successfully saved.
     * @param message the success message to display
     */
    void presentSuccess(String message);
}
