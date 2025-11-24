package interface_adapter.collection;

import entities.Pokemon;
import entities.User;
import use_case.collection.ViewCollectionInputBoundary;
import use_case.collection.ViewCollectionInputData;
import use_case.collection.ViewCollectionInteractor;

import java.util.ArrayList;

public class ViewCollectionController {
    private final ViewCollectionInputBoundary interactor;

    public ViewCollectionController(ViewCollectionInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(ArrayList<Pokemon> ownedPokemon, int currentPage, String filter) {
        final ViewCollectionInputData inputData =
                new ViewCollectionInputData(ownedPokemon, currentPage, filter);

        interactor.execute(inputData);
    }
}
