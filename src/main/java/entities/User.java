package entities;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private String name;
    private int currency;
    private String email; //do we need a password?

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

    public String toJSONString(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("email", email);
        json.put("currency", currency);

        JSONArray pokemons = new JSONArray();
        for (Pokemon pokemon : ownedPokemon){
            pokemons.put(new JSONObject(pokemon.toJSONString()));
        }
        json.put("ownedpokemons", pokemons);
        return json.toString(2);
    }

    public static User fromJSON(JSONObject json){
        int id = json.getInt("id");
        String name = json.getString("name");
        String email = json.getString("email");
        int currency = json.getInt("currency");

        User user = new User(id, name, email, currency);
        JSONArray pokemons = json.getJSONArray("ownedpokemons");
        for (int i = 0; i < pokemons.length(); i++){
            JSONObject pokeJSON = pokemons.getJSONObject(i);
            Pokemon p = Pokemon.fromJSON(pokeJSON);
            user.addPokemon(p);
        }
        return user;
    }

}
