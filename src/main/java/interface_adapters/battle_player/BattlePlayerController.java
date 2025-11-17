package interface_adapters.battle_player;
import entities.Turn;
import use_case.battle_player.BattlePlayerInputData;
import use_case.battle_player.BattlePlayerInputBoundary;
import use_case.open_pack.OpenPackInputBoundary;

public class BattlePlayerController {

    private final BattlePlayerInputBoundary battlePlayerUseCaseInteractor;

    public BattlePlayerController(BattlePlayerInputBoundary battlePlayerUseCaseInteractor) {
        this.battlePlayerUseCaseInteractor = battlePlayerUseCaseInteractor;

    }
    public void battle(Turn turn) {
        BattlePlayerInputData battlePlayerInputData = new BattlePlayerInputData(turn);
        battlePlayerUseCaseInteractor.execute(battlePlayerInputData);
    }
}
