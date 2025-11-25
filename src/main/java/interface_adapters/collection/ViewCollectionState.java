package interface_adapters.collection;

import entities.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class ViewCollectionState {
    private List<Pokemon> pokemonOnPage;
    private Pokemon selectedPokemon;
    private String errorMessage;
    private List<Pokemon> ownedPokemon;

    public Pokemon getSelectedPokemon() {
        return selectedPokemon;
    }

    public void setSelectedPokemon(Pokemon selectedPokemon) {
        this.selectedPokemon = selectedPokemon;
    }

    public List<Pokemon> getPokemonOnPage() {
        return pokemonOnPage;
    }

    public void setPokemonOnPage(List<Pokemon> pokemonOnPage) {
        this.pokemonOnPage = pokemonOnPage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<Pokemon> getOwnedPokemon() {
        return ownedPokemon;
    }

    public void setOwnedPokemon(List<Pokemon> ownedPokemon) {
        this.ownedPokemon = ownedPokemon;
    }
}
