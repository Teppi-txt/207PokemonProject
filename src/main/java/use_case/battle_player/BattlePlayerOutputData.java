package use_case.battle_player;

import entities.battle.Battle;
import entities.battle.Turn;

public class BattlePlayerOutputData {
    private final Turn turn;
    private final Battle battle;
    private final String turnResult;
    private final boolean battleEnded;
    private final String battleStatus;

    public BattlePlayerOutputData(
            Turn turn, Battle battle, String turnResult, boolean battleEnded, String battleStatus) {
        this.turn = turn;
        this.battle = battle;
        this.turnResult = turnResult;
        this.battleEnded = battleEnded;
        this.battleStatus = battleStatus;
    }

    public Turn getTurn() {
        return turn;
    }

    public Battle getBattle() {
        return battle;
    }

    public String getTurnResult() {
        return turnResult;
    }

    public boolean isBattleEnded() {
        return battleEnded;
    }

    public String getBattleStatus() {
        return battleStatus;
    }
}

