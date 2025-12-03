package use_case.pick_moveset;

import entities.battle.Move;
import entities.Pokemon;

import java.util.List;
import java.util.Map;

public class PickMovesetOutputData {

    private final Map<Pokemon, List<Move>> pokemonMoves;

    public PickMovesetOutputData(Map<Pokemon, List<Move>> pokemonMoves) {
        this.pokemonMoves = pokemonMoves;
    }

    public Map<Pokemon, List<Move>> getPokemonMoves() {
        return pokemonMoves;
    }
}
