package use_case.open_pack;

import entities.Pack;
import entities.Pokemon;
import entities.User;

import java.util.List;

public class OpenPackInteractor {

    private static final int PACK_COST = 1000;
    private final OpenPackUserDataAccessInterface userDataAccess;
    private final OpenPackOutputBoundary openPackPresenter;
    private final Pack pack;

    public OpenPackInteractor(OpenPackUserDataAccessInterface userDataAccess,
                              OpenPackOutputBoundary openPackPresenter,
                              Pack pack){
        this.userDataAccess = userDataAccess;
        this.openPackPresenter = openPackPresenter;
        this.pack = pack;
    }

    public void execute(OpenPackInputData inputData) {
        final String username = inputData.getUsername();

        if (userDataAccess.existsByName(username)){
            openPackPresenter.prepareFailView(username + " User does not exist.");
            return;
        }

        final User user = userDataAccess.get(username);

        if (!user.canAffordPack(PACK_COST)){
            openPackPresenter.prepareFailView("Not enough currency to open pack.");
            return;
        }

        user.buyPack(PACK_COST);

        List<Pokemon> openedCards = pack.openPack();

        for (Pokemon pokemon : openedCards){
            user.addPokemon(pokemon);
        }

        userDataAccess.save(user);

        OpenPackOutputData outputData =
                new OpenPackOutputData(user.getName(), openedCards, user.getCurrency());
        openPackPresenter.prepareSuccessView(outputData);

    }




}
