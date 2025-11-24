package use_case.collection;

import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewCollectionInteractor implements ViewCollectionInputBoundary {
    private ViewCollectionOutputBoundary viewCollectionPresenter;
    private User user;

    public ViewCollectionInteractor(ViewCollectionOutputBoundary viewCollectionPresenter, User user) {
        this.viewCollectionPresenter = viewCollectionPresenter;
        this.user = user;
    }

    @Override
    public void execute(ViewCollectionInputData viewCollectionInputData) {
        String filter = viewCollectionInputData.getFilter();
        int currentPage = Math.max(viewCollectionInputData.getCurrentPage(), 0);
        ArrayList<Pokemon> currentPokemon = viewCollectionInputData.getPokemonOnPage();

        ViewCollectionOutputData outputData = new ViewCollectionOutputData();

        if (Objects.equals(filter, "all")) {
            List<Pokemon> pagePokemon = JSONLoader.allPokemon.subList(currentPage * 25, currentPage * 25 + 25);
            outputData.setPokemonOnPage(new ArrayList<>(pagePokemon));
        } else if (Objects.equals(filter, "owned")) {
            outputData.setPokemonOnPage(new ArrayList<>(user.getOwnedPokemon().subList(currentPage * 25, currentPage * 25 + 25)));
        } else if (Objects.equals(filter, "shiny")) {
            List<Pokemon> pagePokemon = getShinies(user.getOwnedPokemon()).subList(currentPage * 25, currentPage * 25 + 25);
            outputData.setPokemonOnPage(new ArrayList<>(pagePokemon));
        }
        outputData.setSelectedPokemon(JSONLoader.allPokemon.get(1));

        viewCollectionPresenter.prepareSuccessView(outputData);
    }

    private ArrayList<Pokemon> getShinies(List<Pokemon> ownedPokemon) {
        ArrayList<Pokemon> shinyPokemon = new ArrayList<>();
        for (Pokemon pokemon : ownedPokemon) {
            if (pokemon.isShiny()) {
                shinyPokemon.add(pokemon);
            }
        }
        return shinyPokemon;
    }
}
