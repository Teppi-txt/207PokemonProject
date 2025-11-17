package entities;

import java.io.Serializable;

public class Battle implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private User player1;
    private User player2;
    private String status;
    private User winner;

    public Battle(int id, User player1, User player2) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.status = "PENDING";
        this.winner = null;
    }

    public void startBattle(){
        if ("IN_PROGRESS".equals(status) || "COMPLETED".equals(status)) {
            return;
        }
        this.status = "IN_PROGRESS";
        this.winner = null;
    }

    public void endBattle(User winner){
        this.winner = winner;
        this.status = "COMPLETED";
    }

    public String getBattleStatus(){
        return this.status;
    }

    //added the getters and setters for the battle

    public User getPlayer1() {
        return player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public User getWinner() {
        return winner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }
}
