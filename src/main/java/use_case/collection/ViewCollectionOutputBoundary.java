package use_case.collection;

import use_case.open_pack.OpenPackOutputData;

public interface ViewCollectionOutputBoundary {
    /**
     * Prepares the success view for the Collection use case
     * @param outputData the output data
     */
    void prepareSuccessView(ViewCollectionOutputData outputData);

    /**
     * Prepares the failure view for the View Collection use case
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage, ViewCollectionOutputData outputData);

    void switchToHomeView();
}
