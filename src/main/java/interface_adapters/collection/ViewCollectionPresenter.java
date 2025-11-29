package interface_adapters.collection;

import interface_adapters.ViewModel;
import use_case.collection.ViewCollectionOutputBoundary;
import use_case.collection.ViewCollectionOutputData;

public class ViewCollectionPresenter implements ViewCollectionOutputBoundary {
    private final ViewCollectionViewModel viewCollectionViewModel;
    private final ViewModel homeViewModel = null;

    public ViewCollectionPresenter(ViewCollectionViewModel viewCollectionViewModel) {
        this.viewCollectionViewModel = viewCollectionViewModel;
    }

    @Override
    public void prepareSuccessView(ViewCollectionOutputData outputData) {
        final ViewCollectionState viewCollectionState = viewCollectionViewModel.getState();
        viewCollectionState.setSelectedPokemon(outputData.getSelectedPokemon());
        viewCollectionState.setPokemonOnPage(outputData.getPokemonOnPage());
        viewCollectionState.setOwnedPokemon(outputData.getOwnedPokemon());
        this.viewCollectionViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage, ViewCollectionOutputData outputData) {
        final ViewCollectionState viewCollectionState = viewCollectionViewModel.getState();
        viewCollectionState.setSelectedPokemon(null);
        viewCollectionState.setPokemonOnPage(null);
        viewCollectionState.setOwnedPokemon(outputData.getOwnedPokemon());
        viewCollectionState.setErrorMessage(errorMessage);
        this.viewCollectionViewModel.firePropertyChanged();
    }

    @Override
    public void switchToHomeView() {
        viewCollectionViewModel.setState((ViewCollectionState) homeViewModel.getState());
        viewCollectionViewModel.firePropertyChanged();
    }
}
