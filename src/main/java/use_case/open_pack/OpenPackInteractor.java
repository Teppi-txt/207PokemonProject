package use_case.open_pack;

import entities.Pack;
import entities.Pokemon;
import entities.User;

import java.util.ArrayList;
import java.util.List;

public class OpenPackInteractor implements OpenPackInputBoundary {

    private final OpenPackUserDataAccessInterface userDataAccess;
    private final OpenPackOutputBoundary presenter;
    private final Pack pack;

    private static final int PACK_COST = 1000;

    public OpenPackInteractor(OpenPackUserDataAccessInterface userDataAccess,
                              OpenPackOutputBoundary presenter,
                              Pack pack) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
        this.pack = pack;
    }

    @Override
    public void execute(OpenPackInputData input) {
        User user = userDataAccess.get();

        if (!user.canAffordPack(PACK_COST)) {
            presenter.prepareFailView("Not enough currency!");
            return;
        }

        List<Pokemon> openedCards = pack.openPack();

        List<Boolean> duplicateFlags = new ArrayList<>();
        for (Pokemon p : openedCards) {
            duplicateFlags.add(user.hasDuplicatePokemon(p));
        }

        user.buyPack(PACK_COST);

        userDataAccess.save(user);

        OpenPackOutputData outputData = new OpenPackOutputData(
                openedCards,
                duplicateFlags,
                user.getCurrency()
        );

        presenter.prepareSuccessView(outputData);
    }
}
