package interface_adapters.pick_moveset;

import pokeapi.JSONLoader;
import use_case.pick_moveset.PickMovesetInputBoundary;
import use_case.pick_moveset.PickMovesetInputData;
import entities.Pokemon;
import entities.Move;
import entities.Deck;

import java.util.List;

public class PickMovesetController {

    private final PickMovesetInputBoundary interactor;

    public PickMovesetController(PickMovesetInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadMoves(Deck deck) {
        PickMovesetInputData inputData = new PickMovesetInputData(deck);
        interactor.execute(inputData);
    }

    public void saveMoves(Pokemon pokemon, List<Move> chosen) {
        interactor.saveMoves(pokemon, chosen);
    }

    public Move fetchMoveDetail(String name) {
        for (Move m : JSONLoader.getInstance().getAllMoves()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }


}
