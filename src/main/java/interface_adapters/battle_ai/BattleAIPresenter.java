package interface_adapters.battle_ai;

import entities.AIPlayer;
import entities.Battle;
import entities.Pokemon;
import entities.User;
import use_case.battle_ai.BattleAIOutputBoundary;
import use_case.battle_ai.BattleAIOutputData;
import use_case.battle_player.BattlePlayerOutputBoundary;
import use_case.battle_player.BattlePlayerOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Battle AI use cases.
 * Transforms use case output data into view model format.
 * Implements both AI and Player output boundaries.
 */
public class BattleAIPresenter implements BattleAIOutputBoundary, BattlePlayerOutputBoundary {

    private final BattleAIViewModel viewModel;

    public BattleAIPresenter(BattleAIViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BattleAIOutputData outputData) {
        // Update battle state from AI turn
        updateViewModelFromBattle(outputData.getBattle());

        // Update turn description
        viewModel.setCurrentTurnDescription(outputData.getTurnResult());

        // Update battle status
        viewModel.setBattleStatus(outputData.getBattleStatus());
        viewModel.setBattleEnded(outputData.isBattleEnded());

        // If battle ended, set winner and currency
        if (outputData.isBattleEnded()) {
            Battle battle = outputData.getBattle();
            if (battle.getWinner() != null) {
                viewModel.setWinnerName(battle.getWinner().getName());
                // Award currency: winner gets 500, loser gets 100
                int currency = battle.getWinner().getName().equals(viewModel.getPlayer1Name()) ? 500 : 100;
                viewModel.setCurrencyAwarded(currency);
            }
        }
    }

    @Override
    public void prepareSuccessView(BattlePlayerOutputData outputData) {
        // Update battle state from player turn
        updateViewModelFromBattle(outputData.getBattle());

        // Update turn description
        viewModel.setCurrentTurnDescription(outputData.getTurnResult());

        // Update battle status
        viewModel.setBattleStatus(outputData.getBattleStatus());
        viewModel.setBattleEnded(outputData.isBattleEnded());

        // If battle ended, set winner and currency
        if (outputData.isBattleEnded()) {
            Battle battle = outputData.getBattle();
            if (battle.getWinner() != null) {
                viewModel.setWinnerName(battle.getWinner().getName());
                // Award currency: winner gets 500, loser gets 100
                int currency = battle.getWinner().getName().equals(viewModel.getPlayer1Name()) ? 500 : 100;
                viewModel.setCurrencyAwarded(currency);
            }
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }

    /**
     * Updates the view model with current battle state.
     * Note: This method updates basic battle info. Pokemon team updates
     * are handled separately via initializeBattle and updateTeams methods
     * since Battle entity stores Users, not the AIPlayer with team info.
     */
    private void updateViewModelFromBattle(Battle battle) {
        if (battle == null) {
            return;
        }

        // Update player names
        viewModel.setPlayer1Name(battle.getPlayer1().getName());
        viewModel.setPlayer2Name(battle.getPlayer2().getName());

        // Update battle status
        viewModel.setBattleStatus(battle.getBattleStatus());
    }

    /**
     * Updates team information in the view model.
     * Call this after turn execution to refresh Pokemon HP and status.
     */
    public void updateTeams(List<Pokemon> player1Team, AIPlayer aiPlayer) {
        // Update player 1 team
        if (player1Team != null) {
            List<BattleAIViewModel.PokemonViewModel> player1ViewModels = new ArrayList<>();
            for (Pokemon pokemon : player1Team) {
                player1ViewModels.add(new BattleAIViewModel.PokemonViewModel(pokemon));
            }
            viewModel.setPlayer1Team(player1ViewModels);

            // Update active Pokemon (first non-fainted)
            for (Pokemon pokemon : player1Team) {
                if (!pokemon.isFainted()) {
                    viewModel.setPlayer1Active(new BattleAIViewModel.PokemonViewModel(pokemon));
                    break;
                }
            }
        }

        // Update player 2 (AI) team
        if (aiPlayer != null) {
            List<BattleAIViewModel.PokemonViewModel> player2ViewModels = new ArrayList<>();
            for (Pokemon pokemon : aiPlayer.getTeam()) {
                player2ViewModels.add(new BattleAIViewModel.PokemonViewModel(pokemon));
            }
            viewModel.setPlayer2Team(player2ViewModels);

            // Update player 2 active Pokemon
            if (aiPlayer.getActivePokemon() != null) {
                viewModel.setPlayer2Active(new BattleAIViewModel.PokemonViewModel(aiPlayer.getActivePokemon()));
            }
        }
    }

    /**
     * Initializes the view model with battle data at the start.
     */
    public void initializeBattle(Battle battle, List<Pokemon> player1Team, AIPlayer aiPlayer) {
        viewModel.setBattleStatus("IN_PROGRESS");
        viewModel.setPlayer1Name(battle.getPlayer1().getName());
        viewModel.setPlayer2Name(aiPlayer.getName());

        // Set player 1 team
        List<BattleAIViewModel.PokemonViewModel> player1ViewModels = new ArrayList<>();
        for (Pokemon pokemon : player1Team) {
            player1ViewModels.add(new BattleAIViewModel.PokemonViewModel(pokemon));
        }
        viewModel.setPlayer1Team(player1ViewModels);
        if (!player1Team.isEmpty()) {
            viewModel.setPlayer1Active(new BattleAIViewModel.PokemonViewModel(player1Team.get(0)));
        }

        // Set player 2 (AI) team
        List<BattleAIViewModel.PokemonViewModel> player2ViewModels = new ArrayList<>();
        for (Pokemon pokemon : aiPlayer.getTeam()) {
            player2ViewModels.add(new BattleAIViewModel.PokemonViewModel(pokemon));
        }
        viewModel.setPlayer2Team(player2ViewModels);
        if (aiPlayer.getActivePokemon() != null) {
            viewModel.setPlayer2Active(new BattleAIViewModel.PokemonViewModel(aiPlayer.getActivePokemon()));
        }

        viewModel.setTurnNumber(1);
        viewModel.setCurrentTurnDescription("Battle started!");
    }
}
