package interface_adapters.main_menu;

/**
 * State for the main menu view.
 */
public class MainMenuState {

    private String userName;
    private int currency;
    private int pokemonCount;

    public MainMenuState() {
        this.userName = "";
        this.currency = 0;
        this.pokemonCount = 0;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getPokemonCount() {
        return pokemonCount;
    }

    public void setPokemonCount(int pokemonCount) {
        this.pokemonCount = pokemonCount;
    }
}
