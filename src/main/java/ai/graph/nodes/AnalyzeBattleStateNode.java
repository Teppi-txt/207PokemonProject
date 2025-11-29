/*package ai.graph.nodes;

import ai.graph.BattleDecisionState;
import entities.Pokemon;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LangGraph4j node that analyzes the battle state and enriches context.
 * Calculates type effectiveness, stat comparisons, and available options.
 *//*
public class AnalyzeBattleStateNode implements NodeAction<BattleDecisionState> {

    @Override
    public Map<String, Object> apply(BattleDecisionState state) throws Exception {
        Map<String, Object> updates = new HashMap<>();

        // Analyze Pokemon status
        Pokemon aiPokemon = state.getAiPlayer().getActivePokemon();
        Pokemon opponentPokemon = state.getOpponent().getActivePokemon();

        if (aiPokemon != null && opponentPokemon != null) {
            // Calculate type advantages
            String typeAdvantages = analyzeTypeMatchup(aiPokemon, opponentPokemon);
            updates.put("typeAdvantages", typeAdvantages);

            // Calculate stat comparison
            String statComparison = compareStats(aiPokemon, opponentPokemon);
            updates.put("statComparison", statComparison);

            // Analyze HP percentages
            String hpAnalysis = analyzeHP(aiPokemon, opponentPokemon);
            updates.put("hpAnalysis", hpAnalysis);
        }

        // Identify available switch options
        List<Pokemon> team = state.getAiPlayer().getTeam();
        if (team != null) {
            long availablePokemon = team.stream()
                    .filter(p -> !p.isFainted() && p != aiPokemon)
                    .count();
            updates.put("availableSwitchOptions", (int) availablePokemon);
        }

        // Add analysis timestamp
        updates.put("analysisCompleted", true);

        return updates;
    }

    /**
     * Analyze type matchup between Pokemon
     *//*
    private String analyzeTypeMatchup(Pokemon aiPokemon, Pokemon opponentPokemon) {
        StringBuilder analysis = new StringBuilder();

        List<String> aiTypes = aiPokemon.getTypes();
        List<String> opponentTypes = opponentPokemon.getTypes();

        if (aiTypes != null && opponentTypes != null) {
            analysis.append("  Type Matchup: ");
            analysis.append("Your ").append(String.join("/", aiTypes));
            analysis.append(" vs Opponent's ").append(String.join("/", opponentTypes));

            // Simplified type effectiveness analysis
            // In a full implementation, would use a type chart
            if (hasTypeAdvantage(aiTypes, opponentTypes)) {
                analysis.append(" - You have type advantage!");
            } else if (hasTypeAdvantage(opponentTypes, aiTypes)) {
                analysis.append(" - Opponent has type advantage!");
            } else {
                analysis.append(" - Neutral matchup");
            }
        }

        return analysis.toString();
    }

    /**
     * Simple type advantage check (placeholder for full type chart)
     *//*
    private boolean hasTypeAdvantage(List<String> attackerTypes, List<String> defenderTypes) {
        // Simplified type effectiveness - would use full type chart in production
        // This is a placeholder that returns false to avoid incorrect assumptions
        for (String atkType : attackerTypes) {
            for (String defType : defenderTypes) {
                // Example: Water beats Fire, Fire beats Grass, Grass beats Water
                if (atkType.equalsIgnoreCase("water") && defType.equalsIgnoreCase("fire")) return true;
                if (atkType.equalsIgnoreCase("fire") && defType.equalsIgnoreCase("grass")) return true;
                if (atkType.equalsIgnoreCase("grass") && defType.equalsIgnoreCase("water")) return true;
                if (atkType.equalsIgnoreCase("electric") && defType.equalsIgnoreCase("water")) return true;
                // Add more type matchups as needed
            }
        }
        return false;
    }

    /**
     * Compare stats between Pokemon
     *//*
    private String compareStats(Pokemon aiPokemon, Pokemon opponentPokemon) {
        if (aiPokemon.getStats() == null || opponentPokemon.getStats() == null) {
            return "  Stats: Unable to compare";
        }

        StringBuilder comparison = new StringBuilder();
        comparison.append("  Stat Comparison: ");

        int aiSpeed = aiPokemon.getStats().getSpeed();
        int oppSpeed = opponentPokemon.getStats().getSpeed();

        if (aiSpeed > oppSpeed) {
            comparison.append("You are faster. ");
        } else if (aiSpeed < oppSpeed) {
            comparison.append("Opponent is faster. ");
        } else {
            comparison.append("Equal speed. ");
        }

        int aiAttack = aiPokemon.getStats().getAttack();
        int oppDefense = opponentPokemon.getStats().getDefense();

        if (aiAttack > oppDefense * 1.5) {
            comparison.append("Strong attack advantage.");
        } else if (aiAttack < oppDefense * 0.7) {
            comparison.append("Opponent has strong defense.");
        }

        return comparison.toString();
    }

    /**
     * Analyze HP status
     *//*
    private String analyzeHP(Pokemon aiPokemon, Pokemon opponentPokemon) {
        if (aiPokemon.getStats() == null || opponentPokemon.getStats() == null) {
            return "  HP: Unable to analyze";
        }

        StringBuilder analysis = new StringBuilder();
        analysis.append("  HP Status: ");

        int aiHP = aiPokemon.getStats().getHp();
        int oppHP = opponentPokemon.getStats().getHp();

        if (aiHP < 30) {
            analysis.append("Your Pokemon is low on HP! ");
        } else if (aiHP < 60) {
            analysis.append("Your Pokemon at moderate HP. ");
        }

        if (oppHP < 30) {
            analysis.append("Opponent is low on HP - good KO opportunity!");
        } else if (oppHP < 60) {
            analysis.append("Opponent at moderate HP.");
        }

        return analysis.toString();
    }
}*/
