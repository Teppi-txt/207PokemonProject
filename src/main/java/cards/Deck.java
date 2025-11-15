package cards;

import entities.Pokemon;

import java.util.ArrayList;

public class Deck {
    int id;
    String name;
    ArrayList<Pokemon> pokemons;

    public Deck() {
        this.id = 0; //this isnt final, need 2 find a way for ids to not overlap
        this.name = "Deck " + (this.id + 1); //??
        this.pokemons = new ArrayList<>();
    }

    public Deck(int id, String name, ArrayList<Pokemon> pokemons) {
        this.id = id;
        this.name = name;
        this.pokemons = pokemons;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public ArrayList<Pokemon> getPokemons() { return pokemons; }
    public void setPokemons(ArrayList<Pokemon> pokemons) { this.pokemons = pokemons; }

    public void addPokemon(Pokemon pokemon) { this.pokemons.add(pokemon); }
    public void removePokemon(Pokemon pokemon) { this.pokemons.remove(pokemon); }

}
