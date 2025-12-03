package interface_adapters.battle_player;

import entities.battle.Battle;
import entities.battle.Turn;


public class BattlePlayerState {

    private Turn turn;
    private Battle battle;
    private String turnResult;
    private boolean battleEnded;
    private String battleStatus;
    private String errorMessage; // if it is null there is no error

    public Turn getTurn() {
        return turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public String getTurnResult() {
        return turnResult;
    }

    public void setTurnResult(String turnResult) {
        this.turnResult = turnResult;
    }

    public boolean isBattleEnded() {
        return battleEnded;
    }

    public void setBattleEnded(boolean battleEnded) {
        this.battleEnded = battleEnded;
    }

    public String getBattleStatus() {
        return battleStatus;
    }

    public void setBattleStatus(String battleStatus) {
        this.battleStatus = battleStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
