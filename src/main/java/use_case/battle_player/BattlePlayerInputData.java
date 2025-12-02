package use_case.battle_player;

import entities.battle.Turn;

public class BattlePlayerInputData {
    private final Turn turn;

    public BattlePlayerInputData(Turn turn) {
        this.turn = turn;
    }

    public Turn getTurn() {
        return turn;
    }
}

