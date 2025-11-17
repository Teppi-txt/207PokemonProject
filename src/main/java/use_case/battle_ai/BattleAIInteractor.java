package use_case.battle_ai;

import entities.*;
import ai.graph.Decision;

/**
 * Interactor for the battle AI use case.
 * This class contains the business logic for executing an AI player's turn in a battle.
 * Follows Clean Architecture principles with dependency inversion.
 */
public class BattleAIInteractor implements BattleAIInputBoundary {

    private final BattleAIUserDataAccessInterface dataAccess;
    private final BattleAIOutputBoundary presenter;

    /**
     * Constructs a BattleAIInteractor with dependency injection.
     * @param dataAccess the data access interface for battle and user persistence
     * @param presenter the output boundary for presenting results
     */
    public BattleAIInteractor(BattleAIUserDataAccessInterface dataAccess,
                              BattleAIOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(BattleAIInputData inputData) {
        // Phase 1: Validation
        final Battle battle = inputData.getBattle();
        if (battle == null) {
            presenter.prepareFailView("Battle not found");
            return;
        }

        if ("COMPLETED".equals(battle.getBattleStatus())) {
            presenter.prepareFailView("Battle is already completed");
            return;
        }

        if (!"IN_PROGRESS".equals(battle.getBattleStatus())) {
            presenter.prepareFailView("Battle is not in progress");
            return;
        }

        final AIPlayer aiPlayer = inputData.getAiPlayer();
        if (aiPlayer == null) {
            presenter.prepareFailView("AI player not found");
            return;
        }

        if (!aiPlayer.hasAvailablePokemon()) {
            presenter.prepareFailView("AI player has no available Pokemon");
            return;
        }

        // Verify AI player is part of this battle
        User aiUser = getAIUser(battle, aiPlayer);
        if (aiUser == null) {
            presenter.prepareFailView("AI player is not part of this battle");
            return;
        }

        // Phase 2: Decision-making
        Decision decision;
        try {
            if (inputData.isForcedSwitch()) {
                // AI must switch (active Pokemon fainted)
                Pokemon switchTarget = aiPlayer.decideSwitch(battle);
                if (switchTarget == null) {
                    presenter.prepareFailView("AI failed to choose a Pokemon to switch to");
                    return;
                }
                decision = Decision.switchPokemon(switchTarget, "Forced switch - active Pokemon fainted", 1.0);
            } else {
                // AI chooses move
                Move selectedMove = aiPlayer.chooseMove(battle);
                if (selectedMove == null) {
                    presenter.prepareFailView("AI failed to choose a move");
                    return;
                }
                decision = Decision.move(selectedMove, "AI chose move", 1.0);
            }
        } catch (Exception e) {
            presenter.prepareFailView("Error during AI decision-making: " + e.getMessage());
            return;
        }

        // Phase 3: Turn execution
        Turn turn = createTurnFromDecision(decision, aiPlayer, getNextTurnNumber(battle));
        if (turn == null) {
            presenter.prepareFailView("Failed to create turn from AI decision");
            return;
        }

        turn.executeTurn();
        String turnResult = turn.getResult();

        // Record turn in AI's battle history
        aiPlayer.recordTurn(turn);

        // Phase 4: Battle state management
        User opponentUser = getOpponentUser(battle, aiUser);
        boolean battleEnded = false;
        User winner = null;

        // Check if either player is defeated
        boolean aiDefeated = !hasAvailablePokemon(aiUser);
        boolean opponentDefeated = !hasAvailablePokemon(opponentUser);

        if (opponentDefeated) {
            battleEnded = true;
            winner = aiUser;
        } else if (aiDefeated) {
            battleEnded = true;
            winner = opponentUser;
        }

        // Update battle state if battle ended
        if (battleEnded && winner != null) {
            battle.endBattle(winner);
            // Award currency
            winner.addCurrency(500);
            dataAccess.saveUser(winner);

            User loser = battle.getPlayer1().equals(winner) ? battle.getPlayer2() : battle.getPlayer1();
            loser.addCurrency(100);
            dataAccess.saveUser(loser);
        }

        // Save battle state
        dataAccess.saveBattle(battle);

        // Phase 5: Output
        BattleAIOutputData outputData = new BattleAIOutputData(
                turn,
                battle,
                turnResult,
                battleEnded,
                battle.getBattleStatus(),
                decision
        );

        presenter.prepareSuccessView(outputData);
    }

    /**
     * Helper method to find which User corresponds to the AIPlayer in the battle.
     * @param battle the battle
     * @param aiPlayer the AI player
     * @return the User object for the AI player, or null if not found
     */
    private User getAIUser(Battle battle, AIPlayer aiPlayer) {
        // Check if player1 matches AI player
        if (battle.getPlayer1() != null && battle.getPlayer1().getName().equals(aiPlayer.getName())) {
            return battle.getPlayer1();
        }
        // Check if player2 matches AI player
        if (battle.getPlayer2() != null && battle.getPlayer2().getName().equals(aiPlayer.getName())) {
            return battle.getPlayer2();
        }
        return null;
    }

    /**
     * Helper method to get the opponent User.
     * @param battle the battle
     * @param currentUser the current user
     * @return the opponent user
     */
    private User getOpponentUser(Battle battle, User currentUser) {
        if (battle.getPlayer1().equals(currentUser)) {
            return battle.getPlayer2();
        } else {
            return battle.getPlayer1();
        }
    }

    /**
     * Helper method to check if a user has any available (non-fainted) Pokemon.
     * @param user the user
     * @return true if user has at least one non-fainted Pokemon, false otherwise
     */
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

    /**
     * Helper method to create a Turn object from an AI Decision.
     * @param decision the AI's decision
     * @param player the AI player
     * @param turnNumber the turn number
     * @return the created Turn object
     */
    private Turn createTurnFromDecision(Decision decision, Player player, int turnNumber) {
        if (decision.isMove()) {
            return new MoveTurn(turnNumber, player, turnNumber, decision.getSelectedMove());
        } else if (decision.isSwitch()) {
            Pokemon currentPokemon = player.getActivePokemon();
            Pokemon newPokemon = decision.getSwitchTarget();
            return new SwitchTurn(turnNumber, player, turnNumber, currentPokemon, newPokemon);
        }
        return null;
    }

    /**
     * Helper method to get the next turn number.
     * In a real implementation, this would track turn count in the battle.
     * For now, we'll use a simple incrementing counter.
     * @param battle the battle
     * @return the next turn number
     */
    private int getNextTurnNumber(Battle battle) {
        // Simple implementation - would need proper turn tracking in Battle entity
        return 1;
    }
}
