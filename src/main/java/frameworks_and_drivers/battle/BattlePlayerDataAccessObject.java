package frameworks_and_drivers.battle;

import entities.battle.Battle;
import entities.user.User;
import use_case.battle_player.BattlePlayerUserDataAccessInterface;

/**
 * Data Access Object for Battle Player use case.
 * This is the framework/driver implementation of the data access interface.
 */
public class BattlePlayerDataAccessObject implements BattlePlayerUserDataAccessInterface {
    
    private Battle currentBattle;
    private User currentUser;
    
    public BattlePlayerDataAccessObject() {
        this.currentBattle = null;
        this.currentUser = null;
    }
    
    /**
     * Sets the current battle
     */
    public void setBattle(Battle battle) {
        this.currentBattle = battle;
    }
    
    /**
     * Sets the current user
     */
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    @Override
    public void saveBattle(Battle battle) {
        this.currentBattle = battle;
    }
    
    @Override
    public Battle getBattle() {
        return currentBattle;
    }
    
    @Override
    public void saveUser(User user) {
        this.currentUser = user;
    }
    
    @Override
    public User getUser() {
        return currentUser;
    }
}

