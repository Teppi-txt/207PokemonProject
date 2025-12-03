package use_case.build_deck;

import java.util.List;

import entities.battle.Deck;
import entities.user.User;

public interface BuildDeckUserDataAccessInterface {
    /**
     * Saves the deck.
     * @param deck the deck to save
     */
    void saveDeck(Deck deck);

    /**
     * Deletes the deck.
     * @param deckId the deck to delete
     */
    void deleteDeck(int deckId);

    /**
     * Gets all decks for the user.
     * @return a list of the user's decks
     */
    List<Deck> getDecks();

    /**
     * Saves the user.
     * @param user the user to save
     */
    void saveUser(User user);

    /**
     * Gets the user.
     * @return the user
     */
    User getUser();

    /**
     * Saves the current user state.
     */
    void saveUser();

    /**
     * Generates and returns the next available deck ID.
     * @return the next unique deck ID
     */
    int getNextDeckId();

    /**
     * Finds a deck by its ID.
     * @param id the ID of the deck
     * @return the Deck or null if not found
     */
    Deck getDeckById(int id);
}
