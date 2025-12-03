package use_case.open_pack;

import entities.Pokemon;
import entities.user.User;

import java.util.ArrayList;
import java.util.List;

import entities.open_pack.Pack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class OpenPackInteractorTest {

    private static class FakeUserDataAccess implements OpenPackUserDataAccessInterface {
        private User user;

        public FakeUserDataAccess(User user) {
            this.user = user;
        }

        @Override
        public User get() {
            return user;
        }

        @Override
        public void saveUser(User user) {
            this.user = user;
        }
    }

    private static class FakePack extends Pack {
        private final List<Pokemon> cards;
        public FakePack(List<Pokemon> cards) {
            super(0, "unit test", new ArrayList<>());
            this.cards = cards;
        }

        @Override
        public List<Pokemon> openPack() {
            return cards;
        }
    }

    @Test
    void successTest() { //can open a pack
        User user = new User(1, "Trainer", "trainer@test.com", 2000);
        Pokemon p1 = new Pokemon("Bulbasaur", 1, new ArrayList<>(), null, new ArrayList<>());
        Pokemon p2 = new Pokemon("Charmander", 4, new ArrayList<>(), null, new ArrayList<>());
        FakeUserDataAccess userData = new FakeUserDataAccess(user);
        FakePack fakePack = new FakePack(List.of(p1, p2));

        OpenPackInputBoundary interactor = getOpenPackInputBoundary(userData, fakePack);
        interactor.execute(new OpenPackInputData());
    }

    @NotNull //sonarqube gave this suggestion
    private static OpenPackInputBoundary getOpenPackInputBoundary(FakeUserDataAccess userData, FakePack fakePack) {
        OpenPackOutputBoundary presenter = new OpenPackOutputBoundary() {
            @Override
            public void prepareSuccessView(OpenPackOutputData outputData) {
                assertEquals(2, outputData.getOpenedCards().size());
                assertEquals(1000, outputData.getRemainingCurrency());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("fail in success test");
            }
        };
        return new OpenPackInteractor(userData, presenter, fakePack); // another sonarqube suggestion
    }

    @Test
    void failureNotEnoughCurrencyTest() { //not enough money
        User user = new User(2, "Broke", "broke@test.com", 2);
        FakeUserDataAccess userData = new FakeUserDataAccess(user);
        FakePack fakePack = new FakePack(new ArrayList<>());

        OpenPackOutputBoundary presenter = new OpenPackOutputBoundary() {
            @Override
            public void prepareSuccessView(OpenPackOutputData outputData) {
                fail("fail of currency test");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Not enough currency!", errorMessage);
            }
        };
        OpenPackInputBoundary interactor = new OpenPackInteractor(userData, presenter, fakePack);
        interactor.execute(new OpenPackInputData());
    }

    @Test
    void duplicatePokemonTest() { // duplicate pokemon
        Pokemon pikachu = new Pokemon("Pikachu", 25, new ArrayList<>(), null, new ArrayList<>());
        Pokemon eevee   = new Pokemon("Eevee", 133, new ArrayList<>(), null, new ArrayList<>());
        User user = new User(3, "Test", "test@test.com", 2000);
        user.addPokemon(pikachu.copy());
        FakeUserDataAccess userData = new FakeUserDataAccess(user);
        OpenPackInputBoundary interactor = getOpenPackInputBoundary(pikachu, eevee, userData);
        interactor.execute(new OpenPackInputData());
    }

    @NotNull //sonarqube
    private static OpenPackInputBoundary getOpenPackInputBoundary(Pokemon pikachu, Pokemon eevee, FakeUserDataAccess userData) {
        FakePack fakePack = new FakePack(List.of(pikachu, eevee));

        OpenPackOutputBoundary presenter = new OpenPackOutputBoundary() {
            @Override
            public void prepareSuccessView(OpenPackOutputData outputData) {
                assertEquals(List.of(true, false), outputData.getDuplicateFlags());
            }
            @Override
            public void prepareFailView(String errorMessage) {
                fail("fail of duplicate test.");
            }
        };
        return new OpenPackInteractor(userData, presenter, fakePack); //sonarqube
    }

    @Test
    void userSavedAfterSuccessTest() { //user is saved
        User user = new User(4, "Save", "save@test.com", 2000);
        Pokemon p = new Pokemon("Bulbasaur", 1, new ArrayList<>(), null, new ArrayList<>());
        FakeUserDataAccess userData = new FakeUserDataAccess(user);
        FakePack fakePack = new FakePack(List.of(p));
        OpenPackOutputBoundary presenter = new OpenPackOutputBoundary() {
            @Override
            public void prepareSuccessView(OpenPackOutputData outputData) {
                assertEquals(1000, outputData.getRemainingCurrency());
                assertEquals(1, userData.get().getOwnedPokemon().size());
            }
            @Override
            public void prepareFailView(String errorMessage) {
                fail("fail of save test.");
            }
        };

        OpenPackInputBoundary interactor = new OpenPackInteractor(userData, presenter, fakePack);
        interactor.execute(new OpenPackInputData());
    }

}
