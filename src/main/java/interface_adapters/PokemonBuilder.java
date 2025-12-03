package interface_adapters;

import entities.Move;
import entities.Pokemon;
import entities.Stats;

import java.util.ArrayList;

/**
 * Builder class for the Pokemon entity.
 */

public class PokemonBuilder {

    private final Pokemon pokemon;

    public PokemonBuilder() {
        this.pokemon = new Pokemon();
    }

    public PokemonBuilder addName(String name) {
        pokemon.setName(name);
        return this;
    }

    public PokemonBuilder addID(int id) {
        pokemon.setID(id);
        return this;
    }

    public PokemonBuilder addType(ArrayList<String> types) {
        pokemon.setTypes(types);
        return this;
    }

    public PokemonBuilder addMove(ArrayList<String> moves) {
        pokemon.setMoves(moves);
        return this;
    }

    public PokemonBuilder addStats(Stats stats) {
        pokemon.setStats(stats);
        return this;
    }

    public Pokemon build() {
        return pokemon;
    }
}