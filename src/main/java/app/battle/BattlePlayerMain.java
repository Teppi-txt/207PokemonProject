package app.battle;

import entities.Deck;
import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Entry point for running the Battle Player flow with the latest setup views.
public class BattlePlayerMain {
    
    private static final Map<String, List<String>> PREBUILT_DECKS = new LinkedHashMap<>();
    private static JFrame setupView;
    private static User user;
    
    public static void main(String[] args) {
        // boot swing on the edt
        SwingUtilities.invokeLater(() -> {
            try {
                JSONLoader.getInstance().loadPokemon();
                JSONLoader.getInstance().loadMoves();
                seedDecks();
                user = createDefaultUser();
                showSetup();
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
    public static void showSetup() {
        if (setupView != null) {
            setupView.dispose();
        }

        Runnable returnToSetup = BattlePlayerMain::showSetup;
        setupView = BattlePlayerFactory.createSetupView(user, returnToSetup);
        setupView.setVisible(true);
    }

    private static User createDefaultUser() {
        User defaultUser = new User(1, "Trainer", "trainer@pokemon.com", 5000);

        // Give the user a healthy roster to choose from.
        int ownedCount = Math.min(30, JSONLoader.getInstance().getAllPokemon().size());
        for (int i = 0; i < ownedCount; i++) {
            defaultUser.addPokemon(JSONLoader.getInstance().getAllPokemon().get(i).copy());
        }

        // Seed decks from our template list so the deck-based setup view works.
        int deckId = 1;
        for (Map.Entry<String, List<String>> entry : PREBUILT_DECKS.entrySet()) {
            Deck deck = new Deck(deckId++, entry.getKey());
            for (String pick : entry.getValue()) {
                deck.addPokemon(fetchPokemonFromJson(pick));
                if (deck.getPokemons().size() >= Deck.DECK_LIMIT) {
                    break;
                }
            }
            if (deck.getPokemons().size() >= 3) {
                defaultUser.addDeck(deck);
            }
        }

        return defaultUser;
    }

    private static Pokemon fetchPokemonFromJson(String name) {
        for (Pokemon candidate : JSONLoader.getInstance().getAllPokemon()) {
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

    private static void seedDecks() {
        if (!PREBUILT_DECKS.isEmpty()) {
            return;
        }
        PREBUILT_DECKS.put("Kanto Starters", List.of("bulbasaur", "charmander", "squirtle"));
        PREBUILT_DECKS.put("Electric Speedsters", List.of("pikachu", "electabuzz", "ampharos"));
        PREBUILT_DECKS.put("Rock Solid", List.of("golem", "onix", "rhydon"));
        PREBUILT_DECKS.put("Water Masters", List.of("blastoise", "psyduck", "poliwhirl"));
        PREBUILT_DECKS.put("Eevee Friends", List.of("vaporeon", "flareon", "umbreon"));
    }
    
}
