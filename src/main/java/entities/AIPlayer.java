package entities;

import ai.graph.BattleDecisionState;
import ai.graph.Decision;
import ai.graph.DecisionGraph;
import cards.Deck;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AIPlayer implements Player, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Deck deck;
    private List<Pokemon> team;
    private Pokemon activePokemon;
    private String difficulty;
    private int wins;
    private int losses;
    private List<Turn> battleHistory;
    private transient DecisionGraph decisionGraph;

    public AIPlayer() {
        this.name = "AI Player";
        this.deck = new Deck();
        this.team = new ArrayList<>();
        this.activePokemon = null;
        this.difficulty = "medium";
        this.wins = 0;
        this.losses = 0;
        this.battleHistory = new ArrayList<>();
        this.decisionGraph = null;
    }

    public AIPlayer(String name) {
        this.name = name;
        this.deck = new Deck();
        this.team = new ArrayList<>();
        this.activePokemon = null;
        this.difficulty = "medium";
        this.wins = 0;
        this.losses = 0;
        this.battleHistory = new ArrayList<>();
        this.decisionGraph = null;
    }

    public AIPlayer(String name, String difficulty) {
        this.name = name;
        this.deck = new Deck();
        this.team = new ArrayList<>();
        this.activePokemon = null;
        this.difficulty = difficulty;
        this.wins = 0;
        this.losses = 0;
        this.battleHistory = new ArrayList<>();
        this.decisionGraph = null;
    }

    public AIPlayer(String name, Deck deck, String difficulty) {
        this.name = name;
        this.deck = deck;
        this.team = new ArrayList<>();
        this.activePokemon = null;
        this.difficulty = difficulty;
        this.wins = 0;
        this.losses = 0;
        this.battleHistory = new ArrayList<>();
        this.decisionGraph = null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Deck getDeck() {
        return this.deck;
    }

    @Override
    public Move chooseMove(Battle battle) {
        // Initialize decision graph if needed
        if (decisionGraph == null) {
            decisionGraph = DecisionGraph.buildGraph(difficulty);
        }

        // Get opponent player
        Player opponent = getOpponent(battle);

        // Create initial state
        BattleDecisionState initialState = new BattleDecisionState(
            battle, this, opponent, battleHistory, difficulty
        );

        try {
            // Execute decision graph using LangGraph4j
            Decision decision = decisionGraph.execute(initialState);

            // Extract and return the move
            if (decision != null && decision.isMove()) {
                return decision.getSelectedMove();
            }
        } catch (Exception e) {
            System.err.println("Error executing decision graph: " + e.getMessage());
            e.printStackTrace();
        }

        // Fallback to empty move if decision failed
        return new Move();
    }

    /**
     * Get the opponent player from the battle.
     * Since Battle stores Users (not Players), we create a temporary Player wrapper.
     */
    private Player getOpponent(Battle battle) {
        if (battle == null) {
            return null;
        }

        // AIPlayer is typically player2, so opponent is player1
        // But we need to check which one this AIPlayer is
        if (battle.getPlayer2() != null && battle.getPlayer2().getName().equals(this.name)) {
            // We are player2, so player1 is opponent
            return new HumanPlayerWrapper(battle.getPlayer1());
        } else if (battle.getPlayer1() != null) {
            // We are player1, so player2 is opponent
            return new HumanPlayerWrapper(battle.getPlayer2());
        }

        return null;
    }

    /**
     * Temporary wrapper to make User act like a Player for AI decision making.
     * Must be serializable for LangGraph state cloning.
     */
    private static class HumanPlayerWrapper implements Player, java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final entities.User user;
        private Pokemon activePokemon;
        private List<Pokemon> team;

        public HumanPlayerWrapper(entities.User user) {
            this.user = user;
            this.team = user.getOwnedPokemon();

            // Find the active Pokemon - it's the first one in the team (battle uses first as active)
            // or the first non-fainted one
            if (team != null && !team.isEmpty()) {
                // First try to use the first Pokemon (most likely active in battle)
                if (!team.get(0).isFainted()) {
                    this.activePokemon = team.get(0);
                } else {
                    // Find first non-fainted
                    for (Pokemon p : team) {
                        if (!p.isFainted()) {
                            this.activePokemon = p;
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public String getName() {
            return user.getName();
        }

        @Override
        public cards.Deck getDeck() {
            return new cards.Deck();
        }

        @Override
        public Move chooseMove(Battle battle) {
            return new Move(); // Human doesn't auto-choose
        }

        @Override
        public Pokemon getActivePokemon() {
            return activePokemon;
        }

        @Override
        public void switchPokemon(Pokemon pokemon) {
            this.activePokemon = pokemon;
        }

        @Override
        public List<Pokemon> getTeam() {
            return team;
        }

        @Override
        public void useItem(Item item, Pokemon target) {
            // No-op
        }

        @Override
        public boolean hasAvailablePokemon() {
            if (team == null) return false;
            for (Pokemon p : team) {
                if (!p.isFainted()) return true;
            }
            return false;
        }

        @Override
        public boolean isDefeated() {
            return !hasAvailablePokemon();
        }
    }

    @Override
    public Pokemon getActivePokemon() {
        return activePokemon;
    }

    @Override
    public void switchPokemon(Pokemon pokemon) {
        if (team.contains(pokemon) && !pokemon.isFainted()) {
            this.activePokemon = pokemon;
        }
    }

    @Override
    public List<Pokemon> getTeam() {
        return team;
    }

    @Override
    public void useItem(Item item, Pokemon target) {
        // AI logic for using items on Pokemon would go here
        // Could vary based on difficulty level
    }

    @Override
    public boolean hasAvailablePokemon() {
        for (Pokemon pokemon : team) {
            if (!pokemon.isFainted()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDefeated() {
        return !hasAvailablePokemon();
    }

    public Battle initiateBattle(User player1, User player2) {
        Battle battle = new Battle(0, player1, player2);
        battle.startBattle();
        return battle;
    }

    public void processTurn(Battle battle, Move move) {
        // Logic to process a turn would go here
        // This would update the battle state based on the move
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void recordWin() {
        this.wins++;
    }

    public void recordLoss() {
        this.losses++;
    }

    public void setTeam(List<Pokemon> team) {
        this.team = team;
    }

    public void setActivePokemon(Pokemon activePokemon) {
        this.activePokemon = activePokemon;
    }

    public void addPokemonToTeam(Pokemon pokemon) {
        this.team.add(pokemon);
        if (activePokemon == null && !pokemon.isFainted()) {
            this.activePokemon = pokemon;
        }
    }

    public void removePokemonFromTeam(Pokemon pokemon) {
        this.team.remove(pokemon);
        if (activePokemon == pokemon) {
            this.activePokemon = null;
        }
    }

    /**
     * Decide which Pokemon to switch to using AI logic.
     */
    public Pokemon decideSwitch(Battle battle) {
        // Initialize decision graph if needed
        if (decisionGraph == null) {
            decisionGraph = DecisionGraph.buildGraph(difficulty);
        }

        // Get opponent player
        Player opponent = getOpponent(battle);

        // Create initial state
        BattleDecisionState initialState = new BattleDecisionState(
            battle, this, opponent, battleHistory, difficulty
        );

        // Force switch decision type
        initialState.addMetadata("decisionType", "switch");

        try {
            // Execute decision graph using LangGraph4j
            Decision decision = decisionGraph.execute(initialState);

            // Extract and return the Pokemon to switch to
            if (decision != null && decision.isSwitch()) {
                return decision.getSwitchTarget();
            }
        } catch (Exception e) {
            System.err.println("Error executing decision graph for switch: " + e.getMessage());
            e.printStackTrace();
        }

        // Fallback: return first available Pokemon
        for (Pokemon p : team) {
            if (!p.isFainted() && p != activePokemon) {
                return p;
            }
        }

        return null;
    }

    /**
     * Record a turn in the battle history for context.
     */
    public void recordTurn(Turn turn) {
        if (battleHistory == null) {
            battleHistory = new ArrayList<>();
        }
        battleHistory.add(turn);
    }

    /**
     * Clear the battle history (for new battles).
     */
    public void clearHistory() {
        if (battleHistory != null) {
            battleHistory.clear();
        }
    }

    /**
     * Get the battle history.
     */
    public List<Turn> getBattleHistory() {
        return battleHistory;
    }
}
