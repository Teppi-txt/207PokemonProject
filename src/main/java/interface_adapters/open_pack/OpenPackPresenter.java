package interface_adapters.open_pack;

import use_case.open_pack.OpenPackOutputBoundary;
import use_case.open_pack.OpenPackOutputData;

public class OpenPackPresenter implements OpenPackOutputBoundary {

    private final OpenPackViewModel viewModel;

    public OpenPackPresenter(OpenPackViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(OpenPackOutputData outputData) {

        OpenPackState newState = new OpenPackState();

        newState.setOpenedCards(outputData.getOpenedCards());
        newState.setDuplicateFlags(outputData.getDuplicateFlags());
        newState.setRemainingCurrency(outputData.getRemainingCurrency());

        newState.setRevealIndex(0);
        newState.setRevealMode(true);

        newState.setErrorMessage(null);

        viewModel.setState(newState);
    }

    @Override
    public void prepareFailView(String errorMessage) {

        OpenPackState newState = new OpenPackState();

        newState.setErrorMessage(errorMessage);
        newState.setRevealMode(false);

        viewModel.setState(newState);
    }

}
