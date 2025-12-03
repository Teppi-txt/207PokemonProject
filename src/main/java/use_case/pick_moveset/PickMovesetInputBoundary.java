package use_case.pick_moveset;

import java.util.List;

import entities.Pokemon;
import entities.battle.Move;

public interface PickMovesetInputBoundary {

    /**
     * Loads the possible moves for the Pokémon specified in the input data.
     * @param inputData the data required to load available moves
     */
    void execute(PickMovesetInputData inputData);

    /**
     * Saves the player's chosen moves (1–4) for a given Pokémon without
     * modifying the original Pokémon entity.
     * @param pokemon the Pokémon for which moves are being selected
     * @param chosenMoves the list of moves chosen by the player
     */
    void saveMoves(Pokemon pokemon, List<Move> chosenMoves);
}
