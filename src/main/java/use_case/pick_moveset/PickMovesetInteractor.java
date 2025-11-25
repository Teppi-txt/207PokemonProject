package use_case.pick_moveset;

import entities.Move;
import entities.Pokemon;
import pokeapi.PokeAPIFetcher;

import java.util.*;

public class PickMovesetInteractor implements PickMovesetInputBoundary {

    private final PickMovesetOutputBoundary presenter;
    private final PickMovesetUserDataAccessInterface dataAccess = new DefaultDataAccess();

    public PickMovesetInteractor(PickMovesetOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(PickMovesetInputData inputData) {
        Map<Pokemon, List<Move>> outputMap = new HashMap<>();

        for (Pokemon p : inputData.getDeck().getPokemons()) {

            // Primary source: default dataAccess
            List<Move> moves = dataAccess.getMovesForPokemon(p);

            // Fallback: cached moves
            if (moves == null || moves.isEmpty()) {
                moves = dataAccess.getCachedMoves(p);
            }
            if (moves == null) {
                moves = List.of();
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

        try {
            dataAccess.saveMoveset(pokemon, chosenMoves);
            presenter.presentSuccess("Moveset saved for " + pokemon.getName() + "!");
        } catch (Exception e) {
            presenter.presentFailure("Failed to save moveset: " + e.getMessage());
        }
    }



    private static class DefaultDataAccess implements PickMovesetUserDataAccessInterface {

        private final Map<String, List<Move>> cache = new HashMap<>();
        private final Map<String, List<Move>> savedMoves = new HashMap<>();

        @Override
        public List<Move> getMovesForPokemon(Pokemon p) {
            String key = p.getName().toLowerCase();
            if (cache.containsKey(key)) {
                return cache.get(key);
            }

            List<Move> list = new ArrayList<>();
            for (String mvName : p.getMoves()) {
                try {
                    Move m = PokeAPIFetcher.getMove(mvName);
                    list.add(m);
                } catch (Exception ignored) {
                    // skip failed move
                }
            }

            cache.put(key, list);
            return list;
        }

        @Override
        public List<Move> getCachedMoves(Pokemon p) {
            return cache.getOrDefault(
                    p.getName().toLowerCase(),
                    new ArrayList<>()
            );
        }

        @Override
        public void saveMoveset(Pokemon p, List<Move> chosenMoves) {
            savedMoves.put(p.getName().toLowerCase(), chosenMoves);
            System.out.println("Saved moveset for " + p.getName() + ": " + chosenMoves);
        }
    }
}
