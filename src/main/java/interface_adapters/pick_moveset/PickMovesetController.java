package interface_adapters.pick_moveset;

import java.util.List;

import entities.Pokemon;
import entities.battle.Deck;
import entities.battle.Move;
import pokeapi.JSONLoader;
import use_case.pick_moveset.PickMovesetInputBoundary;
import use_case.pick_moveset.PickMovesetInputData;

/**
 * Controller for picking movesets.
 */

public class PickMovesetController {

    private final PickMovesetInputBoundary interactor;

    /**
     * Creates the controller.
     * @param interactor input boundary
     */
    public PickMovesetController(PickMovesetInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Loads moves for a deck.
     * @param deck the deck
     */
    public void loadMoves(Deck deck) {
        final PickMovesetInputData inputData = new PickMovesetInputData(deck);
        interactor.execute(inputData);
    }

    /**
     * Saves chosen moves.
     * @param pokemon the Pokémon
     * @param chosen the selected moves
     */
    public void saveMoves(Pokemon pokemon, List<Move> chosen) {
        interactor.saveMoves(pokemon, chosen);
    }

    /**
     * Fetches a move by name.
     * @param name move name
     * @return the move or null
     */
    public Move fetchMoveDetail(String name) {
        for (Move m : JSONLoader.getInstance().getAllMoves()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
}
