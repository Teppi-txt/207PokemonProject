package ai;

import ai.graph.BattleDecisionState;
import ai.graph.Decision;
import ai.graph.DecisionGraph;
import entities.Deck;
import entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test runner to verify DecisionGraph functionality.
 * Run this class directly to test the graph execution.
 */
public class GraphTestRunner {

    public static void main(String[] args) {
        System.out.println("=== DecisionGraph Test Runner ===\n");

        try {
            // Test 1: Graph creation
            testGraphCreation();

            // Test 2: Graph execution with mock data
            testGraphExecution();

            // Test 3: Different difficulty levels
            testDifficultyLevels();

            System.out.println("\n=== All tests completed successfully! ===");

        } catch (Exception e) {
            System.err.println("\n=== Test failed with error ===");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Test 1: Verify graph can be created without errors
     */
    private static void testGraphCreation() {
        System.out.println("Test 1: Graph Creation");
        System.out.println("----------------------");

        DecisionGraph graph = new DecisionGraph();
        System.out.println("✓ DecisionGraph created successfully");

        DecisionGraph easyGraph = DecisionGraph.buildGraph("easy");
        System.out.println("✓ Easy difficulty graph created");

        DecisionGraph mediumGraph = DecisionGraph.buildGraph("medium");
        System.out.println("✓ Medium difficulty graph created");

        DecisionGraph hardGraph = DecisionGraph.buildGraph("hard");
        System.out.println("✓ Hard difficulty graph created");

        System.out.println();
    }

    /**
     * Test 2: Execute graph with mock battle data
     */
    private static void testGraphExecution() throws Exception {
        System.out.println("Test 2: Graph Execution");
        System.out.println("-----------------------");

        // Create mock Pokemon for AI player
        ArrayList<String> fireTypes = new ArrayList<>(Arrays.asList("fire"));
        ArrayList<String> fireMoves = new ArrayList<>(Arrays.asList("ember", "flame-thrower", "fire-blast", "tackle"));
        Stats charizardStats = new Stats(150, 84, 78, 109, 85, 100);
        Pokemon charizard = new Pokemon("Charizard", 6, fireTypes, charizardStats, fireMoves);

        ArrayList<String> waterTypes = new ArrayList<>(Arrays.asList("water"));
        ArrayList<String> waterMoves = new ArrayList<>(Arrays.asList("water-gun", "hydro-pump", "surf", "tackle"));
        Stats blastoiseStats = new Stats(140, 83, 100, 85, 105, 78);
        Pokemon blastoise = new Pokemon("Blastoise", 9, waterTypes, blastoiseStats, waterMoves);

        // Create mock Pokemon for opponent
        ArrayList<String> grassTypes = new ArrayList<>(Arrays.asList("grass", "poison"));
        ArrayList<String> grassMoves = new ArrayList<>(Arrays.asList("vine-whip", "razor-leaf", "solar-beam", "tackle"));
        Stats venusaurStats = new Stats(145, 82, 83, 100, 100, 80);
        Pokemon venusaur = new Pokemon("Venusaur", 3, grassTypes, venusaurStats, grassMoves);

        // Create AI player
        AIPlayer aiPlayer = new AIPlayer("Test AI", "medium");
        aiPlayer.addPokemonToTeam(charizard);
        aiPlayer.addPokemonToTeam(blastoise);
        aiPlayer.setActivePokemon(charizard);

        // Create opponent player (mock implementation)
        Player opponent = createMockOpponent(venusaur);

        // Create mock battle
        User user1 = createMockUser("AI Player");
        User user2 = createMockUser("Opponent");
        Battle battle = new Battle(1, user1, user2);
        battle.startBattle();

        // Create initial state
        List<Turn> turnHistory = new ArrayList<>();
        BattleDecisionState initialState = new BattleDecisionState(
            battle, aiPlayer, opponent, turnHistory, "medium"
        );

        System.out.println("Created battle state:");
        System.out.println("  AI Pokemon: " + aiPlayer.getActivePokemon().getName());
        System.out.println("  Opponent Pokemon: " + opponent.getActivePokemon().getName());
        System.out.println("  Difficulty: medium");
        System.out.println();

        // Execute the graph
        System.out.println("Executing DecisionGraph...");
        DecisionGraph graph = new DecisionGraph();
        Decision decision = graph.execute(initialState);

        // Verify result
        if (decision != null) {
            System.out.println("✓ Graph executed successfully");
            System.out.println("\nDecision details:");
            System.out.println("  " + decision.toString());

            if (decision.isMove() && decision.getSelectedMove() != null) {
                System.out.println("  Move name: " + decision.getSelectedMove().getName());
            } else if (decision.isSwitch() && decision.getSwitchTarget() != null) {
                System.out.println("  Switch to: " + decision.getSwitchTarget().getName());
            }
        } else {
            throw new Exception("Graph returned null decision");
        }

        System.out.println();
    }

    /**
     * Test 3: Verify different difficulty levels work
     */
    private static void testDifficultyLevels() throws Exception {
        System.out.println("Test 3: Difficulty Levels");
        System.out.println("-------------------------");

        // Create simple test scenario
        ArrayList<String> types = new ArrayList<>(Arrays.asList("normal"));
        ArrayList<String> moves = new ArrayList<>(Arrays.asList("tackle", "scratch"));
        Stats stats = new Stats(100, 50, 50, 50, 50, 50);

        Pokemon pokemon1 = new Pokemon("TestMon1", 1, types, stats, moves);
        Pokemon pokemon2 = new Pokemon("TestMon2", 2, types, stats, moves);

        AIPlayer aiPlayer = new AIPlayer("Test AI");
        aiPlayer.addPokemonToTeam(pokemon1);
        aiPlayer.setActivePokemon(pokemon1);

        Player opponent = createMockOpponent(pokemon2);
        User user1 = createMockUser("AI");
        User user2 = createMockUser("Opponent");
        Battle battle = new Battle(2, user1, user2);

        // Test each difficulty
        String[] difficulties = {"easy", "medium", "hard"};
        for (String difficulty : difficulties) {
            BattleDecisionState state = new BattleDecisionState(
                battle, aiPlayer, opponent, new ArrayList<>(), difficulty
            );

            DecisionGraph graph = DecisionGraph.buildGraph(difficulty);
            Decision decision = graph.execute(state);

            if (decision != null) {
                System.out.println("✓ " + difficulty + " difficulty executed successfully");
            } else {
                throw new Exception(difficulty + " difficulty failed");
            }
        }

        System.out.println();
    }

    /**
     * Create a mock opponent player (serializable)
     */
    private static Player createMockOpponent(Pokemon activePokemon) {
        return new MockPlayer(activePokemon);
    }

    /**
     * Serializable mock player implementation
     */
    private static class MockPlayer implements Player, java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private Pokemon active;
        private List<Pokemon> team;

        public MockPlayer(Pokemon activePokemon) {
            this.active = activePokemon;
            this.team = new ArrayList<>(Arrays.asList(activePokemon));
        }

        @Override
        public String getName() {
            return "Mock Opponent";
        }

        @Override
        public Deck getDeck() {
            return new Deck();
        }

        @Override
        public Move chooseMove(Battle battle) {
            return new Move().setName("tackle").setType("normal").setPower(40);
        }

        @Override
        public Pokemon getActivePokemon() {
            return active;
        }

        @Override
        public void switchPokemon(Pokemon pokemon) {
            active = pokemon;
        }

        @Override
        public List<Pokemon> getTeam() {
            return team;
        }

        @Override
        public void useItem(Item item, Pokemon target) {
        }

        @Override
        public boolean hasAvailablePokemon() {
            return !active.isFainted();
        }

        @Override
        public boolean isDefeated() {
            return active.isFainted();
        }
    }

    /**
     * Create a mock user
     */
    private static User createMockUser(String username) {
        return new User(1, username, "test@example.com", 1000);
    }
}
