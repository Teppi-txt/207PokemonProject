package use_case.collection;

import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;

import java.util.ArrayList;
import java.util.Objects;

public class ViewCollectionInteractor implements ViewCollectionInputBoundary{

    @Override
    public void execute(ViewCollectionInputData viewCollectionInputData) {
        final User user = new User(0, "Teppi", "teppipersonal@gmail.com", 100);
        for (int i = 0; i < 50; i++) {
            user.addPokemon(JSONLoader.allPokemon.get(2*i));
        }
        JSONLoader.loadPokemon();

        String filter = viewCollectionInputData.getFilter();
        int currentPage = viewCollectionInputData.getCurrentPage();
        ArrayList<Pokemon> ownedPokemon = viewCollectionInputData.getOwnedPokemon();

        ViewCollectionOutputData outputData = new ViewCollectionOutputData();

        if (Objects.equals(filter, "all")) {
            outputData.setDisplayedPokemon(JSONLoader.allPokemon);
        } else if (Objects.equals(filter, "owned")) {
            outputData.setDisplayedPokemon(ownedPokemon);
        } else if (Objects.equals(filter, "shiny")) {
            outputData.setDisplayedPokemon(getShinies(ownedPokemon));
        }
    }

    private ArrayList<Pokemon> getShinies(ArrayList<Pokemon> ownedPokemon) {
        ArrayList<Pokemon> shinyPokemon = new ArrayList<>();
        for (Pokemon pokemon : ownedPokemon) {
            if (pokemon.isShiny()) {
                shinyPokemon.add(pokemon);
            }
        }
        return shinyPokemon;
    }
}
