package interface_adapters;

import entities.battle.Move;

/**
 * Builder class for the Move entity.
 */

public class MoveBuilder {

    private final Move move;

    public MoveBuilder() {
        this.move = new Move();
    }

    public MoveBuilder addName(String name) {
        move.setName(name);
        return this;
    }

    public MoveBuilder addType(String type) {
        move.setType(type);
        return this;
    }

    public MoveBuilder addDamageClass(String damageClass) {
        move.setDamageClass(damageClass);
        return this;
    }

    public MoveBuilder addPower(Integer power) {
        move.setPower(power);
        return this;
    }

    public MoveBuilder addAccuracy(Integer accuracy) {
        move.setAccuracy(accuracy);
        return this;
    }

    public MoveBuilder addPriority(Integer priority) {
        move.setPriority(priority);
        return this;
    }

    public MoveBuilder addEffect(String effect) {
        move.setEffect(effect);
        return this;
    }

    public Move build() {
        return move;
    }
}