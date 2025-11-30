package view.collection;

import entities.Pokemon;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.util.List;

public class CollectionViewHelpers {
    public static Border paddingBorder(int px) {
        return new EmptyBorder(px, px, px, px);
    }

    public static boolean pokemonIsInList(Pokemon pokemon, List<Pokemon> pokemons) {
        for (Pokemon listPokemon : pokemons) {
            if (listPokemon.getID() == pokemon.getID()) {
                return true;
            }
        }
        return false;
    }

    public static Pokemon getPokemonInList(Pokemon pokemon, List<Pokemon> pokemons) {
        for (Pokemon listPokemon : pokemons) {
            if (listPokemon.getID() == pokemon.getID()) {
                return listPokemon;
            }
        }
        return null;
    }
}