package use_case.collection;

import java.util.*;

import entities.Pokemon;
import entities.user.User;
import pokeapi.JSONLoader;

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
        final String filter = viewCollectionInputData.getFilter();
        final int currentPage = Math.max(viewCollectionInputData.getCurrentPage(), 0);

        final ViewCollectionOutputData outputData = new ViewCollectionOutputData();

        final List<Pokemon> result = new ArrayList<>(getPokemonList(filter));
        // sort the page
        result.sort(Comparator.comparingInt(Pokemon::getID));

        final int from = currentPage * PAGE_SIZE;
        final int to = Math.min(from + PAGE_SIZE, result.size());
        if (from < result.size()) {
            outputData.setPokemonOnPage(new ArrayList<>(result.subList(from, to)));
        }
        else {
            outputData.setPokemonOnPage(new ArrayList<>());
        }

        outputData.setHasNextPage(to < result.size());

        // check if displaying makes sense or if the fail view should be used
        if (!outputData.getPokemonOnPage().isEmpty()) {
            outputData.setOwnedPokemon(user.getOwnedPokemon());
            outputData.setSelectedPokemon(outputData.getPokemonOnPage().get(0));
            viewCollectionPresenter.prepareSuccessView(outputData);
        }
        else {
            final String errorMessage = "You do not have any " + filter + " pokemon.";
            viewCollectionPresenter.prepareFailView(errorMessage, outputData);
        }
    }

    private List<Pokemon> getPokemonList(String filter) {
        final List<Pokemon> result;
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
        final List<Pokemon> shinyPokemon = new ArrayList<>();
        for (Pokemon pokemon : ownedPokemon) {
            if (pokemon.isShiny()) {
                shinyPokemon.add(pokemon);
            }
        }
        return shinyPokemon;
    }

    /**
     * Switches the application back to the home view.
     */
    public void switchToHomeView() {
        viewCollectionPresenter.switchToHomeView();
    }
}
