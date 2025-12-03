package interface_adapters.open_pack;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * View Model for the open pack use case
 */

public class OpenPackViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private OpenPackState state = new OpenPackState();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }


    public OpenPackState getState() {
        return state;
    }

    public void setState(OpenPackState newState) {
        OpenPackState oldState = this.state;
        this.state = newState;
        support.firePropertyChange("state", oldState, newState);
    }
}
