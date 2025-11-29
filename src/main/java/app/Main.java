package app;

import pokeapi.JSONLoader;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JSONLoader.loadPokemon();
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addCollectionView()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}