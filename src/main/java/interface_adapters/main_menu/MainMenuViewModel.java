package interface_adapters.main_menu;

import interface_adapters.ViewModel;

/**
 * ViewModel for the main menu view.
 */
public class MainMenuViewModel extends ViewModel<MainMenuState> {

    public static final String VIEW_NAME = "main_menu";

    public MainMenuViewModel() {
        super(VIEW_NAME);
        setState(new MainMenuState());
    }
}
