package entities;

import java.util.ArrayList;

public class Pokemon {
    String name;
    int id;
    ArrayList<String> types;
    Stats stats;
    ArrayList<String> moves; // IDK if pokemon should have a list of MOVE objects (high redundancy)
                             // or a list of Strings of move_names which can be looked up

    public Pokemon(String name, int id, ArrayList<String> types, Stats stats,  ArrayList<String> moves) {
        this.name = name;
        this.id = id;
        this.types = types;
        this.stats = stats;
        this.moves = moves;
    }

    @Override
    public String toString() {
        return name + "(#" + id + ", " + String.join(", ", types) + ")";
    }

    public Stats getStats() { return this.stats; }
    public void setStats(Stats stats) { this.stats = stats; }

    public ArrayList<String> getMoves() { return this.moves; }
    public void setMoves(ArrayList<String> moves) { this.moves = moves; }
}
