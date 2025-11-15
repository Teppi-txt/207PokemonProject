package org.example;

import java.util.List;

public interface Player {
    String getName();

    Deck getDeck();

    Move chooseMove(Battle battle);

    Pokemon getActivePokemon();

    void switchPokemon(Pokemon pokemon);

    List<Pokemon> getTeam();

    void useItem(Item item, Pokemon target);

    boolean hasAvailablePokemon();

    boolean isDefeated();
}
