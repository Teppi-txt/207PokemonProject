package entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;    // private so no other classes can modify/access
    private int id;
    private ArrayList<String> types;
    private Stats stats;
    private ArrayList<String> moves; // IDK if pokemon should have a list of MOVE objects (high redundancy)
                             // or a list of Strings of move_names which can be looked up
    private boolean shiny = false;  //default of each pokemon is not shiny

    public Pokemon(String name, int id, ArrayList<String> types, Stats stats,  ArrayList<String> moves) {
        this.name = name;
        this.id = id;
        this.types = types;
        this.stats = stats;
        this.moves = moves;
    }

    // new for shiny pokemon
    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    // cloning the pokemon so that they can be marked with the shiny attribute not changing the original data
    public Pokemon copy() {
        Pokemon clone =  new Pokemon(
                this.name,
                this.id,
                new ArrayList<>(this.types),
                this.stats,
                new ArrayList<>(this.moves)
        );
        clone.setShiny(this.shiny);
        return clone;
    }

    @Override
    public String toString() {
        return name + "(#" + id + ", " + String.join(", ", types) + ")";
    }

    public String getName() { return this.name; }

    public int getId() { return this.id; }

    public ArrayList<String> getTypes() { return this.types; }

    public Stats getStats() { return this.stats; }
    public void setStats(Stats stats) { this.stats = stats; }

    public ArrayList<String> getMoves() { return this.moves; }
    public void setMoves(ArrayList<String> moves) { this.moves = moves; }

    public boolean isFainted() {
        return this.stats.getHp() <= 0;
    }

    public String toJSONString() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"name\":\"").append(name).append("\",");
        json.append("\"id\":").append(id).append(",");

        // typing
        json.append("\"types\":[");
        for (int i = 0; i < types.size(); i++) {
            json.append("\"").append(types.get(i)).append("\"");
            if (i < types.size() - 1) json.append(",");
        }
        json.append("],");


        json.append("\"stats\":").append(stats.toJSONString()).append(",");

        json.append("\"moves\":[");
        for (int i = 0; i < moves.size(); i++) {
            json.append("\"").append(moves.get(i)).append("\"");
            if (i < moves.size() - 1) json.append(",");
        }
        json.append("]}");
        return json.toString();
    }
}
