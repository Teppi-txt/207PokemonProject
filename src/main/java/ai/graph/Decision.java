package ai.graph;

import entities.battle.Move;
import entities.Pokemon;
import java.io.Serializable;

/**
 * Represents an AI's chosen action in battle.
 * Can be either a move decision or a switch decision.
 */
public class Decision implements Serializable {
    private static final long serialVersionUID = 1L;
    private final DecisionType type;
    private final Move selectedMove;
    private final Pokemon switchTarget;
    private final String reasoning;
    private final double confidence;

    public enum DecisionType {
        MOVE,
        SWITCH
    }

    private Decision(DecisionType type, Move selectedMove, Pokemon switchTarget,
                     String reasoning, double confidence) {
        this.type = type;
        this.selectedMove = selectedMove;
        this.switchTarget = switchTarget;
        this.reasoning = reasoning;
        this.confidence = confidence;
    }

    /**
     * Creates a move decision.
     */
    public static Decision move(Move move, String reasoning, double confidence) {
        return new Decision(DecisionType.MOVE, move, null, reasoning, confidence);
    }

    /**
     * Creates a switch decision.
     */
    public static Decision switchPokemon(Pokemon pokemon, String reasoning, double confidence) {
        return new Decision(DecisionType.SWITCH, null, pokemon, reasoning, confidence);
    }

    public DecisionType getType() {
        return type;
    }

    public Move getSelectedMove() {
        return selectedMove;
    }

    public Pokemon getSwitchTarget() {
        return switchTarget;
    }

    public String getReasoning() {
        return reasoning;
    }

    public double getConfidence() {
        return confidence;
    }

    public boolean isMove() {
        return type == DecisionType.MOVE;
    }

    public boolean isSwitch() {
        return type == DecisionType.SWITCH;
    }

    @Override
    public String toString() {
        if (isMove()) {
            return String.format("Decision{type=MOVE, move=%s, reasoning=%s, confidence=%.2f}",
                    selectedMove != null ? selectedMove.getName() : "null",
                    reasoning, confidence);
        } else {
            return String.format("Decision{type=SWITCH, pokemon=%s, reasoning=%s, confidence=%.2f}",
                    switchTarget != null ? switchTarget.getName() : "null",
                    reasoning, confidence);
        }
    }
}
