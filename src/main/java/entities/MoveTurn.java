/*
package entities;

public class MoveTurn extends Turn {
    private Move move;
    private int damage;
    private String effectiveness;
    private String statusEffect;

    public MoveTurn() {
        super();
    }

    public MoveTurn(int id, Player player, int turnNumber, Move move) {
        super(id, player, turnNumber);
        this.move = move;
        this.damage = 0;
        this.effectiveness = "normal";
        this.statusEffect = "";
    }

    @Override
    public void executeTurn() {
        // Logic to execute the move would go here
        // Calculate damage, effectiveness, apply status effects
        this.result = "Move executed: " + move.getName();
    }

    @Override
    public String getTurnDetails() {
        return "Turn " + turnNumber + " (ID: " + id + "): Player " + player.getName() +
               " used " + move.getName() + ". Damage: " + damage +
               ", Effectiveness: " + effectiveness + ". Result: " + result;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(String effectiveness) {
        this.effectiveness = effectiveness;
    }

    public String getStatusEffect() {
        return statusEffect;
    }

    public void setStatusEffect(String statusEffect) {
        this.statusEffect = statusEffect;
    }
}
*/