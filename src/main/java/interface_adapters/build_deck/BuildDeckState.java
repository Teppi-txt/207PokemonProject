package interface_adapters.build_deck;

import entities.Deck;
/**
 * The State for the Build Deck Use Case.
 */

import java.util.List;

public class BuildDeckState {
    private Deck deck;
    private List<Deck> allDecks; // <--- NEW
    private String errorMessage;

    public Deck getDeck() {
        return deck;
    }
    public void setDeck(Deck deck) { this.deck = deck; }

    public List<Deck> getAllDecks() { return allDecks; } // <--- NEW
    public void setAllDecks(List<Deck> allDecks) { this.allDecks = allDecks; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
