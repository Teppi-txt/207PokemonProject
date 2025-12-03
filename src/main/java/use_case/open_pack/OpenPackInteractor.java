package use_case.open_pack;

import java.util.ArrayList;
import java.util.List;

import entities.Pokemon;
import entities.open_pack.Pack;
import entities.user.User;

public class OpenPackInteractor implements OpenPackInputBoundary {

    private static final int PACK_COST = 1000;

    private final OpenPackUserDataAccessInterface userDataAccess;
    private final OpenPackOutputBoundary presenter;
    private final Pack pack;

    public OpenPackInteractor(OpenPackUserDataAccessInterface userDataAccess,
                              OpenPackOutputBoundary presenter,
                              Pack pack) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
        this.pack = pack;
    }

    @Override
    public void execute(OpenPackInputData input) {
        final User user = userDataAccess.get();

        if (!user.canAffordPack(PACK_COST)) {
            presenter.prepareFailView("Not enough currency!");
            return;
        }

        // Open the pack
        final List<Pokemon> openedCards = pack.openPack();

        final List<Boolean> duplicateFlags = new ArrayList<>();
        for (Pokemon p : openedCards) {
            duplicateFlags.add(user.hasDuplicatePokemon(p));
            user.addPokemon(p);
        }

        // Deduct currency
        user.buyPack(PACK_COST);

        userDataAccess.saveUser(user);

        final OpenPackOutputData outputData = new OpenPackOutputData(
                openedCards,
                duplicateFlags,
                user.getCurrency()
        );

        presenter.prepareSuccessView(outputData);
    }
}
