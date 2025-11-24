package interface_adapter.collection;

import entities.Pokemon;
import entities.User;
import pokeapi.JSONLoader;
import use_case.collection.ViewCollectionInputBoundary;
import use_case.collection.ViewCollectionInputData;
import use_case.collection.ViewCollectionInteractor;
import view.CollectionView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ViewCollectionController {
    private final ViewCollectionInputBoundary interactor;

    public ViewCollectionController(ViewCollectionInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(ArrayList<Pokemon> ownedPokemon, int currentPage, String filter) {
        final ViewCollectionInputData inputData =
                new ViewCollectionInputData(ownedPokemon, currentPage, filter);

        interactor.execute(inputData);
    }

    public static void main(String[] args) {
        JSONLoader.loadPokemon();
        System.out.println(JSONLoader.allPokemon.size());
        final User user = new User(0, "Teppi", "teppipersonal@gmail.com", 100);
        for (int i = 0; i < 150; i++) {
            user.addPokemon(JSONLoader.allPokemon.get(2 * i));
        }

        ViewCollectionViewModel vcvm = new ViewCollectionViewModel();
        ViewCollectionPresenter vcp = new ViewCollectionPresenter(vcvm);
        ViewCollectionInteractor interactor = new ViewCollectionInteractor(vcp, user);
        ViewCollectionController vcc = new ViewCollectionController(interactor);
        CollectionView view = new CollectionView(vcvm);

        view.setController(vcc);

        JFrame application = new JFrame("Pokemon");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(view, BorderLayout.CENTER);
        application.setMinimumSize(new Dimension(700, 600));
        application.pack();
        application.setVisible(true);

        vcc.execute((ArrayList<Pokemon>) user.getOwnedPokemon(), 0, "all");
    }
}
