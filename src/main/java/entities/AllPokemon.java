package entities;

import java.util.ArrayList;
import java.util.List;

public class AllPokemon {
    // the single instance — not created until needed
    private static volatile AllPokemon instance;

    // the list that used to be static/global
    private final List<Pokemon> allPokemon;

    // private constructor — prevents other classes from instantiating
    private AllPokemon() {
        this.allPokemon = new ArrayList<>();
    }
    // singleton access
    public static AllPokemon getInstance() {
        if (instance == null) {
            synchronized (AllPokemon.class) {
                if (instance == null) {
                    instance = new AllPokemon();
                }
            }
        }
        return instance;
    }
    
    // this replaces direct access to allPokemon list
    public List<Pokemon> getAllPokemon() {
        return allPokemon;
    }
}
