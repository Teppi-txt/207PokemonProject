package use_case.open_pack;

import entities.Pack;
import entities.Pokemon;
import entities.User;

import java.util.List;

public class OpenPackInteractor implements OpenPackInputBoundary{

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

    @Override
    public void execute(OpenPackInputData inputData) {

        final User user = userDataAccess.get();
        if (user == null) {
            openPackPresenter.prepareFailView("User not found");
            return;
        }

        if (!user.canAffordPack(PACK_COST)){
            openPackPresenter.prepareFailView("Not enough currency to open pack.");
            return;
        }

        user.buyPack(PACK_COST);

        List<Pokemon> openedCards = pack.openPack();

        for (Pokemon pokemon : openedCards){
            boolean isDuplicate = user.hasDuplicatePokemon(pokemon);
            user.addPokemon(pokemon);
            if(isDuplicate){
                user.addCurrency(50);
            }
        }

        userDataAccess.save(user);

        OpenPackOutputData outputData =
                new OpenPackOutputData(openedCards, user.getCurrency());
        openPackPresenter.prepareSuccessView(outputData);

    }




}
