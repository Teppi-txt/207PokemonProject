package use_case.pick_moveset;

import entities.Pokemon;
import entities.battle.Move;

import java.util.List;

public interface PickMovesetInputBoundary {

    // Load possible moves for each Pokémon
    void execute(PickMovesetInputData inputData);

    // Save selected 1–4 moves (NOT modifying Pokémon entity)
    void saveMoves(Pokemon pokemon, List<Move> chosenMoves);
}
