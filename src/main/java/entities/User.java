package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private String name;
    private int currency;
    private String email; //do we need a password?
    private final Map<Integer, Deck> decks = new HashMap<>();

    private final List<Pokemon> ownedPokemon;

    public User(int id, String name, String email, int currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.email = email;
        this.ownedPokemon = new ArrayList<>();
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getCurrency() {
        return currency;
    }

    public List<Pokemon> getOwnedPokemon() {
        return ownedPokemon;
    }

    public void addPokemon(Pokemon pokemon) {
        this.ownedPokemon.add(pokemon);
    }

    public Map<Integer, Deck> getDecks() { return decks; }

    public void addDeck(Deck deck) {
        decks.put(deck.getId(), deck);
    }

    //got rid of openPack method because that will be used with the open pack interactor

    public boolean canAffordPack(int amount){
        return currency >= amount;
    }

    public void buyPack(int amount){
        currency -= amount;
    }

    public void addCurrency(int amount){
        currency += amount;
    }

    public boolean hasDuplicatePokemon(Pokemon pokemon){ // checks if the pokemon id is the same and then checks if its shiny or not to see if the user pulled a duplicated card
        for (Pokemon owned : ownedPokemon){
            if(owned.getID() == pokemon.getID() && owned.isShiny() == pokemon.isShiny()){
                return true;
            }
        }
        return false;
    }

    public Pokemon getPokemonById(int id){
        for (Pokemon p : ownedPokemon){
            if (p.getID() == id) return p;
        }
        return null;
    }

    public Deck getDeckById(int id) {
        return decks.get(id);
    }
}
