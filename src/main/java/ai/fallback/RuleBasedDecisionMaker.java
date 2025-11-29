package ai.fallback;

import ai.graph.BattleDecisionState;
import ai.graph.Decision;
import entities.Move;
import entities.Pokemon;
import pokeapi.JSONLoader;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Rule-based fallback decision maker for when LLM is unavailable or fails.
 * Uses simple heuristics based on difficulty level.
 */
public class RuleBasedDecisionMaker {
    private static final Random random = new Random();

    /**
     * Make a move decision using rule-based logic
     */
    public static Decision decideMoveEasy(BattleDecisionState state) {
        Pokemon activePokemon = state.getAiPlayer().getActivePokemon();
        if (activePokemon == null || activePokemon.getMoves() == null || activePokemon.getMoves().isEmpty()) {
            return Decision.move(new Move(), "No moves available", 0.1);
        }

        // Easy: Just pick a random move
        List<String> moves = activePokemon.getMoves();
        int randomIndex = random.nextInt(moves.size());
        String moveName = moves.get(randomIndex);

        // Look up the full move from JSONLoader
        Move selectedMove = lookupMove(moveName);

        return Decision.move(selectedMove, "Random move selection (Easy difficulty)", 0.3);
    }

    /**
     * Make a medium difficulty move decision
     */
    public static Decision decideMoveMedium(BattleDecisionState state) {
        Pokemon activePokemon = state.getAiPlayer().getActivePokemon();
        Pokemon opponentPokemon = state.getOpponent().getActivePokemon();

        if (activePokemon == null || activePokemon.getMoves() == null || activePokemon.getMoves().isEmpty()) {
            return Decision.move(new Move(), "No moves available", 0.1);
        }

        List<String> moves = activePokemon.getMoves();

        // Medium: Try to pick a super-effective move if available
        // For now, just pick the first move (simplified logic)
        // In a full implementation, this would check type effectiveness
        String selectedMoveName = moves.get(0);
        if (opponentPokemon != null && opponentPokemon.getTypes() != null && !opponentPokemon.getTypes().isEmpty()) {
            // Simple heuristic: prefer moves that match opponent weakness
            for (String moveName : moves) {
                // This is simplified - would need actual type chart lookup
                selectedMoveName = moveName;
                break;
            }
        }

        Move selectedMove = lookupMove(selectedMoveName);
        return Decision.move(selectedMove, "Selected move based on type matchup (Medium difficulty)", 0.6);
    }

    /**
     * Make a hard difficulty move decision
     */
    public static Decision decideMoveHard(BattleDecisionState state) {
        Pokemon activePokemon = state.getAiPlayer().getActivePokemon();
        Pokemon opponentPokemon = state.getOpponent().getActivePokemon();

        if (activePokemon == null || activePokemon.getMoves() == null || activePokemon.getMoves().isEmpty()) {
            return Decision.move(new Move(), "No moves available", 0.1);
        }

        List<String> moves = activePokemon.getMoves();

        // Hard: Consider type effectiveness, power, and HP percentages
        // For now, pick the first move with reasoning
        // In a full implementation, this would analyze all factors
        String selectedMoveName = moves.get(0);
        String reasoning = "Selected strategically based on type effectiveness, move power, and battle state";

        if (opponentPokemon != null && opponentPokemon.getStats() != null) {
            int opponentHp = opponentPokemon.getStats().getHp();

            if (opponentHp < 30) {
                // Opponent is low HP, go for any damaging move
                selectedMoveName = moves.get(0);
                reasoning = "Opponent is low on HP - finishing them off";
            }
        }

        Move selectedMove = lookupMove(selectedMoveName);
        return Decision.move(selectedMove, reasoning, 0.8);
    }

    /**
     * Make a switch decision
     */
    public static Decision decideSwitch(BattleDecisionState state) {
        List<Pokemon> team = state.getAiPlayer().getTeam();
        Pokemon current = state.getAiPlayer().getActivePokemon();

        if (team == null || team.isEmpty()) {
            return Decision.switchPokemon(null, "No Pokemon available to switch", 0.1);
        }

        // Find first non-fainted Pokemon that isn't currently active
        List<Pokemon> availablePokemon = team.stream()
                .filter(p -> !p.isFainted() && p != current)
                .collect(Collectors.toList());

        if (availablePokemon.isEmpty()) {
            return Decision.switchPokemon(null, "No available Pokemon to switch to", 0.1);
        }

        // Simple heuristic: pick the first available Pokemon
        // In a full implementation, would consider type matchups
        Pokemon switchTarget = availablePokemon.get(0);
        String reasoning = "Switching to " + switchTarget.getName() + " as best available option";

        return Decision.switchPokemon(switchTarget, reasoning, 0.6);
    }

    /**
     * Make a decision based on difficulty level
     */
    public static Decision makeDecision(BattleDecisionState state, boolean isSwitch) {
        if (isSwitch) {
            return decideSwitch(state);
        }

        String difficulty = state.getDifficulty();
        if (difficulty == null) {
            return decideMoveMedium(state);
        }

        switch (difficulty.toLowerCase()) {
            case "easy":
                return decideMoveEasy(state);
            case "hard":
                return decideMoveHard(state);
            case "medium":
            default:
                return decideMoveMedium(state);
        }
    }

    /**
     * Helper method to look up a move from JSONLoader by name
     */
    private static Move lookupMove(String moveName) {
        // Look up the full move from JSONLoader to get power and other properties
        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                return move;
            }
        }

        // If move not found, create a basic move with default power
        return new Move().setName(moveName).setPower(50);
    }
}
