package use_case.build_deck;

public interface BuildDeckInputBoundary {
    /**
     * Executes the open_pack use case
     * @param buildDeckInputData the input data
     */
    void execute(BuildDeckInputData buildDeckInputData);
}