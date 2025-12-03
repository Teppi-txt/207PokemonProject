package interface_adapters.build_deck;

import java.util.List;

import entities.Pokemon;
import use_case.build_deck.BuildDeckInputBoundary;
import use_case.build_deck.BuildDeckInputData;

/**
* The Controller for the Build Deck Use Case.
 */

public class BuildDeckController {
    private final BuildDeckInputBoundary buildDeckUseCaseInteractor;

    public BuildDeckController(BuildDeckInputBoundary buildDeckUseCaseInteractor) {
        this.buildDeckUseCaseInteractor = buildDeckUseCaseInteractor;
    }

    /**
     * Executes the Build Deck Use Case.
     * @param deckId the ID of the deck to build/edit (-1 for new) <--- MODIFIED.
     * @param deckName the name of the deck.
     * @param pokemon the list of Pokémon in the deck.
     * @param isRandom whether the deck is randomly generated.
     * @param delete whether the deck is to be deleted.
     */
    public void buildDeck(int deckId, String deckName, List<Pokemon> pokemon, boolean isRandom, boolean delete) {
        // <--- MODIFIED
        final BuildDeckInputData buildDeckInputData = new BuildDeckInputData(deckId, deckName, pokemon, isRandom, delete);
        buildDeckUseCaseInteractor.execute(buildDeckInputData);
    }

    /**
     * Executes the Delete Deck Use Case.
     * @param deckId the ID of the deck to build/edit (-1 for new) <--- MODIFIED.
     */
    public void deleteDeck(int deckId) {
        final BuildDeckInputData inputData = new BuildDeckInputData(deckId, null,
                null, false, true);
        buildDeckUseCaseInteractor.execute(inputData);
    }
}
