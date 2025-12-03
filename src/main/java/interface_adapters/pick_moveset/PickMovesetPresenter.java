package interface_adapters.pick_moveset;

import entities.battle.Move;
import entities.Pokemon;
import use_case.pick_moveset.PickMovesetOutputBoundary;
import use_case.pick_moveset.PickMovesetOutputData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PickMovesetPresenter implements PickMovesetOutputBoundary {

    private final PickMovesetViewModel viewModel;

    public PickMovesetPresenter(PickMovesetViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(PickMovesetOutputData data) {

        PickMovesetState newState = new PickMovesetState();

        Map<Pokemon, List<String>> uiMap = new HashMap<>();
        for (Map.Entry<Pokemon, List<Move>> entry : data.getPokemonMoves().entrySet()) {
            Pokemon p = entry.getKey();
            List<String> names = entry.getValue().stream()
                    .map(Move::getName)
                    .collect(Collectors.toList());
            uiMap.put(p, names);
        }

        newState.setAvailableMoves(uiMap);
        viewModel.setState(newState);
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentFailure(String errorMessage) {
        PickMovesetState newState = new PickMovesetState();
        newState.setError(errorMessage);
        viewModel.setState(newState);
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentSuccess(String message) {
        PickMovesetState newState = new PickMovesetState();
        newState.setMessage(message);
        viewModel.setState(newState);
        viewModel.firePropertyChanged();
    }
}
