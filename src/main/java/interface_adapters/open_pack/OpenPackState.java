package interface_adapters.open_pack;

import entities.Pokemon;

import java.util.ArrayList;
import java.util.List;
/**
 * The state for the open pack view model
 */

public class OpenPackState {

    private List<Pokemon> openedCards = new ArrayList<>();
    private int remainingCurrency;
    private String errorMessage;
    private boolean revealMode;
    private int revealIndex;
    private List<Boolean> duplicateFlags;

    public List<Pokemon> getOpenedCards() {
        return openedCards;
    }

    public void setOpenedCards(List<Pokemon> openedCards) {
        this.openedCards = openedCards;
    }

    public int getRemainingCurrency() {
        return remainingCurrency;
    }

    public void setRemainingCurrency(int remainingCurrency) {
        this.remainingCurrency = remainingCurrency;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isRevealMode() { return revealMode; }

    public void setRevealMode(boolean revealMode) { this.revealMode = revealMode; }

    public int getRevealIndex() { return revealIndex; }

    public void setRevealIndex(int revealIndex) { this.revealIndex = revealIndex; }

    public List<Boolean> getDuplicateFlags() { return duplicateFlags; }

    public void setDuplicateFlags(List<Boolean> duplicateFlags) { this.duplicateFlags = duplicateFlags; }

}
