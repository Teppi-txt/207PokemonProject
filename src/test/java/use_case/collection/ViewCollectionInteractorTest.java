
package use_case.collection;
import entities.Pokemon;
import entities.User;
import interface_adapters.collection.ViewCollectionPresenter;
import interface_adapters.collection.ViewCollectionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pokeapi.JSONLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.json.XMLTokener.entity;
import static org.junit.jupiter.api.Assertions.*;

class ViewCollectionInteractorTest {
    private RecordingPresenter presenter;
    private User user;
    private ViewCollectionInteractor interactor;

    void setUp() {
        user = new User(1, "Trainer", "trainer@pokemon.com", 5000);

        // Add specific Gen 1 starter Pokémon by ID
        int[] starterIds = {1, 4, 7, 25, 133};  // Bulbasaur, Charmander, Squirtle, Pikachu, Eevee
        for (int id : starterIds) {
            Pokemon pokemon = JSONLoader.getInstance().getAllPokemon().get(id);
            if (pokemon != null) {
                user.addPokemon(pokemon.copy());
            }
        }

        for (int i = 160; i < 190; i++) {
            Pokemon pokemon = JSONLoader.getInstance().getAllPokemon().get(i);
            if (pokemon != null) {
                Pokemon shiny = pokemon.copy();
                shiny.setShiny(true);
                user.addPokemon(shiny);
            }
        }

        presenter = new RecordingPresenter();

        interactor = new ViewCollectionInteractor(presenter, user);
    }

    @Test
    void ownedSuccessTest() {
        setUp();
        ViewCollectionInputData input1 = new ViewCollectionInputData(new ArrayList<>(), 0, "owned");
        interactor.execute( input1 );
        ArrayList<Pokemon> userFirstPage = new ArrayList<>(
                user.getOwnedPokemon().subList(0, Math.min(user.getOwnedPokemon().size(), 20)));
        assertEquals(presenter.lastOutput.getPokemonOnPage(), userFirstPage);
    }

    @Test
    void allSuccessTest() {
        setUp();
        ViewCollectionInputData input1 = new ViewCollectionInputData(new ArrayList<>(), 0, "all");
        interactor.execute( input1 );
        ArrayList<Pokemon> userFirstPage = new ArrayList<>(
                JSONLoader.getInstance().getAllPokemon().subList(0, 20));
        assertEquals(presenter.lastOutput.getPokemonOnPage(), userFirstPage);
    }

    @Test
    void successTestPageTen() {
        setUp();
        ViewCollectionInputData input1 = new ViewCollectionInputData(new ArrayList<>(), 10, "all");
        interactor.execute( input1 );
        ArrayList<Pokemon> userFirstPage = new ArrayList<>(
                JSONLoader.getInstance().getAllPokemon().subList(200, 220));
        assertEquals(presenter.lastOutput.getPokemonOnPage(), userFirstPage);
    }

    @Test
    void filterFailsNoOwned() {
        setUp();
        user.getOwnedPokemon().clear();
        ViewCollectionInputData input1 = new ViewCollectionInputData(new ArrayList<>(), 0, "owned");
        interactor.execute( input1 );
        assertEquals("You do not have any owned pokemon.", presenter.lastErrorMessage);
    }

    @Test
    void allShinyOnShinyFilter() {
        setUp();
        ViewCollectionInputData input1 = new ViewCollectionInputData(new ArrayList<>(), 0, "shiny");
        interactor.execute( input1 );

        boolean failed = false;
        for (Pokemon pokemon : presenter.lastOutput.getPokemonOnPage()) {
            if (!pokemon.isShiny()) {
                failed = true;
                break;
            }
        }
        assertFalse(failed);
    }

    @Test
    void invalidFilterEmpty() {
        setUp();
        ViewCollectionInputData input1 = new ViewCollectionInputData(new ArrayList<>(), 0, "absc");
        interactor.execute( input1 );

        assertTrue(presenter.lastOutput.getPokemonOnPage().isEmpty());
    }

    @Test
    void switchToHomeView() {
        setUp();
        interactor.switchToHomeView(  );
        assertTrue(presenter.switchedToHome);
    }


    @Test
    void backOnPageZero() {
        setUp();
        ViewCollectionInputData input1 = new ViewCollectionInputData(new ArrayList<>(), -10, "all");
        ViewCollectionInputData input2 = new ViewCollectionInputData(new ArrayList<>(), 0, "all");
        interactor.execute( input1 );
        ViewCollectionOutputData outputData1 = presenter.lastOutput;
        interactor.execute( input2 );
        assertEquals(presenter.lastOutput.getPokemonOnPage(), outputData1.getPokemonOnPage());
    }

    public class RecordingPresenter implements ViewCollectionOutputBoundary {

        public ViewCollectionOutputBoundary presenter;
        public ViewCollectionOutputData lastOutput;
        public String lastErrorMessage;
        public boolean switchedToHome = false;

        @Override
        public void prepareSuccessView(ViewCollectionOutputData outputData) {
            lastOutput = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage, ViewCollectionOutputData outputData) {
            lastOutput = outputData;
            lastErrorMessage = errorMessage;
        }

        @Override
        public void switchToHomeView() {
            switchedToHome = true;
        }
    }
}
