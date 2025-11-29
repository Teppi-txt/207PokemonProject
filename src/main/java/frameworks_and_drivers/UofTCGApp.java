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
import use_case.open_pack.OpenPackOutputData;

import javax.swing.*;
import java.util.List;

public class UofTCGApp {

    public static void main(String[] args) {

        JSONLoader.loadPokemon();

        List<Pokemon> cardPool = JSONLoader.allPokemon;
        System.out.println("Card pool size = " + cardPool.size());

        Pack pack = new Pack(1, "UofT Base Set", cardPool);

        JsonUserDataAccess userDataAccess = new JsonUserDataAccess("user.json");
        if (userDataAccess.get() == null) {
            User newUser = new User(1, "TestUser", "test@uoft.ca", 5000);
            userDataAccess.save(newUser);
        }

        User user = userDataAccess.get();

        OpenPackViewModel viewModel = new OpenPackViewModel();
        viewModel.getState().setRemainingCurrency(user.getCurrency());

        SwingUtilities.invokeLater(() -> {
            ViewManagerFrame frame = new ViewManagerFrame(viewModel, null);
            OpenPackOutputBoundary presenter = new OpenPackPresenter(viewModel, frame);
            OpenPackInputBoundary interactor = new OpenPackInteractor(userDataAccess, presenter, pack);
            OpenPackController controller = new OpenPackController(interactor);

            frame.setController(controller);
        });

    }
}