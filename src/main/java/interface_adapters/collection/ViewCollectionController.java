package interface_adapters.collection;

import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;
import use_case.collection.ViewCollectionInputBoundary;
import use_case.collection.ViewCollectionInputData;
import use_case.collection.ViewCollectionInteractor;
import view.CollectionView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewCollectionController {
    private final ViewCollectionInputBoundary interactor;

    public ViewCollectionController(ViewCollectionInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(List<Pokemon> ownedPokemon, int currentPage, String filter) {
        final ViewCollectionInputData inputData =
                new ViewCollectionInputData(ownedPokemon, currentPage, filter);
        interactor.execute(inputData);
    }

    public void switchToHomeView() {
        interactor.switchToHomeView();
    }
}
