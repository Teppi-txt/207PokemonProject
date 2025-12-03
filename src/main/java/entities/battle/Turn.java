package entities.battle;

import entities.Player;

import java.io.Serializable;

/**
 * Entity representing a specific turn of a pokemon battle.
 */

public abstract class Turn implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int id;
    protected Player player;
    protected int turnNumber;
    protected String result;

    public Turn() {
    }

    public Turn(int id, Player player, int turnNumber) {
        this.id = id;
        this.player = player;
        this.turnNumber = turnNumber;
        this.result = "";
    }

    public abstract void executeTurn();

    public abstract String getTurnDetails();

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

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
