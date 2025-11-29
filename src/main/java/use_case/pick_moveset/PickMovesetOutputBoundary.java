package use_case.pick_moveset;

public interface PickMovesetOutputBoundary {

    void present(PickMovesetOutputData outputData);

    void presentFailure(String errorMessage);

    void presentSuccess(String message);
}
