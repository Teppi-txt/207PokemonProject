package interface_adapters.open_pack;

public class OpenPackViewModel {

    public static final String VIEW_NAME = "open pack";

    private OpenPackState state = new OpenPackState();

    public String getViewName() {
        return VIEW_NAME;
    }

    public OpenPackState getState() {
        return state;
    }

    public void setState(OpenPackState state) {
        this.state = state;
    }

}
