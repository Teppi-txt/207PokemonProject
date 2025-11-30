package pokeapi;

import entities.AllMoves;
import entities.AllPokemon;
import entities.Move;
import entities.Pokemon;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class JSONLoader {
    private static volatile JSONLoader instance;

    private JSONLoader() {}

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void loadPokemon() {
        try {
            String jsonContent = readFile("src/assets/data/pokemon.json", StandardCharsets.UTF_8);
            JSONArray pokemonJSONArray = new JSONArray(jsonContent);

            for (int i = 0; i < pokemonJSONArray.length(); i++) {
                AllPokemon.getInstance().getAllPokemon().add(Pokemon.fromJSON(pokemonJSONArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadMoves() {
        try {
            String jsonContent = readFile("src/assets/data/moves.json", StandardCharsets.UTF_8);
            JSONArray moveJSONArray = new JSONArray(jsonContent);

            for (int i = 0; i < moveJSONArray.length(); i++) {
                AllMoves.getInstance().getAllMoves().add(Move.fromJSON(moveJSONArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static void main(String[] args) {
        loadMoves();
        loadPokemon();
    }
}
