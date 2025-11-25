package interface_adapters.collection;

import use_case.collection.ViewCollectionOutputBoundary;
import use_case.collection.ViewCollectionOutputData;

public class ViewCollectionPresenter implements ViewCollectionOutputBoundary {
    private final ViewCollectionViewModel viewCollectionViewModel;

    public ViewCollectionPresenter(ViewCollectionViewModel viewCollectionViewModel) {
        this.viewCollectionViewModel = viewCollectionViewModel;
    }

    @Override
    public void prepareSuccessView(ViewCollectionOutputData outputData) {
        final ViewCollectionState viewCollectionState = viewCollectionViewModel.getState();
        viewCollectionState.setSelectedPokemon(outputData.getSelectedPokemon());
        viewCollectionState.setPokemonOnPage(outputData.getPokemonOnPage());
        viewCollectionState.setOwnedPokemon(outputData.getOwnedPokemon());
        this.viewCollectionViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ViewCollectionState viewCollectionState = viewCollectionViewModel.getState();
        viewCollectionState.setSelectedPokemon(null);
        viewCollectionState.setPokemonOnPage(null);
        viewCollectionState.setOwnedPokemon(null);
        this.viewCollectionViewModel.firePropertyChange();
    }
}
