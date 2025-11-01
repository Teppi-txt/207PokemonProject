package entities;

import java.util.ArrayList;

public class Pokemon {
    String name;
    int id;
    ArrayList<String> types;
    Stats stats;

    public Pokemon(String name, int id, ArrayList<String> types, Stats stats) {
        this.name = name;
        this.id = id;
        this.types = types;
        this.stats = stats;
    }

    @Override
    public String toString() {
        return name + "(#" + id + ", " + String.join(", ", types) + ")";
    }

    public Stats getStats() {
        return this.stats;
    }
}
