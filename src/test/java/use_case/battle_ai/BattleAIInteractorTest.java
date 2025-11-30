package use_case.battle_ai;

import entities.*;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleAIInteractorTest extends TestCase {

    public void testBattleNotFound() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(null);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        interactor.execute(new BattleAIInputData(null, aiPlayer, false));

        assertEquals("Battle not found", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testBattleAlreadyCompleted() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("AI", 80, pokemonWithHp("Squirtle", 35));
        Battle battle = new Battle(1, player1, player2);
        battle.endBattle(player1);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        aiPlayer.addPokemonToTeam(pokemonWithHp("Squirtle", 35));
        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertEquals("Battle is already completed", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testBattleNotInProgress() {
        User player1 = userWithPokemon("Ash", 90, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("AI", 70, pokemonWithHp("Squirtle", 35));
        Battle battle = new Battle(2, player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        aiPlayer.addPokemonToTeam(pokemonWithHp("Squirtle", 35));
        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertEquals("Battle is not in progress", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testAIPlayerNotFound() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("Gary", 80, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        interactor.execute(new BattleAIInputData(battle, null, false));

        assertEquals("AI player not found", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testAIPlayerHasNoAvailablePokemon() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("AI", 80, pokemonWithHp("Squirtle", 0));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        aiPlayer.addPokemonToTeam(pokemonWithHp("Squirtle", 0)); // Fainted Pokemon
        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertEquals("AI player has no available Pokemon", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testAIPlayerNotPartOfBattle() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("Gary", 80, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("DifferentAI");
        aiPlayer.addPokemonToTeam(pokemonWithHp("Pikachu", 50));
        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertEquals("AI player is not part of this battle", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testForcedSwitchFailsToChoosePokemon() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("AI", 80, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        aiPlayer.addPokemonToTeam(pokemonWithHp("Squirtle", 35));
        aiPlayer.setSwitchDecision(null); // AI fails to choose
        interactor.execute(new BattleAIInputData(battle, aiPlayer, true));

        assertEquals("AI failed to choose a Pokemon to switch to", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testAIFailsToChooseMove() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("AI", 80, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        aiPlayer.addPokemonToTeam(pokemonWithHp("Squirtle", 35));
        aiPlayer.setMoveDecision(null); // AI fails to choose move
        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertEquals("AI failed to choose a move", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testBattleContinuesWithAvailablePokemon() {
        User player1 = userWithPokemon("Ash", 200, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("AI", 150, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        Move tackle = createMove("tackle", "normal", 40);
        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        Pokemon aiPokemon = pokemonWithHp("Squirtle", 35);
        aiPlayer.addPokemonToTeam(aiPokemon);
        aiPlayer.setActivePokemon(aiPokemon);
        aiPlayer.setMoveDecision(tackle);

        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertSame(battle, presenter.outputData.getBattle());
        assertFalse(presenter.outputData.isBattleEnded());
        assertEquals("IN_PROGRESS", presenter.outputData.getBattle().getBattleStatus());
        assertSame(battle, gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
        assertEquals(200, player1.getCurrency());
        assertEquals(150, player2.getCurrency());
    }

    public void testAIWinsAwardsCurrency() {
        User player1 = userWithPokemon("Ash", 220, pokemonWithHp("Pikachu", 0));
        User player2 = userWithPokemon("AI", 180, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        Move tackle = createMove("tackle", "normal", 40);
        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        Pokemon aiPokemon = pokemonWithHp("Squirtle", 35);
        aiPlayer.addPokemonToTeam(aiPokemon);
        aiPlayer.setActivePokemon(aiPokemon);
        aiPlayer.setMoveDecision(tackle);

        int player1Start = player1.getCurrency();
        int player2Start = player2.getCurrency();

        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", presenter.outputData.getBattle().getBattleStatus());
        assertSame(player2, battle.getWinner());
        assertEquals(player2Start + 500, player2.getCurrency());
        assertEquals(player1Start + 100, player1.getCurrency());
        assertEquals(2, gateway.savedUsers.size());
        assertSame(battle, gateway.savedBattle);
    }

    public void testOpponentWinsAwardsCurrency() {
        // Player1 has healthy Pokemon, player2 (AI user) has all fainted Pokemon
        User player1 = userWithPokemon("Ash", 300, pokemonWithHp("Bulbasaur", 40));
        // AI user's owned Pokemon are all fainted - this determines battle end
        User player2 = userWithPokemon("AI", 400, pokemonWithHp("Squirtle", 0));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        Move tackle = createMove("tackle", "normal", 40);
        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        // AIPlayer's team has available Pokemon (for validation)
        // But the User's owned Pokemon are fainted (determines battle end)
        Pokemon activePokemon = pokemonWithHp("Charmander", 50);
        aiPlayer.addPokemonToTeam(activePokemon);
        aiPlayer.setActivePokemon(activePokemon);
        aiPlayer.setMoveDecision(tackle);

        int player1Start = player1.getCurrency();
        int player2Start = player2.getCurrency();

        interactor.execute(new BattleAIInputData(battle, aiPlayer, false));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", presenter.outputData.getBattle().getBattleStatus());
        assertSame(player1, battle.getWinner());
        assertEquals(player1Start + 500, player1.getCurrency());
        assertEquals(player2Start + 100, player2.getCurrency());
        assertEquals(2, gateway.savedUsers.size());
        assertSame(battle, gateway.savedBattle);
    }

    public void testForcedSwitchSuccess() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("AI", 100, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattleAIInteractor interactor = new BattleAIInteractor(gateway, presenter);

        MockAIPlayer aiPlayer = new MockAIPlayer("AI");
        Pokemon faintedPokemon = pokemonWithHp("Squirtle", 0);
        Pokemon reservePokemon = pokemonWithHp("Charmander", 50);
        aiPlayer.addPokemonToTeam(faintedPokemon);
        aiPlayer.addPokemonToTeam(reservePokemon);
        aiPlayer.setActivePokemon(faintedPokemon);
        aiPlayer.setSwitchDecision(reservePokemon);

        interactor.execute(new BattleAIInputData(battle, aiPlayer, true));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertFalse(presenter.outputData.isBattleEnded());
        assertEquals("IN_PROGRESS", presenter.outputData.getBattle().getBattleStatus());
        assertNotNull(presenter.outputData.getExecutedTurn());
        assertTrue(presenter.outputData.getExecutedTurn() instanceof entities.SwitchTurn);
        assertSame(battle, gateway.savedBattle);
    }

    // Helper methods

    private Pokemon pokemonWithHp(String name, int hp) {
        Stats stats = new Stats(hp, 10, 10, 10, 10, 10);
        ArrayList<String> types = new ArrayList<>();
        types.add("normal");
        ArrayList<String> moves = new ArrayList<>();
        moves.add("tackle");
        return new Pokemon(name, name.hashCode(), types, stats, moves);
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

    // Mock classes for testing

    private static class MockAIPlayer extends AIPlayer {
        private Move moveDecision;
        private Pokemon switchDecision;
        private boolean moveDecisionSet = false;
        private boolean switchDecisionSet = false;

        MockAIPlayer(String name) {
            super(name, "easy");
        }

        void setMoveDecision(Move move) {
            this.moveDecision = move;
            this.moveDecisionSet = true;
        }

        void setSwitchDecision(Pokemon pokemon) {
            this.switchDecision = pokemon;
            this.switchDecisionSet = true;
        }

        @Override
        public Move chooseMove(Battle battle) {
            if (moveDecisionSet) {
                return moveDecision;
            }
            return super.chooseMove(battle);
        }

        @Override
        public Pokemon decideSwitch(Battle battle) {
            if (switchDecisionSet) {
                return switchDecision;
            }
            return super.decideSwitch(battle);
        }
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
        private Battle battle;
        private Battle savedBattle;
        private final List<User> savedUsers = new ArrayList<>();
        private List<Pokemon> playerTeam = new ArrayList<>();
        private AIPlayer aiPlayer;
        private Pokemon playerActivePokemon;

        InMemoryBattleGateway(Battle battle) {
            this.battle = battle;
        }

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
            savedUsers.add(user);
        }

        @Override
        public User getUser() {
            return battle != null ? battle.getPlayer1() : null;
        }

        @Override
        public List<Pokemon> getAllPokemon() {
            return new ArrayList<>();
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
    }
}
