package use_case.build_deck;

import java.util.List;

import entities.Pokemon;

public class BuildDeckInputData {
    private final int deckId;
    // ^^ NEW
    private final String deckName;
    private final List<Pokemon> pokemon;
    private final boolean isRandom;
    private final boolean delete;

    public BuildDeckInputData(
            int deckId, String deckName, List<Pokemon> pokemon, boolean isRandom, boolean delete) {
        // ^^ MODIFIED Constructor
        this.deckId = deckId;
        this.deckName = deckName;
        this.pokemon = pokemon;
        this.isRandom = isRandom;
        this.delete = delete;
    }

    public int getDeckId() {
        return deckId;
    }
    // ^^ NEW Getter

    public String getDeckName() {
        return deckName;
    }

    public List<Pokemon> getPokemon() {
        return pokemon;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public boolean isDelete() {
        return delete;
    }
}
