package use_case.battle_ai;

import entities.Battle;
import entities.AIPlayer;
import entities.Pokemon;
import entities.Turn;
import entities.User;

import java.util.List;

/**
 * Input data for the Battle AI use case.
 * Supports three modes:
 * 1. Setup mode: creates new battle
 * 2. Player turn mode: executes player's turn
 * 3. AI turn mode: executes AI's turn
 */
public class BattleAIInputData {

    // Turn execution fields
    private final Turn turn;
    private final Battle battle;
    private final AIPlayer aiPlayer;
    private final boolean forcedSwitch;

    // Setup fields (optional - used when battle is null)
    private final User user;
    private final List<Pokemon> playerTeam;
    private final String difficulty;

    // Player switch fields
    private final Pokemon switchTarget;

    /**
     * Constructor for executing a player's turn.
     */
    public BattleAIInputData(Turn turn) {
        this.turn = turn;
        this.battle = null;
        this.aiPlayer = null;
        this.forcedSwitch = false;
        this.user = null;
        this.playerTeam = null;
        this.difficulty = null;
        this.switchTarget = null;
    }

    /**
     * Constructor for executing an AI turn.
     */
    public BattleAIInputData(Battle battle, AIPlayer aiPlayer, boolean forcedSwitch) {
        this.turn = null;
        this.battle = battle;
        this.aiPlayer = aiPlayer;
        this.forcedSwitch = forcedSwitch;
        this.user = null;
        this.playerTeam = null;
        this.difficulty = null;
        this.switchTarget = null;
    }

    /**
     * Constructor for setting up a new battle.
     */
    public BattleAIInputData(User user, List<Pokemon> playerTeam, String difficulty) {
        this.turn = null;
        this.battle = null;
        this.aiPlayer = null;
        this.forcedSwitch = false;
        this.user = user;
        this.playerTeam = playerTeam;
        this.difficulty = difficulty;
        this.switchTarget = null;
    }

    /**
     * Constructor for player switching Pokemon.
     */
    public BattleAIInputData(Pokemon switchTarget) {
        this.turn = null;
        this.battle = null;
        this.aiPlayer = null;
        this.forcedSwitch = false;
        this.user = null;
        this.playerTeam = null;
        this.difficulty = null;
        this.switchTarget = switchTarget;
    }

    public Battle getBattle() {
        return battle;
    }

    public AIPlayer getAiPlayer() {
        return aiPlayer;
    }

    public boolean isForcedSwitch() {
        return forcedSwitch;
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

    public Turn getTurn() {
        return turn;
    }

    public Pokemon getSwitchTarget() {
        return switchTarget;
    }

    /**
     * Returns true if this is a setup request.
     */
    public boolean isSetupRequest() {
        return user != null && playerTeam != null;
    }

    /**
     * Returns true if this is a player turn request.
     */
    public boolean isPlayerTurnRequest() {
        return turn != null;
    }

    /**
     * Returns true if this is an AI turn request.
     */
    public boolean isAITurnRequest() {
        return battle != null && aiPlayer != null;
    }

    /**
     * Returns true if this is a player switch request.
     */
    public boolean isPlayerSwitchRequest() {
        return switchTarget != null;
    }
}
