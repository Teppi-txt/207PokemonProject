package entities;

import java.io.Serializable;

import entities.battle.Stats;
import org.json.JSONObject;
import pokeapi.JSONUtility;

import java.util.ArrayList;

/**
 * Pokemon entity for the project.
 */

public class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    // private so no other classes can modify/access
    private int id;
    private ArrayList<String> types;
    private Stats stats;
    private ArrayList<String> moves;
    // IDK if pokemon should have a list of MOVE objects (high redundancy)
    // or a list of Strings of move_names which can be looked up
    private boolean shiny = false;
    // default of each pokemon is not shiny

    public Pokemon(String name, int id, ArrayList<String> types, Stats stats, ArrayList<String> moves) {
        this.name = name;
        this.id = id;
        this.types = types;
        this.stats = stats;
        this.moves = moves;
    }

    public Pokemon() {
    }

    public static Pokemon fromJSON(JSONObject jsonObject) {
        final String name = jsonObject.getString("name");
        final Integer id = jsonObject.getInt("id");
        final ArrayList<String> types = JSONUtility.jsonArrayToString(jsonObject.getJSONArray("types"));
        final ArrayList<String> moves = JSONUtility.jsonArrayToString(jsonObject.getJSONArray("moves"));
        final Stats stats = Stats.fromJSON(jsonObject.getJSONObject("stats"));
        final Pokemon p = new Pokemon(name, id, types, stats, moves);
        if (jsonObject.has("shiny")) {
            p.setShiny(jsonObject.getBoolean("shiny"));
        }

        return p;
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

    public void setName(String name) {
         this.name = name;
    }

    public int getId() {
        return this.id;
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

    public ArrayList<String> getTypes() {
        return this.types;
    }

    public void setMoves(ArrayList<String> moves) {
        this.moves = moves;
    }

    public boolean isFainted() {
        return stats.getHp() <= 0;
    }

    // cloning the pokemon so that they can be marked with the shiny attribute not changing the original data
    public Pokemon copy() {
        ArrayList<String>copiedTypes = this.types != null ? new ArrayList<>(this.types) : new ArrayList<>();
        ArrayList<String>copiedMoves = this.moves != null ? new ArrayList<>(this.moves) : new ArrayList<>();
        Stats copiedStats = this.stats != null ? this.stats.copy() : null;
        Pokemon clone = new Pokemon(
                this.name,
                this.id,
                copiedTypes,
                copiedStats,  // Copy stats so HP changes don't affect original
                copiedMoves
        );
        clone.setShiny(this.shiny);
        return clone;
    }

    @Override
    public String toString() {
        return name + "(#" + id + ", " + String.join(", ", types) + ", " + stats.toString() + ")";
    }

    public String toJSONString() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"name\":\"").append(name).append("\",");
        json.append("\"id\":").append(id).append(",");
        json.append("\"shiny\":").append(shiny).append(",");
        json.append("\"types\":[");
        for (int i = 0; i < types.size(); i++) {
            json.append("\"").append(types.get(i)).append("\"");
            if (i < types.size() - 1) {
                json.append(",");
            }
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

    private static final String SPRITE_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";
    private static final String ANIMATED_FRONT_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/";
    private static final String ANIMATED_BACK_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/back/";

    public String getRegularSpriteURL() {
        return SPRITE_BASE_URL + id + ".png";
    }

    public String getShinySpriteURL() {
        return SPRITE_BASE_URL + "shiny/" + id + ".png";
    }

    public String getSpriteUrl() {
        return shiny ? getShinySpriteURL() : getRegularSpriteURL();
    }

    public String getRegularFrontGIF() {
        return ANIMATED_FRONT_BASE_URL + id + ".gif";
    }

    public String getShinyFrontGIF() {
        return ANIMATED_FRONT_BASE_URL + "shiny/" + id + ".gif";
    }

    public String getFrontGIF() {
        return shiny ? getShinyFrontGIF() : getRegularFrontGIF();
    }

    public String getRegularBackGIF() {
        return ANIMATED_BACK_BASE_URL + id + ".gif";
    }

    public String getShinyBackGIF() {
        return ANIMATED_BACK_BASE_URL + "shiny/" + id + ".gif";
    }

    public String getBackGIF() {
        return shiny ? getShinyBackGIF() : getRegularBackGIF();
    }

    // Aliases for backward compatibility with existing battle views
    public String getAnimatedSpriteUrl() {
        if (id <= 649) {
            return getFrontGIF();
        }
        return getSpriteUrl();
    }

    public String getAnimatedBackSpriteUrl() {
        if (id <= 649) {
            return getBackGIF();
        }
        return SPRITE_BASE_URL + "back/" + id + ".png";
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public void setID(int id) {
        this.id = id;
    }
}
