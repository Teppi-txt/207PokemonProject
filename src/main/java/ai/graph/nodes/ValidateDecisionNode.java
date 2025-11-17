package ai.graph.nodes;

import ai.fallback.RuleBasedDecisionMaker;
import ai.graph.BattleDecisionState;
import ai.graph.Decision;
import entities.Pokemon;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LangGraph4j node that validates decisions are legal and executable.
 * If invalid, invokes fallback decision maker.
 */
public class ValidateDecisionNode implements NodeAction<BattleDecisionState> {

    @Override
    public Map<String, Object> apply(BattleDecisionState state) throws Exception {
        Map<String, Object> updates = new HashMap<>();

        Decision decision = (Decision) state.getMetadata().get("currentDecision");
        if (decision == null) {
            decision = state.getCurrentDecision();
        }

        if (decision == null) {
            // No decision was made - use fallback
            boolean isSwitch = "switch".equals(state.getMetadata().get("decisionType"));
            Decision fallbackDecision = RuleBasedDecisionMaker.makeDecision(state, isSwitch);
            updates.put("currentDecision", fallbackDecision);
            updates.put("validationPassed", false);
            updates.put("validationError", "No decision was made");
            return updates;
        }

        // Validate based on decision type
        if (decision.isMove()) {
            boolean valid = validateMoveDecision(decision, state);
            updates.put("validationPassed", valid);

            if (!valid) {
                // Invalid move - use fallback
                Decision fallbackDecision = RuleBasedDecisionMaker.makeDecision(state, false);
                updates.put("currentDecision", fallbackDecision);
                updates.put("validationError", "Invalid move decision");
            }
        } else if (decision.isSwitch()) {
            boolean valid = validateSwitchDecision(decision, state);
            updates.put("validationPassed", valid);

            if (!valid) {
                // Invalid switch - use fallback
                Decision fallbackDecision = RuleBasedDecisionMaker.makeDecision(state, true);
                updates.put("currentDecision", fallbackDecision);
                updates.put("validationError", "Invalid switch decision");
            }
        }

        return updates;
    }

    /**
     * Validate move decision
     */
    private boolean validateMoveDecision(Decision decision, BattleDecisionState state) {
        if (decision.getSelectedMove() == null) {
            return false;
        }

        Pokemon activePokemon = state.getAiPlayer().getActivePokemon();
        if (activePokemon == null) {
            return false;
        }

        List<String> availableMoves = activePokemon.getMoves();
        if (availableMoves == null || availableMoves.isEmpty()) {
            return false;
        }

        // Check if the selected move is in the available moves
        String selectedMoveName = decision.getSelectedMove().getName();
        if (selectedMoveName == null) {
            return false;
        }

        for (String move : availableMoves) {
            if (move.equalsIgnoreCase(selectedMoveName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Validate switch decision
     */
    private boolean validateSwitchDecision(Decision decision, BattleDecisionState state) {
        if (decision.getSwitchTarget() == null) {
            return false;
        }

        Pokemon switchTarget = decision.getSwitchTarget();
        Pokemon currentActive = state.getAiPlayer().getActivePokemon();

        // Cannot switch to the currently active Pokemon
        if (switchTarget == currentActive) {
            return false;
        }

        // Cannot switch to a fainted Pokemon
        if (switchTarget.isFainted()) {
            return false;
        }

        // Check if Pokemon is in the team
        List<Pokemon> team = state.getAiPlayer().getTeam();
        if (team == null || !team.contains(switchTarget)) {
            return false;
        }

        return true;
    }
}
