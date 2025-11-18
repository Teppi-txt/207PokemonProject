package use_case.pick_moveset;

import cards.Deck;

public class PickMovesetInputData {

    private final Deck deck;

    public PickMovesetInputData(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }
}
