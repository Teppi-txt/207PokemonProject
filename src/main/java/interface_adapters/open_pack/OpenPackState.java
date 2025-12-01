package interface_adapters.open_pack;

import entities.Pokemon;

import java.util.ArrayList;
import java.util.List;
/**
 * The state for the open pack view model
 */

public class OpenPackState {

    private int remainingCurrency;

    private List<Pokemon> openedCards;

    private List<Boolean> duplicateFlags;

    private boolean revealMode;
    private int revealIndex;

    private String errorMessage;

    public OpenPackState() {
        this.openedCards = new ArrayList<>();
        this.duplicateFlags = new ArrayList<>();
    }

    public OpenPackState(OpenPackState other) {
        this.remainingCurrency = other.remainingCurrency;
        this.openedCards = new ArrayList<>(other.openedCards);
        this.duplicateFlags = new ArrayList<>(other.duplicateFlags);
        this.revealMode = other.revealMode;
        this.revealIndex = other.revealIndex;
        this.errorMessage = other.errorMessage;
    }

    public int getRemainingCurrency() { return remainingCurrency; }

    public List<Pokemon> getOpenedCards() { return openedCards; }

    public List<Boolean> getDuplicateFlags() { return duplicateFlags; }

    public boolean isRevealMode() { return revealMode; }

    public int getRevealIndex() { return revealIndex; }

    public String getErrorMessage() { return errorMessage; }

    public void setRemainingCurrency(int amount) { this.remainingCurrency = amount; }

    public void setOpenedCards(List<Pokemon> cards) {
        this.openedCards = cards == null ? new ArrayList<>() : new ArrayList<>(cards);
    }

    public void setDuplicateFlags(List<Boolean> flags) {
        this.duplicateFlags = flags == null ? new ArrayList<>() : new ArrayList<>(flags);
    }

    public void setRevealMode(boolean b) { this.revealMode = b; }

    public void setRevealIndex(int idx) { this.revealIndex = idx; }

    public void setErrorMessage(String msg) { this.errorMessage = msg; }

}
