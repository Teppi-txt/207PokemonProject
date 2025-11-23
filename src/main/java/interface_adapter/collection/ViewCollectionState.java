package interface_adapter.collection;

import entities.Pokemon;

import java.util.ArrayList;

public class ViewCollectionState {
    private ArrayList<Pokemon> pokemonOnPage;
    private Pokemon selectedPokemon;

    public Pokemon getSelectedPokemon() {
        return selectedPokemon;
    }

    public void setSelectedPokemon(Pokemon selectedPokemon) {
        this.selectedPokemon = selectedPokemon;
    }

    public ArrayList<Pokemon> getPokemonOnPage() {
        return pokemonOnPage;
    }

    public void setPokemonOnPage(ArrayList<Pokemon> pokemonOnPage) {
        this.pokemonOnPage = pokemonOnPage;
    }
}
