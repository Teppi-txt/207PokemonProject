package use_case.battle_player;

import entities.battle.Battle;
import entities.user.User;

public interface BattlePlayerUserDataAccessInterface {

    /**
     * Saves the battle state.
     * @param battle the battle to save
     */
    void saveBattle(Battle battle);

    /**
     * Gets the battle by id or returns current battle.
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

