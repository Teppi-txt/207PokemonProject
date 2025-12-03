package interface_adapters.battle_player;

import use_case.battle_player.BattlePlayerOutputBoundary;
import use_case.battle_player.BattlePlayerOutputData;

/**
 * The presenter for the Battle Player use case.
 */

public class BattlePlayerPresenter implements BattlePlayerOutputBoundary {
    private final BattlePlayerViewModel viewModel;

    public BattlePlayerPresenter(BattlePlayerViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BattlePlayerOutputData outputData) {
        final BattlePlayerState battlePlayerState = new BattlePlayerState();
        
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
        final BattlePlayerState battlePlayerState = new BattlePlayerState();
        battlePlayerState.setErrorMessage(errorMessage);
        viewModel.setState(battlePlayerState);
    }
}
