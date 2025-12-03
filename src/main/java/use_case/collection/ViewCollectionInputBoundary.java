package use_case.collection;

public interface ViewCollectionInputBoundary {

    /**
     * Executes the request to load and display the user's collection.
     * @param viewCollectionInputData the input data needed to load the collection
     */
    void execute(ViewCollectionInputData viewCollectionInputData);

    /**
     * Switches the application back to the home view.
     */
    void switchToHomeView();
}
