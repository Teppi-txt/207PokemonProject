package use_case.battle_ai;

import entities.Pokemon;
import entities.user.User;

import java.util.List;

/**
 * Input data for the Battle AI use case.
 * Supports three modes:
 * 1. Setup mode: creates new battle
 * 2. Player move mode: executes player's move by index
 * 3. Player switch mode: switches player's Pokemon by ID
 */
public class BattleAIInputData {

    // Setup fields
    private final User user;
    private final List<Pokemon> playerTeam;
    private final String difficulty;

    // Player switch fields
    private final int switchTargetId;

    // Player move fields
    private final int moveIndex;

    /**
     * Constructor for setting up a new battle.
     */
    public BattleAIInputData(User user, List<Pokemon> playerTeam, String difficulty) {
        this.user = user;
        this.playerTeam = playerTeam;
        this.difficulty = difficulty;
        this.switchTargetId = -1;
        this.moveIndex = -1;
    }

    /**
     * Private constructor for player switching Pokemon by ID.
     */
    private BattleAIInputData(int switchTargetId, boolean isSwitch) {
        this.user = null;
        this.playerTeam = null;
        this.difficulty = null;
        this.switchTargetId = switchTargetId;
        this.moveIndex = -1;
    }

    /**
     * Constructor for player using a move by index.
     */
    public BattleAIInputData(int moveIndex) {
        this.user = null;
        this.playerTeam = null;
        this.difficulty = null;
        this.switchTargetId = -1;
        this.moveIndex = moveIndex;
    }

    /**
     * Factory method to create input data for switching by Pokemon ID.
     */
    public static BattleAIInputData forSwitchById(int pokemonId) {
        return new BattleAIInputData(pokemonId, true);
    }

    public User getUser() {
        return user;
    }

    public List<Pokemon> getPlayerTeam() {
        return playerTeam;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getSwitchTargetId() {
        return switchTargetId;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    /**
     * Returns true if this is a setup request.
     */
    public boolean isSetupRequest() {
        return user != null && playerTeam != null;
    }

    /**
     * Returns true if this is a player move request.
     */
    public boolean isPlayerMoveRequest() {
        return moveIndex >= 0;
    }

    /**
     * Returns true if this is a player switch request (by ID).
     */
    public boolean isPlayerSwitchRequest() {
        return switchTargetId >= 0;
    }
}
