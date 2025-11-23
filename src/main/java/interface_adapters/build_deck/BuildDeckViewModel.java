package interface_adapters.build_deck;
/**
 * The view model for build deck view.
 */
public class BuildDeckViewModel {

    public static final String VIEW_NAME = "build deck";

    private BuildDeckState state = new BuildDeckState();

    public String getViewName() {
        return VIEW_NAME;
    }

    public BuildDeckState getState() {
        return state;
    }

    public void setState(BuildDeckState state) {
        this.state = state;
    }
}
