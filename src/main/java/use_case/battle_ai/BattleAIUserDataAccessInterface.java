package use_case.battle_ai;

import entities.Battle;
import entities.User;

/**
 * Data access interface for the battle AI use case.
 * This interface defines the contract for accessing and persisting battle and user data.
 */
public interface BattleAIUserDataAccessInterface {

    /**
     * Saves the battle state.
     * @param battle the battle to save
     */
    void saveBattle(Battle battle);

    /**
     * Gets the current battle.
     * @return the battle
     */
    Battle getBattle();

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
}
