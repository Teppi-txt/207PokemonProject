package interface_adapters.open_pack;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class OpenPackViewModel {

    public static final String VIEW_NAME = "open pack";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private OpenPackState state = new OpenPackState();

    public String getViewName() {
        return VIEW_NAME;
    }

    public OpenPackState getState() {
        return state;
    }

    public void setState(OpenPackState newState) {
        OpenPackState oldState = this.state;
        this.state = newState;

        // Notify listeners (views)
        support.firePropertyChange("state", oldState, newState);
    }

    // Views call this to subscribe to updates
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
