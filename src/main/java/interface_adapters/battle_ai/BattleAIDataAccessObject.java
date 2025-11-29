package interface_adapters.battle_ai;

import entities.Battle;
import entities.User;
import use_case.battle_ai.BattleAIUserDataAccessInterface;
import use_case.battle_player.BattlePlayerUserDataAccessInterface;

/**
 * Data Access Object for Battle AI use cases.
 * Implements both BattleAIUserDataAccessInterface and BattlePlayerUserDataAccessInterface.
 * Manages current user and battle state in memory.
 */
public class BattleAIDataAccessObject implements BattleAIUserDataAccessInterface,
        BattlePlayerUserDataAccessInterface {

    private User currentUser;
    private Battle currentBattle;

    public BattleAIDataAccessObject() {
        this.currentUser = null;
        this.currentBattle = null;
    }

    /**
     * Sets the current user for the session.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Sets the current battle for the session.
     */
    public void setCurrentBattle(Battle battle) {
        this.currentBattle = battle;
    }

    @Override
    public void saveBattle(Battle battle) {
        this.currentBattle = battle;
        // Note: For now, battles are only saved in memory.
        // Future enhancement: persist to JSON file via JSONSaver
    }

    @Override
    public Battle getBattle() {
        return currentBattle;
    }

    @Override
    public void saveUser(User user) {
        this.currentUser = user;
        // Note: For now, users are only saved in memory.
        // Future enhancement: persist to JSON file via JSONSaver
    }

    @Override
    public User getUser() {
        return currentUser;
    }
}
