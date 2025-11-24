package interface_adapters.open_pack;

import entities.Pokemon;
import use_case.open_pack.OpenPackOutputBoundary;
import use_case.open_pack.OpenPackOutputData;

import java.util.ArrayList;
import java.util.List;

public class OpenPackPresenter implements OpenPackOutputBoundary {
    private final OpenPackViewModel viewModel;

    public OpenPackPresenter(OpenPackViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(OpenPackOutputData outputData) {

        OpenPackState openPackState = new OpenPackState();

        openPackState.setOpenedCards(outputData.getOpenedCards());
        openPackState.setRemainingCurrency(outputData.getRemainingCurrency());
        openPackState.setErrorMessage(null);

        viewModel.setState(openPackState);

    }

    @Override
    public void prepareFailView(String errorMessage) {
        OpenPackState openPackState = new OpenPackState();
        openPackState.setErrorMessage(errorMessage);
        viewModel.setState(openPackState);
    }

}
