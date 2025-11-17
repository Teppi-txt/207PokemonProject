package use_case.collection;

import entities.Pokemon;

import java.util.ArrayList;

public class ViewCollectionInputData {
    private ArrayList<Pokemon> ownedPokemon;

    public ViewCollectionInputData(ArrayList<Pokemon> ownedPokemon) {
        this.ownedPokemon = ownedPokemon;
    }
}
