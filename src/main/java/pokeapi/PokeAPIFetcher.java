package pokeapi;

import entities.Pokemon;
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
            final String pokemonName = responseBody.getString("name");
            final int pokemonID = responseBody.getInt("id");
            return new Pokemon(pokemonName, pokemonID);

        } catch (IOException | JSONException exception) {
            throw new PokemonNotFoundException(pokemon);
        }
    }

    public static void main(String[] args) throws PokemonNotFoundException {
        PokeAPIFetcher fetcher = new PokeAPIFetcher();
        System.out.println(fetcher.getPokemon("raichu"));

    }
}
