package use_case.pick_moveset;

import entities.Deck;
import entities.Move;
import entities.Pokemon;
import pokeapi.JSONLoader;

import java.util.*;

public class PickMovesetInteractor implements PickMovesetInputBoundary {

    private final PickMovesetOutputBoundary presenter;


    public PickMovesetInteractor(PickMovesetOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(PickMovesetInputData inputData) {
        Map<Pokemon, List<Move>> outputMap = new HashMap<>();
        for (Pokemon p : inputData.getDeck().getPokemons()) {
            List<Move> moves = new ArrayList<>();
            for (String mvName : p.getMoves()) {
                for (Move m : JSONLoader.allMoves) {
                    if (m.getName().equalsIgnoreCase(mvName)) {
                        moves.add(m);
                    }
                }
            }
            outputMap.put(p, moves);
        }
        presenter.present(new PickMovesetOutputData(outputMap));
    }


    @Override
    public void saveMoves(Pokemon pokemon, List<Move> chosenMoves) {
        if (chosenMoves == null || chosenMoves.isEmpty()) {
            presenter.presentFailure("You must choose at least 1 move.");
            return;
        }

        if (chosenMoves.size() > 4) {
            presenter.presentFailure("A Pokémon can only have up to 4 moves.");
            return;
        }

        presenter.presentSuccess("Moveset saved for " + pokemon.getName());
    }
}
