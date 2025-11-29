package interface_adapters.build_deck;

import use_case.build_deck.BuildDeckInputBoundary;
import use_case.build_deck.BuildDeckInputData;
import entities.Pokemon;

import java.util.List;
/*
The Controller for the Build Deck Use Case
 */

public class BuildDeckController {
    private final BuildDeckInputBoundary buildDeckUseCaseInteractor;

    public BuildDeckController(BuildDeckInputBoundary buildDeckUseCaseInteractor) {
        this.buildDeckUseCaseInteractor = buildDeckUseCaseInteractor;
    }

    /**
     * Executes the Build Deck Use Case.
     * @param deckId the ID of the deck to build/edit (-1 for new) <--- MODIFIED
     * @param deckName the name of the deck
     * @param pokemon the list of Pokémon in the deck
     * @param isRandom whether the deck is randomly generated
     */
    public void buildDeck(int deckId, String deckName, List<Pokemon> pokemon, boolean isRandom) { // <--- MODIFIED
        BuildDeckInputData buildDeckInputData = new BuildDeckInputData(deckId, deckName, pokemon, isRandom);
        buildDeckUseCaseInteractor.execute(buildDeckInputData);
    }
}