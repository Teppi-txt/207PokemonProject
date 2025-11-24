package use_case.collection;

import entities.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class ViewCollectionOutputData {

    private ArrayList<Pokemon> pokemonOnPage;
    private Pokemon selectedPokemon;

    public ViewCollectionOutputData() {}

    public ViewCollectionOutputData(ArrayList<Pokemon> pokemonOnPage, Pokemon selectedPokemon) {
        this.pokemonOnPage = pokemonOnPage;
        this.selectedPokemon = selectedPokemon;
    }

    public ArrayList<Pokemon> getPokemonOnPage() {
        return pokemonOnPage;
    }

    public void setPokemonOnPage(ArrayList<Pokemon> pokemonOnPage) {
        this.pokemonOnPage = pokemonOnPage;
    }

    public Pokemon getSelectedPokemon() {
        return selectedPokemon;
    }

    public void setSelectedPokemon(Pokemon selectedPokemon) {
        this.selectedPokemon = selectedPokemon;
    }
}