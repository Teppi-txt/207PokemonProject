package interface_adapters.battle_player;

import use_case.battle_player.BattlePlayerOutputBoundary;
import use_case.battle_player.BattlePlayerOutputData;

public class BattlePlayerPresenter implements BattlePlayerOutputBoundary {
    private final BattlePlayerViewModel viewModel;

    public BattlePlayerPresenter(BattlePlayerViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BattlePlayerOutputData outputData) {
        BattlePlayerState battlePlayerState = new BattlePlayerState();
        
        battlePlayerState.setTurn(outputData.getTurn());
        battlePlayerState.setBattle(outputData.getBattle());
        battlePlayerState.setTurnResult(outputData.getTurnResult());
        battlePlayerState.setBattleEnded(outputData.isBattleEnded());
        battlePlayerState.setBattleStatus(outputData.getBattleStatus());
        battlePlayerState.setErrorMessage(null);

        viewModel.setState(battlePlayerState);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        BattlePlayerState battlePlayerState = new BattlePlayerState();
        battlePlayerState.setErrorMessage(errorMessage);
        viewModel.setState(battlePlayerState);
    }
}
