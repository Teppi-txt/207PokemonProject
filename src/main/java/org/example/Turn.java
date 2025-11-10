package org.example;

public class Turn {
    private int id;
    private Player player;
    private Move move;
    private String result;
    private int damage;
    private String effectiveness;
    private String statusEffect;
    private int turnNumber;

    public Turn() {
    }

    public Turn(int id, Player player, Move move) {
        this.id = id;
        this.player = player;
        this.move = move;
        this.result = "";
        this.damage = 0;
        this.effectiveness = "normal";
        this.statusEffect = "";
        this.turnNumber = 0;
    }

    public void executeTurn() {
        // Logic to execute the turn would go here
        // This would process the move and update the result
        this.result = "Turn executed";
    }

    public String getTurnDetails() {
        return "Turn " + id + ": Player " + player.getName() +
               " used " + move.getName() + ". Result: " + result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }
}
