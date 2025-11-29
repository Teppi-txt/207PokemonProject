package frameworks_and_drivers;

import entities.Pack;
import entities.Pokemon;
import entities.User;
import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackPresenter;
import interface_adapters.open_pack.OpenPackViewModel;
import pokeapi.JSONLoader;
import use_case.open_pack.OpenPackInputBoundary;
import use_case.open_pack.OpenPackInteractor;
import use_case.open_pack.OpenPackOutputBoundary;

import java.util.List;

public class UofTCGApp {

    public static void main(String[] args) {
        // load pokemon from json
        System.out.println("Loading Pokémon data...");
        JSONLoader.loadPokemon();
        if (JSONLoader.allPokemon.isEmpty()) {
            System.err.println("ERROR: No Pokémon loaded. Check your pokemon.json path.");
            return;
        }
        System.out.println("Loaded " + JSONLoader.allPokemon.size() + " Pokémon.");
        System.out.println("First Pokémon: " + JSONLoader.allPokemon.get(0).getName());


        // Create a Pack
        List<Pokemon> cardPool = JSONLoader.allPokemon;
        Pack pack = new Pack(1, "UofT Base Set", cardPool);


        // Create User Storage (load or create new)
        JsonUserDataAccess userDataAccess = new JsonUserDataAccess("user.json");
        // If no user exists, create a new "test" user
        if (userDataAccess.get() == null) {
            System.out.println("Creating NEW user...");
            User newUser = new User(1, "TestUser", "test@uoft.ca", 5000);
            userDataAccess.save(newUser);
        }


        OpenPackViewModel viewModel = new OpenPackViewModel();
        OpenPackOutputBoundary presenter = new OpenPackPresenter(viewModel);
        OpenPackInputBoundary interactor =
                new OpenPackInteractor(userDataAccess, presenter, pack);
        OpenPackController controller = new OpenPackController(interactor);


        // Start GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            System.out.println("Launching GUI...");
            new ViewManagerFrame(viewModel, controller);
        });
    }

}
