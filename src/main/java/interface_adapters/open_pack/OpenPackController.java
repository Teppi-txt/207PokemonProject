package interface_adapters.open_pack;

import use_case.open_pack.OpenPackInputBoundary;
import use_case.open_pack.OpenPackInputData;
/*
The controller for the open pack use case
 */

public class OpenPackController {

    private final OpenPackInputBoundary openPackUseCaseInteractor;

    public OpenPackController(OpenPackInputBoundary openPackUseCaseInteractor) {
        this.openPackUseCaseInteractor = openPackUseCaseInteractor;
    }

    public void openPack(){
        OpenPackInputData openPackInputData = new OpenPackInputData();
        openPackUseCaseInteractor.execute(openPackInputData);
    }

}
