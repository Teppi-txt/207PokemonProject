package interface_adapters.build_deck;

import use_case.build_deck.BuildDeckOutputBoundary;
import use_case.build_deck.BuildDeckOutputData;

import java.util.ArrayList;

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
        BuildDeckState buildDeckState = new BuildDeckState();
        buildDeckState.setDeck(outputData.getDeck());
        buildDeckState.setAllDecks(outputData.getAllDecks()); // <--- NEW
        buildDeckState.setErrorMessage(null);
        this.viewModel.setState(buildDeckState);
        this.viewModel.firePropertyChanged();
    }

    // prepareFailView remains the same, except it should ensure allDecks is set to an empty list or fetched separately if a failure occurs on load.
    @Override
    public void prepareFailView(String errorMessage) {
        BuildDeckState state = new BuildDeckState();
        state.setDeck(null);
        state.setAllDecks(new ArrayList<>()); // Ensure allDecks is not null
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        this.viewModel.firePropertyChanged();
    }
}
