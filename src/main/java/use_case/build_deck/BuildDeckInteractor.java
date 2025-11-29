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

    public BuildDeckInteractor(BuildDeckUserDataAccessInterface dataAccess,
                               BuildDeckOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(BuildDeckInputData inputData) {

        User user = dataAccess.getUser();
        if (user == null) {
            presenter.prepareFailView("User not found.");
            return;
        }

        // handling random team generation
        List<Pokemon> deckPokemons;

        if (inputData.isRandom()) {
            deckPokemons = generateRandomDeck(user);
        } else {
            deckPokemons = validateAndFetchSelectedPokemons(inputData, user);
            if (deckPokemons == null) return;  // validation failed & presenter already notified
        }

        // create and save deck
        int deckId = dataAccess.getNextDeckId();
        String name = inputData.getDeckName();
        Deck deck = new Deck(deckId, name);

        for (Pokemon p : deckPokemons) {
            deck.addPokemon(p);
        }

        dataAccess.saveDeck(deck);
        dataAccess.saveUser(user);

        presenter.prepareSuccessView(new BuildDeckOutputData(deck));
    }

    // helpers
    private List<Pokemon> validateAndFetchSelectedPokemons(BuildDeckInputData input, User user) {

        // deck size limit
        if (input.getPokemon().size() > DECK_LIMIT) {
            presenter.prepareFailView("You cannot add more than " + DECK_LIMIT + " Pokémon.");
            return null;
        }

        List<Pokemon> result = new ArrayList<>();

        for (Pokemon pokemon : input.getPokemon()) {
            Pokemon found = user.getPokemonById(pokemon.getID());

            if (found == null) {
                presenter.prepareFailView("You do not own the Pokémon " + pokemon.getName());
                return null;
            }

            result.add(found);
        }

        return result;
    }

    private List<Pokemon> generateRandomDeck(User user) {
        List<Pokemon> owned = new ArrayList<>(user.getOwnedPokemon());
        Collections.shuffle(owned);

        int size = Math.min(DECK_LIMIT, owned.size());
        return owned.subList(0, size);
    }
}
