package entities.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import entities.Pokemon;
import entities.battle.Deck;

/**
 * Entity representing one user of the pokemon app.
 */
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

    public Map<Integer, Deck> getDecks() {
        return decks;
    }

    public void addDeck(Deck deck) {
        decks.put(deck.getId(), deck);
    }

    public void deleteDeck(int deckId) {
        decks.remove(deckId);
    }

    //got rid of openPack method because that will be used with the open pack interactor

    public boolean canAffordPack(int amount) {
        return currency >= amount;
    }

    public void buyPack(int amount) {
        currency -= amount;
    }

    public void addCurrency(int amount) {
        currency += amount;
    }

    public boolean hasDuplicatePokemon(Pokemon pokemon) { // checks if the pokemon id is the same and then checks if its shiny or not to see if the user pulled a duplicated card
        for (Pokemon owned : ownedPokemon) {
            if(owned.getID() == pokemon.getID() && owned.isShiny() == pokemon.isShiny()){
                return true;
            }
        }
        return false;
    }

    public Pokemon getPokemonById(int id) {
        for (Pokemon p : ownedPokemon) {
            if (p.getID() == id) {
                return p;
            }
        }
        return null;
    }

    public Deck getDeckById(int id) {
        return decks.get(id);
    }

    public String toJSONString() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("email", email);
        json.put("currency", currency);

        final JSONArray pokemons = new JSONArray();
        for (Pokemon pokemon : ownedPokemon) {
            pokemons.put(new JSONObject(pokemon.toJSONString()));
        }
        json.put("ownedpokemons", pokemons);

        final JSONArray decksArray = new JSONArray();
        for (Deck deck : decks.values()) {
            decksArray.put(deck.toJSONObject());
        }
        json.put("decks", decksArray);

        return json.toString(2);
    }

    public static User fromJSON(JSONObject json) {
        final int id = json.getInt("id");
        final String name = json.getString("name");
        final String email = json.getString("email");
        final int currency = json.getInt("currency");

        final User user = new User(id, name, email, currency);
        final JSONArray pokemons = json.getJSONArray("ownedpokemons");
        for (int i = 0; i < pokemons.length(); i++) {
            final JSONObject pokeJSON = pokemons.getJSONObject(i);
            final Pokemon p = Pokemon.fromJSON(pokeJSON);
            user.addPokemon(p);
        }

        if (json.has("decks")) {
            final JSONArray decksArray = json.getJSONArray("decks");
            for (int i = 0; i < decksArray.length(); i++) {
                final JSONObject deckJSON = decksArray.getJSONObject(i);
                final Deck deck = Deck.fromJSON(deckJSON);
                user.addDeck(deck);
            }
        }

        return user;
    }

}
