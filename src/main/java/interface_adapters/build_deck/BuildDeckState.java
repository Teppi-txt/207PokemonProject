package interface_adapters.build_deck;

import entities.Deck;
/**
 * The state for the Build Deck View Model.
 */

public class BuildDeckState {
    private Deck deck;
    private String errorMessage; // if it is null there's no error

    public Deck getDeck() {
        return deck;
    }
    public void setDeck(Deck deck) { this.deck = deck; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
