package use_case.open_pack;

import entities.user.User;

public interface OpenPackUserDataAccessInterface {

    /**
     * Returns the shared user instance.
     */
    User get();

    /**
     * Saves the user state (currency, pokemon, decks, etc.)
     */
    void saveUser(User user);
}
