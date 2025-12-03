package interface_adapters.build_deck;

import java.util.ArrayList;

import use_case.build_deck.BuildDeckOutputBoundary;
import use_case.build_deck.BuildDeckOutputData;

/**
 * The Presenter for the Build Deck Use Case.
 */
public class BuildDeckPresenter implements BuildDeckOutputBoundary {
    private final BuildDeckViewModel viewModel;

    public BuildDeckPresenter(BuildDeckViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BuildDeckOutputData outputData) {
        final BuildDeckState buildDeckState = new BuildDeckState();
        buildDeckState.setDeck(outputData.getDeck());
        buildDeckState.setAllDecks(outputData.getAllDecks());
        buildDeckState.setErrorMessage(null);
        this.viewModel.setState(buildDeckState);
        this.viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final BuildDeckState state = new BuildDeckState();
        state.setDeck(null);
        state.setAllDecks(new ArrayList<>());
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        this.viewModel.firePropertyChanged();
    }
}
