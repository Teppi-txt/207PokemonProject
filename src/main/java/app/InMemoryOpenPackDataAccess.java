package app;

import entities.User;
import use_case.open_pack.OpenPackUserDataAccessInterface;

/**
 * In-memory implementation of OpenPackUserDataAccessInterface.
 * Wraps a shared User instance for use with the main application.
 */
public class InMemoryOpenPackDataAccess implements OpenPackUserDataAccessInterface {

    private final User user;

    public InMemoryOpenPackDataAccess(User user) {
        this.user = user;
    }

    @Override
    public void save(User user) {
        // No-op for in-memory storage - the user object is modified directly
    }

    @Override
    public User get() {
        return user;
    }
}
