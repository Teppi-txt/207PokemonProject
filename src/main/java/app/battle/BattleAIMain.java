package app.battle;

import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;

import javax.swing.*;

/**
 * Main entry point for the Battle AI GUI application.
 * Initializes data and launches the deck selection view.
 */
public class BattleAIMain {

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }

        // Load Pokemon data from local JSON file
        System.out.println("Loading Pokemon data...");
        JSONLoader.getInstance().loadPokemon();
        JSONLoader.getInstance().loadMoves();
        System.out.println("Loaded " + JSONLoader.getInstance().getAllPokemon().size() + " Pokemon");
        System.out.println("Loaded " + JSONLoader.getInstance().getAllMoves().size() + " Moves");

        // Create a test user with some Pokemon
        User user = createTestUser();

        // Build and display the battle GUI
        SwingUtilities.invokeLater(() -> {
            JFrame view = BattleAIFactory.create(user);
            view.setVisible(true);
        });
    }

    /**
     * Creates a test user with some Pokemon for demonstration.
     */
    private static User createTestUser() {
        User user = new User(1, "Ash", "ash@pokemon.com", 1000);

        // Add some Pokemon to the user's collection
        if (!JSONLoader.getInstance().getAllPokemon().isEmpty()) {
            // Add first 6 Pokemon from the loaded data
            int count = Math.min(6, JSONLoader.getInstance().getAllPokemon().size());
            for (int i = 0; i < count; i++) {
                Pokemon pokemon = JSONLoader.getInstance().getAllPokemon().get(i).copy();
                user.addPokemon(pokemon);
            }
            System.out.println("Test user created with " + user.getOwnedPokemon().size() + " Pokemon");
        } else {
            System.err.println("Warning: No Pokemon loaded! User has empty collection.");
        }

        return user;
    }
}
