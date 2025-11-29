package interface_adapters.build_deck;

import entities.Deck;
import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader; // Assuming this loads Pokemon data
import use_case.build_deck.*;
import view.BuildDeckView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple main method to test the BuildDeckView and interactor,
 * implementing the new data access methods and correct controller sequencing.
 */
public class BuildDeckControllerTest {

    public static void main(String[] args) {
        System.out.println("Loading Pokémon data...");
        JSONLoader.loadPokemon();

        // create a test user with some Pokémon
        User user = new User(0, "Anvi", "anviraj@gmail.com", 100);
        List<Pokemon> allPokemon = JSONLoader.allPokemon;
        // give the user this first 20 Pokémon
        for (int i = 0; i < 20; i++) {
            if (i < allPokemon.size()) {
                user.addPokemon(allPokemon.get(i));
            }
        }
        System.out.println("Test User created with " + user.getOwnedPokemon().size() + " Pokémon.");
        BuildDeckViewModel deckViewModel = new BuildDeckViewModel();

        // implementing all required methods
        BuildDeckUserDataAccessInterface dataAccess = new BuildDeckUserDataAccessInterface() {
            private final Map<Integer, Deck> decks = new HashMap<>();
            {
                Deck initialDeck = new Deck(100, "New Deck");
                if (user.getOwnedPokemon().size() >= 3) {
                    initialDeck.addPokemon(user.getOwnedPokemon().get(0));
                    initialDeck.addPokemon(user.getOwnedPokemon().get(1));
                    initialDeck.addPokemon(user.getOwnedPokemon().get(2));
                }
                decks.put(initialDeck.getId(), initialDeck);
            }

            @Override
            public User getUser() { return user; }

            @Override
            public void saveUser(User user) {  }

            @Override
            public void saveDeck(entities.Deck deck) {
                decks.put(deck.getId(), deck);
                System.out.println("--- Deck SAVED/UPDATED: ID=" + deck.getId() + ", Name=" + deck.getName() + " ---");
                System.out.println(deck.getPokemons().size());
            }

            @Override
            public List<Deck> getDecks() {
                return new ArrayList<>(decks.values());
            }

            @Override
            public Deck getDeckById(int id) {
                return decks.get(id);
            }

            @Override
            public int getNextDeckId() {
                // return an ID one greater than the current max ID
                int currentMaxId = 0;
                for (int id : decks.keySet()) {
                    if (id > currentMaxId) {
                        currentMaxId = id;
                    }
                }
                // ensures new decks start at 1 or higher than the existing max
                return currentMaxId > 0 ? currentMaxId + 1 : 1;
            }
        };

        BuildDeckPresenter presenter = new BuildDeckPresenter(deckViewModel);
        BuildDeckInteractor interactor = new BuildDeckInteractor(dataAccess, presenter);
        BuildDeckController controller = new BuildDeckController(interactor);

        BuildDeckView buildDeckView = new BuildDeckView(deckViewModel, user);

        buildDeckView.setController(controller);

        System.out.println("Triggering initial load...");
        controller.buildDeck(-1, null, null, false);

        JFrame frame = new JFrame("Deck Builder Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(buildDeckView, BorderLayout.CENTER);
        frame.setMinimumSize(new Dimension(1000, 700)); // Adjusted size for better layout view
        frame.pack();
        frame.setVisible(true);
    }
}