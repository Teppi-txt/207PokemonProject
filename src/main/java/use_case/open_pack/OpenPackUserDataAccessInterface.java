package use_case.open_pack;
import entities.User;

public interface OpenPackUserDataAccessInterface {

    /**
     * Saves the user.
     * @param user the user to save
     */
    void save(User user);

    User get();


}
