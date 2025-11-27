package use_case.build_deck;

import entities.Pokemon;

import java.util.List;

public class BuildDeckInputData {
    private final String deckName;
    private final List<Pokemon> pokemon;
    private final boolean isRandom;

    public BuildDeckInputData(String deckName, List<Pokemon> pokemon, boolean isRandom) {
        this.deckName = deckName;
        this.pokemon = pokemon;
        this.isRandom = isRandom;
    }

    public String getDeckName() { return deckName; }
    public List<Pokemon> getPokemon() { return pokemon; }
    public boolean isRandom() { return isRandom; }
}
