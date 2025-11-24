package use_case.build_deck;
import entities.Deck;
import entities.User;

public interface BuildDeckUserDataAccessInterface {
    /**
     * Saves the deck.
     * @param deck the deck to save
     */
    void saveDeck(Deck deck);

    /**
     * Gets the deck.
     * @return the deck
     */
    Deck getDeck();

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
}
