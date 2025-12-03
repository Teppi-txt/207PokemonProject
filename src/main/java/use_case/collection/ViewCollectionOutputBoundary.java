package use_case.collection;

public interface ViewCollectionOutputBoundary {
    /**
     * Prepares the success view for the Collection use case.
     * @param outputData the output data.
     */
    void prepareSuccessView(ViewCollectionOutputData outputData);

    /**
     * Prepares the failure view for the View Collection use case.
     * @param errorMessage the explanation of the failure.
     * @param outputData the data object containing the current collection state
     */
    void prepareFailView(String errorMessage, ViewCollectionOutputData outputData);

    /**
     * Requests that the application switch back to the home view.
     */
    void switchToHomeView();
}
