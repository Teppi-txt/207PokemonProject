package pokeapi;

import entities.Move;
import entities.Pokemon;
import entities.Stats;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;


public class PokeAPIFetcher {
    private static final String API_HEADER = "https://pokeapi.co/api/v2/";

    static class PokemonNotFoundException extends Exception {
        public PokemonNotFoundException(String pokemon) {
            super("Pokemon not found: " + pokemon);
        }
    }

    static class MoveNotFoundException extends Exception {
        public MoveNotFoundException(String move) {
            super("Move not found: " + move);
        }
    }

    private static final OkHttpClient client = new OkHttpClient();

    public static Pokemon getPokemon(String pokemon) throws PokemonNotFoundException {
        Request request = new Request.Builder().url(API_HEADER + "pokemon/" + pokemon).build();
        final Call call = client.newCall(request);
        try {
            Response response = call.execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            String pokemonName = responseBody.getString("name");
            int pokemonID = responseBody.getInt("id");
            ArrayList<String> types = extractTypesFromJSON(responseBody);
            Stats stats = extractStatsFromJSON(responseBody);
            ArrayList<String> moves = extractMovesFromJSON(responseBody);


            return new Pokemon(pokemonName, pokemonID, types, stats, moves);

        } catch (IOException | JSONException exception) {
            throw new PokemonNotFoundException(pokemon);
        }
    }
    
    public static ArrayList<String> getAllPokemonNames() throws PokemonNotFoundException {
        Request request = new Request.Builder().url(API_HEADER + "pokemon/" + "?offset=0&limit=200").build();
        final Call call = client.newCall(request);
        try {
            Response response = call.execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            final JSONArray results = responseBody.getJSONArray("results");

            ArrayList<String> pokemonNames = new ArrayList<>();
            for (int i = 0; i < results.length(); i++) {
                pokemonNames.add(results.getJSONObject(i).getString("name"));
            }

            return pokemonNames;

        } catch (IOException | JSONException exception) {
            throw new PokemonNotFoundException("all pokemon");
        }
    }

    public Move getMove(String move) throws MoveNotFoundException {
        Request request = new Request.Builder().url(API_HEADER + "move/" + move).build();
        final Call call = client.newCall(request);

        try {
            Response response = call.execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            Move returnMove = new Move()
                    .setName(responseBody.getString("name"))
                    .setAccuracy(responseBody.getInt("accuracy"))
                    .setPriority(responseBody.getInt("priority"))
                    .setPower(responseBody.getInt("power"))
                    .setType(responseBody.getJSONObject("type").getString("name"))
                    .setEffect(responseBody.getJSONObject("meta").getJSONObject("ailment").getString("name"))
                    .setDamageClass(responseBody.getJSONObject("damage_class").getString("name"));

            return returnMove;

        } catch (IOException | JSONException exception) {
            throw new MoveNotFoundException(move);
        }
    }

    private static ArrayList<String> extractMovesFromJSON(JSONObject responseBody) {
        final JSONArray moveJA = responseBody.getJSONArray("moves");
        ArrayList<String> moves = new ArrayList<>();
        for (int i = 0; i < moveJA.length(); i++) {
            moves.add(moveJA.getJSONObject(i).getJSONObject("move").getString("name"));
        }
        return moves;
    }

    private static ArrayList<String> extractTypesFromJSON(JSONObject responseBody) {
        final JSONArray typeJA = responseBody.getJSONArray("types");
        ArrayList<String> types = new ArrayList<>();
        for (int i = 0; i < typeJA.length(); i++) {
            types.add(typeJA.getJSONObject(i).getJSONObject("type").getString("name"));
        }
        return types;
    }

    private static Stats extractStatsFromJSON(JSONObject responseBody) {
        Stats stats = new Stats();
        final JSONArray statsJA = responseBody.getJSONArray("stats");

        for (int i = 0; i < statsJA.length(); i++) {
            JSONObject statObj = statsJA.getJSONObject(i);
            String statName = statObj.getJSONObject("stat").getString("name");
            stats.setStat(statName, statObj.getInt("base_stat"));
        }
        return stats;
    }

    public static void main(String[] args) throws PokemonNotFoundException, MoveNotFoundException {
        PokeAPIFetcher fetcher = new PokeAPIFetcher();
        System.out.println(fetcher.getPokemon("farigiraf"));
    }
}
