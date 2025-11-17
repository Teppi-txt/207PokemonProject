package pokeapi;

import entities.Move;
import entities.Pokemon;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class JSONSaver {
    public static void savePokemonData() {
        try {
            ArrayList<String> pokemons = PokeAPIFetcher.getAllPokemonNames();
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream("src/assets/data/pokemon.json"), StandardCharsets.UTF_8));
            writer.write("[");

            for(String pokemonName : pokemons) {
                Pokemon pokemon =  PokeAPIFetcher.getPokemon(pokemonName);
                writer.write(pokemon.toJSONString());

                // don't want trailing comma
                if (!pokemonName.equals(pokemons.get(pokemons.size() - 1))) {
                    writer.write(",");
                }

                writer.write("\n");
            }
            writer.write("]");
            writer.close();
        } catch (PokeAPIFetcher.PokemonNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMoveData() {
        try {
            ArrayList<String> moves = PokeAPIFetcher.getAllMoveNames();
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("src/assets/data/moves.json"), StandardCharsets.UTF_8));
            writer.write("[");

            for(String moveName : moves) {
                Move pokemon =  PokeAPIFetcher.getMove(moveName);
                writer.write(pokemon.toJSONString());

                // don't want trailing comma
                if (!moveName.equals(moves.get(moves.size() - 1))) {
                    writer.write(",");
                }

                writer.write("\n");
            }
            writer.write("]");
            writer.close();
        } catch (PokeAPIFetcher.MoveNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void downloadFile(String url, String localPath) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(localPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void main(String[] args) {
        saveMoveData();
        savePokemonData();
    }
}
