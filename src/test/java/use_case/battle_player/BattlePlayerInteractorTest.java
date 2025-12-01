package use_case.battle_player;

import entities.Battle;
import entities.Pokemon;
import entities.Stats;
import entities.Turn;
import entities.User;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattlePlayerInteractorTest extends TestCase {

    public void testBattleNotFound() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(null);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        FixedTurn turn = new FixedTurn("no battle");
        interactor.execute(new BattlePlayerInputData(turn));

        assertEquals("battle not found", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
        assertFalse(turn.wasExecuted());
    }

    public void testBattleAlreadyCompleted() {
        User player1 = userWithPokemon("Ash", 100, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("Gary", 80, pokemonWithHp("Squirtle", 35));
        Battle battle = new Battle(1, player1, player2);
        battle.endBattle(player1);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        FixedTurn turn = new FixedTurn("completed");
        interactor.execute(new BattlePlayerInputData(turn));

        assertEquals("battle is already completed", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
        assertFalse(turn.wasExecuted());
    }

    public void testBattleNotInProgress() {
        Battle battle = new Battle(
                2,
                userWithPokemon("Ash", 90, pokemonWithHp("Bulbasaur", 40)),
                userWithPokemon("Gary", 70, pokemonWithHp("Squirtle", 35))
        );
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        FixedTurn turn = new FixedTurn("pending");
        interactor.execute(new BattlePlayerInputData(turn));

        assertEquals("battle is not in progress", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
        assertFalse(turn.wasExecuted());
    }

    public void testInvalidTurn() {
        Battle battle = battleInProgress(
                userWithPokemon("Ash", 110, pokemonWithHp("Bulbasaur", 40)),
                userWithPokemon("Gary", 90, pokemonWithHp("Squirtle", 35))
        );
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        interactor.execute(new BattlePlayerInputData(null));

        assertEquals("turn is invalid", presenter.errorMessage);
        assertNull(presenter.outputData);
        assertNull(gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
    }

    public void testBattleContinuesWithAvailablePokemon() {
        User player1 = userWithPokemon("Ash", 200, pokemonWithHp("Bulbasaur", 40));
        User player2 = userWithPokemon("Gary", 150, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        FixedTurn turn = new FixedTurn("regular hit");
        interactor.execute(new BattlePlayerInputData(turn));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertSame(turn, presenter.outputData.getTurn());
        assertSame(battle, presenter.outputData.getBattle());
        assertEquals("regular hit", presenter.outputData.getTurnResult());
        assertFalse(presenter.outputData.isBattleEnded());
        assertEquals("IN_PROGRESS", presenter.outputData.getBattleStatus());
        assertEquals("IN_PROGRESS", battle.getBattleStatus());
        assertSame(battle, gateway.savedBattle);
        assertTrue(gateway.savedUsers.isEmpty());
        assertEquals(200, player1.getCurrency());
        assertEquals(150, player2.getCurrency());
        assertTrue(turn.wasExecuted());
    }

    public void testPlayerOneWinsAwardsCurrency() {
        User player1 = userWithPokemon(
                "Ash",
                220,
                pokemonWithHp("Pikachu", 25),
                pokemonWithHp("Charizard", 10)
        );
        User player2 = userWithPokemon("Gary", 180, pokemonWithHp("Squirtle", 0));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        int player1Start = player1.getCurrency();
        int player2Start = player2.getCurrency();
        FixedTurn turn = new FixedTurn("player one finishing blow");

        interactor.execute(new BattlePlayerInputData(turn));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", presenter.outputData.getBattleStatus());
        assertSame(player1, battle.getWinner());
        assertEquals(player1Start + 500, player1.getCurrency());
        assertEquals(player2Start + 100, player2.getCurrency());
        assertEquals(2, gateway.savedUsers.size());
        assertSame(player1, gateway.savedUsers.get(0));
        assertSame(player2, gateway.savedUsers.get(1));
        assertSame(battle, gateway.savedBattle);
        assertTrue(turn.wasExecuted());
    }

    public void testPlayerTwoWinsAwardsCurrency() {
        User player1 = userWithPokemon(
                "Ash",
                300,
                pokemonWithHp("Bulbasaur", 0),
                pokemonWithHp("Pikachu", 0)
        );
        User player2 = userWithPokemon("Gary", 400, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        int player1Start = player1.getCurrency();
        int player2Start = player2.getCurrency();
        FixedTurn turn = new FixedTurn("finishing blow");

        interactor.execute(new BattlePlayerInputData(turn));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", presenter.outputData.getBattleStatus());
        assertSame(player2, battle.getWinner());
        assertEquals(player2Start + 500, player2.getCurrency());
        assertEquals(player1Start + 100, player1.getCurrency());
        assertEquals(2, gateway.savedUsers.size());
        assertSame(player2, gateway.savedUsers.get(0));
        assertSame(player1, gateway.savedUsers.get(1));
        assertSame(battle, gateway.savedBattle);
        assertTrue(turn.wasExecuted());
    }

    public void testDrawEndsBattleWithoutAwardingCurrency() {
        User player1 = userWithPokemon("Ash", 150);
        User player2 = userWithPokemon("Gary", 120);
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        int player1Start = player1.getCurrency();
        int player2Start = player2.getCurrency();
        FixedTurn turn = new FixedTurn("stalemate");

        interactor.execute(new BattlePlayerInputData(turn));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", presenter.outputData.getBattleStatus());
        assertNull(battle.getWinner());
        assertEquals(player1Start, player1.getCurrency());
        assertEquals(player2Start, player2.getCurrency());
        assertTrue(gateway.savedUsers.isEmpty());
        assertSame(battle, gateway.savedBattle);
        assertTrue(turn.wasExecuted());
    }

    public void testDrawWhenPlayersAreNull() {
        Battle battle = new Battle(77, null, null);
        battle.startBattle();

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        FixedTurn turn = new FixedTurn("no players");
        interactor.execute(new BattlePlayerInputData(turn));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", presenter.outputData.getBattleStatus());
        assertNull(battle.getWinner());
        assertTrue(gateway.savedUsers.isEmpty());
        assertSame(battle, gateway.savedBattle);
        assertTrue(turn.wasExecuted());
    }

    public void testPlayerWithNullPokemonListCountsAsNoPokemon() {
        User player1 = new NullOwnedPokemonUser("Ash", 210);
        User player2 = userWithPokemon("Gary", 180, pokemonWithHp("Squirtle", 35));
        Battle battle = battleInProgress(player1, player2);

        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryBattleGateway gateway = new InMemoryBattleGateway(battle);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(gateway, presenter);

        int player1Start = player1.getCurrency();
        int player2Start = player2.getCurrency();
        FixedTurn turn = new FixedTurn("null list finishing blow");

        interactor.execute(new BattlePlayerInputData(turn));

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isBattleEnded());
        assertEquals("COMPLETED", presenter.outputData.getBattleStatus());
        assertSame(player2, battle.getWinner());
        assertEquals(player2Start + 500, player2.getCurrency());
        assertEquals(player1Start + 100, player1.getCurrency());
        assertEquals(2, gateway.savedUsers.size());
        assertSame(player2, gateway.savedUsers.get(0));
        assertSame(player1, gateway.savedUsers.get(1));
        assertSame(battle, gateway.savedBattle);
        assertTrue(turn.wasExecuted());
    }

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

    private static class NullOwnedPokemonUser extends User {
        NullOwnedPokemonUser(String name, int currency) {
            super(name.hashCode(), name, name + "@example.com", currency);
        }

        @Override
        public List<Pokemon> getOwnedPokemon() {
            return null;
        }
    }

    private static class FixedTurn extends Turn {
        private final String textResult;
        private boolean executed;

        FixedTurn(String textResult) {
            super(10, null, 1);
            this.textResult = textResult;
            this.executed = false;
        }

        @Override
        public void executeTurn() {
            this.result = textResult;
            this.executed = true;
        }

        @Override
        public String getTurnDetails() {
            return textResult;
        }

        boolean wasExecuted() {
            return executed;
        }
    }

    private static class RecordingPresenter implements BattlePlayerOutputBoundary {
        private BattlePlayerOutputData outputData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(BattlePlayerOutputData outputData) {
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private static class InMemoryBattleGateway implements BattlePlayerUserDataAccessInterface {
        private Battle battle;
        private Battle savedBattle;
        private final List<User> savedUsers = new ArrayList<>();

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
    }
}
