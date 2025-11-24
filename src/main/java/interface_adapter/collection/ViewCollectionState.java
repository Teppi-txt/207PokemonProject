package interface_adapter.collection;

import entities.Pokemon;

import java.util.ArrayList;

public class ViewCollectionState {
    private ArrayList<Pokemon> pokemonOnPage;
    private Pokemon selectedPokemon;
    private String errorMessage;

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
