package interface_adapters.battle_ai;

import entities.*;
import pokeapi.JSONLoader;
import use_case.battle_ai.BattleAIInputBoundary;
import use_case.battle_ai.BattleAIInputData;
import use_case.battle_player.BattlePlayerInputBoundary;
import use_case.battle_player.BattlePlayerInputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Controller for Battle AI feature.
 * Orchestrates the battle flow between human player and AI opponent.
 * Handles user input and coordinates use case execution.
 */
public class BattleAIController {

    private final BattlePlayerInputBoundary playerInteractor;
    private final BattleAIInputBoundary aiInteractor;
    private final BattleAIDataAccessObject dataAccess;
    private final BattleAIPresenter presenter;

    private Battle currentBattle;
    private AIPlayer aiPlayer;
    private List<Pokemon> playerTeam;
    private Pokemon playerActivePokemon;
    private int turnNumber;

    public BattleAIController(BattlePlayerInputBoundary playerInteractor,
                              BattleAIInputBoundary aiInteractor,
                              BattleAIDataAccessObject dataAccess,
                              BattleAIPresenter presenter) {
        this.playerInteractor = playerInteractor;
        this.aiInteractor = aiInteractor;
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.turnNumber = 0;
    }

    /**
     * Starts a new battle with the selected deck against an AI opponent.
     *
     * @param user The human player
     * @param selectedDeck The Pokemon selected for battle (3 Pokemon)
     * @param difficulty AI difficulty level ("easy", "medium", "hard")
     */
    public void startBattle(User user, List<Pokemon> selectedDeck, String difficulty) {
        if (selectedDeck == null || selectedDeck.size() < 3) {
            presenter.prepareFailView("You need at least 3 Pokemon to battle!");
            return;
        }

        // Initialize player team (make copies to avoid modifying original Pokemon)
        this.playerTeam = new ArrayList<>();
        for (Pokemon pokemon : selectedDeck) {
            this.playerTeam.add(pokemon.copy());
        }
        this.playerActivePokemon = playerTeam.get(0);

        // Generate AI opponent
        this.aiPlayer = generateAIDeck(difficulty);
        if (this.aiPlayer == null || this.aiPlayer.getTeam().isEmpty()) {
            presenter.prepareFailView("Failed to generate AI opponent!");
            return;
        }

        // Create AI user (temporary user for battle system)
        User aiUser = new User(999, aiPlayer.getName(), "ai@pokemon.com", 0);

        // Create battle
        this.currentBattle = new Battle(new Random().nextInt(10000), user, aiUser);
        this.currentBattle.startBattle();

        // Store in data access
        dataAccess.setCurrentUser(user);
        dataAccess.setCurrentBattle(currentBattle);

        // Initialize presenter
        presenter.initializeBattle(currentBattle, playerTeam, aiPlayer);

        this.turnNumber = 1;
    }

    /**
     * Executes a player move during their turn.
     * Simplified battle mechanics - applies damage and executes AI response.
     *
     * @param selectedMove The move the player chose to use
     */
    public void executePlayerMove(Move selectedMove) {
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            presenter.prepareFailView("No active battle!");
            return;
        }

        if (playerActivePokemon == null || playerActivePokemon.isFainted()) {
            presenter.prepareFailView("Your Pokemon has fainted! Please switch.");
            return;
        }

        Pokemon aiActivePokemon = aiPlayer.getActivePokemon();
        if (aiActivePokemon == null) {
            presenter.prepareFailView("AI has no active Pokemon!");
            return;
        }

        // Execute player's move
        String turnResult = executeMoveSimple(playerActivePokemon, selectedMove, aiActivePokemon);
        System.out.println("Player turn: " + turnResult);

        // Update view model
        presenter.updateTeams(playerTeam, aiPlayer);

        // Check if AI Pokemon fainted
        if (aiActivePokemon.isFainted()) {
            System.out.println("AI's " + aiActivePokemon.getName() + " fainted!");
            // Check if AI has any Pokemon left
            if (!aiPlayer.hasAvailablePokemon()) {
                // Player wins!
                checkBattleEnd();
                return;
            }
            // AI must switch to next available Pokemon
            switchAIToNextPokemon();
        }

        // Check if battle ended
        if (checkBattleEnd()) {
            return;
        }

        // Increment turn number
        turnNumber++;

        // Execute AI turn automatically (with small delay for UX)
        javax.swing.Timer aiTurnTimer = new javax.swing.Timer(500, e -> {
            executeAITurnSimplified();
        });
        aiTurnTimer.setRepeats(false);
        aiTurnTimer.start();
    }

    /**
     * Executes player switch during their turn.
     *
     * @param pokemon The Pokemon to switch to
     */
    public void executePlayerSwitch(Pokemon pokemon) {
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            presenter.prepareFailView("No active battle!");
            return;
        }

        if (!playerTeam.contains(pokemon)) {
            presenter.prepareFailView("This Pokemon is not in your team!");
            return;
        }

        if (pokemon.isFainted()) {
            presenter.prepareFailView("This Pokemon has fainted!");
            return;
        }

        // Switch Pokemon
        this.playerActivePokemon = pokemon;
        System.out.println("You switched to " + pokemon.getName());

        // Update presenter
        presenter.updateTeams(playerTeam, aiPlayer);

        // Increment turn number
        turnNumber++;

        // Execute AI turn
        executeAITurnSimplified();
    }

    /**
     * Executes the AI's turn automatically (simplified version).
     */
    private void executeAITurnSimplified() {
        if (currentBattle == null || !"IN_PROGRESS".equals(currentBattle.getBattleStatus())) {
            return;
        }

        Pokemon aiActivePokemon = aiPlayer.getActivePokemon();
        if (aiActivePokemon == null || aiActivePokemon.isFainted()) {
            return;
        }

        if (playerActivePokemon == null || playerActivePokemon.isFainted()) {
            return;
        }

        // AI chooses a random move (simplified - could use AI decision graph here)
        Move aiMove = chooseRandomMove(aiActivePokemon);
        if (aiMove == null) {
            System.out.println("AI has no valid moves!");
            return;
        }

        // Execute AI's move
        String turnResult = executeMoveSimple(aiActivePokemon, aiMove, playerActivePokemon);
        System.out.println("AI turn: " + turnResult);

        // Update view model
        presenter.updateTeams(playerTeam, aiPlayer);

        // Check if player's Pokemon fainted
        if (playerActivePokemon.isFainted()) {
            System.out.println("Your " + playerActivePokemon.getName() + " fainted!");
            // Check if player has any Pokemon left
            if (!hasAvailablePlayerPokemon()) {
                // AI wins!
                checkBattleEnd();
                return;
            }
            // Player must switch to next available Pokemon
            switchPlayerToNextPokemon();
        }

        // Check if battle ended
        checkBattleEnd();

        // Increment turn number
        turnNumber++;
    }

    /**
     * Checks if the battle has ended and updates accordingly.
     *
     * @return true if battle ended, false otherwise
     */
    private boolean checkBattleEnd() {
        // Check if all player Pokemon fainted
        boolean allPlayerFainted = true;
        for (Pokemon pokemon : playerTeam) {
            if (!pokemon.isFainted()) {
                allPlayerFainted = false;
                break;
            }
        }

        // Check if all AI Pokemon fainted
        boolean allAIFainted = !aiPlayer.hasAvailablePokemon();

        if (allPlayerFainted) {
            currentBattle.endBattle(currentBattle.getPlayer2()); // AI wins
            presenter.initializeBattle(currentBattle, playerTeam, aiPlayer);
            return true;
        } else if (allAIFainted) {
            currentBattle.endBattle(currentBattle.getPlayer1()); // Player wins
            // Award currency
            User player = currentBattle.getPlayer1();
            player.addCurrency(500);
            presenter.initializeBattle(currentBattle, playerTeam, aiPlayer);
            return true;
        }

        return false;
    }

    /**
     * Generates an AI opponent with a random or pre-defined deck.
     *
     * @param difficulty The difficulty level
     * @return AIPlayer with a team of 3 Pokemon
     */
    private AIPlayer generateAIDeck(String difficulty) {
        // Ensure Pokemon data is loaded
        if (JSONLoader.allPokemon.isEmpty()) {
            return null;
        }

        // Create AI player
        AIPlayer ai = new AIPlayer("AI Opponent", difficulty);

        // Generate team based on difficulty
        List<Pokemon> aiTeam = new ArrayList<>();

        if ("easy".equalsIgnoreCase(difficulty)) {
            // Easy: Pick weaker Pokemon (low IDs)
            List<Pokemon> weakPokemon = new ArrayList<>();
            for (Pokemon p : JSONLoader.allPokemon) {
                if (p.getId() <= 151) { // Gen 1 Pokemon
                    weakPokemon.add(p);
                }
            }
            Collections.shuffle(weakPokemon);
            for (int i = 0; i < 3 && i < weakPokemon.size(); i++) {
                aiTeam.add(weakPokemon.get(i).copy());
            }
        } else if ("hard".equalsIgnoreCase(difficulty)) {
            // Hard: Pick legendary/strong Pokemon
            List<Pokemon> strongPokemon = new ArrayList<>();
            for (Pokemon p : JSONLoader.allPokemon) {
                if (p.getStats().getHp() > 80) { // High HP Pokemon
                    strongPokemon.add(p);
                }
            }
            Collections.shuffle(strongPokemon);
            for (int i = 0; i < 3 && i < strongPokemon.size(); i++) {
                aiTeam.add(strongPokemon.get(i).copy());
            }
        } else {
            // Medium: Random Pokemon
            List<Pokemon> shuffled = new ArrayList<>(JSONLoader.allPokemon);
            Collections.shuffle(shuffled);
            for (int i = 0; i < 3 && i < shuffled.size(); i++) {
                aiTeam.add(shuffled.get(i).copy());
            }
        }

        // Set team and active Pokemon
        ai.setTeam(aiTeam);
        if (!aiTeam.isEmpty()) {
            ai.setActivePokemon(aiTeam.get(0));
        }

        return ai;
    }

    /**
     * Gets the current battle.
     */
    public Battle getCurrentBattle() {
        return currentBattle;
    }

    /**
     * Gets the player's active Pokemon.
     */
    public Pokemon getPlayerActivePokemon() {
        return playerActivePokemon;
    }

    /**
     * Gets the player's team.
     */
    public List<Pokemon> getPlayerTeam() {
        return playerTeam;
    }

    /**
     * Gets the AI player.
     */
    public AIPlayer getAiPlayer() {
        return aiPlayer;
    }

    /**
     * Simplified move execution.
     * Directly applies damage based on move power.
     */
    private String executeMoveSimple(Pokemon attacker, Move move, Pokemon defender) {
        if (move == null || defender == null) {
            return "Move failed!";
        }

        // Calculate damage (simplified - real calculation would consider types, stats, etc.)
        int damage = move.getPower() != null ? move.getPower() : 20;
        int currentHP = defender.getStats().getHp();
        int newHP = Math.max(0, currentHP - damage);

        // Update HP
        Stats newStats = new Stats(
                newHP,
                defender.getStats().getAttack(),
                defender.getStats().getDefense(),
                defender.getStats().getSpAttack(),
                defender.getStats().getSpDefense(),
                defender.getStats().getSpeed()
        );
        defender.setStats(newStats);

        return attacker.getName() + " used " + move.getName() + "! " +
                defender.getName() + " took " + damage + " damage! (HP: " + newHP + "/" + currentHP + ")";
    }

    /**
     * Chooses a random move for a Pokemon.
     */
    private Move chooseRandomMove(Pokemon pokemon) {
        if (pokemon.getMoves() == null || pokemon.getMoves().isEmpty()) {
            return null;
        }

        // Pick a random move name
        String moveName = pokemon.getMoves().get(new Random().nextInt(pokemon.getMoves().size()));

        // Look up the move in JSONLoader
        for (Move move : JSONLoader.allMoves) {
            if (move.getName().equalsIgnoreCase(moveName)) {
                return move;
            }
        }

        // If move not found, create a basic tackle move
        Move tackle = new Move();
        tackle.setName("Tackle");
        tackle.setPower(40);
        return tackle;
    }

    /**
     * Switches AI to next available Pokemon.
     */
    private void switchAIToNextPokemon() {
        for (Pokemon pokemon : aiPlayer.getTeam()) {
            if (!pokemon.isFainted()) {
                aiPlayer.setActivePokemon(pokemon);
                System.out.println("AI switched to " + pokemon.getName());
                return;
            }
        }
    }

    /**
     * Switches player to next available Pokemon.
     */
    private void switchPlayerToNextPokemon() {
        for (Pokemon pokemon : playerTeam) {
            if (!pokemon.isFainted()) {
                playerActivePokemon = pokemon;
                System.out.println("You switched to " + pokemon.getName());
                return;
            }
        }
    }

    /**
     * Checks if player has any available Pokemon.
     */
    private boolean hasAvailablePlayerPokemon() {
        for (Pokemon pokemon : playerTeam) {
            if (!pokemon.isFainted()) {
                return true;
            }
        }
        return false;
    }
}
