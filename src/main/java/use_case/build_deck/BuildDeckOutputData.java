package use_case.build_deck;

import entities.Deck;

public class BuildDeckOutputData {
    private final Deck deck;

    public BuildDeckOutputData(Deck deck){
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }
}
