package interface_adapter.collection;

import use_case.collection.ViewCollectionOutputBoundary;
import use_case.collection.ViewCollectionOutputData;
import interface_adapter.ViewModel;

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
        this.viewCollectionViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ViewCollectionState viewCollectionState = viewCollectionViewModel.getState();
        viewCollectionState.setSelectedPokemon(null);
        viewCollectionState.setPokemonOnPage(null);
        this.viewCollectionViewModel.firePropertyChange();
    }
}
