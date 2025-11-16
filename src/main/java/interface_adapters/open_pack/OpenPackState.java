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

}
