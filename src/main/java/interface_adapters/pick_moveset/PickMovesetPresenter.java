package interface_adapters.pick_moveset;

import entities.Move;
import entities.Pokemon;
import use_case.pick_moveset.PickMovesetOutputBoundary;
import use_case.pick_moveset.PickMovesetOutputData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickMovesetPresenter implements PickMovesetOutputBoundary {

    private final PickMovesetViewModel viewModel;

    public PickMovesetPresenter(PickMovesetViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(PickMovesetOutputData data) {
        PickMovesetState newState = new PickMovesetState();

        Map<Pokemon, List<String>> uiMap = new HashMap<>();
        for (var entry : data.getPokemonMoves().entrySet()) {
            Pokemon p = entry.getKey();
            List<String> names = entry.getValue()
                    .stream()
                    .map(Move::getName)
                    .toList();
            uiMap.put(p, names);
        }

        newState.setAvailableMoves(uiMap);
        viewModel.setState(newState);
        viewModel.firePropertyChange();
    }

    @Override
    public void presentFailure(String errorMessage) {
        PickMovesetState newState = new PickMovesetState();
        newState.setError(errorMessage);
        viewModel.setState(newState);
        viewModel.firePropertyChange();
    }

    @Override
    public void presentSuccess(String message) {
        PickMovesetState newState = new PickMovesetState();
        newState.setMessage(message);
        viewModel.setState(newState);
        viewModel.firePropertyChange();
    }
}
