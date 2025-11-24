package use_case.battle_player;

import entities.Battle;
import entities.Pokemon;
import entities.User;
import entities.MoveTurn;
import entities.Player;
import entities.SwitchTurn;
import entities.Turn;

// interactor implementing the battle player use case

public class BattlePlayerInteractor implements BattlePlayerInputBoundary {

    private final BattlePlayerUserDataAccessInterface battleDataAccess;
    private final BattlePlayerOutputBoundary battlePlayerPresenter;

    public BattlePlayerInteractor(BattlePlayerUserDataAccessInterface battleDataAccess,
                                  BattlePlayerOutputBoundary battlePlayerPresenter) {
        this.battleDataAccess = battleDataAccess;
        this.battlePlayerPresenter = battlePlayerPresenter;
    }

    @Override
    public void execute(BattlePlayerInputData inputData) {
        final Battle battle = battleDataAccess.getBattle();
        if (battle == null) {
            battlePlayerPresenter.prepareFailView("battle not found");
            return;
        }

        if ("COMPLETED".equals(battle.getBattleStatus())) {
            battlePlayerPresenter.prepareFailView("battle is already completed");
            return;
        }

        if (!"IN_PROGRESS".equals(battle.getBattleStatus())) {
            battlePlayerPresenter.prepareFailView("battle is not in progress");
            return;
        }

        final Turn turn = inputData.getTurn();
        if (turn == null) {
            battlePlayerPresenter.prepareFailView("turn is invalid");
            return;
        }

        turn.executeTurn();
        String turnResult = turn.getResult();

        boolean battleEnded = false;
        User winner = null;

        User player1 = battle.getPlayer1();
        User player2 = battle.getPlayer2();

        boolean player1HasPokemon = hasAvailablePokemon(player1);
        boolean player2HasPokemon = hasAvailablePokemon(player2);

        if (!player1HasPokemon && !player2HasPokemon) {
            battleEnded = true;
            winner = null; // draw
        } else if (!player1HasPokemon) {
            battleEnded = true;
            winner = player2;
        } else if (!player2HasPokemon) {
            battleEnded = true;
            winner = player1;
        }

        if (battleEnded) {
            battle.endBattle(winner);
            if (winner != null) {
                winner.addCurrency(500);
                battleDataAccess.saveUser(winner);

                User loser = battle.getPlayer1().equals(winner) ? battle.getPlayer2() : battle.getPlayer1();
                loser.addCurrency(100);
                battleDataAccess.saveUser(loser);
            }
        }

        battleDataAccess.saveBattle(battle);

        //send it back
        BattlePlayerOutputData outputData = new BattlePlayerOutputData(
                turn,
                battle,
                turnResult,
                battleEnded,
                battle.getBattleStatus()
        );

        battlePlayerPresenter.prepareSuccessView(outputData);
    }

    // helper to see if a user can still fight
    private boolean hasAvailablePokemon(User user) {
        if (user == null || user.getOwnedPokemon() == null || user.getOwnedPokemon().isEmpty()) {
            return false;
        }
        for (Pokemon pokemon : user.getOwnedPokemon()) {
            if (!pokemon.isFainted()) {
                return true;
            }
        }
        return false;
    }
}
