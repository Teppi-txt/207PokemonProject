package ai.parsers;

import ai.graph.BattleDecisionState;
import ai.graph.Decision;
import entities.Move;
import entities.Pokemon;
import pokeapi.JSONLoader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses LLM text responses into structured Decision objects.
 */
public class LLMResponseParser {

    // Patterns for extracting decisions
    private static final Pattern MOVE_PATTERN = Pattern.compile("MOVE:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SWITCH_PATTERN = Pattern.compile("SWITCH:\\s*([\\w\\s-]+?)(?:\\n|$)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REASONING_PATTERN = Pattern.compile("REASONING:\\s*(.+?)(?:$|\\n\\n)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * Parse LLM response into a Decision
     */
    public static Decision parseDecision(String llmResponse, BattleDecisionState state) {
        if (llmResponse == null || llmResponse.trim().isEmpty()) {
            return null;
        }

        String reasoning = extractReasoning(llmResponse);
        double confidence = 0.7; // Default confidence for LLM decisions

        // Try to parse as move decision
        Decision moveDecision = parseMoveDecision(llmResponse, state, reasoning, confidence);
        if (moveDecision != null) {
            return moveDecision;
        }

        // Try to parse as switch decision
        Decision switchDecision = parseSwitchDecision(llmResponse, state, reasoning, confidence);
        if (switchDecision != null) {
            return switchDecision;
        }

        // Could not parse - return null
        return null;
    }

    /**
     * Parse move decision from response
     */
    private static Decision parseMoveDecision(String response, BattleDecisionState state,
                                              String reasoning, double confidence) {
        int moveIndex = extractMoveIndex(response);
        if (moveIndex < 0) {
            return null;
        }

        Pokemon activePokemon = state.getAiPlayer().getActivePokemon();
        if (activePokemon == null || activePokemon.getMoves() == null) {
            return null;
        }

        List<String> moves = activePokemon.getMoves();
        if (moveIndex >= moves.size()) {
            return null;
        }

        String moveName = moves.get(moveIndex);

        // Look up the full move from JSONLoader to get power and other properties
        Move selectedMove = null;
        for (Move move : JSONLoader.getInstance().getAllMoves()) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                selectedMove = move;
                break;
            }
        }

        // If move not found, create a basic move with default power
        if (selectedMove == null) {
            selectedMove = new Move().setName(moveName).setPower(50);
        }

        return Decision.move(selectedMove, reasoning, confidence);
    }

    /**
     * Parse switch decision from response
     */
    private static Decision parseSwitchDecision(String response, BattleDecisionState state,
                                                String reasoning, double confidence) {
        String pokemonName = extractPokemonName(response);
        if (pokemonName == null) {
            return null;
        }

        List<Pokemon> team = state.getAiPlayer().getTeam();
        if (team == null || team.isEmpty()) {
            return null;
        }

        // Find Pokemon by name (case-insensitive)
        Pokemon switchTarget = null;
        for (Pokemon p : team) {
            if (p.getName().equalsIgnoreCase(pokemonName.trim())) {
                switchTarget = p;
                break;
            }
        }

        if (switchTarget == null) {
            return null;
        }

        return Decision.switchPokemon(switchTarget, reasoning, confidence);
    }

    /**
     * Extract move index from response
     */
    public static int extractMoveIndex(String response) {
        if (response == null) {
            return -1;
        }

        // Try pattern matching first
        Matcher matcher = MOVE_PATTERN.matcher(response);
        if (matcher.find()) {
            try {
                int moveNum = Integer.parseInt(matcher.group(1));
                return moveNum - 1; // Convert to 0-indexed
            } catch (NumberFormatException e) {
                // Continue to fallback methods
            }
        }

        // Try to find "Move 1", "Move 2", etc.
        Pattern altPattern = Pattern.compile("(?:move|use)\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher altMatcher = altPattern.matcher(response);
        if (altMatcher.find()) {
            try {
                int moveNum = Integer.parseInt(altMatcher.group(1));
                return moveNum - 1;
            } catch (NumberFormatException e) {
                // Fall through
            }
        }

        return -1;
    }

    /**
     * Extract Pokemon name from response
     */
    public static String extractPokemonName(String response) {
        if (response == null) {
            return null;
        }

        Matcher matcher = SWITCH_PATTERN.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // Try alternative patterns
        Pattern altPattern = Pattern.compile("switch to\\s+([\\w\\s-]+?)(?:\\.|\\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher altMatcher = altPattern.matcher(response);
        if (altMatcher.find()) {
            return altMatcher.group(1).trim();
        }

        return null;
    }

    /**
     * Extract reasoning from response
     */
    public static String extractReasoning(String response) {
        if (response == null) {
            return "No reasoning provided";
        }

        Matcher matcher = REASONING_PATTERN.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // If no explicit reasoning section, use the whole response as reasoning
        // but limit length
        String cleaned = response.trim();
        if (cleaned.length() > 200) {
            cleaned = cleaned.substring(0, 200) + "...";
        }

        return cleaned;
    }
}
