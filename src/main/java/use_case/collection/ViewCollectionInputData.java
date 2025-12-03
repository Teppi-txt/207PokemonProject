package use_case.collection;

import java.util.List;

import entities.Pokemon;

public class ViewCollectionInputData {
    private List<Pokemon> pokemonOnPage;
    private int currentPage = 0;
    private String filter;

    public ViewCollectionInputData(List<Pokemon> pokemonOnPage, int currentPage, String filter) {
        this.pokemonOnPage = pokemonOnPage;
        this.currentPage = currentPage;
        this.filter = filter;
    }

    public List<Pokemon> getPokemonOnPage() {
        return pokemonOnPage;
    }

    public void setPokemonOnPage(List<Pokemon> pokemonOnPage) {
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
