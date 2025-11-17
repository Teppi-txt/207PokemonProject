package use_case.collection;

import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;
import use_case.open_pack.OpenPackOutputData;

import java.util.List;

public class ViewCollectionInteractor implements ViewCollectionInputBoundary{

    @Override
    public void execute(ViewCollectionInputData viewCollectionInputData) {
        final User user = new User(0, "Teppi", "teppipersonal@gmail.com", 100);
        JSONLoader.loadPokemon();
        for (int i = 0; i < 50; i++) {
            user.addPokemon(JSONLoader.allPokemon.get(2*i));
        }
        if (user == null) {
            return;
        }

    }
}
