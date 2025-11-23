package interface_adapters.build_deck;

import use_case.build_deck.BuildDeckOutputData;

public class BuildDeckPresenter {
    private final BuildDeckViewModel viewModel;

    public BuildDeckPresenter(BuildDeckViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BuildDeckOutputData outputData) {

    }

    @Override
    public void prepareFailView(String errorMessage){
        BuildDeckState buildDeckState = new BuildDeckState();

    }
}
