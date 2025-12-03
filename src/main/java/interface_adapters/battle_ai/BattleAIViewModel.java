package interface_adapters.battle_ai;

import java.util.ArrayList;
import java.util.List;

/**
 * View Model for Battle AI views.
 * Represents the current state of the battle in a view-friendly format.
 * Supports observer pattern for reactive UI updates.
 */

public class BattleAIViewModel {

    // Battle state
    private String battleStatus;
    // "PENDING", "IN_PROGRESS", "COMPLETED"
    private String currentTurnDescription;
    private int turnNumber;

    // Player 1 (Human)
    private String player1Name;
    private List<PokemonViewModel> player1Team;
    private PokemonViewModel player1Active;

    // Player 2 (AI)
    private String player2Name;
    private List<PokemonViewModel> player2Team;
    private PokemonViewModel player2Active;

    // UI state
    private String errorMessage;
    private String winnerName;
    private int currencyAwarded;
    private boolean battleEnded;
    private String playerSwitchedToName;
    private String aiSwitchedToName;

    // Listeners for observer pattern
    private final List<ViewModelListener> listeners;

    public BattleAIViewModel() {
        this.battleStatus = "PENDING";
        this.currentTurnDescription = "";
        this.turnNumber = 0;
        this.player1Name = "";
        this.player2Name = "";
        this.player1Team = new ArrayList<>();
        this.player2Team = new ArrayList<>();
        this.errorMessage = null;
        this.winnerName = null;
        this.currencyAwarded = 0;
        this.battleEnded = false;
        this.playerSwitchedToName = null;
        this.aiSwitchedToName = null;
        this.listeners = new ArrayList<>();
    }

    /**
     * Adds a listener to be notified of view model changes.
     */
    public void addListener(ViewModelListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all listeners of a change.
     */
    private void notifyListeners() {
        for (ViewModelListener listener : listeners) {
            listener.onViewModelChanged();
        }
    }

    // Getters
    public String getBattleStatus() {
        return battleStatus;
    }

    public String getCurrentTurnDescription() {
        return currentTurnDescription;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public List<PokemonViewModel> getPlayer1Team() {
        return player1Team;
    }

    public PokemonViewModel getPlayer1Active() {
        return player1Active;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public List<PokemonViewModel> getPlayer2Team() {
        return player2Team;
    }

    public PokemonViewModel getPlayer2Active() {
        return player2Active;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public int getCurrencyAwarded() {
        return currencyAwarded;
    }

    public boolean isBattleEnded() {
        return battleEnded;
    }

    public String getPlayerSwitchedToName() {
        return playerSwitchedToName;
    }

    public String getAiSwitchedToName() {
        return aiSwitchedToName;
    }

    /**
     * Sets battle status.
     * @param battleStatus new status
     */
    public void setBattleStatus(String battleStatus) {
        this.battleStatus = battleStatus;
        notifyListeners();
    }

    /**
     * Sets turn description.
     * @param currentTurnDescription text
     */
    public void setCurrentTurnDescription(String currentTurnDescription) {
        this.currentTurnDescription = currentTurnDescription;
        notifyListeners();
    }

    /**
     * Sets turn number.
     * @param turnNumber turn
     */
    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
        notifyListeners();
    }

    /**
     * Sets player 1 name.
     * @param player1Name name
     */
    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
        notifyListeners();
    }

    /**
     * Sets player 1 team.
     * @param player1Team team
     */
    public void setPlayer1Team(List<PokemonViewModel> player1Team) {
        this.player1Team = player1Team;
        notifyListeners();
    }

    /**
     * Sets player 1 active.
     * @param player1Active active Pokémon
     */
    public void setPlayer1Active(PokemonViewModel player1Active) {
        this.player1Active = player1Active;
        notifyListeners();
    }

    /**
     * Sets player 2 name.
     * @param player2Name name
     */
    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
        notifyListeners();
    }

    /**
     * Sets player 2 team.
     * @param player2Team team
     */
    public void setPlayer2Team(List<PokemonViewModel> player2Team) {
        this.player2Team = player2Team;
        notifyListeners();
    }

    /**
     * Sets player 2 active.
     * @param player2Active active Pokémon
     */
    public void setPlayer2Active(PokemonViewModel player2Active) {
        this.player2Active = player2Active;
        notifyListeners();
    }

    /**
     * Sets error message.
     * @param errorMessage msg
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        notifyListeners();
    }

    /**
     * Sets winner name.
     * @param winnerName name
     */
    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
        notifyListeners();
    }

    /**
     * Sets currency awarded.
     * @param currencyAwarded amount
     */
    public void setCurrencyAwarded(int currencyAwarded) {
        this.currencyAwarded = currencyAwarded;
        notifyListeners();
    }

    /**
     * Sets battle ended flag.
     * @param battleEnded flag
     */
    public void setBattleEnded(boolean battleEnded) {
        this.battleEnded = battleEnded;
        notifyListeners();
    }

    /**
     * Sets player switch name.
     * @param playerSwitchedToName name
     */
    public void setPlayerSwitchedToName(String playerSwitchedToName) {
        this.playerSwitchedToName = playerSwitchedToName;
        notifyListeners();
    }

    /**
     * Sets AI switch name.
     * @param aiSwitchedToName name
     */
    public void setAiSwitchedToName(String aiSwitchedToName) {
        this.aiSwitchedToName = aiSwitchedToName;
        notifyListeners();
    }

    /**
     * Nested class representing a simplified Pokemon view model.
     */
    public static class PokemonViewModel {
        private final String name;
        private final int id;
        private final int currentHP;
        private final int maxHP;
        private final List<String> moveNames;
        private final List<String> types;
        private final boolean isFainted;
        private final String spriteUrl;
        private final String frontGifUrl;
        private final String backGifUrl;

        public PokemonViewModel(String name, int id, int currentHP, int maxHP,
                                List<String> moveNames, List<String> types,
                                boolean isFainted, String spriteUrl,
                                String frontGifUrl, String backGifUrl) {
            this.name = name;
            this.id = id;
            this.currentHP = currentHP;
            this.maxHP = maxHP;
            this.moveNames = moveNames;
            this.types = types;
            this.isFainted = isFainted;
            this.spriteUrl = spriteUrl;
            this.frontGifUrl = frontGifUrl;
            this.backGifUrl = backGifUrl;
        }

        // Getters
        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public int getCurrentHP() {
            return currentHP;
        }

        public int getMaxHP() {
            return maxHP;
        }

        public List<String> getMoveNames() {
            return moveNames;
        }

        public List<String> getTypes() {
            return types;
        }

        public boolean isFainted() {
            return isFainted;
        }

        public String getSpriteUrl() {
            return spriteUrl;
        }

        public String getFrontGifUrl() {
            return frontGifUrl;
        }

        public String getBackGifUrl() {
            return backGifUrl;
        }

        public float getHPPercent() {
            return maxHP > 0 ? (float) currentHP / maxHP : 0f;
        }
    }

    /**
     * Listener interface for observing view model changes.
     */
    public interface ViewModelListener {
        void onViewModelChanged();
    }
}
