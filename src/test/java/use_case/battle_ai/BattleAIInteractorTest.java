package use_case.battle_ai;

import entities.*;
import entities.battle.AIPlayer;
import entities.battle.Battle;
import entities.battle.Move;
import entities.battle.Stats;
import entities.user.User;
import junit.framework.TestCase;
import java.util.*;

public class BattleAIInteractorTest extends TestCase {

    private RecordingPresenter presenter;
    private InMemoryBattleGateway gateway;
    private BattleAIInteractor interactor;

    @Override
    protected void setUp() {
        presenter = new RecordingPresenter();
        gateway = new InMemoryBattleGateway();
        interactor = new BattleAIInteractor(gateway, presenter);
    }

    // Subclasses to hit defensive branches
    private static class NullUserSetup extends BattleAIInputData {
        NullUserSetup() { super(null, new ArrayList<>(), "easy"); }
        @Override public boolean isSetupRequest() { return true; }
        @Override public User getUser() { return null; }
    }

    private static class NullTeamSetup extends BattleAIInputData {
        private final User u;
        NullTeamSetup(User u) { super(u, null, "easy"); this.u = u; }
        @Override public boolean isSetupRequest() { return true; }
        @Override public User getUser() { return u; }
        @Override public List<Pokemon> getPlayerTeam() { return null; }
    }

    private static class NegativeIndexMove extends BattleAIInputData {
        NegativeIndexMove() { super(0); }
        @Override public boolean isPlayerMoveRequest() { return true; }
        @Override public int getMoveIndex() { return -1; }
    }


    public void testSetupSuccess() {
        gateway.setAllPokemon(createPool());
        User user = user("Ash", pokemon("Pikachu", 1, 50));
        interactor.execute(new BattleAIInputData(user, user.getOwnedPokemon(), "medium"));
        assertNull(presenter.errorMessage);

        // Test null difficulty defaults to medium
        setUp();
        gateway.setAllPokemon(createPool());
        user = user("Ash", pokemon("Pikachu", 1, 50));
        interactor.execute(new BattleAIInputData(user, user.getOwnedPokemon(), null));
        assertNull(presenter.errorMessage);
    }

    public void testSetupValidation() {
        // Empty team
        User user = user("Ash", pokemon("Pikachu", 1, 50));
        interactor.execute(new BattleAIInputData(user, new ArrayList<>(), "easy"));
        assertEquals("Invalid setup: user and team required", presenter.errorMessage);

        // Null user (defensive)
        setUp();
        interactor.execute(new NullUserSetup());
        assertEquals("Invalid setup: user and team required", presenter.errorMessage);

        // Null team (defensive)
        setUp();
        interactor.execute(new NullTeamSetup(user("Ash", pokemon("Pikachu", 1, 50))));
        assertEquals("Invalid setup: user and team required", presenter.errorMessage);

        // Invalid request (null user via normal path)
        setUp();
        interactor.execute(new BattleAIInputData(null, Arrays.asList(pokemon("Pikachu", 1, 50)), "easy"));
        assertEquals("Invalid request", presenter.errorMessage);
    }

    public void testPlayerMoveValidation() {
        // Battle not found
        interactor.execute(new BattleAIInputData(0));
        assertEquals("Battle not found", presenter.errorMessage);

        // Battle completed
        setUp();
        Battle b = battle(); b.endBattle(b.getPlayer1());
        gateway.saveBattle(b);
        interactor.execute(new BattleAIInputData(0));
        assertEquals("Battle is already completed", presenter.errorMessage);

        // User null
        setUp();
        gateway.saveBattle(battle());
        gateway.saveAIPlayer(aiPlayer());
        gateway.setPlayerActivePokemon(pokemon("Pikachu", 1, 100));
        interactor.execute(new BattleAIInputData(0));
        assertEquals("Battle state not found", presenter.errorMessage);

        // AI player null
        setUp();
        gateway.saveBattle(battle());
        gateway.saveUser(user("Ash", pokemon("Pikachu", 1, 100)));
        gateway.setPlayerActivePokemon(pokemon("Pikachu", 1, 100));
        interactor.execute(new BattleAIInputData(0));
        assertEquals("Battle state not found", presenter.errorMessage);

        // Player Pokemon null
        setUp();
        gateway.saveBattle(battle());
        gateway.saveUser(user("Ash", pokemon("Pikachu", 1, 100)));
        gateway.saveAIPlayer(aiPlayer());
        interactor.execute(new BattleAIInputData(0));
        assertEquals("Battle state not found", presenter.errorMessage);

        // Invalid move index
        setUp();
        setupState(pokemon("Pikachu", 1, 100), pokemon("Squirtle", 2, 100));
        interactor.execute(new BattleAIInputData(99));
        assertEquals("Invalid move index", presenter.errorMessage);

        // Negative index (defensive)
        setUp();
        setupState(pokemon("Pikachu", 1, 100), pokemon("Squirtle", 2, 100));
        interactor.execute(new NegativeIndexMove());
        assertEquals("Invalid move index", presenter.errorMessage);
    }

    public void testPlayerMoveSuccess() {
        // Normal move, unknown move fallback
        setupState(pokemon("Pikachu", 1, 100), pokemon("Squirtle", 2, 100));
        interactor.execute(new BattleAIInputData(0));
        assertNull(presenter.errorMessage);
        assertFalse(presenter.outputData.isBattleEnded());
    }

    public void testPlayerMovePlayerWins() {
        // AI Pokemon fainted, no backup - player wins
        Pokemon aiPoke = pokemon("Squirtle", 2, 1, 100);
        setupStateWithAITeam(pokemon("Pikachu", 1, 100), Arrays.asList(aiPoke));
        gateway.addMove(move("hyperbeam", 200));
        interactor.execute(new BattleAIInputData(0));
        assertTrue(presenter.outputData.isBattleEnded());
    }

    public void testPlayerMoveAISwitches() {
        // AI Pokemon faints, has backup
        Pokemon ai1 = pokemon("Squirtle", 2, 1, 100);
        Pokemon ai2 = pokemon("Charmander", 3, 100);
        setupStateWithAITeam(pokemon("Pikachu", 1, 100), Arrays.asList(ai1, ai2));
        gateway.addMove(move("hyperbeam", 200));
        interactor.execute(new BattleAIInputData(0));
        assertNotNull(presenter.outputData.getAiSwitchedTo());
    }

    public void testPlayerMoveAIWins() {
        // Player Pokemon faints, no backup - AI wins
        Pokemon playerPoke = pokemon("Pikachu", 1, 1, 100);
        User p1 = user("Ash", playerPoke);
        setupCustom(p1, playerPoke, Arrays.asList(playerPoke), pokemon("Squirtle", 2, 100));
        gateway.addMove(move("tackle", 5));
        gateway.addMove(move("hyperbeam", 200));
        interactor.execute(new BattleAIInputData(0));
        assertTrue(presenter.outputData.isBattleEnded());
    }

    public void testPlayerMovePlayerSwitches() {
        // Player Pokemon faints, has backup
        Pokemon active = pokemon("Pikachu", 1, 1, 100);
        Pokemon backup = pokemon("Charizard", 2, 100);
        User p1 = user("Ash", active, backup);
        setupCustom(p1, active, Arrays.asList(active, backup), pokemon("Squirtle", 3, 100));
        gateway.addMove(move("tackle", 5));
        gateway.addMove(move("hyperbeam", 200));
        interactor.execute(new BattleAIInputData(0));
        assertNotNull(presenter.outputData.getPlayerSwitchedTo());
    }

    public void testPlayerMoveEdgeCases() {
        // AI has no move - uses fallback
        setupState(pokemon("Pikachu", 1, 100), pokemonNoMoves("Squirtle", 2, 100));
        gateway.addMove(move("tackle", 5));
        interactor.execute(new BattleAIInputData(0));
        assertNull(presenter.errorMessage);
    }

    public void testPlayerSwitchValidation() {
        // Battle not found
        interactor.execute(BattleAIInputData.forSwitchById(1));
        assertEquals("Battle not found", presenter.errorMessage);

        // Battle completed
        setUp();
        Battle b = battle(); b.endBattle(b.getPlayer1());
        gateway.saveBattle(b);
        interactor.execute(BattleAIInputData.forSwitchById(1));
        assertEquals("Battle is already completed", presenter.errorMessage);

        // Pokemon not found
        setUp();
        gateway.saveBattle(battle());
        gateway.savePlayerTeam(Arrays.asList(pokemon("Pikachu", 1, 50)));
        interactor.execute(BattleAIInputData.forSwitchById(999));
        assertEquals("Pokemon not found in team", presenter.errorMessage);

        // Fainted Pokemon
        setUp();
        gateway.saveBattle(battle());
        gateway.savePlayerTeam(Arrays.asList(pokemon("Pikachu", 99, 0, 100)));
        interactor.execute(BattleAIInputData.forSwitchById(99));
        assertEquals("Cannot switch to fainted Pokemon", presenter.errorMessage);

        // User null
        setUp();
        gateway.saveBattle(battle());
        gateway.savePlayerTeam(Arrays.asList(pokemon("Pikachu", 1, 50), pokemon("Charizard", 2, 80)));
        gateway.saveAIPlayer(aiPlayer());
        interactor.execute(BattleAIInputData.forSwitchById(2));
        assertEquals("Battle state not found", presenter.errorMessage);

        // AI player null
        setUp();
        gateway.saveBattle(battle());
        gateway.savePlayerTeam(Arrays.asList(pokemon("Pikachu", 1, 50), pokemon("Charizard", 2, 80)));
        gateway.saveUser(user("Ash", pokemon("Pikachu", 1, 50)));
        interactor.execute(BattleAIInputData.forSwitchById(2));
        assertEquals("Battle state not found", presenter.errorMessage);
    }

    public void testPlayerSwitchSuccess() {
        setupSwitch(pokemon("Pikachu", 1, 50), pokemon("Charizard", 2, 80), pokemon("Squirtle", 3, 100));
        gateway.addMove(move("tackle", 5));
        interactor.execute(BattleAIInputData.forSwitchById(2));
        assertNull(presenter.errorMessage);
    }

    public void testPlayerSwitchAINoMove() {
        setupSwitch(pokemon("Pikachu", 1, 50), pokemon("Charizard", 2, 80), pokemonNoMoves("Squirtle", 3, 100));
        interactor.execute(BattleAIInputData.forSwitchById(2));
        assertNull(presenter.errorMessage);
        assertFalse(presenter.outputData.isBattleEnded());
    }

    public void testPlayerSwitchPlayerAutoSwitch() {
        // Player switches, AI KOs that Pokemon, player has backup - auto-switch
        Pokemon act = pokemon("Pikachu", 1, 50);
        Pokemon sw = pokemon("Charizard", 2, 1, 80); // low HP, will faint
        Pokemon backup = pokemon("Venusaur", 4, 100); // backup for auto-switch
        User p1 = user("Ash", act, sw, backup);
        setupSwitchCustom(p1, act, Arrays.asList(act, sw, backup), pokemon("Squirtle", 3, 100));
        gateway.addMove(move("hyperbeam", 200));
        interactor.execute(BattleAIInputData.forSwitchById(2)); // switch to Charizard (low HP)
        assertNotNull(presenter.outputData.getPlayerSwitchedTo());
        assertFalse(presenter.outputData.isBattleEnded());
    }

    public void testPlayerSwitchBattleEnds() {
        // AI wins after switch
        Pokemon act = pokemon("Pikachu", 1, 0, 50);
        Pokemon sw = pokemon("Charizard", 2, 1, 80);
        User p1 = user("Ash", act, sw);
        setupSwitchCustom(p1, act, Arrays.asList(act, sw), pokemon("Squirtle", 3, 100));
        gateway.addMove(move("hyperbeam", 200));
        interactor.execute(BattleAIInputData.forSwitchById(2));
        assertTrue(presenter.outputData.isBattleEnded());

        // Player wins (AI no Pokemon)
        setUp();
        Pokemon a = pokemon("Pikachu", 1, 50);
        Pokemon s = pokemon("Charizard", 2, 100);
        Pokemon aiPoke = pokemon("Squirtle", 3, 0, 50);
        User player = user("Ash", a, s);
        User aiUser = user("AI", aiPoke);
        Battle battle = new Battle(99, player, aiUser); battle.startBattle();
        AIPlayer aiP = new AIPlayer("AI", "easy");
        aiP.setTeam(Arrays.asList(aiPoke));
        aiP.setActivePokemon(aiPoke);
        gateway.saveBattle(battle);
        gateway.saveUser(player);
        gateway.saveAIPlayer(aiP);
        gateway.setPlayerActivePokemon(a);
        gateway.savePlayerTeam(Arrays.asList(a, s));
        gateway.addMove(move("tackle", 5));
        interactor.execute(BattleAIInputData.forSwitchById(2));
        assertTrue(presenter.outputData.isBattleEnded());
    }

    public void testPlayerSwitchCompletedDuringSwitch() {
        // Line 302: battle completed after setPlayerActivePokemon
        Pokemon act = pokemon("Pikachu", 1, 50);
        Pokemon sw = pokemon("Charizard", 2, 80);
        User p1 = user("Ash", act, sw);
        User p2 = user("AI", pokemon("Squirtle", 3, 50));
        Battle b = new Battle(99, p1, p2); b.startBattle();
        AIPlayer ai = new AIPlayer("AI", "easy");
        ai.setTeam(Arrays.asList(pokemon("Squirtle", 3, 50)));
        ai.setActivePokemon(ai.getTeam().get(0));
        gateway.saveBattle(b);
        gateway.saveUser(p1);
        gateway.saveAIPlayer(ai);
        gateway.setPlayerActivePokemon(act);
        gateway.savePlayerTeam(Arrays.asList(act, sw));
        gateway.completeBattleOnSwitch = true;
        interactor.execute(BattleAIInputData.forSwitchById(2));
        assertTrue(presenter.outputData.isBattleEnded());
    }

// Helper methods
    private Pokemon pokemon(String name, int id, int hp) { return pokemon(name, id, hp, hp); }

    private Pokemon pokemon(String name, int id, int curHp, int maxHp) {
        Stats s = new Stats(maxHp, 10, 10, 10, 10, 10);
        Pokemon p = new Pokemon(name, id, new ArrayList<>(Arrays.asList("normal")), s, new ArrayList<>(Arrays.asList("tackle")));
        if (curHp < maxHp) p.getStats().setHp(curHp);
        return p;
    }

    private Pokemon pokemonNoMoves(String name, int id, int hp) {
        return new Pokemon(name, id, new ArrayList<>(Arrays.asList("normal")), new Stats(hp, 10, 10, 10, 10, 10), new ArrayList<>());
    }

    private User user(String name, Pokemon... pokes) {
        User u = new User(name.hashCode(), name, name + "@t.com", 200);
        for (Pokemon p : pokes) u.addPokemon(p);
        return u;
    }

    private Battle battle() {
        Battle b = new Battle(99, user("Ash", pokemon("Pikachu", 1, 50)), user("AI", pokemon("Squirtle", 2, 50)));
        b.startBattle();
        return b;
    }

    private AIPlayer aiPlayer() {
        AIPlayer ai = new AIPlayer("AI", "easy");
        ai.setTeam(Arrays.asList(pokemon("Squirtle", 2, 100)));
        ai.setActivePokemon(ai.getTeam().get(0));
        return ai;
    }

    private Move move(String name, int power) {
        return new Move().setName(name).setType("normal").setDamageClass("physical").setPower(power).setAccuracy(100).setPriority(0).setEffect("Damage");
    }

    private void setupState(Pokemon player, Pokemon ai) {
        User p1 = user("Ash", player);
        setupCustom(p1, player, Arrays.asList(player), ai);
    }

    private void setupStateWithAITeam(Pokemon player, List<Pokemon> aiTeam) {
        User p1 = user("Ash", player);
        User p2 = user("AI", aiTeam.toArray(new Pokemon[0]));
        Battle b = new Battle(99, p1, p2); b.startBattle();
        AIPlayer ai = new AIPlayer("AI", "easy");
        ai.setTeam(aiTeam);
        ai.setActivePokemon(aiTeam.get(0));
        gateway.saveBattle(b);
        gateway.saveUser(p1);
        gateway.saveAIPlayer(ai);
        gateway.setPlayerActivePokemon(player);
        gateway.savePlayerTeam(Arrays.asList(player));
    }

    private void setupCustom(User p1, Pokemon active, List<Pokemon> team, Pokemon ai) {
        User p2 = user("AI", ai);
        Battle b = new Battle(99, p1, p2); b.startBattle();
        AIPlayer aiP = new AIPlayer("AI", "easy");
        aiP.setTeam(Arrays.asList(ai));
        aiP.setActivePokemon(ai);
        gateway.saveBattle(b);
        gateway.saveUser(p1);
        gateway.saveAIPlayer(aiP);
        gateway.setPlayerActivePokemon(active);
        gateway.savePlayerTeam(team);
    }

    private void setupSwitch(Pokemon active, Pokemon switchTo, Pokemon ai) {
        User p1 = user("Ash", active, switchTo);
        setupSwitchCustom(p1, active, Arrays.asList(active, switchTo), ai);
    }

    private void setupSwitchCustom(User p1, Pokemon active, List<Pokemon> team, Pokemon ai) {
        User p2 = user("AI", ai);
        Battle b = new Battle(99, p1, p2); b.startBattle();
        AIPlayer aiP = new AIPlayer("AI", "easy");
        aiP.setTeam(Arrays.asList(ai));
        aiP.setActivePokemon(ai);
        gateway.saveBattle(b);
        gateway.saveUser(p1);
        gateway.saveAIPlayer(aiP);
        gateway.setPlayerActivePokemon(active);
        gateway.savePlayerTeam(team);
    }

    private List<Pokemon> createPool() {
        List<Pokemon> pool = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Stats s = new Stats(50, 50, 50, 50, 50, 50);
            pool.add(new Pokemon("P" + i, i, new ArrayList<>(Arrays.asList("normal")), s, new ArrayList<>(Arrays.asList("tackle"))));
        }
        return pool;
    }

// test doubles
    private static class RecordingPresenter implements BattleAIOutputBoundary {
        BattleAIOutputData outputData;
        String errorMessage;
        public void prepareSuccessView(BattleAIOutputData d) { outputData = d; }
        public void prepareFailView(String e) { errorMessage = e; }
    }

    private static class InMemoryBattleGateway implements BattleAIUserDataAccessInterface {
        User user; Battle battle; List<Pokemon> team = new ArrayList<>(), pool = new ArrayList<>();
        List<Move> moves = new ArrayList<>(); AIPlayer ai; Pokemon active;
        boolean completeBattleOnSwitch = false;
        int setActiveCount = 0;

        public void saveBattle(Battle b) { battle = b; }
        public Battle getBattle() { return battle; }
        public void saveUser(User u) { user = u; }
        public User getUser() { return user; }
        public List<Pokemon> getAllPokemon() { return pool; }
        public void setAllPokemon(List<Pokemon> p) { pool = p; }
        public void savePlayerTeam(List<Pokemon> t) { team = t; }
        public List<Pokemon> getPlayerTeam() { return team; }
        public void saveAIPlayer(AIPlayer a) { ai = a; }
        public AIPlayer getAIPlayer() { return ai; }
        public void setPlayerActivePokemon(Pokemon p) {
            active = p;
            setActiveCount++;
            if (completeBattleOnSwitch && setActiveCount >= 2 && battle != null) {
                battle.endBattle(battle.getPlayer1());
            }
        }
        public Pokemon getPlayerActivePokemon() { return active; }
        public Move getMoveByName(String n) {
            for (Move m : moves) if (m.getName().equalsIgnoreCase(n)) return m;
            return null;
        }
        public void addMove(Move m) { moves.add(m); }
    }
}
