
package app;



import entities.Pokemon;
import entities.User;
import interface_adapters.ViewManagerModel;
import interface_adapters.collection.ViewCollectionController;
import interface_adapters.collection.ViewCollectionPresenter;
import interface_adapters.collection.ViewCollectionViewModel;
import pokeapi.JSONLoader;
import use_case.collection.ViewCollectionInteractor;
import view.CollectionView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final User user = new User(0, "Teppi", "teppipersonal@gmail.com", 100);
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    private CollectionView collectionView;
    private ViewCollectionViewModel collectionViewModel;

    public AppBuilder() {
        for (int i = 0; i < 150; i++) {
            if (JSONLoader.allPokemon.get(i).getTypes().contains("water")) {
                Pokemon pok = JSONLoader.allPokemon.get(i).copy();
                if (i % 2 == 0) {
                    pok.setShiny(true);
                }
                user.addPokemon(pok);
            }
        }
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addCollectionView() {
        collectionViewModel = new ViewCollectionViewModel();
        collectionView = new CollectionView(collectionViewModel);

        ViewCollectionPresenter vcp = new ViewCollectionPresenter(collectionViewModel);
        ViewCollectionInteractor interactor = new ViewCollectionInteractor(vcp, user);
        ViewCollectionController vcc = new ViewCollectionController(interactor);

        collectionView.setController(vcc);
        vcc.execute(user.getOwnedPokemon(), 0, "all");
        cardPanel.add(collectionView, collectionViewModel.getViewName());
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Pokemon");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(collectionViewModel.getViewName());
        viewManagerModel.firePropertyChanged();

        return application;
    }
}
