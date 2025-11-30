package interface_adapters.open_pack;

import view.ViewManager;
import use_case.open_pack.OpenPackOutputBoundary;
import use_case.open_pack.OpenPackOutputData;

public class OpenPackPresenter implements OpenPackOutputBoundary {
    private final OpenPackViewModel viewModel;
    private final ViewManager viewManager;

    public OpenPackPresenter(OpenPackViewModel viewModel, ViewManager viewManager) {
        this.viewModel = viewModel;
        this.viewManager = viewManager;
    }

    @Override
    public void prepareSuccessView(OpenPackOutputData outputData) {
        if (outputData == null || outputData.getOpenedCards() == null || outputData.getDuplicateFlags() == null) {
            return;
        }

        OpenPackState newState = new OpenPackState();

        newState.setOpenedCards(outputData.getOpenedCards());
        newState.setDuplicateFlags(outputData.getDuplicateFlags());
        newState.setRemainingCurrency(outputData.getRemainingCurrency());

        newState.setRevealIndex(0);
        newState.setRevealMode(true);

        newState.setErrorMessage(null);

        viewModel.setState(newState);

        viewManager.showOpenPack();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        OpenPackState newState = new OpenPackState();
        newState.setErrorMessage(errorMessage);
        newState.setRevealMode(false);

        viewModel.setState(newState);
    }

}
