package use_case.pick_moveset;

import entities.Move;
import entities.Pokemon;

import java.util.List;

public interface PickMovesetUserDataAccessInterface {
    List<Move> getMovesForPokemon(Pokemon pokemon);
    List<Move> getCachedMoves(Pokemon pokemon);
    void saveMoveset(Pokemon pokemon, List<Move> chosenMoves);
}
