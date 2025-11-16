package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int currency;

    private final List<Pokemon> ownedPokemon;

    public User(int id, String name, String email, int currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.ownedPokemon = new ArrayList<>();
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCurrency() {
        return currency;
    }

    public List<Pokemon> getOwnedPokemon() {
        return ownedPokemon;
    }

    public void addPokemon(Pokemon pokemon) {
        this.ownedPokemon.add(pokemon);
    }

    public List<Pokemon> openPack(int packID) {
        //this has not been implemented yet as pack class is not created
        return new ArrayList<>();
    }
}
