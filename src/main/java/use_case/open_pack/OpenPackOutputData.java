package use_case.open_pack;
import entities.Pokemon;
import java.util.List;

public class OpenPackOutputData {

    private final List<Pokemon> openedCards;
    private final int remainingCurrency;

    public OpenPackOutputData(List<Pokemon> openedCards, int remainingCurrency) {
        this.openedCards = openedCards;
        this.remainingCurrency = remainingCurrency;
    }

    public List<Pokemon> getOpenedCards() {
        return openedCards;
    }

    public int getRemainingCurrency() {
        return remainingCurrency;
    }


}
