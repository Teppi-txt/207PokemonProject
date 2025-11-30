package view.collection;

import entities.Pokemon;
import entities.User;
import entities.PriceCalculator;
import frameworks_and_drivers.JsonUserDataAccess;

import javax.swing.*;
import java.awt.*;

public class BuyButtons {

    private final JsonUserDataAccess userDataAccess;

    public BuyButtons(JsonUserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    /**
     * Returns a panel containing the Buy + Shiny Buy buttons.
     */
    public JPanel createBuyButtons(Pokemon pokemon, Component parentComponent) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int price = PriceCalculator.getPrice(pokemon);
        int shinyPrice = PriceCalculator.getShinyPrice(pokemon);

        JButton buyButton = new JButton("Buy (" + price + ")");
        JButton buyShinyButton = new JButton("Shiny (" + shinyPrice + ")");

        buyButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        buyShinyButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        buyButton.addActionListener(e -> attemptBuy(pokemon, false, parentComponent));

        buyShinyButton.addActionListener(e -> attemptBuy(pokemon, true, parentComponent));

        panel.add(Box.createHorizontalGlue());
        panel.add(buyButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(buyShinyButton);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    /**
     * Attempts to buy the Pokémon (shiny or regular).
     */
    private void attemptBuy(Pokemon pokemon, boolean shiny, Component parentComponent) {

        User user = userDataAccess.get();

        boolean ownsAlready = user.getOwnedPokemon().stream().anyMatch(p ->
                p.getID() == pokemon.getID() && p.isShiny() == shiny);

        if (ownsAlready) {
            JOptionPane.showMessageDialog(parentComponent,
                    shiny ? "You already own this shiny Pokémon!"
                            : "You already own this Pokémon!");
            return;
        }

        int cost = shiny ?
                PriceCalculator.getShinyPrice(pokemon) :
                PriceCalculator.getPrice(pokemon);

        if (!user.canAffordPack(cost)) {
            JOptionPane.showMessageDialog(parentComponent, "Not enough currency!");
            return;
        }

        user.buyPack(cost);

        Pokemon toAdd = pokemon;
        if (shiny) {
            Pokemon shinyCopy = new Pokemon(
                    pokemon.getName(),
                    pokemon.getID(),
                    pokemon.getTypes(),
                    pokemon.getStats(),
                    pokemon.getMoves()
            );
            shinyCopy.setShiny(true);
            toAdd = shinyCopy;
        }

        user.addPokemon(toAdd);
        userDataAccess.save(user);

        JOptionPane.showMessageDialog(parentComponent,
                shiny ?
                        "Successfully bought shiny " + pokemon.getName() + "!" :
                        "Successfully bought " + pokemon.getName() + "!");
    }
}
