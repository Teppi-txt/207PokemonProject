package use_case.battle_ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entities.*;
import entities.battle.*;
import entities.user.User;
import entities.user.UserPlayerAdapter;

/**
 * Interactor for the Battle AI use case.
 * Handles battle setup, player moves, and player switches.
 */
public class BattleAIInteractor implements BattleAIInputBoundary {
    private static final int AI_TEAM_SIZE = 3;
    private static final int LOSS_REWARD = 100;
    private static final int WIN_REWARD = 500;

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
        }
        else if (inputData.isPlayerMoveRequest()) {
            executePlayerMove(inputData);
        }
        else if (inputData.isPlayerSwitchRequest()) {
            executePlayerSwitch(inputData);
        }
        else {
            presenter.prepareFailView("Invalid request");
        }
    }

    /**
     * Sets up a new battle with AI opponent.
     * @param inputData the input data containing the user, team, and difficulty settings
     */
    private void executeSetup(BattleAIInputData inputData) {
        final User user = inputData.getUser();
        final List<Pokemon> playerTeam = inputData.getPlayerTeam();
        final String difficulty = inputData.getDifficulty();

        if (user == null || playerTeam == null || playerTeam.isEmpty()) {
            presenter.prepareFailView("Invalid setup: user and team required");
            return;
        }

        // Store player data
        dataAccess.saveUser(user);

        // Create copies of player's Pokemon for battle (so damage doesn't affect originals)
        final List<Pokemon> battleTeam = new ArrayList<>();
        for (Pokemon p : playerTeam) {
            battleTeam.add(p.copy());
        }
        dataAccess.savePlayerTeam(battleTeam);
        dataAccess.setPlayerActivePokemon(battleTeam.get(0));

        // Create a battle-specific user copy with only the selected team
        // This avoids modifying the original user's owned Pokemon list
        final User battleUser = new User(user.getId(), user.getName(), user.getEmail(), user.getCurrency());
        for (Pokemon p : battleTeam) {
            battleUser.addPokemon(p);
        }

        // Create AI player with generated team based on difficulty
        final String diff = difficulty != null ? difficulty : "medium";
        final AIPlayer aiPlayer = new AIPlayer("AI Trainer", diff);
        final List<Pokemon> aiTeam = generateAITeam(diff);
        aiPlayer.setTeam(aiTeam);
        aiPlayer.setActivePokemon(aiTeam.get(0));
        dataAccess.saveAIPlayer(aiPlayer);

        // Create AI user wrapper
        final User aiUser = new User(0, aiPlayer.getName(), "", 0);
        for (Pokemon p : aiTeam) {
            aiUser.addPokemon(p);
        }

        // Create and start battle using the battle-specific user copy
        final Battle battle = new Battle(0, battleUser, aiUser);
        battle.startBattle();
        dataAccess.saveBattle(battle);

        // Return success
        final BattleAIOutputData outputData = new BattleAIOutputData(
                null, battle, "Battle started! Choose your move.", false);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Executes a player's move by index, then AI's turn.
     * @param inputData  the input data containing the move index requested by the player
     */
    private void executePlayerMove(BattleAIInputData inputData) {
        final Battle battle = dataAccess.getBattle();
        final int moveIndex = inputData.getMoveIndex();

        // Validation
        if (battle == null) {
            presenter.prepareFailView("Battle not found");
            return;
        }

        if ("COMPLETED".equals(battle.getBattleStatus())) {
            presenter.prepareFailView("Battle is already completed");
            return;
        }

        // Get current state
        final User currentUser = dataAccess.getUser();
        final AIPlayer aiPlayer = dataAccess.getAIPlayer();
        final Pokemon playerPokemon = dataAccess.getPlayerActivePokemon();

        if (currentUser == null || aiPlayer == null || playerPokemon == null) {
            presenter.prepareFailView("Battle state not found");
            return;
        }

        // Get move by index
        final List<String> moveNames = playerPokemon.getMoves();
        if (moveIndex < 0 || moveIndex >= moveNames.size()) {
            presenter.prepareFailView("Invalid move index");
            return;
        }

        final String moveName = moveNames.get(moveIndex);
        Move selectedMove = findMoveByName(moveName);
        if (selectedMove == null) {
            selectedMove = new Move().setName(moveName).setPower(40);
        }

        // Track AI's Pokemon before player's turn
        final Pokemon aiPokemonBefore = aiPlayer.getActivePokemon();

        // Create and execute player's turn
        final Player playerAdapter = new UserPlayerAdapter(currentUser);
        final MoveTurn playerTurn = new MoveTurn(1, playerAdapter, 1, selectedMove, aiPlayer);
        playerTurn.executeTurn();
        final String playerResult = playerTurn.getResult();

        // Check if AI's Pokemon fainted and auto-switch
        Pokemon aiSwitchedTo = null;
        if (aiPokemonBefore.isFainted()) {
            for (Pokemon p : aiPlayer.getTeam()) {
                if (!p.isFainted()) {
                    aiPlayer.switchPokemon(p);
                    aiSwitchedTo = p;
                    break;
                }
            }
        }

        // Check if battle ended after player's turn
        final User player1 = battle.getPlayer1();
        final User player2 = battle.getPlayer2();
        boolean battleEnded = false;

        if (!hasAvailablePokemon(player2)) {
            battle.endBattle(player1);
            awardCurrency(player1, player2);
            battleEnded = true;
            dataAccess.saveBattle(battle);
            final BattleAIOutputData outputData = new BattleAIOutputData(playerTurn, battle, playerResult, true, null, aiSwitchedTo);
            presenter.prepareSuccessView(outputData);
            return;
        }

        // Execute AI's turn (AI always has a move available)
        final Move aiMove = aiPlayer.chooseMove(battle);
        Pokemon playerSwitchedTo = null;

        // AI is always player2, player is always player1
        final Player targetPlayer = new UserPlayerAdapter(player1);
        final MoveTurn aiTurn = new MoveTurn(1, aiPlayer, 1, aiMove, targetPlayer);
        aiTurn.executeTurn();
        final String aiResult = aiTurn.getResult();
        aiPlayer.recordTurn(aiTurn);

        // Check if player's Pokemon fainted and auto-switch
        final Pokemon playerActivePokemon = dataAccess.getPlayerActivePokemon();
        if (playerActivePokemon.isFainted()) {
            final List<Pokemon> playerTeam = dataAccess.getPlayerTeam();
            // Update battle user's list order for presenter
            final List<Pokemon> battleUserList = player1.getOwnedPokemon();
            for (Pokemon p : playerTeam) {
                if (!p.isFainted()) {
                    dataAccess.setPlayerActivePokemon(p);
                    battleUserList.remove(p);
                    battleUserList.add(0, p);
                    playerSwitchedTo = p;
                    break;
                }
            }
        }

        // Check battle end
        if (!hasAvailablePokemon(player1)) {
            battle.endBattle(player2);
            awardCurrency(player2, player1);
            battleEnded = true;
        }

        dataAccess.saveBattle(battle);

        // Combine results
        String fullResult = "YOU:\n" + playerResult;
        if (aiSwitchedTo != null) {
            fullResult += "\n\nAI sent out " + aiSwitchedTo.getName().toUpperCase() + "!";
        }
        fullResult += "\n\nAI:\n" + aiResult;
        if (playerSwitchedTo != null) {
            fullResult += "\n\nYou sent out " + playerSwitchedTo.getName().toUpperCase() + "!";
        }

        final BattleAIOutputData outputData = new BattleAIOutputData(playerTurn, battle, fullResult, battleEnded, playerSwitchedTo, aiSwitchedTo);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Finds a move by name from the move database.
     */
    private Move findMoveByName(String moveName) {
        return dataAccess.getMoveByName(moveName);
    }

    /**
     * Executes a player's switch by Pokemon ID and then AI's turn.
     */
    private void executePlayerSwitch(BattleAIInputData inputData) {
        final Battle battle = dataAccess.getBattle();
        final int switchTargetId = inputData.getSwitchTargetId();

        // Validation
        if (battle == null) {
            presenter.prepareFailView("Battle not found");
            return;
        }

        if ("COMPLETED".equals(battle.getBattleStatus())) {
            presenter.prepareFailView("Battle is already completed");
            return;
        }

        // Find the Pokemon by ID in player's team
        final List<Pokemon> playerTeam = dataAccess.getPlayerTeam();
        Pokemon switchTarget = null;
        for (Pokemon p : playerTeam) {
            if (p.getId() == switchTargetId) {
                switchTarget = p;
                break;
            }
        }

        if (switchTarget == null) {
            presenter.prepareFailView("Pokemon not found in team");
            return;
        }

        if (switchTarget.isFainted()) {
            presenter.prepareFailView("Cannot switch to fainted Pokemon");
            return;
        }

        // Get current state
        final User currentUser = dataAccess.getUser();
        final AIPlayer aiPlayer = dataAccess.getAIPlayer();
        final Pokemon previousPokemon = dataAccess.getPlayerActivePokemon();

        if (currentUser == null || aiPlayer == null) {
            presenter.prepareFailView("Battle state not found");
            return;
        }

        // Update active Pokemon
        dataAccess.setPlayerActivePokemon(switchTarget);

        // Move switched Pokemon to front of battle user's list (so presenter shows correct active)
        final User battleUser = battle.getPlayer1();
        final List<Pokemon> battleUserList = battleUser.getOwnedPokemon();
        battleUserList.remove(switchTarget);
        battleUserList.add(0, switchTarget);

        // Create and execute switch turn
        final Player playerAdapter = new UserPlayerAdapter(currentUser);
        final SwitchTurn switchTurn = new SwitchTurn(1, playerAdapter, 1, previousPokemon, switchTarget);
        switchTurn.executeTurn();
        final String switchResult = "You switched to " + switchTarget.getName() + "!";

        // Check if battle ended after switch
        if ("COMPLETED".equals(battle.getBattleStatus())) {
            final BattleAIOutputData outputData = new BattleAIOutputData(switchTurn, battle, switchResult, true);
            presenter.prepareSuccessView(outputData);
            return;
        }

        // Execute AI's turn (AI always has a move available)
        final Move aiMove = aiPlayer.chooseMove(battle);
        // AI is always player2, player is always player1
        final User aiUser = battle.getPlayer2();
        final Player targetPlayer = new UserPlayerAdapter(battleUser);
        final MoveTurn aiTurn = new MoveTurn(1, aiPlayer, 1, aiMove, targetPlayer);
        aiTurn.executeTurn();
        final String aiResult = aiTurn.getResult();
        aiPlayer.recordTurn(aiTurn);

        // Check if player's Pokemon fainted and auto-switch
        Pokemon playerSwitchedTo = null;
        final Pokemon playerActivePokemon = dataAccess.getPlayerActivePokemon();
        if (playerActivePokemon.isFainted()) {
            for (Pokemon p : playerTeam) {
                if (!p.isFainted()) {
                    dataAccess.setPlayerActivePokemon(p);
                    battleUserList.remove(p);
                    battleUserList.add(0, p);
                    playerSwitchedTo = p;
                    break;
                }
            }
        }

        // Check battle end
        boolean battleEnded = false;
        if (!hasAvailablePokemon(aiUser)) {
            battle.endBattle(battleUser);
            awardCurrency(battleUser, aiUser);
            battleEnded = true;
        }
        else if (!hasAvailablePokemon(battleUser)) {
            battle.endBattle(aiUser);
            awardCurrency(aiUser, battleUser);
            battleEnded = true;
        }

        dataAccess.saveBattle(battle);

        // Combine results
        final String fullResult = switchResult + "\n\nAI: " + aiResult;
        final BattleAIOutputData outputData = new
                BattleAIOutputData(aiTurn, battle, fullResult, battleEnded, playerSwitchedTo, null);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Generates a random AI team of up to 3 Pokemon.
     */
    private List<Pokemon> generateAITeam(String difficulty) {
        final List<Pokemon> allPokemon = new ArrayList<>(dataAccess.getAllPokemon());
        Collections.shuffle(allPokemon);
        final List<Pokemon> aiTeam = new ArrayList<>();
        final int limit = Math.min(AI_TEAM_SIZE, allPokemon.size());
        for (int i = 0; i < limit; i++) {
            aiTeam.add(allPokemon.get(i).copy());
        }
        return aiTeam;
    }

    private boolean hasAvailablePokemon(User user) {
        for (Pokemon pokemon : user.getOwnedPokemon()) {
            if (!pokemon.isFainted()) {
                return true;
            }
        }
        return false;
    }

    private void awardCurrency(User winner, User loser) {
        // Get the original user (not the battle copy)
        final User originalUser = dataAccess.getUser();

        // Player is always player1, AI is always player2
        // Check if player won or lost by comparing with winner
        final boolean playerWon = winner.getId() == originalUser.getId();

        if (playerWon) {
            originalUser.addCurrency(WIN_REWARD);
        }
        else {
            originalUser.addCurrency(LOSS_REWARD);
        }
        dataAccess.saveUser(originalUser);
    }
}
