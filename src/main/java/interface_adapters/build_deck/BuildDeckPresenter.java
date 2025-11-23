package interface_adapters.build_deck;

import use_case.build_deck.BuildDeckOutputBoundary;
import use_case.build_deck.BuildDeckOutputData;

/**
 * The Presenter for the Build Deck Use Case.
 */
public class BuildDeckPresenter implements BuildDeckOutputBoundary{
    private final BuildDeckViewModel viewModel;

    public BuildDeckPresenter(BuildDeckViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BuildDeckOutputData outputData) {
        // On success, switch to the build deck view

        BuildDeckState buildDeckState = new BuildDeckState();
        buildDeckState.setDeck(outputData.getDeck());
        buildDeckState.setErrorMessage(null);
    }

    @Override
    public void prepareFailView(String errorMessage){
        BuildDeckState buildDeckState = new BuildDeckState();

    }
}
