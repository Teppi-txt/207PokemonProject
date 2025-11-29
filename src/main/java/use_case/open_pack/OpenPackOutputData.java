package use_case.open_pack;

import entities.Pokemon;
import java.util.List;

public class OpenPackOutputData {

    private final List<Pokemon> openedCards;
    private final List<Boolean> duplicateFlags;
    private final int remainingCurrency;

    public OpenPackOutputData(List<Pokemon> openedCards,
                              List<Boolean> duplicateFlags,
                              int remainingCurrency) {
        this.openedCards = openedCards;
        this.duplicateFlags = duplicateFlags;
        this.remainingCurrency = remainingCurrency;
    }

    public List<Pokemon> getOpenedCards() {
        return openedCards;
    }

    public List<Boolean> getDuplicateFlags() {
        return duplicateFlags;
    }

    public int getRemainingCurrency() {
        return remainingCurrency;
    }
}
