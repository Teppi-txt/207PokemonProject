package use_case.battle_player;

import entities.Pokemon;
import entities.battle.Battle;
import entities.battle.Turn;
import entities.user.User;

// interactor implementing the battle player use case

public class BattlePlayerInteractor implements BattlePlayerInputBoundary {

    private static final int LOSS_REWARD = 100;
    private static final int WIN_REWARD = 500;
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

        // executes the turn
        turn.executeTurn();
        final String turnResult = turn.getResult();

        boolean battleEnded = false;
        User winner = null;

        // creates two Users to represent the two players
        final User player1 = battle.getPlayer1();
        final User player2 = battle.getPlayer2();

        final boolean player1HasPokemon = hasAvailablePokemon(player1);
        final boolean player2HasPokemon = hasAvailablePokemon(player2);

        // checks if either player has no available pokemon left
        if (!player1HasPokemon && !player2HasPokemon) {
            battleEnded = true;
            winner = null;
            // draw
        }
        else if (!player1HasPokemon) {
            battleEnded = true;
            winner = player2;
        }
        else if (!player2HasPokemon) {
            battleEnded = true;
            winner = player1;
        }

        // ends the battle if it has ended
        if (battleEnded) {
            battle.endBattle(winner);
            if (winner != null) {
                winner.addCurrency(WIN_REWARD);
                battleDataAccess.saveUser(winner);

                final User loser = battle.getPlayer1().equals(winner) ? battle.getPlayer2() : battle.getPlayer1();
                loser.addCurrency(LOSS_REWARD);
                battleDataAccess.saveUser(loser);
            }
        }

        battleDataAccess.saveBattle(battle);

        // prepares the output data
        final BattlePlayerOutputData outputData = new BattlePlayerOutputData(
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
