package interface_adapters.build_deck;

import interface_adapters.ViewModel;

/**
 * The View Model for the Build Deck Use Case.
 */
public class BuildDeckViewModel extends ViewModel<BuildDeckState> {
    public static final String VIEW_NAME = "build deck";

    public BuildDeckViewModel() {
        super(VIEW_NAME);
        setState(new  BuildDeckState());
    }
}
