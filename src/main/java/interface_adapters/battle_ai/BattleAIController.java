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
    private final BattleAIViewModel viewModel;

    private Battle currentBattle;
    private AIPlayer aiPlayer;
    private List<Pokemon> playerTeam;
    private Pokemon playerActivePokemon;
    private int turnNumber;
    private String lastTurnDescription;

    public BattleAIController(BattlePlayerInputBoundary playerInteractor,
                              BattleAIInputBoundary aiInteractor,
                              BattleAIDataAccessObject dataAccess,
                              BattleAIPresenter presenter,
                              BattleAIViewModel viewModel) {
        this.playerInteractor = playerInteractor;
        this.aiInteractor = aiInteractor;
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.viewModel = viewModel;
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
        // Give AI user the AI Pokemon team so getOpponent() can see them
        for (Pokemon p : aiPlayer.getTeam()) {
            aiUser.addPokemon(p);
        }

        // Update human player's owned Pokemon to reflect battle team
        // (so AI can see opponent's team via Battle object)
        user.getOwnedPokemon().clear();
        user.getOwnedPokemon().addAll(playerTeam);

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
        this.lastTurnDescription = turnResult;

        // Update view model
        presenter.updateTeams(playerTeam, aiPlayer);

        // Check if AI Pokemon fainted
        if (aiActivePokemon.isFainted()) {
            String faintMessage = "AI's " + aiActivePokemon.getName() + " fainted!";
            System.out.println(faintMessage);
            this.lastTurnDescription += " " + faintMessage;

            // Check if AI has any Pokemon left
            if (!aiPlayer.hasAvailablePokemon()) {
                // Player wins!
                checkBattleEnd();
                return;
            }
            // AI must switch to next available Pokemon
            switchAIToNextPokemon();
            String switchMessage = "AI sent out " + aiPlayer.getActivePokemon().getName() + "!";
            System.out.println(switchMessage);
            this.lastTurnDescription += " " + switchMessage;
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

        // Find the Pokemon in our team by ID (reference equality may fail)
        Pokemon targetPokemon = null;
        for (Pokemon p : playerTeam) {
            if (p.getId() == pokemon.getId()) {
                targetPokemon = p;
                break;
            }
        }

        if (targetPokemon == null) {
            presenter.prepareFailView("This Pokemon is not in your team!");
            return;
        }

        if (targetPokemon.isFainted()) {
            presenter.prepareFailView("This Pokemon has fainted!");
            return;
        }

        // Switch Pokemon
        this.playerActivePokemon = targetPokemon;
        System.out.println("You switched to " + targetPokemon.getName());

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

        System.out.println("\n=== AI TURN START ===");
        System.out.println("AI's Pokemon: " + aiActivePokemon.getName() + " (HP: " + aiActivePokemon.getStats().getHp() + ")");
        System.out.println("Player's Pokemon: " + playerActivePokemon.getName() + " (HP: " + playerActivePokemon.getStats().getHp() + ")");
        System.out.println("AI Difficulty: " + aiPlayer.getDifficulty());

        // Update player's owned Pokemon to reflect battle team (for AI to see)
        updatePlayerTeamInBattle();

        // Try to use LangGraph AI decision
        Move aiMove = null;
        try {
            System.out.println("Attempting to use LangGraph AI decision engine...");
            aiMove = aiPlayer.chooseMove(currentBattle);
            if (aiMove != null && aiMove.getName() != null && !aiMove.getName().isEmpty()) {
                System.out.println("✓ LangGraph AI chose move: " + aiMove.getName() + " (Power: " + aiMove.getPower() + ")");
            } else {
                System.out.println("✗ LangGraph returned null/empty move, falling back to random selection");
                aiMove = null;
            }
        } catch (Exception e) {
            System.out.println("✗ LangGraph AI failed: " + e.getMessage());
            System.out.println("Falling back to random move selection");
            aiMove = null;
        }

        // Fallback to random if LangGraph failed
        if (aiMove == null) {
            aiMove = chooseRandomMove(aiActivePokemon);
            if (aiMove != null) {
                System.out.println("Random fallback chose: " + aiMove.getName() + " (Power: " + aiMove.getPower() + ")");
            }
        }

        if (aiMove == null) {
            System.out.println("AI has no valid moves!");
            System.out.println("=== AI TURN END ===\n");
            return;
        }

        // Execute AI's move
        String turnResult = executeMoveSimple(aiActivePokemon, aiMove, playerActivePokemon);
        System.out.println("Turn result: " + turnResult);
        System.out.println("=== AI TURN END ===\n");
        this.lastTurnDescription = turnResult;

        // Update view model
        presenter.updateTeams(playerTeam, aiPlayer);

        // Check if player's Pokemon fainted
        if (playerActivePokemon.isFainted()) {
            String faintMessage = "Your " + playerActivePokemon.getName() + " fainted!";
            System.out.println(faintMessage);
            this.lastTurnDescription += " " + faintMessage;

            // Check if player has any Pokemon left
            if (!hasAvailablePlayerPokemon()) {
                // AI wins!
                checkBattleEnd();
                return;
            }
            // Player must switch to next available Pokemon
            switchPlayerToNextPokemon();
            String switchMessage = "Go, " + playerActivePokemon.getName() + "!";
            System.out.println(switchMessage);
            this.lastTurnDescription += " " + switchMessage;
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
        if (JSONLoader.getInstance().getAllPokemon().isEmpty()) {
            return null;
        }

        // Create AI player
        AIPlayer ai = new AIPlayer("AI Opponent", difficulty);

        // Generate team based on difficulty
        List<Pokemon> aiTeam = new ArrayList<>();

        if ("easy".equalsIgnoreCase(difficulty)) {
            // Easy: Pick actually weak Pokemon (low base stats, unevolved)
            List<Pokemon> weakPokemon = new ArrayList<>();
            for (Pokemon p : JSONLoader.getInstance().getAllPokemon()) {
                // Calculate total base stats
                Stats s = p.getStats();
                int totalStats = s.getHp() + s.getAttack() + s.getDefense() +
                                 s.getSpAttack() + s.getSpDefense() + s.getSpeed();
                // Only pick Pokemon with total stats < 350 (weak/unevolved Pokemon)
                // Also limit to Gen 1-2 for familiarity
                if (totalStats < 350 && p.getId() <= 251) {
                    weakPokemon.add(p);
                }
            }
            Collections.shuffle(weakPokemon);
            for (int i = 0; i < 3 && i < weakPokemon.size(); i++) {
                aiTeam.add(weakPokemon.get(i).copy());
            }
        } else if ("hard".equalsIgnoreCase(difficulty)) {
            // Hard: Pick strong fully-evolved Pokemon (total stats > 500)
            List<Pokemon> strongPokemon = new ArrayList<>();
            for (Pokemon p : JSONLoader.getInstance().getAllPokemon()) {
                Stats s = p.getStats();
                int totalStats = s.getHp() + s.getAttack() + s.getDefense() +
                                 s.getSpAttack() + s.getSpDefense() + s.getSpeed();
                if (totalStats > 500) {
                    strongPokemon.add(p);
                }
            }
            Collections.shuffle(strongPokemon);
            for (int i = 0; i < 3 && i < strongPokemon.size(); i++) {
                aiTeam.add(strongPokemon.get(i).copy());
            }
        } else {
            // Medium: Pick mid-tier Pokemon (total stats 350-480)
            List<Pokemon> midPokemon = new ArrayList<>();
            for (Pokemon p : JSONLoader.getInstance().getAllPokemon()) {
                Stats s = p.getStats();
                int totalStats = s.getHp() + s.getAttack() + s.getDefense() +
                                 s.getSpAttack() + s.getSpDefense() + s.getSpeed();
                if (totalStats >= 350 && totalStats <= 480 && p.getId() <= 386) {
                    midPokemon.add(p);
                }
            }
            Collections.shuffle(midPokemon);
            for (int i = 0; i < 3 && i < midPokemon.size(); i++) {
                aiTeam.add(midPokemon.get(i).copy());
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
     * Move execution using Gen I damage formula.
     * Calculates damage based on stats, types, and move properties.
     */
    private String executeMoveSimple(Pokemon attacker, Move move, Pokemon defender) {
        if (move == null || defender == null) {
            return "Move failed!";
        }

        if (attacker == null) {
            return "No attacker!";
        }

        // Calculate damage using the Gen I formula
        int damage = entities.DamageCalculator.calculateDamage(attacker, defender, move);
        int currentHP = defender.getStats().getHp();
        int newHP = Math.max(0, currentHP - damage);

        // Update HP directly on stats (preserves maxHp)
        defender.getStats().setHp(newHP);

        // Get effectiveness for display
        String effectiveness = entities.DamageCalculator.getEffectivenessDescription(move, defender);

        // Build result message
        StringBuilder result = new StringBuilder();
        result.append(attacker.getName()).append(" used ").append(move.getName()).append("! ");

        if (damage > 0) {
            result.append(defender.getName()).append(" took ").append(damage).append(" damage");
            if (!"normal".equals(effectiveness)) {
                result.append(" (").append(effectiveness).append(")");
            }
            result.append("! (HP: ").append(newHP).append("/").append(defender.getStats().getMaxHp()).append(")");
        } else {
            result.append("It had no effect on ").append(defender.getName()).append("!");
        }

        return result.toString();
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
        for (Move move : JSONLoader.getInstance().getAllMoves()) {
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

    /**
     * Gets the description of the last turn (for battle log).
     */
    public String getLastTurnDescription() {
        return lastTurnDescription;
    }

    /**
     * Gets the view model.
     */
    public BattleAIViewModel getViewModel() {
        return viewModel;
    }

    /**
     * Updates the player's team in the battle's User object so AI can see it.
     */
    private void updatePlayerTeamInBattle() {
        if (currentBattle != null && currentBattle.getPlayer1() != null) {
            User player = currentBattle.getPlayer1();
            // Clear and update with battle team
            List<Pokemon> ownedPokemon = player.getOwnedPokemon();
            ownedPokemon.clear();
            ownedPokemon.addAll(playerTeam);
        }
    }
}
