package pokeapi;

import entities.Move;
import entities.Pokemon;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class JSONLoader {

    // singleton instance
    private static volatile JSONLoader instance;

    // canonical storage lists
    private final ArrayList<Pokemon> allPokemon = new ArrayList<>();
    private final ArrayList<Move> allMoves = new ArrayList<>();

    // private constructor
    private JSONLoader() {
        loadPokemon();
        loadMoves();
    }

    // singleton getter
    public static JSONLoader getInstance() {
        if (instance == null) {
            synchronized (JSONLoader.class) {
                if (instance == null) {
                    instance = new JSONLoader();
                }
            }
        }
        return instance;
    }

    // file reading utility
    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    // load Pokémon
    public void loadPokemon() {
        try {
            String jsonContent = readFile("src/assets/data/pokemon.json");
            JSONArray pokemonJSONArray = new JSONArray(jsonContent);

            for (int i = 0; i < pokemonJSONArray.length(); i++) {
                allPokemon.add(Pokemon.fromJSON(pokemonJSONArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Pokémon", e);
        }
    }

    // load Moves
    public void loadMoves() {
        try {
            String jsonContent = readFile("src/assets/data/moves.json");
            JSONArray moveJSONArray = new JSONArray(jsonContent);

            for (int i = 0; i < moveJSONArray.length(); i++) {
                allMoves.add(Move.fromJSON(moveJSONArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load moves", e);
        }
    }

    // getters
    public ArrayList<Pokemon> getAllPokemon() {
        return allPokemon;
    }

    public ArrayList<Move> getAllMoves() {
        return allMoves;
    }
}
