package use_case.battle_ai;

import entities.*;
import ai.graph.Decision;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Interactor for the Battle AI use case.
 * Handles both battle setup and AI turn execution.
 */
public class BattleAIInteractor implements BattleAIInputBoundary {

    private final BattleAIUserDataAccessInterface dataAccess;
    private final BattleAIOutputBoundary presenter;

    public BattleAIInteractor(BattleAIUserDataAccessInterface dataAccess,
                              BattleAIOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(BattleAIInputData inputData) {
        if (inputData.isSetupRequest()) {
            executeSetup(inputData);
        } else {
            executeAITurn(inputData);
        }
    }

    /**
     * Sets up a new battle with AI opponent.
     */
    private void executeSetup(BattleAIInputData inputData) {
        User user = inputData.getUser();
        List<Pokemon> playerTeam = inputData.getPlayerTeam();
        String difficulty = inputData.getDifficulty();

        if (user == null || playerTeam == null || playerTeam.isEmpty()) {
            presenter.prepareFailView("Invalid setup: user and team required");
            return;
        }

        // Store player data
        dataAccess.saveUser(user);
        dataAccess.savePlayerTeam(playerTeam);
        dataAccess.setPlayerActivePokemon(playerTeam.get(0));

        // Set player's owned Pokemon for battle
        user.getOwnedPokemon().clear();
        for (Pokemon p : playerTeam) {
            user.addPokemon(p);
        }

        // Create AI player with generated team
        AIPlayer aiPlayer = new AIPlayer("AI Trainer", difficulty != null ? difficulty : "medium");
        List<Pokemon> aiTeam = generateAITeam();
        aiPlayer.setTeam(aiTeam);
        aiPlayer.setActivePokemon(aiTeam.isEmpty() ? null : aiTeam.get(0));
        dataAccess.saveAIPlayer(aiPlayer);

        // Create AI user wrapper
        User aiUser = new User(0, aiPlayer.getName(), "", 0);
        for (Pokemon p : aiTeam) {
            aiUser.addPokemon(p);
        }

        // Create and start battle
        Battle battle = new Battle(0, user, aiUser);
        battle.startBattle();
        dataAccess.saveBattle(battle);

        // Return success
        BattleAIOutputData outputData = new BattleAIOutputData(
                null, battle, "Battle started! Choose your move.", false);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Generates a random team of 3 Pokemon for the AI.
     */
    private List<Pokemon> generateAITeam() {
        List<Pokemon> allPokemon = dataAccess.getAllPokemon();
        List<Pokemon> aiTeam = new ArrayList<>();
        Random random = new Random();

        while (aiTeam.size() < 3 && !allPokemon.isEmpty()) {
            int index = random.nextInt(allPokemon.size());
            Pokemon selected = allPokemon.get(index).copy();
            aiTeam.add(selected);
        }

        return aiTeam;
    }

    /**
     * Executes an AI player's turn in the battle.
     */
    private void executeAITurn(BattleAIInputData inputData) {
        Battle battle = inputData.getBattle();
        AIPlayer aiPlayer = inputData.getAiPlayer();
        boolean forcedSwitch = inputData.isForcedSwitch();

        // Validation
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

        if (aiPlayer == null) {
            presenter.prepareFailView("AI player not found");
            return;
        }

        // Check AI player is part of battle
        User aiUser = findAIUser(battle, aiPlayer);
        if (aiUser == null) {
            presenter.prepareFailView("AI player is not part of this battle");
            return;
        }

        if (!aiPlayer.hasAvailablePokemon()) {
            presenter.prepareFailView("AI player has no available Pokemon");
            return;
        }

        // AI Decision
        Decision decision;
        try {
            if (forcedSwitch) {
                Pokemon switchTarget = aiPlayer.decideSwitch(battle);
                if (switchTarget == null) {
                    presenter.prepareFailView("AI failed to choose a Pokemon to switch to");
                    return;
                }
                decision = Decision.switchPokemon(switchTarget, "Forced switch", 1.0);
            } else {
                Move selectedMove = aiPlayer.chooseMove(battle);
                if (selectedMove == null) {
                    presenter.prepareFailView("AI failed to choose a move");
                    return;
                }
                decision = Decision.move(selectedMove, "AI chose move", 1.0);
            }
        } catch (Exception e) {
            presenter.prepareFailView("Error during AI decision: " + e.getMessage());
            return;
        }

        // Execute Turn
        User opponentUser = getOpponentUser(battle, aiUser);
        Turn turn = createTurn(decision, aiPlayer, opponentUser);

        if (turn == null) {
            presenter.prepareFailView("Failed to create turn");
            return;
        }

        turn.executeTurn();
        String turnResult = turn.getResult();
        aiPlayer.recordTurn(turn);

        // Check battle end and award currency
        boolean battleEnded = false;
        if (!hasAvailablePokemon(aiUser)) {
            battle.endBattle(opponentUser);
            awardCurrency(opponentUser, aiUser);
            battleEnded = true;
        } else if (!hasAvailablePokemon(opponentUser)) {
            battle.endBattle(aiUser);
            awardCurrency(aiUser, opponentUser);
            battleEnded = true;
        }

        dataAccess.saveBattle(battle);

        // Output
        BattleAIOutputData outputData = new BattleAIOutputData(turn, battle, turnResult, battleEnded);
        presenter.prepareSuccessView(outputData);
    }

    private User findAIUser(Battle battle, AIPlayer aiPlayer) {
        if (battle.getPlayer1() != null && battle.getPlayer1().getName().equals(aiPlayer.getName())) {
            return battle.getPlayer1();
        }
        if (battle.getPlayer2() != null && battle.getPlayer2().getName().equals(aiPlayer.getName())) {
            return battle.getPlayer2();
        }
        return null;
    }

    private User getOpponentUser(Battle battle, User currentUser) {
        return battle.getPlayer1().equals(currentUser) ? battle.getPlayer2() : battle.getPlayer1();
    }

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

    private Turn createTurn(Decision decision, Player player, User targetUser) {
        if (decision.isMove()) {
            Player targetPlayer = new UserPlayerAdapter(targetUser);
            return new MoveTurn(1, player, 1, decision.getSelectedMove(), targetPlayer);
        } else if (decision.isSwitch()) {
            return new SwitchTurn(1, player, 1, player.getActivePokemon(), decision.getSwitchTarget());
        }
        return null;
    }

    private void awardCurrency(User winner, User loser) {
        winner.addCurrency(500);
        loser.addCurrency(100);
        dataAccess.saveUser(winner);
        dataAccess.saveUser(loser);
    }
}
