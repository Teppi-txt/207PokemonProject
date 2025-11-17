package use_case.battle_player;

import entities.Battle;
import entities.User;

public interface BattlePlayerUserDataAccessInterface {

    /**
     * saves the battle state.
     * @param battle the battle to save
     */
    void saveBattle(Battle battle);

    /**
     * gets the battle by id or returns current battle.
     * @return the battle
     */
    Battle getBattle();

    /**
     * saves the user.
     * @param user the user to save
     */
    void saveUser(User user);

    /**
     * gets the user.
     * @return the user
     */
    User getUser();
}

