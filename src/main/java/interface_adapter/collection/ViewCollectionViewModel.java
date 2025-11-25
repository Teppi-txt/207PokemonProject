package interface_adapter.collection;

import entities.Pokemon;
import interface_adapter.ViewModel;

import java.util.ArrayList;

public class ViewCollectionViewModel extends ViewModel<ViewCollectionState> {
    public ViewCollectionViewModel() {
        super("collection");
        setState(new ViewCollectionState());
    }
}
