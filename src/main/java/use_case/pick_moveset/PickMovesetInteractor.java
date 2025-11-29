/*package use_case.pick_moveset;

import cards.Deck;
import entities.Move;
import entities.Pokemon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickMovesetInteractor implements PickMovesetInputBoundary {

    private final PickMovesetOutputBoundary presenter;
    private final PickMovesetUserDataAccessInterface dataAccess;

    public PickMovesetInteractor(PickMovesetOutputBoundary presenter,
                                 PickMovesetUserDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(PickMovesetInputData inputData) {
        Deck deck = inputData.getDeck();
        Map<Pokemon, List<Move>> outputMap = new HashMap<>();

        for (Pokemon p : deck.getPokemons()) {
            List<Move> moves = dataAccess.getMovesForPokemon(p);

            if (moves == null || moves.isEmpty()) {
                moves = dataAccess.getCachedMoves(p);
            }

            outputMap.put(p, moves);
        }

        presenter.present(new PickMovesetOutputData(outputMap));
    }

    @Override
    public void saveMoves(Pokemon pokemon, List<Move> chosenMoves) {

        if (chosenMoves.size() > 4) {
            presenter.presentFailure("A Pokémon can only have up to 4 moves.");
            return;
        }

        dataAccess.saveMoveset(pokemon, chosenMoves);

        presenter.presentSuccess("Moveset saved successfully.");
    }
}
*/