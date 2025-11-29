/*
package ai.serializers;

import ai.config.DifficultyConfig;
import ai.graph.BattleDecisionState;
import entities.Pokemon;
import entities.Turn;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts battle state into LLM-friendly text format.
 */
/*
public class BattleContextSerializer {

    /**
     * Serialize battle state for LLM consumption
     *//*
    public static String serializeForLLM(BattleDecisionState state) {
        StringBuilder context = new StringBuilder();

        // Battle header
        context.append("=== BATTLE SITUATION ===\n\n");

        // Your active Pokemon
        Pokemon aiPokemon = state.getAiPlayer().getActivePokemon();
        if (aiPokemon != null) {
            context.append("YOUR ACTIVE POKEMON:\n");
            context.append(formatPokemonInfo(aiPokemon, true));
            context.append("\n");
        }

        // Opponent's active Pokemon
        Pokemon opponentPokemon = state.getOpponent().getActivePokemon();
        if (opponentPokemon != null) {
            context.append("OPPONENT'S ACTIVE POKEMON:\n");
            context.append(formatPokemonInfo(opponentPokemon, false));
            context.append("\n");
        }

        // Your team (bench)
        List<Pokemon> team = state.getAiPlayer().getTeam();
        if (team != null && !team.isEmpty()) {
            context.append("YOUR TEAM:\n");
            List<Pokemon> benchPokemon = team.stream()
                    .filter(p -> p != aiPokemon)
                    .collect(Collectors.toList());

            if (!benchPokemon.isEmpty()) {
                for (Pokemon p : benchPokemon) {
                    context.append("  - ").append(formatPokemonSummary(p)).append("\n");
                }
            } else {
                context.append("  (No other Pokemon available)\n");
            }
            context.append("\n");
        }

        // Turn history (if configured)
        DifficultyConfig config = DifficultyConfig.forLevel(state.getDifficulty());
        int historyLimit = config.getTurnHistoryLimit();
        if (historyLimit > 0 && state.getTurnHistory() != null && !state.getTurnHistory().isEmpty()) {
            context.append("RECENT BATTLE HISTORY:\n");
            context.append(formatTurnHistory(state.getTurnHistory(), historyLimit));
            context.append("\n");
        }

        // Analysis metadata (if available)
        if (state.getMetadata().containsKey("typeAdvantages")) {
            context.append("ANALYSIS:\n");
            context.append(state.getMetadata().get("typeAdvantages")).append("\n");
        }

        return context.toString();
    }

    /**
     * Format detailed Pokemon information with moves
     *//*
    private static String formatPokemonInfo(Pokemon pokemon, boolean includeMoves) {
        StringBuilder info = new StringBuilder();

        // Basic info
        info.append(String.format("  %s", pokemon.getName()));

        // Types
        if (pokemon.getTypes() != null && !pokemon.getTypes().isEmpty()) {
            info.append(" (").append(String.join(", ", pokemon.getTypes())).append(")");
        }

        // Stats
        if (pokemon.getStats() != null) {
            info.append(String.format("\n  HP: %d | ", pokemon.getStats().getHp()));
            info.append(String.format("ATK: %d | DEF: %d | ",
                    pokemon.getStats().getAttack(),
                    pokemon.getStats().getDefense()));
            info.append(String.format("SP.ATK: %d | SP.DEF: %d | SPD: %d",
                    pokemon.getStats().getSpAttack(),
                    pokemon.getStats().getSpDefense(),
                    pokemon.getStats().getSpeed()));
        }

        // Moves (only for AI's Pokemon)
        if (includeMoves && pokemon.getMoves() != null && !pokemon.getMoves().isEmpty()) {
            info.append("\n  AVAILABLE MOVES:\n");
            List<String> moves = pokemon.getMoves();
            for (int i = 0; i < moves.size(); i++) {
                info.append(String.format("    %d. %s\n", i + 1, moves.get(i)));
            }
        }

        return info.toString();
    }

    /**
     * Format Pokemon summary (for bench display)
     *//*q
    private static String formatPokemonSummary(Pokemon pokemon) {
        StringBuilder summary = new StringBuilder();
        summary.append(pokemon.getName());

        if (pokemon.getTypes() != null && !pokemon.getTypes().isEmpty()) {
            summary.append(" (").append(String.join(", ", pokemon.getTypes())).append(")");
        }

        if (pokemon.getStats() != null) {
            summary.append(String.format(" - HP: %d", pokemon.getStats().getHp()));
        }

        if (pokemon.isFainted()) {
            summary.append(" [FAINTED]");
        }

        return summary.toString();
    }

    /**
     * Format turn history
     *//*
    private static String formatTurnHistory(List<Turn> history, int limit) {
        if (history == null || history.isEmpty()) {
            return "  No recent turns\n";
        }

        StringBuilder formatted = new StringBuilder();
        int start = Math.max(0, history.size() - limit);
        List<Turn> recentTurns = history.subList(start, history.size());

        for (Turn turn : recentTurns) {
            formatted.append("  - ").append(turn.getTurnDetails()).append("\n");
        }

        return formatted.toString();
    }

    /**
     * Format move information (for detailed analysis)
     *//*
    public static String formatMoveInfo(String moveName) {
        // Simplified for now - would query move database in full implementation
        return String.format("Move: %s", moveName);
    }
}
*/
