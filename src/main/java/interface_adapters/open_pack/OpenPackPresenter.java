package interface_adapters.open_pack;

import frameworks_and_drivers.ViewManager;
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

        System.out.println("PRESENTER: METHOD ENTERED");

        if (outputData == null) {
            System.out.println("PRESENTER: outputData is NULL");
            return;
        }

        System.out.println("PRESENTER: openedCards = " + outputData.getOpenedCards());
        if (outputData.getOpenedCards() == null) {
            System.out.println("PRESENTER: openedCards is NULL");
            return;
        }

        if (outputData.getDuplicateFlags() == null) {
            System.out.println("PRESENTER: duplicateFlags is NULL");
            return;
        }

        if (outputData.getOpenedCards().isEmpty()) {
            System.out.println("PRESENTER: openedCards EMPTY");
        }

        System.out.println("PRESENTER: preparing success view");
        System.out.println("PRESENTER: openedCards = " + outputData.getOpenedCards().size());

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
        System.out.println("hi");

        OpenPackState newState = new OpenPackState();
        newState.setErrorMessage(errorMessage);
        newState.setRevealMode(false);

        viewModel.setState(newState);
    }

}
