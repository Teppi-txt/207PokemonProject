package interface_adapters.build_deck;

import use_case.build_deck.BuildDeckInputBoundary;
import use_case.build_deck.BuildDeckInputData;

import java.util.List;
/*
The controller for the build deck use case
 */

public class BuildDeckController {
    private final BuildDeckInputBoundary buildDeckUseCaseInteractor;

    public BuildDeckController(BuildDeckInputBoundary buildDeckUseCaseInteractor) {
        this.buildDeckUseCaseInteractor = buildDeckUseCaseInteractor;
    }

    /**
     * Executes the Login Use Case.
     * @param deckName the name of the deck being made
     * @param pokemon the list of Pokemon in the deck
     * @param isRandom whether or not the deck is randomly generated
     */
    public void buildDeck(String deckName, List<Pokemon> pokemon, boolean isRandom) {
        BuildDeckInputData buildDeckInputData = new BuildDeckInputData(deckName, pokemon, isRandom);
        buildDeckUseCaseInteractor.execute(buildDeckInputData);
    }
}
