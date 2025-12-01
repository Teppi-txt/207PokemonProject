package app.data_access;

import entities.User;
import frameworks_and_drivers.JsonUserDataAccess;
import use_case.open_pack.OpenPackUserDataAccessInterface;

/**
 * In-memory implementation of OpenPackUserDataAccessInterface.
 * Wraps a shared User instance and forwards saving to JsonUserDataAccess.
 */
public class InMemoryOpenPackDataAccess implements OpenPackUserDataAccessInterface {

    private final User user;
    private final JsonUserDataAccess fileSaver;

    public InMemoryOpenPackDataAccess(User user, JsonUserDataAccess fileSaver) {
        this.user = user;
        this.fileSaver = fileSaver;
    }

    @Override
    public User get() {
        return user;
    }

    @Override
    public void saveUser(User user) {
        fileSaver.saveUser(user);
    }
}
