package use_case.open_pack;

import entities.user.User;

public interface OpenPackUserDataAccessInterface {

    /**
     * Returns the shared user instance.
     * @return the User object currently stored
     */
    User get();

    /**
     * Saves the user state (currency, pokemon, decks, etc.).
     * @param user the User object to be saved
     */
    void saveUser(User user);
}
