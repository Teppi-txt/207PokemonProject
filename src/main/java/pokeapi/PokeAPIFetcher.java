package pokeapi;

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
    private final String API_HEADER = "https://pokeapi.co/api/v2/";

    class PokemonNotFoundException extends Exception{
        public PokemonNotFoundException(String pokemon) {
            super("Pokemon not found: " + pokemon);
        }
    }

    private final OkHttpClient client = new OkHttpClient();

    public Pokemon getPokemon(String pokemon) throws PokemonNotFoundException {
        Request request = new Request.Builder().url(API_HEADER + "pokemon/" + pokemon).build();
        final Call call = client.newCall(request);
        try {
            Response response = call.execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            String pokemonName = responseBody.getString("name");
            int pokemonID = responseBody.getInt("id");
            ArrayList<String> types = extractTypesFromJSON(responseBody);
            Stats stats = extractStatsFromJSON(responseBody);


            return new Pokemon(pokemonName, pokemonID, types, stats);

        } catch (IOException | JSONException exception) {
            throw new PokemonNotFoundException(pokemon);
        }
    }

    private ArrayList<String> extractTypesFromJSON(JSONObject responseBody) {
        final JSONArray typeJA = responseBody.getJSONArray("types");
        ArrayList<String> types = new ArrayList<>();
        for (int i = 0; i < typeJA.length(); i++) {
            types.add(typeJA.getJSONObject(i).getJSONObject("type").getString("name"));
        }
        return types;
    }

    private Stats extractStatsFromJSON(JSONObject responseBody) {
        Stats stats = new Stats();
        final JSONArray statsJA = responseBody.getJSONArray("stats");

        for (int i = 0; i < statsJA.length(); i++) {
            JSONObject statObj = statsJA.getJSONObject(i);
            String statName = statObj.getJSONObject("stat").getString("name");
            stats.setStat(statName, statObj.getInt("base_stat"));
        }
        return stats;
    }

    public static void main(String[] args) throws PokemonNotFoundException {
        PokeAPIFetcher fetcher = new PokeAPIFetcher();
        System.out.println(fetcher.getPokemon("farigiraf"));
        System.out.println(fetcher.getPokemon("farigiraf").getStats());
    }
}
