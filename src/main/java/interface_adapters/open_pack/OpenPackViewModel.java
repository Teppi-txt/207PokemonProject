package interface_adapters.open_pack;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * View Model for the open pack use case.
 */

public class OpenPackViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private OpenPackState state = new OpenPackState();

    /**
     * Adds a property change listener.
     * @param listener the listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public OpenPackState getState() {
        return state;
    }

    /**
     * Sets the state.
     * @param newState the new state
     */
    public void setState(OpenPackState newState) {
        final OpenPackState oldState = this.state;
        this.state = newState;
        support.firePropertyChange("state", oldState, newState);
    }
}
