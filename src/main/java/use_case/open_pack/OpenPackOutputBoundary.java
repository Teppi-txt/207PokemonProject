package use_case.open_pack;

/**
 * Output Boundary for the Open Pack Use Case.
 */

public interface OpenPackOutputBoundary {
    /**
     * Prepares the success view for the Open Pack Use Case.
     * @param outputData the output data.
     */
    void prepareSuccessView(OpenPackOutputData outputData);

    /**
     * Prepares the failure view for the Open Pack Use Case.
     * @param errorMessage the explanation of the failure.
     */
    void prepareFailView(String errorMessage);
}
