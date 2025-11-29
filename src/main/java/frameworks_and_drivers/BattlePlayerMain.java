package frameworks_and_drivers;

import interface_adapters.battle_player.BattlePlayerController;
import interface_adapters.battle_player.BattlePlayerPresenter;
import interface_adapters.battle_player.BattlePlayerState;
import interface_adapters.battle_player.BattlePlayerViewModel;
import use_case.battle_player.BattlePlayerInteractor;
import use_case.battle_player.BattlePlayerUserDataAccessInterface;
import entities.Battle;
import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// entry point wiring the battle player flow end-to-end
public class BattlePlayerMain {
    
    private static final Map<String, List<String>> PREBUILT_DECKS = new LinkedHashMap<>();
    private static BattlePlayerView battleView;
    private static BattleSetupView setupView;
    
    public static void main(String[] args) {
        // boot swing on the edt
        SwingUtilities.invokeLater(() -> {
            try {
                JSONLoader.loadMoves();
                JSONLoader.loadPokemon();
                seedDecks();
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
        if (battleView != null) {
            battleView.dispose();
            battleView = null;
        }
        setupView = new BattleSetupView(PREBUILT_DECKS, BattlePlayerMain::startBattleWithSelection);
        setupView.setVisible(true);
    }

    private static void startBattleWithSelection(String player1Name, String player2Name, String deck1Key, String deck2Key) {
        String fallbackDeck = PREBUILT_DECKS.keySet().stream().findFirst().orElseThrow(() ->
                new IllegalStateException("No decks configured"));
        String resolvedDeck1 = deck1Key != null ? deck1Key : fallbackDeck;
        String resolvedDeck2 = deck2Key != null ? deck2Key : fallbackDeck;

        User user1 = createUserFromDeck(player1Name, 1, resolvedDeck1);
        User user2 = createUserFromDeck(player2Name, 2, resolvedDeck2);

        Battle battle = new Battle(1, user1, user2);
        battle.startBattle();

        BattlePlayerUserDataAccessInterface dataAccess = new BattlePlayerDataAccessObject();
        dataAccess.saveBattle(battle);
        dataAccess.saveUser(user1);

        BattlePlayerViewModel viewModel = new BattlePlayerViewModel();
        BattlePlayerPresenter presenter = new BattlePlayerPresenter(viewModel);
        BattlePlayerInteractor interactor = new BattlePlayerInteractor(dataAccess, presenter);
        BattlePlayerController controller = new BattlePlayerController(interactor);

        battleView = new BattlePlayerView(controller, viewModel, dataAccess, BattlePlayerMain::showSetup);

        BattlePlayerState initialState = new BattlePlayerState();
        initialState.setBattle(battle);
        initialState.setBattleStatus(battle.getBattleStatus());
        initialState.setBattleEnded(false);
        initialState.setTurnResult("Battle started! Select a move or switch Pokemon.");
        viewModel.setState(initialState);

        if (setupView != null) {
            setupView.dispose();
        }
    }

    private static User createUserFromDeck(String name, int id, String deckKey) {
        List<String> picks = PREBUILT_DECKS.get(deckKey);
        if (picks == null || picks.isEmpty()) {
            throw new IllegalArgumentException("Deck not found: " + deckKey);
        }

        String safeName = (name == null || name.isBlank()) ? "Player " + id : name.trim();
        User user = new User(id, safeName, safeName.toLowerCase().replaceAll("\\s+", "") + "@pokemon.com", 1000);
        for (String pick : picks) {
            user.addPokemon(fetchPokemonFromJson(pick));
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

    private static void seedDecks() {
        if (!PREBUILT_DECKS.isEmpty()) {
            return;
        }
        PREBUILT_DECKS.put("Kanto Starters", List.of("bulbasaur", "ivysaur", "venusaur", "charmander", "charmeleon", "charizard"));
        PREBUILT_DECKS.put("Electric Speedsters", List.of("pikachu", "raichu", "jolteon", "electabuzz", "magnezone", "ampharos"));
        PREBUILT_DECKS.put("Rock Solid", List.of("geodude", "graveler", "golem", "onix", "rhyhorn", "rhydon"));
        PREBUILT_DECKS.put("Water Masters", List.of("squirtle", "wartortle", "blastoise", "staryu", "psyduck", "poliwhirl"));
        PREBUILT_DECKS.put("Eevee Friends", List.of("eevee", "vaporeon", "jolteon", "flareon", "espeon", "umbreon"));
    }
    
}
