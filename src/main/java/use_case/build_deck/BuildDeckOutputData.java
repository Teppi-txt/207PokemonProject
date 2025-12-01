package use_case.build_deck;

import entities.Deck;

import java.util.List;

public class BuildDeckOutputData {
    private final Deck deck;
    private final List<Deck> allDecks;

    public BuildDeckOutputData(Deck deck, List<Deck> allDecks){
        this.deck = deck;
        this.allDecks = allDecks;
    }

    public Deck getDeck() {
        return deck;
    }
    public List<Deck> getAllDecks() { return allDecks; }
}