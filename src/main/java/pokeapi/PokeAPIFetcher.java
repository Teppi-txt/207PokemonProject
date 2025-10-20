package pokeapi;

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

    public JSONObject getPokemon(String pokemonName) throws PokemonNotFoundException {
        Request request = new Request.Builder().url(API_HEADER + "pokemon/" + pokemonName).build();
        final Call call = client.newCall(request);
        try {
            Response response = call.execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            System.out.println(responseBody);
            return responseBody;
        } catch (IOException | JSONException exception) {
            throw new PokemonNotFoundException(pokemonName);
        }
    }

    public static void main(String[] args) throws PokemonNotFoundException {
        PokeAPIFetcher fetcher = new PokeAPIFetcher();
        fetcher.getPokemon("raichu");
    }
}
