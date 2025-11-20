package use_case.collection;

import entities.Pokemon;

import java.util.ArrayList;

public class ViewCollectionInputData {
    private ArrayList<Pokemon> ownedPokemon;
    private int currentPage = 0;
    private String filter;

    public ViewCollectionInputData(ArrayList<Pokemon> ownedPokemon, int currentPage, String filter) {
        this.ownedPokemon = ownedPokemon;
        this.currentPage = currentPage;
        this.filter = filter;
    }
}
