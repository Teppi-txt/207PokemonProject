/*package use_case.battle_player;

import entities.Battle;
import entities.Pokemon;
import entities.User;
import entities.MoveTurn;
import entities.Player;
import entities.SwitchTurn;
import entities.Turn;

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

        // someone already won
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

        // this is to see who won, i am not sure if this is correct. I dont think i checked all the cases, please review
        boolean battleEnded = false;
        User winner = null;
        Player currentPlayer = turn.getPlayer();
        
        User currentUser = null;
        User opponentUser = null;
        if (battle.getPlayer1().getName().equals(currentPlayer.getName())) {
            currentUser = battle.getPlayer1();
            opponentUser = battle.getPlayer2();
        } else if (battle.getPlayer2().getName().equals(currentPlayer.getName())) {
            currentUser = battle.getPlayer2();
            opponentUser = battle.getPlayer1();
        } else {
            battlePlayerPresenter.prepareFailView("player not in this battle");
            return;
        }

        // check both players' teams to see if either is completely defeated
        // after a move, check if either player's active pokemon fainted
        boolean currentPlayerDefeated = false;
        boolean opponentDefeated = false;

        if (turn instanceof MoveTurn) {
            if (currentPlayer.getActivePokemon() != null && currentPlayer.getActivePokemon().isFainted()) {
                currentPlayerDefeated = !currentPlayer.hasAvailablePokemon();
            }
            
            
            opponentDefeated = !hasAvailablePokemon(opponentUser);
            
            // determine winner based on who's defeated
            if (opponentDefeated) {
                battleEnded = true;
                winner = currentUser;
            } else if (currentPlayerDefeated) {
                battleEnded = true;
                winner = opponentUser;
            }
        } else {
            currentPlayerDefeated = !hasAvailablePokemon(currentUser);
            opponentDefeated = !hasAvailablePokemon(opponentUser);
            
            if (opponentDefeated) {
                battleEnded = true;
                winner = currentUser;
            } else if (currentPlayerDefeated) {
                battleEnded = true;
                winner = opponentUser;
            }
        }


        if (battleEnded && winner != null) {
            battle.endBattle(winner);
            // winner gets cash as a reward
            winner.addCurrency(500);
            battleDataAccess.saveUser(winner);

            /*
            if (battle.getPlayer1().equals(winner)) {
                User loser = battle.getPlayer2();
            } 
            else {
                User loser = battle.getPlayer1();
            }
            battleDataAccess.saveUser(loser);
            */
/*
           User loser = battle.getPlayer1().equals(winner) ? battle.getPlayer2() : battle.getPlayer1();
           loser.addCurrency(100);
           battleDataAccess.saveUser(loser);


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

    // i made this to help me check who won, can be deleted if redundant. let me know 
    private boolean hasAvailablePokemon(User user) {
        if (user == null || user.getOwnedPokemon().isEmpty()) {
            return false;
        }
        for (Pokemon pokemon : user.getOwnedPokemon()) {
            if (!pokemon.isFainted()) {
                return true;
            }
        }
        return false;
    }
}*/

