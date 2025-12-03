package use_case.collection;

import java.util.List;

import entities.Pokemon;

public class ViewCollectionOutputData {

    private List<Pokemon> pokemonOnPage;
    private Pokemon selectedPokemon;
    private List<Pokemon> ownedPokemon;
    private boolean hasNextPage;

    public ViewCollectionOutputData() {
    }

    public ViewCollectionOutputData(List<Pokemon> pokemonOnPage, Pokemon selectedPokemon, List<Pokemon> ownedPokemon) {
        this.pokemonOnPage = pokemonOnPage;
        this.selectedPokemon = selectedPokemon;
        this.ownedPokemon = ownedPokemon;
    }

    public List<Pokemon> getPokemonOnPage() {
        return pokemonOnPage;
    }

    public void setPokemonOnPage(List<Pokemon> pokemonOnPage) {
        this.pokemonOnPage = pokemonOnPage;
    }

    public Pokemon getSelectedPokemon() {
        return selectedPokemon;
    }

    public void setSelectedPokemon(Pokemon selectedPokemon) {
        this.selectedPokemon = selectedPokemon;
    }

    public List<Pokemon> getOwnedPokemon() {
        return ownedPokemon;
    }

    public void setOwnedPokemon(List<Pokemon> ownedPokemon) {
        this.ownedPokemon = ownedPokemon;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
