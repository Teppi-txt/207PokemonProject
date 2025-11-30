package entities;

import entities.Move;

import java.util.ArrayList;

public class AllMoves {
    // the single instance — not created until needed
    private static volatile AllMoves instance;

    // the data this singleton manages
    private final ArrayList<Move> moves = new ArrayList<>();

    // private constructor — prevents other classes from instantiating
    private AllMoves() {}
    // singleton access
    public static AllMoves getInstance() {
        if (instance == null) {
            synchronized (AllMoves.class) {
                if (instance == null) {
                    instance = new AllMoves();
                }
            }
        }
        return instance;
    }

    // this replaces direct access to allMoves list
    public ArrayList<Move> getAllMoves() {
        return moves;
    }

    // other helpers
    public void addMove(Move move) {
        moves.add(move);
    }

    public Move getMove(int index) {
        return moves.get(index);
    }

    public int size() {
        return moves.size();
    }
}
