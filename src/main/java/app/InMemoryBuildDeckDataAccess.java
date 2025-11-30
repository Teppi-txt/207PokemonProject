package app;

import entities.Deck;
import entities.User;
import use_case.build_deck.BuildDeckUserDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of BuildDeckUserDataAccessInterface.
 * Wraps a shared User instance for use with the main application.
 */
public class InMemoryBuildDeckDataAccess implements BuildDeckUserDataAccessInterface {

    private final User user;

    public InMemoryBuildDeckDataAccess(User user) {
        this.user = user;
    }

    @Override
    public void saveDeck(Deck deck) {
        user.addDeck(deck);
    }

    @Override
    public void deleteDeck(int deckId) {
        user.deleteDeck(deckId);
    }

    @Override
    public List<Deck> getDecks() {
        return new ArrayList<>(user.getDecks().values());
    }

    @Override
    public void saveUser(User user) {
        // No-op for in-memory storage - the user object is modified directly
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public int getNextDeckId() {
        int maxId = 0;
        for (Integer id : user.getDecks().keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }

    @Override
    public Deck getDeckById(int id) {
        return user.getDeckById(id);
    }
}
