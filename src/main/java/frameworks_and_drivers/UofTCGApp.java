package frameworks_and_drivers;

import entities.Pack;
import entities.Pokemon;
import entities.User;
import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackPresenter;
import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;
import pokeapi.JSONLoader;
import use_case.open_pack.OpenPackInputBoundary;
import use_case.open_pack.OpenPackInteractor;
import use_case.open_pack.OpenPackOutputBoundary;

import java.util.List;

public class UofTCGApp {

    public static void main(String[] args) {

        JSONLoader.loadPokemon();
        List<Pokemon> cardPool = JSONLoader.allPokemon;
        Pack pack = new Pack(1, "UofT Base Set", cardPool);

        JsonUserDataAccess userDataAccess = new JsonUserDataAccess("user.json");
        if (userDataAccess.get() == null) {
            User newUser = new User(1, "TestUser", "test@uoft.ca", 5000);
            userDataAccess.save(newUser);
        }

        User user = userDataAccess.get();

        OpenPackViewModel viewModel = new OpenPackViewModel();
        OpenPackState initState = new OpenPackState();
        initState.setRemainingCurrency(user.getCurrency());
        initState.setRevealMode(false);
        initState.setRevealIndex(0);
        viewModel.setState(initState);

        OpenPackOutputBoundary presenter = new OpenPackPresenter(viewModel);
        OpenPackInputBoundary interactor = new OpenPackInteractor(userDataAccess, presenter, pack);
        OpenPackController controller = new OpenPackController(interactor);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new ViewManagerFrame(viewModel, controller);
        });
    }
}
