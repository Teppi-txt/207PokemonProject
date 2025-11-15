package use_case.open_pack;
import entities.Pokemon;
import java.util.List;

public class OpenPackOutputData {

    private final String username;
    private final List<Pokemon> openedCards;
    private final int remainingCurrency;

    public OpenPackOutputData(String username, List<Pokemon> openedCards, int remainingCurrency) {
        this.username = username;
        this.openedCards = openedCards;
        this.remainingCurrency = remainingCurrency;
    }

    public String getUsername() {
        return username;
    }

    public List<Pokemon> getOpenedCards() {
        return openedCards;
    }

    public int getRemainingCurrency() {
        return remainingCurrency;
    }


}
