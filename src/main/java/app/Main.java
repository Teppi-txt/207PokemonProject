package app;

import pokeapi.JSONLoader;

import javax.swing.*;

/**
 * Main entry point for the Pokemon Battle Game application.
 * Initializes data and builds the unified application with all features.
 */
public class Main {
    public static void main(String[] args) {
        // Set up Swing look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Load Pokemon and Move data
                System.out.println("Loading Pokemon data...");
                System.out.println("Loaded " + JSONLoader.getInstance().getAllPokemon().size() + " Pokemon");

                System.out.println("Loading Move data...");
                System.out.println("Loaded " + JSONLoader.getInstance().getAllMoves().size() + " Moves");

                // Build and display the application
                AppBuilder appBuilder = new AppBuilder();
                JFrame application = appBuilder
                        .addMainMenuView()
                        .addCollectionView()
                        .addBuildDeckView()
                        .addOpenPackView()
                        .build();

                application.pack();
                application.setLocationRelativeTo(null);
                application.setVisible(true);

                System.out.println("Application started successfully!");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error starting application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
