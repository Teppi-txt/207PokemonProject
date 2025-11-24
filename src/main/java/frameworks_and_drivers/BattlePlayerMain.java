package frameworks_and_drivers;

import interface_adapters.battle_player.*;
import use_case.battle_player.*;
import entities.*;
import cards.Deck;
import pokeapi.JSONLoader;

import javax.swing.*;
import java.util.ArrayList;

// entry point wiring the battle player flow end-to-end
public class BattlePlayerMain {
    
    private static BattlePlayerView view;
    
    public static void main(String[] args) {
        // boot swing on the edt
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
    
    // wire clean-architecture layers together
    private static void initializeApplication() {
        // preload local JSON data so we stay offline
        JSONLoader.loadMoves();
        JSONLoader.loadPokemon();

        User user1 = createTestUser("Ash", 1);
        User user2 = createTestUser("Gary", 2);
        
        Battle battle = new Battle(1, user1, user2);
        battle.startBattle();
        
        BattlePlayerDataAccessObject dataAccess = new BattlePlayerDataAccessObject();
        dataAccess.setBattle(battle);
        dataAccess.setUser(user1);
        
        BattlePlayerViewModel viewModel = new BattlePlayerViewModel();
        
        BattlePlayerPresenter presenter = new BattlePlayerPresenter(viewModel);
        
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(dataAccess, presenter);
        
        BattlePlayerController controller = new BattlePlayerController(interactor);
        
        view = new BattlePlayerView(controller, viewModel);
        
        BattlePlayerState initialState = new BattlePlayerState();
        initialState.setBattle(battle);
        initialState.setBattleStatus(battle.getBattleStatus());
        initialState.setBattleEnded(false);
        initialState.setTurnResult("Battle started! Select a move or switch Pokemon.");
        viewModel.setState(initialState);
    }
    
    // build a demo user roster
    private static User createTestUser(String name, int id) {
        User user = new User(id, name, name.toLowerCase() + "@pokemon.com", 1000);
        String[] picks = {"chansey", "terapagos-stellar", "bulbasaur", "pikachu", "eevee", "onix"};
        for (String pick : picks) {
            try {
                Pokemon p = fetchPokemonFromJson(pick);
                user.addPokemon(p);
            } catch (Exception e) {
                // If local lookup fails for a specific pokemon, add a minimal placeholder so team size stays 6
                ArrayList<String> types = new ArrayList<>();
                types.add("normal");
                ArrayList<String> moves = new ArrayList<>();
                moves.add("tackle");
                moves.add("scratch");
                Stats stats = new Stats(80, 40, 40, 40, 40, 40);
                Pokemon fallback = new Pokemon(pick, 0, types, stats, moves);
                user.addPokemon(fallback);
            }
        }
        return user;
    }

    private static Pokemon fetchPokemonFromJson(String name) {
        for (Pokemon candidate : JSONLoader.allPokemon) {
            if (candidate.getName().equalsIgnoreCase(name)) {
                ArrayList<String> moves = candidate.getMoves();
                int limit = Math.min(4, moves.size());
                ArrayList<String> trimmed = new ArrayList<>(moves.subList(0, limit));
                return new Pokemon(
                    candidate.getName(),
                    candidate.getId(),
                    new ArrayList<>(candidate.getTypes()),
                    candidate.getStats().copy(),
                    trimmed
                );
            }
        }
        throw new IllegalArgumentException("Pokemon not found in local JSON: " + name);
    }
    
}
