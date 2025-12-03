package interface_adapters.pick_moveset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Pokemon;

/**
 * State for pick moveset.
 */

public class PickMovesetState {

    private Map<Pokemon, List<String>> availableMoves = new HashMap<>();
    private String message = "";
    private String error = "";

    public Map<Pokemon, List<String>> getAvailableMoves() {
        return availableMoves;
    }

    public void setAvailableMoves(Map<Pokemon, List<String>> availableMoves) {
        this.availableMoves = availableMoves;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
