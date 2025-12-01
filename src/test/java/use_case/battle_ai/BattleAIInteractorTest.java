package use_case.battle_ai;

import entities.AIPlayer;
import entities.Battle;
import entities.Move;
import entities.Pokemon;
import entities.Stats;
import entities.User;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleAIInteractorTest extends TestCase {


    public void testSetupSuccess() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        gateway.setAllPokemon(createPokemonPool());
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User user = userWithPokemon("Ash", 100, pokemonWithHp("Pikachu", 50));
        List<Pokemon> playerTeam = Arrays.asList(pokemonWithHp("Pikachu", 50));

        interactor.execute(new BattleAIInputData(user, playerTeam, "easy"));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertNotNull(gateway.savedBattle);
        assertEquals("IN_PROGRESS", gateway.savedBattle.getBattleStatus());
    }

    public void testSetupFailsWithNullUser() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        List<Pokemon> playerTeam = Arrays.asList(pokemonWithId("Pikachu", 1, 50));

        // When user is null, isSetupRequest() returns false, so it falls through to "Invalid request"
        interactor.execute(new BattleAIInputData(null, playerTeam, "easy"));

        assertEquals("Invalid request", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testSetupFailsWithEmptyTeam() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User user = userWithPokemon("Ash", 100);

        interactor.execute(new BattleAIInputData(user, new ArrayList<>(), "easy"));

        assertEquals("Invalid setup: user and team required", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    // ==================== Player Move Tests ====================

    public void testPlayerMoveBattleNotFound() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        interactor.execute(new BattleAIInputData(0));

        assertEquals("Battle not found", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerMoveBattleAlreadyCompleted() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Pikachu", 50));
        User player2 = userWithPokemon("AI Trainer", 100, pokemonWithHp("Squirtle", 50));
        Battle battle = new Battle(1, player1, player2);
        battle.endBattle(player1);
        gateway.saveBattle(battle);

        interactor.execute(new BattleAIInputData(0));

        assertEquals("Battle is already completed", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerMoveBattleStateNotFound() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Pikachu", 50));
        User player2 = userWithPokemon("AI Trainer", 100, pokemonWithHp("Squirtle", 50));
        Battle battle = battleInProgress(player1, player2);
        gateway.saveBattle(battle);
        // Don't set user, aiPlayer, or playerActivePokemon

        interactor.execute(new BattleAIInputData(0));

        assertEquals("Battle state not found", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerMoveInvalidMoveIndex() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Pikachu", 50));
        User player2 = userWithPokemon("AI Trainer", 100, pokemonWithHp("Squirtle", 50));
        Battle battle = battleInProgress(player1, player2);

        Pokemon playerPokemon = pokemonWithHp("Pikachu", 50);
        AIPlayer aiPlayer = new AIPlayer("AI Trainer", "easy");
        aiPlayer.setTeam(Arrays.asList(pokemonWithHp("Squirtle", 50)));
        aiPlayer.setActivePokemon(aiPlayer.getTeam().get(0));

        gateway.saveBattle(battle);
        gateway.saveUser(player1);
        gateway.saveAIPlayer(aiPlayer);
        gateway.setPlayerActivePokemon(playerPokemon);

        interactor.execute(new BattleAIInputData(10)); // Invalid index

        assertEquals("Invalid move index", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerMoveSuccess() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User player1 = userWithPokemon("Ash", 200, pokemonWithHp("Pikachu", 100));
        User player2 = userWithPokemon("AI Trainer", 150, pokemonWithHp("Squirtle", 100));
        Battle battle = battleInProgress(player1, player2);

        Pokemon playerPokemon = pokemonWithHp("Pikachu", 100);
        AIPlayer aiPlayer = new AIPlayer("AI Trainer", "easy");
        Pokemon aiPokemon = pokemonWithHp("Squirtle", 100);
        aiPlayer.setTeam(Arrays.asList(aiPokemon));
        aiPlayer.setActivePokemon(aiPokemon);

        gateway.saveBattle(battle);
        gateway.saveUser(player1);
        gateway.saveAIPlayer(aiPlayer);
        gateway.setPlayerActivePokemon(playerPokemon);
        gateway.savePlayerTeam(Arrays.asList(playerPokemon));
        gateway.addMove(createMove("tackle", "normal", 40));

        interactor.execute(new BattleAIInputData(0));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertSame(battle, presenter.outputData.getBattle());
        assertFalse(presenter.outputData.isBattleEnded());
        assertEquals("IN_PROGRESS", battle.getBattleStatus());
    }

    public void testPlayerMoveEndsGamePlayerWins() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        // Player1 has healthy Pokemon, Player2 (AI) has all fainted Pokemon
        User player1 = userWithPokemon("Ash", 200, pokemonWithHp("Pikachu", 100));
        // Player2's owned Pokemon must be fainted for battle to end
        Pokemon faintedSquirtle = pokemonWithHpAndMaxHp("Squirtle", 0, 100);
        User player2 = userWithPokemon("AI Trainer", 150, faintedSquirtle);
        Battle battle = battleInProgress(player1, player2);

        Pokemon playerPokemon = pokemonWithHp("Pikachu", 100);
        AIPlayer aiPlayer = new AIPlayer("AI Trainer", "easy");
        Pokemon aiPokemon = pokemonWithHpAndMaxHp("Squirtle", 0, 100); // Already fainted
        aiPlayer.setTeam(Arrays.asList(aiPokemon));
        aiPlayer.setActivePokemon(aiPokemon);

        gateway.saveBattle(battle);
        gateway.saveUser(player1);
        gateway.saveAIPlayer(aiPlayer);
        gateway.setPlayerActivePokemon(playerPokemon);
        gateway.savePlayerTeam(Arrays.asList(playerPokemon));
        gateway.addMove(createMove("tackle", "normal", 40));

        int player1Start = player1.getCurrency();
        int player2Start = player2.getCurrency();

        interactor.execute(new BattleAIInputData(0));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", battle.getBattleStatus());
        assertEquals(player1Start + 500, player1.getCurrency());
        assertEquals(player2Start + 100, player2.getCurrency());
    }


    public void testPlayerSwitchBattleNotFound() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        interactor.execute(BattleAIInputData.forSwitchById(1));

        assertEquals("Battle not found", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerSwitchBattleAlreadyCompleted() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Pikachu", 50));
        User player2 = userWithPokemon("AI Trainer", 100, pokemonWithHp("Squirtle", 50));
        Battle battle = new Battle(1, player1, player2);
        battle.endBattle(player1);
        gateway.saveBattle(battle);

        interactor.execute(BattleAIInputData.forSwitchById(1));

        assertEquals("Battle is already completed", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerSwitchPokemonNotFound() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Pikachu", 50));
        User player2 = userWithPokemon("AI Trainer", 100, pokemonWithHp("Squirtle", 50));
        Battle battle = battleInProgress(player1, player2);

        Pokemon playerPokemon = pokemonWithHp("Pikachu", 50);
        gateway.saveBattle(battle);
        gateway.savePlayerTeam(Arrays.asList(playerPokemon));

        interactor.execute(BattleAIInputData.forSwitchById(9999)); // Non-existent ID

        assertEquals("Pokemon not found in team", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerSwitchToFaintedPokemon() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        User player1 = userWithPokemon("Ash", 100, pokemonWithId("Pikachu", 1, 50));
        User player2 = userWithPokemon("AI Trainer", 100, pokemonWithId("Squirtle", 2, 50));
        Battle battle = battleInProgress(player1, player2);

        Pokemon faintedPokemon = pokemonWithIdAndHp("Charizard", 100, 0, 100); // ID=100, currentHp=0, maxHp=100
        gateway.saveBattle(battle);
        gateway.savePlayerTeam(Arrays.asList(faintedPokemon));

        interactor.execute(BattleAIInputData.forSwitchById(100)); // Use explicit positive ID

        assertEquals("Cannot switch to fainted Pokemon", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    public void testPlayerSwitchSuccess() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway();
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        Pokemon p1Pokemon = pokemonWithId("Pikachu", 1, 50);
        Pokemon p1Pokemon2 = pokemonWithId("Charizard", 2, 80);
        User player1 = userWithPokemon("Ash", 200, p1Pokemon, p1Pokemon2);
        User player2 = userWithPokemon("AI Trainer", 150, pokemonWithId("Squirtle", 3, 50));
        Battle battle = battleInProgress(player1, player2);

        Pokemon activePokemon = pokemonWithId("Pikachu", 10, 50);
        Pokemon switchTarget = pokemonWithId("Charizard", 20, 80);
        AIPlayer aiPlayer = new AIPlayer("AI Trainer", "easy");
        Pokemon aiPokemon = pokemonWithId("Squirtle", 30, 50);
        aiPlayer.setTeam(Arrays.asList(aiPokemon));
        aiPlayer.setActivePokemon(aiPokemon);

        gateway.saveBattle(battle);
        gateway.saveUser(player1);
        gateway.saveAIPlayer(aiPlayer);
        gateway.setPlayerActivePokemon(activePokemon);
        gateway.savePlayerTeam(Arrays.asList(activePokemon, switchTarget));
        gateway.addMove(createMove("tackle", "normal", 40));

        interactor.execute(BattleAIInputData.forSwitchById(20)); // switchTarget's ID

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertSame(battle, presenter.outputData.getBattle());
    }

    // ==================== Helper Methods ====================

    private Pokemon pokemonWithHp(String name, int hp) {
        Stats stats = new Stats(hp, 10, 10, 10, 10, 10);
        ArrayList<String> types = new ArrayList<>();
        types.add("normal");
        ArrayList<String> moves = new ArrayList<>();
        moves.add("tackle");
        return new Pokemon(name, Math.abs(name.hashCode()), types, stats, moves);
    }

    private Pokemon pokemonWithId(String name, int id, int hp) {
        Stats stats = new Stats(hp, 10, 10, 10, 10, 10);
        ArrayList<String> types = new ArrayList<>();
        types.add("normal");
        ArrayList<String> moves = new ArrayList<>();
        moves.add("tackle");
        return new Pokemon(name, id, types, stats, moves);
    }

    private Pokemon pokemonWithIdAndHp(String name, int id, int currentHp, int maxHp) {
        Stats stats = new Stats(maxHp, 10, 10, 10, 10, 10);
        ArrayList<String> types = new ArrayList<>();
        types.add("normal");
        ArrayList<String> moves = new ArrayList<>();
        moves.add("tackle");
        Pokemon pokemon = new Pokemon(name, id, types, stats, moves);
        if (currentHp < maxHp) {
            pokemon.getStats().setHp(currentHp);
        }
        return pokemon;
    }

    private Pokemon pokemonWithHpAndMaxHp(String name, int currentHp, int maxHp) {
        Stats stats = new Stats(maxHp, 10, 10, 10, 10, 10);
        ArrayList<String> types = new ArrayList<>();
        types.add("normal");
        ArrayList<String> moves = new ArrayList<>();
        moves.add("tackle");
        Pokemon pokemon = new Pokemon(name, Math.abs(name.hashCode()), types, stats, moves);
        if (currentHp < maxHp) {
            pokemon.getStats().setHp(currentHp);
        }
        return pokemon;
    }

    private User userWithPokemon(String name, int currency, Pokemon... pokemons) {
        User user = new User(name.hashCode(), name, name + "@example.com", currency);
        Arrays.stream(pokemons).forEach(user::addPokemon);
        return user;
    }

    private Battle battleInProgress(User player1, User player2) {
        Battle battle = new Battle(99, player1, player2);
        battle.startBattle();
        return battle;
    }

    private Move createMove(String name, String type, int power) {
        return new Move()
                .setName(name)
                .setType(type)
                .setDamageClass("physical")
                .setPower(power)
                .setAccuracy(100)
                .setPriority(0)
                .setEffect("Deals damage");
    }

    private List<Pokemon> createPokemonPool() {
        List<Pokemon> pool = new ArrayList<>();
        // Add Pokemon with low stats for "easy" difficulty
        for (int i = 1; i <= 20; i++) {
            Stats stats = new Stats(50, 30, 30, 30, 30, 30); // Total: 200 < 350
            ArrayList<String> types = new ArrayList<>();
            types.add("normal");
            ArrayList<String> moves = new ArrayList<>();
            moves.add("tackle");
            pool.add(new Pokemon("Pokemon" + i, i, types, stats, moves));
        }
        return pool;
    }


    private static class RecordingPresenter implements BattleAIOutputBoundary {
        private BattleAIOutputData outputData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(BattleAIOutputData outputData) {
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private static class InMemoryBattleGateway implements BattleAIUserDataAccessInterface {
        private User currentUser;
        private Battle battle;
        private Battle savedBattle;
        private List<Pokemon> playerTeam = new ArrayList<>();
        private AIPlayer aiPlayer;
        private Pokemon playerActivePokemon;
        private List<Pokemon> allPokemon = new ArrayList<>();
        private List<Move> allMoves = new ArrayList<>();

        @Override
        public void saveBattle(Battle battle) {
            this.savedBattle = battle;
            this.battle = battle;
        }

        @Override
        public Battle getBattle() {
            return battle;
        }

        @Override
        public void saveUser(User user) {
            this.currentUser = user;
        }

        @Override
        public User getUser() {
            return currentUser;
        }

        @Override
        public List<Pokemon> getAllPokemon() {
            return allPokemon;
        }

        public void setAllPokemon(List<Pokemon> pokemon) {
            this.allPokemon = pokemon;
        }

        @Override
        public void savePlayerTeam(List<Pokemon> team) {
            this.playerTeam = team;
        }

        @Override
        public List<Pokemon> getPlayerTeam() {
            return playerTeam;
        }

        @Override
        public void saveAIPlayer(AIPlayer aiPlayer) {
            this.aiPlayer = aiPlayer;
        }

        @Override
        public AIPlayer getAIPlayer() {
            return aiPlayer;
        }

        @Override
        public void setPlayerActivePokemon(Pokemon pokemon) {
            this.playerActivePokemon = pokemon;
        }

        @Override
        public Pokemon getPlayerActivePokemon() {
            return playerActivePokemon;
        }

        @Override
        public Move getMoveByName(String moveName) {
            for (Move move : allMoves) {
                if (move.getName().equalsIgnoreCase(moveName)) {
                    return move;
                }
            }
            return null;
        }

        public void addMove(Move move) {
            this.allMoves.add(move);
        }
    }
}
