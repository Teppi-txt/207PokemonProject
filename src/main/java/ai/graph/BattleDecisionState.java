package ai.graph;

import entities.battle.Battle;
import entities.Player;
import entities.battle.Turn;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * State object for the LangGraph4j decision graph.
 * Extends AgentState to work with LangGraph4j's state management.
 * Holds all battle context, analysis data, and decision outputs.
 */
public class BattleDecisionState extends AgentState {

    // Define the state schema for LangGraph4j
    public static final Map<String, Channel<?>> SCHEMA = Map.of();

    private Battle battle;
    private Player aiPlayer;
    private Player opponent;
    private List<Turn> turnHistory;
    private String difficulty;
    private Map<String, Object> metadata;
    private Decision currentDecision;
    private boolean useFallback;
    private String errorMessage;

    public BattleDecisionState() {
        super(Map.of());
        this.metadata = new HashMap<>();
        this.turnHistory = new ArrayList<>();
        this.useFallback = false;
    }

    @SuppressWarnings("unchecked")
    public BattleDecisionState(Map<String, Object> initData) {
        super(initData);
        // Extract data from initData map
        if (initData.containsKey("battle")) {
            this.battle = (Battle) initData.get("battle");
        }
        if (initData.containsKey("aiPlayer")) {
            this.aiPlayer = (Player) initData.get("aiPlayer");
        }
        if (initData.containsKey("opponent")) {
            this.opponent = (Player) initData.get("opponent");
        }
        if (initData.containsKey("turnHistory")) {
            this.turnHistory = (List<Turn>) initData.get("turnHistory");
        } else {
            this.turnHistory = new ArrayList<>();
        }
        if (initData.containsKey("difficulty")) {
            this.difficulty = (String) initData.get("difficulty");
        }
        if (initData.containsKey("metadata")) {
            this.metadata = (Map<String, Object>) initData.get("metadata");
        } else {
            this.metadata = new HashMap<>();
        }
        if (initData.containsKey("currentDecision")) {
            this.currentDecision = (Decision) initData.get("currentDecision");
        }
        if (initData.containsKey("useFallback")) {
            this.useFallback = (Boolean) initData.get("useFallback");
        } else {
            this.useFallback = false;
        }
        if (initData.containsKey("errorMessage")) {
            this.errorMessage = (String) initData.get("errorMessage");
        }
    }

    public BattleDecisionState(Battle battle, Player aiPlayer, Player opponent,
                               List<Turn> turnHistory, String difficulty) {
        super(Map.of());
        this.battle = battle;
        this.aiPlayer = aiPlayer;
        this.opponent = opponent;
        this.turnHistory = turnHistory != null ? new ArrayList<>(turnHistory) : new ArrayList<>();
        this.difficulty = difficulty;
        this.metadata = new HashMap<>();
        this.useFallback = false;
    }

    // Getters
    public Battle getBattle() {
        return battle;
    }

    public Player getAiPlayer() {
        return aiPlayer;
    }

    public Player getOpponent() {
        return opponent;
    }

    public List<Turn> getTurnHistory() {
        return turnHistory;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Decision getCurrentDecision() {
        return currentDecision;
    }

    public boolean isUseFallback() {
        return useFallback;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Setters for state modification
    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public void setAiPlayer(Player aiPlayer) {
        this.aiPlayer = aiPlayer;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public void setTurnHistory(List<Turn> turnHistory) {
        this.turnHistory = turnHistory;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void setCurrentDecision(Decision currentDecision) {
        this.currentDecision = currentDecision;
    }

    public void setUseFallback(boolean useFallback) {
        this.useFallback = useFallback;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Utility methods
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }

    public void clearMetadata() {
        this.metadata.clear();
    }

    /**
     * Creates a copy of this state for immutable updates.
     */
    public BattleDecisionState copy() {
        BattleDecisionState copy = new BattleDecisionState();
        copy.battle = this.battle;
        copy.aiPlayer = this.aiPlayer;
        copy.opponent = this.opponent;
        copy.turnHistory = new ArrayList<>(this.turnHistory);
        copy.difficulty = this.difficulty;
        copy.metadata = new HashMap<>(this.metadata);
        copy.currentDecision = this.currentDecision;
        copy.useFallback = this.useFallback;
        copy.errorMessage = this.errorMessage;
        return copy;
    }

    @Override
    public String toString() {
        return "BattleDecisionState{" +
                "difficulty='" + difficulty + '\'' +
                ", turnHistory=" + (turnHistory != null ? turnHistory.size() : 0) + " turns" +
                ", useFallback=" + useFallback +
                ", hasDecision=" + (currentDecision != null) +
                '}';
    }
}
