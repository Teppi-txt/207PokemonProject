package use_case.collection;

import entities.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class ViewCollectionOutputData {

    private ArrayList<Pokemon> displayedPokemon;

    public ViewCollectionOutputData() {}

    public ViewCollectionOutputData(ArrayList<Pokemon> displayedPokemon) {
        this.displayedPokemon = displayedPokemon;
    }

    public List<Pokemon> getDisplayedPokemon() {
        return displayedPokemon;
    }

    public void setDisplayedPokemon(ArrayList<Pokemon> displayedPokemon) {
        this.displayedPokemon = displayedPokemon;
    }
}