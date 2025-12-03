package interface_adapters.build_deck;

import java.util.List;

import entities.battle.Deck;

/**
 * A state of the Build Deck view.
 */

public class BuildDeckState {
    private Deck deck;
    private List<Deck> allDecks;
    private String errorMessage;

    public Deck getDeck() {

        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public List<Deck> getAllDecks() {
        return allDecks;
    }

    public void setAllDecks(List<Deck> allDecks) {
        this.allDecks = allDecks;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
