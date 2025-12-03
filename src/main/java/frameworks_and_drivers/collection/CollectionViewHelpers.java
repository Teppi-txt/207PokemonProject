package frameworks_and_drivers.collection;

import entities.Pokemon;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Static class containing helper methods and constants for the Collection View
 */

public class CollectionViewHelpers {
    public static final Dimension COLLECTION_VIEW_WINDOW_SIZE = new Dimension(1000, 700);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);
    public static final Dimension POKEMON_PANEL_SIZE = new Dimension(600, 1000);
    public static final Border SMALL_BORDER = paddingBorder(10);
    public static final Dimension POKEMON_BUTTON_SIZE = new Dimension(124, 124);
    public static final Dimension POKEMON_SPRITE_SIZE = new Dimension(96, 96);

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

    public static Font mainFont(int px) {
        return new Font("Arial", Font.PLAIN, px);
    }
}