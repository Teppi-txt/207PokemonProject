package use_case.battle_ai;

import entities.Battle;
import entities.Pokemon;
import entities.User;
import entities.AIPlayer;

import java.util.List;

/**
 * Data access interface for the battle AI use case.
 */
public interface BattleAIUserDataAccessInterface {

    void saveBattle(Battle battle);

    Battle getBattle();

    void saveUser(User user);

    User getUser();

    /**
     * Gets all available Pokemon for AI team generation.
     */
    List<Pokemon> getAllPokemon();

    /**
     * Saves the AI player state.
     */
    void saveAIPlayer(AIPlayer aiPlayer);

    /**
     * Gets the current AI player.
     */
    AIPlayer getAIPlayer();

    /**
     * Saves the player's team for the battle.
     */
    void savePlayerTeam(List<Pokemon> team);

    /**
     * Gets the player's battle team.
     */
    List<Pokemon> getPlayerTeam();

    /**
     * Sets the player's active Pokemon.
     */
    void setPlayerActivePokemon(Pokemon pokemon);

    /**
     * Gets the player's active Pokemon.
     */
    Pokemon getPlayerActivePokemon();
}
