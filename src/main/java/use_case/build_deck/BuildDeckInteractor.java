package use_case.build_deck;

import entities.Deck;
import entities.Pokemon;
import entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildDeckInteractor implements BuildDeckInputBoundary {
    private static final int DECK_LIMIT = 5;

    private final BuildDeckUserDataAccessInterface dataAccess;
    private final BuildDeckOutputBoundary presenter;

    public BuildDeckInteractor(BuildDeckUserDataAccessInterface dataAccess, BuildDeckOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    // --- Helper Methods ---

    private List<Pokemon> validateAndFetchSelectedPokemons(BuildDeckInputData input, User user) {
        // deck size limit
        if (input.getPokemon().size() > DECK_LIMIT) {
            presenter.prepareFailView("You cannot add more than " + DECK_LIMIT + " Pokémon.");
            return null;
        }

        List<Pokemon> result = new ArrayList<>();

        for (Pokemon pokemon : input.getPokemon()) {
            // Ensures the user actually owns the Pokemon being added
            Pokemon found = user.getPokemonById(pokemon.getID());

            if (found == null) {
                presenter.prepareFailView("You do not own the Pokémon " + pokemon.getName());
                return null;
            }

            result.add(found);
        }

        return result;
    }

    @Override
    public void execute(BuildDeckInputData inputData) {

        User user = dataAccess.getUser();
        if (user == null) {
            presenter.prepareFailView("User not found.");
            return;
        }

        Deck deck;
        boolean isNewDeck = (inputData.getDeckId() == -1);

        if (isNewDeck) {
            // Case 1: NEW Deck
            int deckId = dataAccess.getNextDeckId(); // Correctly gets a new ID
            String name = (inputData.getDeckName() == null || inputData.getDeckName().isEmpty())
                    ? ("Team " + deckId) : inputData.getDeckName();
            deck = new Deck(deckId, name); // Uses the new ID
            dataAccess.saveDeck(deck); // Ensure the new deck appears in the deck list
        } else {
            // Case 2: EDIT Existing Deck or LOAD Existing Deck
            Deck sourceDeck = dataAccess.getDeckById(inputData.getDeckId());
            if (sourceDeck == null) {
                presenter.prepareFailView("Deck with ID " + inputData.getDeckId() + " not found.");
                return;
            }

            // 💥 FIX: Create a COPY of the deck to ensure the view's local manipulations
            // don't corrupt the persistent state in the DAO map.
            deck = new Deck(sourceDeck);

            // Always update the name from inputData (even when just loading, as the name might have been changed in the view)
            if (inputData.getDeckName() != null) {
                deck.setName(inputData.getDeckName()); // Update the name on the copy
            }
        }

        // Handling team generation or explicit list saving
        List<Pokemon> newPokemons = null;
        boolean shouldSave = false; // Flag to track if we need to save/persist changes

        if (inputData.isRandom()) {
            newPokemons = generateRandomDeck(user);
            shouldSave = true; // Randomizing requires a save
        } else if (inputData.getPokemon() != null) {
            // User explicitly provided a list (This happens on the 'Save Deck' button click)
            newPokemons = validateAndFetchSelectedPokemons(inputData, user);
            if (newPokemons == null) return; // Validation failed
            shouldSave = true; // Explicit list requires a save
        } else {
            // Case: Pure Load (inputData.getPokemon() == null AND isRandom() == false)
            // The 'deck' object (which is a copy of the persistent state) already holds the correct Pokemons.
        }

        // --- Apply changes and Save only if a modification occurred (random or explicit list provided) ---
        if (shouldSave) {
            // This is crucial: Use the fetched/validated list of Pokemons to update the deck object
            deck.getPokemons().clear();
            for (Pokemon p : newPokemons) {
                deck.addPokemon(p);
            }

            // Save the modified or new deck object to persistence
            dataAccess.saveDeck(deck);
        }

        // Always present the updated deck for the view to render
        presenter.prepareSuccessView(new BuildDeckOutputData(deck, dataAccess.getDecks()));
    }

    private List<Pokemon> generateRandomDeck(User user) {
        List<Pokemon> owned = new ArrayList<>(user.getOwnedPokemon());
        Collections.shuffle(owned);

        int size = Math.min(DECK_LIMIT, owned.size());
        return owned.subList(0, size);
    }
}
