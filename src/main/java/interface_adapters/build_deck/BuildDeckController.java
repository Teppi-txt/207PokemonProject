package interface_adapters.build_deck;

import use_case.build_deck.BuildDeckInputBoundary;
import use_case.build_deck.BuildDeckInputData;

import java.util.List;

public class BuildDeckController {
    private final BuildDeckInputBoundary buildDeckUseCaseInteractor;

    public BuildDeckController(BuildDeckInputBoundary buildDeckUseCaseInteractor) {
        this.buildDeckUseCaseInteractor = buildDeckUseCaseInteractor;
    }

    public void buildDeck(String deckName, List<Pokemon> pokemon, boolean isRandom) {
        BuildDeckInputData buildDeckInputData = new BuildDeckInputData(deckName, pokemon, isRandom);
        buildDeckUseCaseInteractor.execute(buildDeckInputData);
    }
}
