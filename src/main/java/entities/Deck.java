package entities;

import java.util.ArrayList;

public class Deck {
    private static final int DECK_LIMIT = 5; //idk tbh we can change as needed

    private int id;
    private String name;
    private ArrayList<Pokemon> pokemons;

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

}
