package use_case.build_deck;

/**
 * Output Boundary for the Build Deck Use Case.
 */

public interface BuildDeckOutputBoundary {
    /**
     * Prepares the success view for the Build Deck Use Case
     * @param outputData the output data
     */
    void prepareSuccessView(BuildDeckOutputData outputData);

    /**
     * Prepares the failure view for the Build Deck Use Case
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
