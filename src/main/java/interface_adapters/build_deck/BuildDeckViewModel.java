package interface_adapters.build_deck;

import interface_adapters.ViewModel;

/**
 * The view model for build deck view.
 */
public class BuildDeckViewModel extends ViewModel<BuildDeckState> {
    public static final String VIEW_NAME = "build deck";

    public BuildDeckViewModel() {
        super(VIEW_NAME);
        setState(new  BuildDeckState());
    }
}
