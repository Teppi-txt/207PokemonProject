package use_case.build_deck;

import entities.Deck;
import entities.Pokemon;
import entities.User;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildDeckInteractorTest extends TestCase {
    // helpers
    private Pokemon p(int id, String name) {
        return new Pokemon(name, id, new ArrayList<>(), null, new ArrayList<>());
    }

    private User userWith(Pokemon... pokes) {
        User u = new User(1, "TestUser", "t@test.com", 0);
        Arrays.stream(pokes).forEach(u::addPokemon);
        return u;
    }

    private static class InMemoryDeckGateway implements BuildDeckUserDataAccessInterface {
        private final User user;
        private final List<Deck> decks = new ArrayList<>();
        private Deck savedDeck = null;
        private int nextId = 1;
        int deletedId = -1;

        InMemoryDeckGateway(User user) {
            this.user = user;
        }

        @Override
        public User getUser() {
            return user;
        }

        @Override
        public void saveUser() {

        }

        @Override
        public List<Deck> getDecks() {
            return decks;
        }

        @Override
        public void saveUser(User user) {

        }

        @Override
        public Deck getDeckById(int id) {
            return decks.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        }

        @Override
        public void saveDeck(Deck deck) {
            decks.removeIf(d -> d.getId() == deck.getId());
            decks.add(deck);
            savedDeck = deck;
        }

        @Override
        public void deleteDeck(int id) {
            decks.removeIf(d -> d.getId() == id);
            deletedId = id;
        }

        @Override
        public int getNextDeckId() {
            return nextId++;
        }
    }

    // recording Presenter
    private static class RecordingPresenter implements BuildDeckOutputBoundary {
        BuildDeckOutputData output;
        String error;

        @Override
        public void prepareSuccessView(BuildDeckOutputData outputData) {
            this.output = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.error = errorMessage;
        }
    }

    // tests
    public void testCreateNewDeck_NoPokemon() {
        User user = userWith();
        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);
        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                -1, "First Deck", null, false, false
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertNull(presenter.error);
        assertNotNull(presenter.output);
        assertEquals("First Deck", presenter.output.getDeck().getName());
        assertEquals(0, presenter.output.getDeck().getPokemons().size());
        assertEquals(1, gw.getDecks().size());
    }

    public void testSaveDeckWithPokemons() {
        Pokemon a = p(1, "A");
        Pokemon b = p(2, "B");
        User user = userWith(a, b);

        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);
        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                -1, "Team", List.of(a, b), false, false
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertNull(presenter.error);
        assertEquals(2, presenter.output.getDeck().getPokemons().size());
        assertEquals("Team", presenter.output.getDeck().getName());
        assertEquals(1, gw.getDecks().size());
    }

    public void testPokemonNotOwnedFails() {
        User user = userWith(); // user owns nothing, so they shouldnt be allowed to add pokemon
        Pokemon x = p(50, "X");

        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);
        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                -1, "BadDeck", List.of(x), false, false
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertEquals("You do not own the Pokémon X", presenter.error);
        assertNull(presenter.output);
    }

    public void testOverDeckLimitFails() {
        Pokemon a = p(1, "A");
        Pokemon b = p(2, "B");
        Pokemon c = p(3, "C");
        Pokemon d = p(4, "D"); // too many pokemon for the deck limit, pokemon d shouldnt be added
        User user = userWith(a, b, c, d);

        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);
        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                -1, "TooBig", List.of(a, b, c, d), false, false
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertEquals("You cannot add more than 3 Pokémon.", presenter.error);
        assertNull(presenter.output);
    }

    public void testRandomDeck() {
        Pokemon a = p(1, "A");
        Pokemon b = p(2, "B");
        Pokemon c = p(3, "C");
        User user = userWith(a, b, c);

        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);
        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                -1, "Rnd", null, true, false
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertNull(presenter.error);
        assertEquals(3, presenter.output.getDeck().getPokemons().size());
    }

    public void testEditExistingDeck() {
        Pokemon a = p(1, "A");
        Pokemon b = p(2, "B");
        User user = userWith(a, b);

        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);

        Deck existing = new Deck(1, "OldName");
        existing.addPokemon(a);
        gw.saveDeck(existing);

        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                1, "NewName", List.of(b), false, false
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertNull(presenter.error);
        assertEquals("NewName", presenter.output.getDeck().getName());
        assertEquals(1, presenter.output.getDeck().getPokemons().size());
        assertEquals(2, presenter.output.getDeck().getPokemons().get(0).getID());
    }

    public void testDeleteDeck() {
        User user = userWith();
        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);

        Deck d = new Deck(1, "ToDelete");
        gw.saveDeck(d);

        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                1, null, null, false, true
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertNull(presenter.error);
        assertEquals(1, gw.deletedId);
        assertFalse(gw.getDecks().isEmpty());
    }

    public void testDeleteUnsavedDeckFails() {
        User user = userWith();
        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);
        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                -1, null, null, false, true
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertEquals("Cannot delete an unsaved deck.", presenter.error);
        assertNull(presenter.output);
    }

    public void testDeckNotFoundFails() {
        User user = userWith();
        InMemoryDeckGateway gw = new InMemoryDeckGateway(user);
        RecordingPresenter presenter = new RecordingPresenter();

        BuildDeckInputData input = new BuildDeckInputData(
                999, "Name", null, false, false
        );

        BuildDeckInteractor interactor = new BuildDeckInteractor(gw, presenter);
        interactor.execute(input);

        assertEquals("Deck with ID 999 not found.", presenter.error);
        assertNull(presenter.output);
    }
}
