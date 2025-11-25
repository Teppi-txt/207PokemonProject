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
    private static final int MOVE_COUNT = 937;
    private static final int POKEMON_COUNT = 1328;

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
        Request request = new Request.Builder().url(API_HEADER + "pokemon/" + "?offset=0&limit=" + POKEMON_COUNT).build();
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

    public static ArrayList<String> getAllMoveNames() throws MoveNotFoundException {
        Request request = new Request.Builder().url(API_HEADER + "move/" + "?offset=0&limit=" + MOVE_COUNT).build();
        final Call call = client.newCall(request);
        try {
            Response response = call.execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            final JSONArray results = responseBody.getJSONArray("results");

            ArrayList<String> moveNames = new ArrayList<>();
            for (int i = 0; i < results.length(); i++) {
                moveNames.add(results.getJSONObject(i).getString("name"));
            }

            return moveNames;

        } catch (IOException | JSONException exception) {
            throw new MoveNotFoundException("all moves");
        }
    }

    public static Move getMove(String move) throws MoveNotFoundException {
        Request request = new Request.Builder()
                .url(API_HEADER + "move/" + move)
                .build();
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            JSONObject json = new JSONObject(response.body().string());

            Move m = new Move()
                    .setName(json.getString("name"))
                    .setAccuracy(json.optInt("accuracy", 0))
                    .setPower(json.optInt("power", 0))
                    .setPp(json.optInt("pp", 0))
                    .setPriority(json.optInt("priority", 0))
                    .setType(json.getJSONObject("type").getString("name"))
                    .setDamageClass(json.getJSONObject("damage_class").getString("name"));

            // meta effects
            if (json.has("meta")) {
                JSONObject meta = json.getJSONObject("meta");
                if (meta.has("ailment")) {
                    m.setEffect(meta.getJSONObject("ailment").getString("name"));
                }
                if (meta.has("ailment_chance")) {
                    m.setEffect_chance(meta.getInt("ailment_chance"));
                }
            }

            // effect entries
            if (json.has("effect_entries")) {
                JSONArray arr = json.getJSONArray("effect_entries");
                if (arr.length() > 0) {
                    m.setShortEffect(arr.getJSONObject(0).getString("short_effect"));
                }
            }

            return m;

        } catch (Exception e) {
            throw new MoveNotFoundException(move);
        }
    }



    // given a json string nested in many json objects, extracts it while typechecking for null
    public static String getNestedString(JSONObject obj, String... keys) {
        JSONObject current = obj;
        for (int i = 0; i < keys.length - 1; i++) {
            if (current == null) return null;
            current = current.optJSONObject(keys[i]);
        }
        return (current == null) ? null : current.optString(keys[keys.length - 1], null);
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
