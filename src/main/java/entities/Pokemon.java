package entities;

import java.io.Serializable;
import org.json.JSONObject;
import pokeapi.JSONUtility;

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

    public Pokemon(String name, int id, ArrayList<String> types, Stats stats, ArrayList<String> moves) {
        this.name = name;
        this.id = id;
        this.types = types;
        this.stats = stats;
        this.moves = moves;
    }

    public static Pokemon fromJSON(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        Integer id = jsonObject.getInt("id");
        ArrayList<String> types = JSONUtility.jsonArrayToString(jsonObject.getJSONArray("types"));
        ArrayList<String> moves = JSONUtility.jsonArrayToString(jsonObject.getJSONArray("moves"));
        Stats stats = Stats.fromJSON(jsonObject.getJSONObject("stats"));
        return new Pokemon(name, id, types, stats, moves);
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return this.id;
    }

    public ArrayList<String> getTypes() {
        return this.types;
    }

    public Stats getStats() {
        return this.stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public ArrayList<String> getMoves() {
        return this.moves;
    }

    public void setMoves(ArrayList<String> moves) {
        this.moves = moves;
    }

    public boolean isFainted() {
        return stats.getHp() <= 0;
    }

    // cloning the pokemon so that they can be marked with the shiny attribute not changing the original data
    public Pokemon copy() {
        ArrayList<String> typesCopy = this.types == null ? new ArrayList<>() : new ArrayList<>(this.types);
        ArrayList<String> movesCopy = this.moves == null ? new ArrayList<>() : new ArrayList<>(this.moves);
        Stats statsCopy = this.stats == null ? null : this.stats.copy(); // if Stats has no copy, reuse

        Pokemon clone = new Pokemon(
                this.name,
                this.id,
                typesCopy,
                statsCopy,
                movesCopy
        );
        clone.setShiny(this.shiny);
        return clone;
    }

    @Override
    public String toString() {
        String typesStr = types == null ? "no-types" : String.join(", ", types);
        String statsStr = stats == null ? "no-stats" : stats.toString();

        return name + "(#" + id + ", " + typesStr + ", " + statsStr + ")";

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

    //Sprite images
    private static final String SPRITE_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    public String getRegularSpriteURL() {
        return SPRITE_BASE_URL + id + ".png";
    }

    public String getShinySpriteURL() {
        return SPRITE_BASE_URL + "shiny/" + id + ".png";
    }

    public String getSpriteUrl() {
        return shiny ? getShinySpriteURL() : getRegularSpriteURL();
    }
}

