package use_case.battle_ai;

import entities.*;
import ai.graph.Decision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        } else if (inputData.isPlayerTurnRequest()) {
            executePlayerTurn(inputData);
        } else if (inputData.isPlayerSwitchRequest()) {
            executePlayerSwitch(inputData);
        } else {
            // Default to AI turn (handles validation for null battle/aiPlayer)
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

        // Create AI player with generated team based on difficulty
        String diff = difficulty != null ? difficulty : "medium";
        AIPlayer aiPlayer = new AIPlayer("AI Trainer", diff);
        List<Pokemon> aiTeam = generateAITeam(diff);
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
     * Executes a player's turn in the battle.
     */
    private void executePlayerTurn(BattleAIInputData inputData) {
        Battle battle = dataAccess.getBattle();
        Turn turn = inputData.getTurn();

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

        if (turn == null) {
            presenter.prepareFailView("Turn is invalid");
            return;
        }

        // Track AI's active Pokemon before turn
        AIPlayer aiPlayer = dataAccess.getAIPlayer();
        Pokemon aiPokemonBefore = aiPlayer != null ? aiPlayer.getActivePokemon() : null;

        // Execute the turn
        turn.executeTurn();
        String turnResult = turn.getResult();

        // Check if AI's Pokemon fainted and auto-switch
        Pokemon aiSwitchedTo = null;
        if (aiPlayer != null && aiPokemonBefore != null && aiPokemonBefore.isFainted()) {
            for (Pokemon p : aiPlayer.getTeam()) {
                if (!p.isFainted()) {
                    aiPlayer.switchPokemon(p);
                    aiSwitchedTo = p;
                    break;
                }
            }
        }

        // Check battle end
        User player1 = battle.getPlayer1();
        User player2 = battle.getPlayer2();
        boolean battleEnded = false;

        boolean player1HasPokemon = hasAvailablePokemon(player1);
        boolean player2HasPokemon = hasAvailablePokemon(player2);

        if (!player1HasPokemon && !player2HasPokemon) {
            battle.endBattle(null);
            battleEnded = true;
        } else if (!player1HasPokemon) {
            battle.endBattle(player2);
            awardCurrency(player2, player1);
            battleEnded = true;
        } else if (!player2HasPokemon) {
            battle.endBattle(player1);
            awardCurrency(player1, player2);
            battleEnded = true;
        }

        dataAccess.saveBattle(battle);

        // Output
        BattleAIOutputData outputData = new BattleAIOutputData(turn, battle, turnResult, battleEnded, null, aiSwitchedTo);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Executes a player's switch and then AI's turn.
     */
    private void executePlayerSwitch(BattleAIInputData inputData) {
        Battle battle = dataAccess.getBattle();
        Pokemon switchTarget = inputData.getSwitchTarget();

        // Validation
        if (battle == null) {
            presenter.prepareFailView("Battle not found");
            return;
        }

        if ("COMPLETED".equals(battle.getBattleStatus())) {
            presenter.prepareFailView("Battle is already completed");
            return;
        }

        if (switchTarget == null) {
            presenter.prepareFailView("No switch target specified");
            return;
        }

        // Get current state
        User currentUser = dataAccess.getUser();
        AIPlayer aiPlayer = dataAccess.getAIPlayer();
        Pokemon previousPokemon = dataAccess.getPlayerActivePokemon();

        if (currentUser == null || aiPlayer == null) {
            presenter.prepareFailView("Battle state not found");
            return;
        }

        // Update active Pokemon
        dataAccess.setPlayerActivePokemon(switchTarget);

        // Move switched Pokemon to front of user's list
        List<Pokemon> ownedList = currentUser.getOwnedPokemon();
        if (ownedList.contains(switchTarget)) {
            ownedList.remove(switchTarget);
            ownedList.add(0, switchTarget);
        }

        // Create and execute switch turn
        Player playerAdapter = new UserPlayerAdapter(currentUser);
        SwitchTurn switchTurn = new SwitchTurn(1, playerAdapter, 1, previousPokemon, switchTarget);
        switchTurn.executeTurn();
        String switchResult = "You switched to " + switchTarget.getName() + "!";

        // Check if battle ended after switch
        if ("COMPLETED".equals(battle.getBattleStatus())) {
            BattleAIOutputData outputData = new BattleAIOutputData(switchTurn, battle, switchResult, true);
            presenter.prepareSuccessView(outputData);
            return;
        }

        // Execute AI's turn
        Move aiMove = aiPlayer.chooseMove(battle);
        if (aiMove != null) {
            User aiUser = findAIUser(battle, aiPlayer);
            User opponentUser = getOpponentUser(battle, aiUser);
            Player targetPlayer = new UserPlayerAdapter(opponentUser);
            MoveTurn aiTurn = new MoveTurn(1, aiPlayer, 1, aiMove, targetPlayer);
            aiTurn.executeTurn();
            String aiResult = aiTurn.getResult();
            aiPlayer.recordTurn(aiTurn);

            // Check if player's Pokemon fainted and auto-switch
            Pokemon playerSwitchedTo = null;
            Pokemon playerActivePokemon = dataAccess.getPlayerActivePokemon();
            if (playerActivePokemon != null && playerActivePokemon.isFainted()) {
                List<Pokemon> playerTeam = dataAccess.getPlayerTeam();
                for (Pokemon p : playerTeam) {
                    if (!p.isFainted()) {
                        dataAccess.setPlayerActivePokemon(p);
                        if (ownedList.contains(p)) {
                            ownedList.remove(p);
                            ownedList.add(0, p);
                        }
                        playerSwitchedTo = p;
                        break;
                    }
                }
            }

            // Check battle end
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

            // Combine results
            String fullResult = switchResult + "\n\nAI: " + aiResult;
            BattleAIOutputData outputData = new BattleAIOutputData(aiTurn, battle, fullResult, battleEnded, playerSwitchedTo, null);
            presenter.prepareSuccessView(outputData);
        } else {
            dataAccess.saveBattle(battle);
            BattleAIOutputData outputData = new BattleAIOutputData(switchTurn, battle, switchResult, false);
            presenter.prepareSuccessView(outputData);
        }
    }

    /**
     * Generates AI team based on difficulty level using total base stats.
     * Easy: Pokemon with total stats < 350 (weak/unevolved)
     * Medium: Pokemon with total stats 350-480 (mid-tier)
     * Hard: Pokemon with total stats > 500 (strong fully-evolved)
     */
    private List<Pokemon> generateAITeam(String difficulty) {
        List<Pokemon> allPokemon = dataAccess.getAllPokemon();
        List<Pokemon> aiTeam = new ArrayList<>();
        List<Pokemon> candidates = new ArrayList<>();

        for (Pokemon p : allPokemon) {
            Stats s = p.getStats();
            int totalStats = s.getHp() + s.getAttack() + s.getDefense() +
                             s.getSpAttack() + s.getSpDefense() + s.getSpeed();

            switch (difficulty.toLowerCase()) {
                case "easy":
                    // Weak Pokemon with total stats < 350
                    if (totalStats < 350 && p.getId() <= 251) {
                        candidates.add(p);
                    }
                    break;
                case "hard":
                    // Strong Pokemon with total stats > 500
                    if (totalStats > 500) {
                        candidates.add(p);
                    }
                    break;
                case "medium":
                default:
                    // Mid-tier Pokemon with total stats 350-480
                    if (totalStats >= 350 && totalStats <= 480 && p.getId() <= 386) {
                        candidates.add(p);
                    }
                    break;
            }
        }

        Collections.shuffle(candidates);
        for (int i = 0; i < 3 && i < candidates.size(); i++) {
            aiTeam.add(candidates.get(i).copy());
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

        // Check if player's Pokemon fainted and auto-switch
        Pokemon playerSwitchedTo = null;
        Pokemon playerActivePokemon = dataAccess.getPlayerActivePokemon();
        if (playerActivePokemon != null && playerActivePokemon.isFainted()) {
            List<Pokemon> playerTeam = dataAccess.getPlayerTeam();
            for (Pokemon p : playerTeam) {
                if (!p.isFainted()) {
                    dataAccess.setPlayerActivePokemon(p);
                    // Also update user's owned list order
                    List<Pokemon> ownedList = opponentUser.getOwnedPokemon();
                    if (ownedList.contains(p)) {
                        ownedList.remove(p);
                        ownedList.add(0, p);
                    }
                    playerSwitchedTo = p;
                    break;
                }
            }
        }

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
        BattleAIOutputData outputData = new BattleAIOutputData(turn, battle, turnResult, battleEnded, playerSwitchedTo, null);
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
