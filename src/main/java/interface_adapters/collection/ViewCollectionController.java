package interface_adapters.collection;

import java.util.List;

import entities.Pokemon;
import use_case.collection.ViewCollectionInputBoundary;
import use_case.collection.ViewCollectionInputData;

/**
 * Class responsible for feeding input data to the Collection interactor.
 */

public class ViewCollectionController {
    private final ViewCollectionInputBoundary interactor;

    public ViewCollectionController(ViewCollectionInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the view collection action.
     * @param ownedPokemon list of Pokémon
     * @param currentPage page
     * @param filter filter
     */
    public void execute(List<Pokemon> ownedPokemon, int currentPage, String filter) {
        final ViewCollectionInputData inputData =
                new ViewCollectionInputData(ownedPokemon, currentPage, filter);
        interactor.execute(inputData);
    }

    /**
     * Switches to the home view.
     */
    public void switchToHomeView() {
        interactor.switchToHomeView();
    }
}
