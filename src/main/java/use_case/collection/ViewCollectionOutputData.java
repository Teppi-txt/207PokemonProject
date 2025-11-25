package use_case.collection;

import entities.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class ViewCollectionOutputData {

    private List<Pokemon> pokemonOnPage;
    private Pokemon selectedPokemon;
    private List<Pokemon> ownedPokemon;

    public ViewCollectionOutputData() {}

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
}