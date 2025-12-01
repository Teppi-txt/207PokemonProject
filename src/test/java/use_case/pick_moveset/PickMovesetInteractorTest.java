package use_case.pick_moveset;

import entities.battle.Deck;
import entities.battle.Move;
import entities.Pokemon;
import junit.framework.TestCase;
import pokeapi.JSONLoader;

import java.util.*;

public class PickMovesetInteractorTest extends TestCase {

    private TestPresenter presenter;
    private PickMovesetInteractor interactor;

    @Override
    protected void setUp() {
        presenter = new TestPresenter();
        interactor = new PickMovesetInteractor(presenter);
        JSONLoader.allPokemon.clear();
        JSONLoader.allMoves.clear();
        JSONLoader.loadPokemon();
        JSONLoader.loadMoves();
    }

    public void testExecute_ReturnsCorrectMovesetMap() {
        Pokemon pikachu = JSONLoader.allPokemon.get(25).copy();
        pikachu.getMoves().clear();
        pikachu.getMoves().add("Thunderbolt");
        pikachu.getMoves().add("Surf");
        Deck deck = new Deck(1, "TestExecute");
        deck.addPokemon(pikachu);
        interactor.execute(new PickMovesetInputData(deck));
        assertNotNull(presenter.outputData);
        Map<Pokemon, List<Move>> result = presenter.outputData.getPokemonMoves();
        assertTrue(result.containsKey(pikachu));
        assertEquals(2, result.get(pikachu).size());
        assertEquals("thunderbolt", result.get(pikachu).get(0).getName());
        assertEquals("Surf", result.get(pikachu).get(1).getName());
    }

    public void testExecute_InvalidMoveIgnored() {
        Pokemon bulbasaur = JSONLoader.allPokemon.get(1).copy();
        bulbasaur.getMoves().clear();
        bulbasaur.getMoves().add("NonExistentMove");
        Deck deck = new Deck(2, "InvalidMoveTest");
        deck.addPokemon(bulbasaur);
        interactor.execute(new PickMovesetInputData(deck));
        Map<Pokemon, List<Move>> result = presenter.outputData.getPokemonMoves();
        assertNotNull(result);
        assertTrue(result.containsKey(bulbasaur));
        assertEquals(0, result.get(bulbasaur).size());
    }

    public void testExecute_EmptyMovesList() {
        Pokemon charmander = JSONLoader.allPokemon.get(4).copy();
        charmander.getMoves().clear();
        Deck deck = new Deck(3, "EmptyMoves");
        deck.addPokemon(charmander);
        interactor.execute(new PickMovesetInputData(deck));
        Map<Pokemon, List<Move>> result = presenter.outputData.getPokemonMoves();
        assertTrue(result.containsKey(charmander));
        assertEquals(0, result.get(charmander).size());
    }


    public void testExecute_MultiplePokemon() {
        Pokemon pikachu = JSONLoader.allPokemon.get(25).copy();
        Pokemon squirtle = JSONLoader.allPokemon.get(7).copy();
        pikachu.getMoves().clear();
        pikachu.getMoves().add("Thunderbolt");
        squirtle.getMoves().clear();
        squirtle.getMoves().add("Surf");
        Deck deck = new Deck(4, "Multi");
        deck.addPokemon(pikachu);
        deck.addPokemon(squirtle);
        interactor.execute(new PickMovesetInputData(deck));
        Map<Pokemon, List<Move>> result = presenter.outputData.getPokemonMoves();
        assertEquals(2, result.size());
        assertEquals(1, result.get(pikachu).size());
        assertEquals(1, result.get(squirtle).size());
    }

    public void testExecute_NoAvailableMoves() {
        JSONLoader.allMoves.clear();
        Pokemon pikachu = JSONLoader.allPokemon.get(25).copy();
        pikachu.getMoves().clear();
        pikachu.getMoves().add("Thunderbolt");
        Deck deck = new Deck(5, "NoMoveAvailable");
        deck.addPokemon(pikachu);
        interactor.execute(new PickMovesetInputData(deck));
        Map<Pokemon, List<Move>> result = presenter.outputData.getPokemonMoves();
        assertEquals(0, result.get(pikachu).size());
    }

    public void testExecute_DeckNull() {
        try {
            interactor.execute(new PickMovesetInputData(null));
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
        }
    }


    public void testSaveMoves_Failure_NullMoves() {
        Pokemon bulbasaur = JSONLoader.allPokemon.get(1).copy();
        interactor.saveMoves(bulbasaur, null);
        assertEquals("You must choose at least 1 move.", presenter.failureMessage);
    }

    public void testSaveMoves_Failure_NoMoves() {
        Pokemon bulbasaur = JSONLoader.allPokemon.get(1).copy();
        interactor.saveMoves(bulbasaur, new ArrayList<>());

        assertEquals("You must choose at least 1 move.", presenter.failureMessage);
        assertNull(presenter.successMessage);
    }

    public void testSaveMoves_Failure_TooManyMoves() {
        Pokemon bulbasaur = JSONLoader.allPokemon.get(1).copy();
        List<Move> tooManyMoves = JSONLoader.allMoves.subList(0, 5);
        interactor.saveMoves(bulbasaur, tooManyMoves);
        assertEquals("A Pokémon can only have up to 4 moves.", presenter.failureMessage);
        assertNull(presenter.successMessage);
    }

    public void testSaveMoves_NullMoveInsideList() {
        Pokemon bulbasaur = JSONLoader.allPokemon.get(1).copy();
        List<Move> moves = new ArrayList<>();
        moves.add(null);
        interactor.saveMoves(bulbasaur, moves);
        assertEquals("Moveset saved for " + bulbasaur.getName(), presenter.successMessage);
    }

    public void testSaveMoves_Success() {
        Pokemon bulbasaur = JSONLoader.allPokemon.get(1).copy();
        List<Move> chosenMoves = JSONLoader.allMoves.subList(0, 2);
        interactor.saveMoves(bulbasaur, chosenMoves);
        assertEquals("Moveset saved for " + bulbasaur.getName(), presenter.successMessage);
        assertNull(presenter.failureMessage);
    }


    private static class TestPresenter implements PickMovesetOutputBoundary {
        PickMovesetOutputData outputData;
        String successMessage;
        String failureMessage;

        @Override
        public void present(PickMovesetOutputData outputData) {
            this.outputData = outputData;
        }

        @Override
        public void presentSuccess(String message) {
            this.successMessage = message;
        }

        @Override
        public void presentFailure(String message) {
            this.failureMessage = message;
        }
    }
}

