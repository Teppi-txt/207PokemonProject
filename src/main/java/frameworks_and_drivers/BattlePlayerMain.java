package frameworks_and_drivers;

import interface_adapters.battle_player.*;
import use_case.battle_player.*;
import entities.*;
import cards.Deck;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Main application class for Battle Player use case.
 * This class wires together all the components following Clean Architecture principles:
 * - Frameworks & Drivers: BattlePlayerView, BattlePlayerDataAccessObject
 * - Interface Adapters: BattlePlayerController, BattlePlayerPresenter, BattlePlayerViewModel
 * - Use Cases: BattlePlayerInteractor
 * - Entities: Battle, User, Pokemon, Turn, etc.
 */
public class BattlePlayerMain {
    
    private static BattlePlayerView view;
    
    public static void main(String[] args) {
        // Run on Event Dispatch Thread for Swing
        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error initializing application: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Initializes the application by creating and wiring all components
     */
    private static void initializeApplication() {
        // 1. Create test data (Entities layer)
        User user1 = createTestUser("Ash", 1);
        User user2 = createTestUser("Gary", 2);
        
        // 2. Create Battle (Entities layer)
        Battle battle = new Battle(1, user1, user2);
        battle.startBattle(); // Set status to IN_PROGRESS
        
        // 3. Create Data Access Object (Frameworks & Drivers layer)
        BattlePlayerDataAccessObject dataAccess = new BattlePlayerDataAccessObject();
        dataAccess.setBattle(battle);
        dataAccess.setUser(user1);
        
        // 4. Create ViewModel (Interface Adapters layer)
        BattlePlayerViewModel viewModel = new BattlePlayerViewModel();
        
        // 5. Create Presenter (Interface Adapters layer)
        BattlePlayerPresenter presenter = new BattlePlayerPresenter(viewModel);
        
        // 6. Create Interactor (Use Cases layer)
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(dataAccess, presenter);
        
        // 7. Create Controller (Interface Adapters layer)
        BattlePlayerController controller = new BattlePlayerController(interactor);
        
        // 8. Create View (Frameworks & Drivers layer)
        view = new BattlePlayerView(controller, viewModel);
        
        // 9. Set initial state in ViewModel to trigger view update
        BattlePlayerState initialState = new BattlePlayerState();
        initialState.setBattle(battle);
        initialState.setBattleStatus(battle.getBattleStatus());
        initialState.setBattleEnded(false);
        initialState.setTurnResult("Battle started! Select a move or switch Pokemon.");
        viewModel.setState(initialState);
    }
    
    /**
     * Creates a test user with Pokemon
     */
    private static User createTestUser(String name, int id) {
        User user = new User(id, name, name.toLowerCase() + "@pokemon.com", 1000);
        
        // Create Pokemon for the user
        ArrayList<String> types1 = new ArrayList<>();
        types1.add("Fire");
        ArrayList<String> moves1 = new ArrayList<>();
        moves1.add("Ember");
        moves1.add("Tackle");
        Stats stats1 = new Stats(100, 50, 50, 50, 50, 50);
        Pokemon pokemon1 = new Pokemon("Charmander", 4, types1, stats1, moves1);
        user.addPokemon(pokemon1);
        
        ArrayList<String> types2 = new ArrayList<>();
        types2.add("Water");
        ArrayList<String> moves2 = new ArrayList<>();
        moves2.add("Water Gun");
        moves2.add("Bubble");
        Stats stats2 = new Stats(90, 55, 45, 60, 50, 65);
        Pokemon pokemon2 = new Pokemon("Squirtle", 7, types2, stats2, moves2);
        user.addPokemon(pokemon2);
        
        return user;
    }
    
}

