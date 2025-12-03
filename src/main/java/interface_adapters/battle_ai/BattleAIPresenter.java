package interface_adapters.battle_ai;

import java.util.ArrayList;
import java.util.List;

import entities.battle.Battle;
import entities.Pokemon;
import use_case.battle_ai.BattleAIOutputBoundary;
import use_case.battle_ai.BattleAIOutputData;

/**
 * Presenter for Battle AI use case.
 * Transforms use case output data into view model format.
 */

public class BattleAIPresenter implements BattleAIOutputBoundary {

    private final BattleAIViewModel viewModel;

    public BattleAIPresenter(BattleAIViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(BattleAIOutputData outputData) {
        Battle battle = outputData.getBattle();

        // Update battle state
        if (battle != null) {
            viewModel.setBattleStatus(battle.getBattleStatus());
            viewModel.setPlayer1Name(battle.getPlayer1().getName());
            viewModel.setPlayer2Name(battle.getPlayer2().getName());

            // Update teams from battle
            updateTeamFromBattle(battle);
        }

        // Update turn description
        viewModel.setCurrentTurnDescription(outputData.getTurnResult());
        viewModel.setBattleEnded(outputData.isBattleEnded());

        // Update switch notifications
        if (outputData.getPlayerSwitchedTo() != null) {
            viewModel.setPlayerSwitchedToName(outputData.getPlayerSwitchedTo().getName());
        }
        else {
            viewModel.setPlayerSwitchedToName(null);
        }
        if (outputData.getAiSwitchedTo() != null) {
            viewModel.setAiSwitchedToName(outputData.getAiSwitchedTo().getName());
        }
        else {
            viewModel.setAiSwitchedToName(null);
        }

        // If battle ended, set winner
        if (outputData.isBattleEnded() && battle != null && battle.getWinner() != null) {
            viewModel.setWinnerName(battle.getWinner().getName());
            int currency = battle.getWinner().getName().equals(viewModel.getPlayer1Name()) ? 500 : 100;
            viewModel.setCurrencyAwarded(currency);
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }

    private void updateTeamFromBattle(Battle battle) {
        // Update player 1 team
        if (battle.getPlayer1() != null && battle.getPlayer1().getOwnedPokemon() != null) {
            List<BattleAIViewModel.PokemonViewModel> player1ViewModels = new ArrayList<>();
            for (Pokemon pokemon : battle.getPlayer1().getOwnedPokemon()) {
                player1ViewModels.add(createPokemonViewModel(pokemon));
            }
            viewModel.setPlayer1Team(player1ViewModels);

            // Set active Pokemon
            for (Pokemon pokemon : battle.getPlayer1().getOwnedPokemon()) {
                if (!pokemon.isFainted()) {
                    viewModel.setPlayer1Active(createPokemonViewModel(pokemon));
                    break;
                }
            }
        }

        // Update player 2 team
        if (battle.getPlayer2() != null && battle.getPlayer2().getOwnedPokemon() != null) {
            List<BattleAIViewModel.PokemonViewModel> player2ViewModels = new ArrayList<>();
            for (Pokemon pokemon : battle.getPlayer2().getOwnedPokemon()) {
                player2ViewModels.add(createPokemonViewModel(pokemon));
            }
            viewModel.setPlayer2Team(player2ViewModels);

            // Set active Pokemon
            for (Pokemon pokemon : battle.getPlayer2().getOwnedPokemon()) {
                if (!pokemon.isFainted()) {
                    viewModel.setPlayer2Active(createPokemonViewModel(pokemon));
                    break;
                }
            }
        }
    }

    /**
     * Creates a PokemonViewModel from a Pokemon entity.
     * This method extracts primitive data from the entity for the view model.
     * @param pokemon get the view of the Pokemon for the one Pokemon in battle.
     * @return BattleAIViewModel for view.
     */
    private BattleAIViewModel.PokemonViewModel createPokemonViewModel(Pokemon pokemon) {
        return new BattleAIViewModel.PokemonViewModel(
                pokemon.getName(),
                pokemon.getId(),
                pokemon.getStats().getHp(),
                pokemon.getStats().getMaxHp(),
                new ArrayList<>(pokemon.getMoves()),
                new ArrayList<>(pokemon.getTypes()),
                pokemon.isFainted(),
                pokemon.getSpriteUrl(),
                pokemon.getFrontGIF(),
                pokemon.getBackGIF()
        );
    }
}
