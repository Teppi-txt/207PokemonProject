package interface_adapters.battle_ai;

import entities.AIPlayer;
import entities.Battle;
import entities.Move;
import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;
import use_case.battle_ai.BattleAIUserDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Battle AI use cases.
 * Manages current user, battle state, and team data in memory.
 */
public class BattleAIDataAccessObject implements BattleAIUserDataAccessInterface {

    private User currentUser;
    private Battle currentBattle;
    private List<Pokemon> playerTeam;
    private AIPlayer aiPlayer;
    private Pokemon playerActivePokemon;

    public BattleAIDataAccessObject() {
        this.currentUser = null;
        this.currentBattle = null;
        this.playerTeam = new ArrayList<>();
        this.aiPlayer = null;
        this.playerActivePokemon = null;
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

    @Override
    public List<Pokemon> getAllPokemon() {
        return JSONLoader.allPokemon;
    }

    @Override
    public void saveAIPlayer(AIPlayer aiPlayer) {
        this.aiPlayer = aiPlayer;
    }

    @Override
    public AIPlayer getAIPlayer() {
        return aiPlayer;
    }

    @Override
    public void savePlayerTeam(List<Pokemon> team) {
        this.playerTeam = team;
    }

    @Override
    public List<Pokemon> getPlayerTeam() {
        return playerTeam;
    }

    @Override
    public void setPlayerActivePokemon(Pokemon pokemon) {
        this.playerActivePokemon = pokemon;
    }

    @Override
    public Pokemon getPlayerActivePokemon() {
        return playerActivePokemon;
    }

    @Override
    public Move getMoveByName(String moveName) {
        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                return move;
            }
        }
        return null;
    }
}
