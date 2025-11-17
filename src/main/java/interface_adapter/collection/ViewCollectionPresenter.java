package interface_adapter.collection;

import use_case.collection.ViewCollectionOutputBoundary;
import use_case.collection.ViewCollectionOutputData;

public class ViewCollectionPresenter implements ViewCollectionOutputBoundary {
    @Override
    public void prepareSuccessView(ViewCollectionOutputData outputData) {

    }

    @Override
    public void prepareFailView(String errorMessage) {

    }
}
