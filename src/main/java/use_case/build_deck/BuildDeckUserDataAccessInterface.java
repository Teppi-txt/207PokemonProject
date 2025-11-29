package use_case.build_deck;
import entities.Deck;
import entities.User;
import java.util.List;

public interface BuildDeckUserDataAccessInterface {
    /**
     * Saves the deck.
     * @param deck the deck to save
     */
    void saveDeck(Deck deck);

    /**
     * deletes the deck.
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

    int getNextDeckId();

    /**
     * Finds a deck by its ID.
     * @param id the ID of the deck
     * @return the Deck or null if not found
     */
    Deck getDeckById(int id);
}