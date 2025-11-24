package use_case.collection;

import entities.Pokemon;

import java.util.ArrayList;

public class ViewCollectionInputData {
    private ArrayList<Pokemon> pokemonOnPage;
    private int currentPage = 0;
    private String filter;

    public ViewCollectionInputData(ArrayList<Pokemon> pokemonOnPage, int currentPage, String filter) {
        this.pokemonOnPage = pokemonOnPage;
        this.currentPage = currentPage;
        this.filter = filter;
    }

    public ArrayList<Pokemon> getPokemonOnPage() {
        return pokemonOnPage;
    }

    public void setPokemonOnPage(ArrayList<Pokemon> pokemonOnPage) {
        this.pokemonOnPage = pokemonOnPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
