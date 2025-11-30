package entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DECK_LIMIT = 5; //idk tbh we can change as needed

    private int id;
    private String name;
    private ArrayList<Pokemon> pokemons;

    public Deck(Deck source) {
        this.id = source.id;
        this.name = source.name;
        // CRUCIAL: Create a new ArrayList copy of the Pokemons list references
        this.pokemons = new ArrayList<>(source.pokemons);
    }

    public Deck(int id, String name) {
        this.id = id;
        this.name = (name == null || name.isEmpty()) ? ("Team " + id) : name;
        this.pokemons = new ArrayList<>();
    }

    public Deck(int id, String name, ArrayList<Pokemon> pokemons) {
        this.id = id;
        this.name = name;
        this.pokemons = pokemons;
    }

    public Deck() {

    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public ArrayList<Pokemon> getPokemons() { return pokemons; }
    public void setPokemons(ArrayList<Pokemon> pokemons) { this.pokemons = pokemons; }

    public void addPokemon(Pokemon pokemon) {
        if (pokemons.size() < DECK_LIMIT) {
            pokemons.add(pokemon);
        }
    }
    public void removePokemon(Pokemon pokemon) { this.pokemons.remove(pokemon); }

    @Override
    public String toString() {
        return this.name;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);

        JSONArray pokemonArray = new JSONArray();
        if (pokemons != null) {
            for (Pokemon pokemon : pokemons) {
                pokemonArray.put(new JSONObject(pokemon.toJSONString()));
            }
        }
        json.put("pokemons", pokemonArray);

        return json;
    }

    public static Deck fromJSON(JSONObject json) {
        int id = json.getInt("id");
        String name = json.optString("name", "Team " + id);

        ArrayList<Pokemon> pokemons = new ArrayList<>();
        if (json.has("pokemons")) {
            JSONArray pokemonArray = json.getJSONArray("pokemons");
            for (int i = 0; i < pokemonArray.length(); i++) {
                JSONObject pokeJSON = pokemonArray.getJSONObject(i);
                Pokemon pokemon = Pokemon.fromJSON(pokeJSON);
                pokemons.add(pokemon);
            }
        }

        return new Deck(id, name, pokemons);
    }
}
