package use_case.battle_ai;

import java.util.List;

import entities.Pokemon;
import entities.battle.AIPlayer;
import entities.battle.Battle;
import entities.battle.Move;
import entities.user.User;

/**
 * Data access interface for the battle AI use case.
 */
public interface BattleAIUserDataAccessInterface {

    /**
     * Saves the current battle state.
     * @param battle the battle object to save
     */
    void saveBattle(Battle battle);

    /**
     * Retrieves the current battle.
     * @return the stored battle, or null if none exists
     */
    Battle getBattle();

    /**
     * Saves the user's state (currency, Pokémon, etc.).
     * @param user the user object to save
     */
    void saveUser(User user);

    /**
     * Retrieves the current user.
     * @return the stored user
     */
    User getUser();

    /**
     * Retrieves all available Pokémon that can be used
     * for generating the AI's team.
     * @return a list of available Pokémon
     */
    List<Pokemon> getAllPokemon();

    /**
     * Saves the AI player's state.
     * @param aiPlayer the AI player to save
     */
    void saveAIPlayer(AIPlayer aiPlayer);

    /**
     * Retrieves the current AI player.
     * @return the stored AI player
     */
    AIPlayer getAIPlayer();

    /**
     * Saves the player's selected battle team.
     * @param team the list of Pokémon chosen by the player
     */
    void savePlayerTeam(List<Pokemon> team);

    /**
     * Retrieves the player's battle team.
     * @return the list of Pokémon selected for battle
     */
    List<Pokemon> getPlayerTeam();

    /**
     * Sets the player's currently active Pokémon in battle.
     * @param pokemon the Pokémon to set as active
     */
    void setPlayerActivePokemon(Pokemon pokemon);

    /**
     * Retrieves the player's active Pokémon.
     * @return the active Pokémon
     */
    Pokemon getPlayerActivePokemon();

    /**
     * Gets a move by its name.
     * @param moveName the name of the move to find
     * @return the Move if found, null otherwise
     */
    Move getMoveByName(String moveName);
}
