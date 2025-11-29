package interface_adapters.collection;

import interface_adapters.ViewModel;

public class ViewCollectionViewModel extends ViewModel<ViewCollectionState> {
    public ViewCollectionViewModel() {
        super("collection");
        setState(new ViewCollectionState());
    }
}
