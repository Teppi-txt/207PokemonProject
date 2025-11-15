package entities;

public class Battle {
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
}
