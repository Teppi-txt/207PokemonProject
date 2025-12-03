package interface_adapters.battle_ai;

import java.util.ArrayList;
import java.util.List;

import entities.battle.AIPlayer;
import entities.battle.Battle;
import entities.battle.Move;
import entities.Pokemon;
import entities.user.User;
import use_case.battle_ai.BattleAIUserDataAccessInterface;



/**
 * Data Access Object for Battle AI use cases.
 * Manages current user, battle state, and team data in memory.
 */

public class BattleAIDataAccessObject implements BattleAIUserDataAccessInterface {

    private final List<Pokemon> allPokemon;
    private final List<Move> allMoves;

    private User currentUser;
    private Battle currentBattle;
    private List<Pokemon> playerTeam;
    private AIPlayer aiPlayer;
    private Pokemon playerActivePokemon;

    public BattleAIDataAccessObject(List<Pokemon> allPokemon, List<Move> allMoves) {
        this.allPokemon = allPokemon;
        this.allMoves = allMoves;
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
        return allPokemon;
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
        for (Move move : allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                return move;
            }
        }
        return null;
    }
}
