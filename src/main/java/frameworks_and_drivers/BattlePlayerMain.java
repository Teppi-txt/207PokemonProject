package frameworks_and_drivers;

import interface_adapters.battle_player.*;
import use_case.battle_player.*;
import entities.*;
import cards.Deck;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static BattlePlayerController controller;
    private static BattlePlayerDataAccessObject dataAccess;
    private static UserPlayerAdapter player1Adapter;
    private static UserPlayerAdapter player2Adapter;
    private static Battle battle;
    private static int turnCounter = 1;
    
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
        
        // 2. Create Player adapters (Frameworks & Drivers layer)
        player1Adapter = new UserPlayerAdapter(user1);
        player2Adapter = new UserPlayerAdapter(user2);
        
        // 3. Create Battle (Entities layer)
        battle = new Battle(1, user1, user2);
        battle.startBattle(); // Set status to IN_PROGRESS
        
        // 4. Create Data Access Object (Frameworks & Drivers layer)
        dataAccess = new BattlePlayerDataAccessObject();
        dataAccess.setBattle(battle);
        dataAccess.setUser(user1);
        
        // 5. Create ViewModel (Interface Adapters layer)
        BattlePlayerViewModel viewModel = new BattlePlayerViewModel();
        
        // 6. Create Presenter (Interface Adapters layer)
        BattlePlayerPresenter presenter = new BattlePlayerPresenter(viewModel);
        
        // 7. Create Interactor (Use Cases layer)
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(dataAccess, presenter);
        
        // 8. Create Controller (Interface Adapters layer)
        controller = new BattlePlayerController(interactor);
        
        // 9. Create View (Frameworks & Drivers layer)
        view = new BattlePlayerView(controller, viewModel);
        
        // 10. Create control panel for testing
        createControlPanel();
        
        // Initial view update to show battle state
        view.updateView();
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
    
    /**
     * Creates a control panel with buttons to interact with the battle
     */
    private static void createControlPanel() {
        JFrame controlFrame = new JFrame("Battle Control Panel");
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlFrame.setLayout(new GridLayout(0, 2, 5, 5));
        controlFrame.setSize(500, 300);
        controlFrame.setLocation(850, 0);
        
        // Button to execute a move turn for player 1
        JButton player1MoveButton = new JButton("Player 1: Use Move");
        player1MoveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executePlayerMove(player1Adapter, "Tackle");
            }
        });
        
        // Button to execute a move turn for player 2
        JButton player2MoveButton = new JButton("Player 2: Use Move");
        player2MoveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executePlayerMove(player2Adapter, "Water Gun");
            }
        });
        
        // Button to switch Pokemon for player 1
        JButton player1SwitchButton = new JButton("Player 1: Switch Pokemon");
        player1SwitchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executePlayerSwitch(player1Adapter);
            }
        });
        
        // Button to switch Pokemon for player 2
        JButton player2SwitchButton = new JButton("Player 2: Switch Pokemon");
        player2SwitchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executePlayerSwitch(player2Adapter);
            }
        });
        
        // Button to update view
        JButton updateButton = new JButton("Refresh View");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.updateView();
            }
        });
        
        // Button to start new battle
        JButton newBattleButton = new JButton("New Battle");
        newBattleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeApplication();
            }
        });
        
        controlFrame.add(player1MoveButton);
        controlFrame.add(player2MoveButton);
        controlFrame.add(player1SwitchButton);
        controlFrame.add(player2SwitchButton);
        controlFrame.add(updateButton);
        controlFrame.add(newBattleButton);
        
        controlFrame.setVisible(true);
    }
    
    /**
     * Executes a move turn for a player
     */
    private static void executePlayerMove(UserPlayerAdapter player, String moveName) {
        if (battle == null || !"IN_PROGRESS".equals(battle.getBattleStatus())) {
            JOptionPane.showMessageDialog(null, "Battle is not in progress!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Move move = new Move()
            .setName(moveName)
            .setType("normal")
            .setPower(40)
            .setAccuracy(100)
            .setPriority(0);
        
        MoveTurn turn = new MoveTurn(turnCounter++, player, turnCounter, move);
        view.executeTurn(turn);
    }
    
    /**
     * Executes a switch turn for a player
     */
    private static void executePlayerSwitch(UserPlayerAdapter player) {
        if (battle == null || !"IN_PROGRESS".equals(battle.getBattleStatus())) {
            JOptionPane.showMessageDialog(null, "Battle is not in progress!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find another Pokemon to switch to
        Pokemon currentActive = player.getActivePokemon();
        Pokemon switchTarget = null;
        
        for (Pokemon pokemon : player.getTeam()) {
            if (pokemon != currentActive && !pokemon.isFainted()) {
                switchTarget = pokemon;
                break;
            }
        }
        
        if (switchTarget == null) {
            JOptionPane.showMessageDialog(null, "No other Pokemon available to switch to!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwitchTurn turn = new SwitchTurn(turnCounter++, player, turnCounter, currentActive, switchTarget);
        player.switchPokemon(switchTarget);
        view.executeTurn(turn);
    }
}

