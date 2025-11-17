package ai.graph.nodes;

import ai.graph.BattleDecisionState;
import entities.Pokemon;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.HashMap;
import java.util.Map;

/**
 * LangGraph4j node that determines what type of decision is needed.
 * Decides between forced switch (Pokemon fainted) or tactical decision (move or strategic switch).
 */
public class EvaluateOptionsNode implements NodeAction<BattleDecisionState> {

    @Override
    public Map<String, Object> apply(BattleDecisionState state) throws Exception {
        Map<String, Object> updates = new HashMap<>();

        Pokemon activePokemon = state.getAiPlayer().getActivePokemon();

        // Check if forced to switch (Pokemon fainted or no moves available)
        boolean forcedSwitch = false;
        String reason = "";

        if (activePokemon == null) {
            forcedSwitch = true;
            reason = "No active Pokemon";
        } else if (activePokemon.isFainted()) {
            forcedSwitch = true;
            reason = "Active Pokemon fainted";
        } else if (activePokemon.getMoves() == null || activePokemon.getMoves().isEmpty()) {
            forcedSwitch = true;
            reason = "No moves available";
        }

        updates.put("forcedSwitch", forcedSwitch);
        updates.put("switchReason", reason);

        // Determine decision type for routing
        if (forcedSwitch) {
            updates.put("decisionType", "switch");
        } else {
            // For now, default to move decision
            // In future, could add logic for strategic switching
            updates.put("decisionType", "move");
        }

        updates.put("evaluationCompleted", true);

        return updates;
    }
}
