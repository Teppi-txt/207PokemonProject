package use_case.battle_ai;

import entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interactor for the Battle AI use case.
 * Handles battle setup, player moves, and player switches.
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
        } else if (inputData.isPlayerMoveRequest()) {
            executePlayerMove(inputData);
        } else if (inputData.isPlayerSwitchRequest()) {
            executePlayerSwitch(inputData);
        } else {
            presenter.prepareFailView("Invalid request");
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

        // Create copies of player's Pokemon for battle (so damage doesn't affect originals)
        List<Pokemon> battleTeam = new ArrayList<>();
        for (Pokemon p : playerTeam) {
            battleTeam.add(p.copy());
        }
        dataAccess.savePlayerTeam(battleTeam);
        dataAccess.setPlayerActivePokemon(battleTeam.get(0));

        // Create a battle-specific user copy with only the selected team
        // This avoids modifying the original user's owned Pokemon list
        User battleUser = new User(user.getId(), user.getName(), user.getEmail(), user.getCurrency());
        for (Pokemon p : battleTeam) {
            battleUser.addPokemon(p);
        }

        // Create AI player with generated team based on difficulty
        String diff = difficulty != null ? difficulty : "medium";
        AIPlayer aiPlayer = new AIPlayer("AI Trainer", diff);
        List<Pokemon> aiTeam = generateAITeam(diff);
        aiPlayer.setTeam(aiTeam);
        aiPlayer.setActivePokemon(aiTeam.get(0));
        dataAccess.saveAIPlayer(aiPlayer);

        // Create AI user wrapper
        User aiUser = new User(0, aiPlayer.getName(), "", 0);
        for (Pokemon p : aiTeam) {
            aiUser.addPokemon(p);
        }

        // Create and start battle using the battle-specific user copy
        Battle battle = new Battle(0, battleUser, aiUser);
        battle.startBattle();
        dataAccess.saveBattle(battle);

        // Return success
        BattleAIOutputData outputData = new BattleAIOutputData(
                null, battle, "Battle started! Choose your move.", false);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Executes a player's move by index, then AI's turn.
     */
    private void executePlayerMove(BattleAIInputData inputData) {
        Battle battle = dataAccess.getBattle();
        int moveIndex = inputData.getMoveIndex();

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
        User currentUser = dataAccess.getUser();
        AIPlayer aiPlayer = dataAccess.getAIPlayer();
        Pokemon playerPokemon = dataAccess.getPlayerActivePokemon();

        if (currentUser == null || aiPlayer == null || playerPokemon == null) {
            presenter.prepareFailView("Battle state not found");
            return;
        }

        // Get move by index
        List<String> moveNames = playerPokemon.getMoves();
        if (moveIndex < 0 || moveIndex >= moveNames.size()) {
            presenter.prepareFailView("Invalid move index");
            return;
        }

        String moveName = moveNames.get(moveIndex);
        Move selectedMove = findMoveByName(moveName);
        if (selectedMove == null) {
            selectedMove = new Move().setName(moveName).setPower(40);
        }

        // Track AI's Pokemon before player's turn
        Pokemon aiPokemonBefore = aiPlayer.getActivePokemon();

        // Create and execute player's turn
        Player playerAdapter = new UserPlayerAdapter(currentUser);
        MoveTurn playerTurn = new MoveTurn(1, playerAdapter, 1, selectedMove, aiPlayer);
        playerTurn.executeTurn();
        String playerResult = playerTurn.getResult();

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
        User player1 = battle.getPlayer1();
        User player2 = battle.getPlayer2();
        boolean battleEnded = false;

        if (!hasAvailablePokemon(player2)) {
            battle.endBattle(player1);
            awardCurrency(player1, player2);
            battleEnded = true;
            dataAccess.saveBattle(battle);
            BattleAIOutputData outputData = new BattleAIOutputData(playerTurn, battle, playerResult, true, null, aiSwitchedTo);
            presenter.prepareSuccessView(outputData);
            return;
        }

        // Execute AI's turn (AI always has a move available)
        Move aiMove = aiPlayer.chooseMove(battle);
        Pokemon playerSwitchedTo = null;

        // AI is always player2, player is always player1
        Player targetPlayer = new UserPlayerAdapter(player1);
        MoveTurn aiTurn = new MoveTurn(1, aiPlayer, 1, aiMove, targetPlayer);
        aiTurn.executeTurn();
        String aiResult = aiTurn.getResult();
        aiPlayer.recordTurn(aiTurn);

        // Check if player's Pokemon fainted and auto-switch
        Pokemon playerActivePokemon = dataAccess.getPlayerActivePokemon();
        if (playerActivePokemon.isFainted()) {
            List<Pokemon> playerTeam = dataAccess.getPlayerTeam();
            // Update battle user's list order for presenter
            List<Pokemon> battleUserList = player1.getOwnedPokemon();
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

        BattleAIOutputData outputData = new BattleAIOutputData(playerTurn, battle, fullResult, battleEnded, playerSwitchedTo, aiSwitchedTo);
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
        Battle battle = dataAccess.getBattle();
        int switchTargetId = inputData.getSwitchTargetId();

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
        List<Pokemon> playerTeam = dataAccess.getPlayerTeam();
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
        User currentUser = dataAccess.getUser();
        AIPlayer aiPlayer = dataAccess.getAIPlayer();
        Pokemon previousPokemon = dataAccess.getPlayerActivePokemon();

        if (currentUser == null || aiPlayer == null) {
            presenter.prepareFailView("Battle state not found");
            return;
        }

        // Update active Pokemon
        dataAccess.setPlayerActivePokemon(switchTarget);

        // Move switched Pokemon to front of battle user's list (so presenter shows correct active)
        User battleUser = battle.getPlayer1();
        List<Pokemon> battleUserList = battleUser.getOwnedPokemon();
        battleUserList.remove(switchTarget);
        battleUserList.add(0, switchTarget);

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

        // Execute AI's turn (AI always has a move available)
        Move aiMove = aiPlayer.chooseMove(battle);
        // AI is always player2, player is always player1
        User aiUser = battle.getPlayer2();
        Player targetPlayer = new UserPlayerAdapter(battleUser);
        MoveTurn aiTurn = new MoveTurn(1, aiPlayer, 1, aiMove, targetPlayer);
        aiTurn.executeTurn();
        String aiResult = aiTurn.getResult();
        aiPlayer.recordTurn(aiTurn);

        // Check if player's Pokemon fainted and auto-switch
        Pokemon playerSwitchedTo = null;
        Pokemon playerActivePokemon = dataAccess.getPlayerActivePokemon();
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
        } else if (!hasAvailablePokemon(battleUser)) {
            battle.endBattle(aiUser);
            awardCurrency(aiUser, battleUser);
            battleEnded = true;
        }

        dataAccess.saveBattle(battle);

        // Combine results
        String fullResult = switchResult + "\n\nAI: " + aiResult;
        BattleAIOutputData outputData = new BattleAIOutputData(aiTurn, battle, fullResult, battleEnded, playerSwitchedTo, null);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Generates a random AI team of up to 3 Pokemon.
     */
    private List<Pokemon> generateAITeam(String difficulty) {
        List<Pokemon> allPokemon = new ArrayList<>(dataAccess.getAllPokemon());
        Collections.shuffle(allPokemon);
        List<Pokemon> aiTeam = new ArrayList<>();
        int limit = Math.min(3, allPokemon.size());
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
        winner.addCurrency(500);
        loser.addCurrency(100);
        dataAccess.saveUser(dataAccess.getUser());
    }
}
