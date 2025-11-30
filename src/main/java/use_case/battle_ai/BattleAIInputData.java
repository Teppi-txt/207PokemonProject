package use_case.battle_ai;

import entities.Battle;
import entities.AIPlayer;
import entities.Pokemon;
import entities.User;

import java.util.List;

/**
 * Input data for the Battle AI use case.
 * Supports two modes:
 * 1. Setup mode: battle is null, setup fields provided -> creates new battle
 * 2. Turn mode: battle and aiPlayer provided -> executes AI turn
 */
public class BattleAIInputData {

    // Turn execution fields
    private final Battle battle;
    private final AIPlayer aiPlayer;
    private final boolean forcedSwitch;

    // Setup fields (optional - used when battle is null)
    private final User user;
    private final List<Pokemon> playerTeam;
    private final String difficulty;

    /**
     * Constructor for executing an AI turn.
     */
    public BattleAIInputData(Battle battle, AIPlayer aiPlayer, boolean forcedSwitch) {
        this.battle = battle;
        this.aiPlayer = aiPlayer;
        this.forcedSwitch = forcedSwitch;
        this.user = null;
        this.playerTeam = null;
        this.difficulty = null;
    }

    /**
     * Constructor for setting up a new battle.
     */
    public BattleAIInputData(User user, List<Pokemon> playerTeam, String difficulty) {
        this.battle = null;
        this.aiPlayer = null;
        this.forcedSwitch = false;
        this.user = user;
        this.playerTeam = playerTeam;
        this.difficulty = difficulty;
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

    /**
     * Returns true if this is a setup request (no existing battle).
     */
    public boolean isSetupRequest() {
        return battle == null && user != null && playerTeam != null;
    }
}
