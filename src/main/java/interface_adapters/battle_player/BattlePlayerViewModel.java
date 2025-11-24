package interface_adapters.battle_player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BattlePlayerViewModel {

    public static final String VIEW_NAME = "battle player";
    public static final String STATE_PROPERTY = "state";

    private BattlePlayerState state = new BattlePlayerState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public String getViewName() {
        return VIEW_NAME;
    }

    public BattlePlayerState getState() {
        return state;
    }

    public void setState(BattlePlayerState state) {
        BattlePlayerState oldState = this.state;
        this.state = state;
        support.firePropertyChange(STATE_PROPERTY, oldState, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
