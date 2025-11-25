package interface_adapters.pick_moveset;

import interface_adapters.ViewModel;

public class PickMovesetViewModel extends ViewModel<PickMovesetState> {

    public PickMovesetViewModel() {
        super("pick moveset");
        this.setState(new PickMovesetState());
    }
}


