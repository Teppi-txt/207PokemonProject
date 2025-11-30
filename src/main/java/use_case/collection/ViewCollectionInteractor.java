package use_case.collection;

import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ViewCollectionInteractor implements ViewCollectionInputBoundary {
    private ViewCollectionOutputBoundary viewCollectionPresenter;
    private User user;
    private final int PAGE_SIZE = 20;

    public ViewCollectionInteractor(ViewCollectionOutputBoundary viewCollectionPresenter, User user) {
        this.viewCollectionPresenter = viewCollectionPresenter;
        this.user = user;
    }

    @Override
    public void execute(ViewCollectionInputData viewCollectionInputData) {
        String filter = viewCollectionInputData.getFilter();
        int currentPage = Math.max(viewCollectionInputData.getCurrentPage(), 0);

        ViewCollectionOutputData outputData = new ViewCollectionOutputData();

        List<Pokemon> result = getPokemonList(filter);
        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, result.size());
        if (from < result.size()) {
            outputData.setPokemonOnPage(new ArrayList<>(result.subList(from, to)));
        } else {
            outputData.setPokemonOnPage(new ArrayList<>());
        }

        // check if displaying makes sense or if the fail view should be used
        if (!outputData.getPokemonOnPage().isEmpty()) {
            outputData.setOwnedPokemon(user.getOwnedPokemon());
            outputData.setSelectedPokemon(outputData.getPokemonOnPage().get(0));
            viewCollectionPresenter.prepareSuccessView(outputData);
        } else {
            String errorMessage = "You do not have any " + filter + " pokemon.";
            viewCollectionPresenter.prepareFailView(errorMessage, outputData);
        }
    }

    private List<Pokemon> getPokemonList(String filter) {
        List<Pokemon> result;
        switch (filter) {
            case "all":
                result = JSONLoader.getInstance().getAllPokemon();
                break;
            case "owned":
                result = user.getOwnedPokemon();
                break;
            case "shiny":
                result = getShinies(user.getOwnedPokemon());
                break;
            default:
                result = Collections.emptyList();
        }
        return result;
    }

    private List<Pokemon> getShinies(List<Pokemon> ownedPokemon) {
        List<Pokemon> shinyPokemon = new ArrayList<>();
        for (Pokemon pokemon : ownedPokemon) {
            if (pokemon.isShiny()) {
                shinyPokemon.add(pokemon);
            }
        }
        return shinyPokemon;
    }

    public void switchToHomeView() {
        viewCollectionPresenter.switchToHomeView();
    }
}
