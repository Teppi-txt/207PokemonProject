package entities;

import org.json.JSONObject;
import pokeapi.JSONUtility;

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

    public static Pokemon fromJSON(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        Integer id = jsonObject.getInt("id");
        ArrayList<String> types = JSONUtility.jsonArrayToString(jsonObject.getJSONArray("types"));
        ArrayList<String> moves = JSONUtility.jsonArrayToString(jsonObject.getJSONArray("moves"));
        Stats stats = Stats.fromJSON(jsonObject.getJSONObject("stats"));
        return new Pokemon(name, id, types, stats, moves);
    }

    @Override
    public String toString() {
        return name + "(#" + id + ", " + String.join(", ", types) + ", " + stats.toString() + ")";
    }

    public Stats getStats() { return this.stats; }
    public void setStats(Stats stats) { this.stats = stats; }

    public ArrayList<String> getMoves() { return this.moves; }
    public void setMoves(ArrayList<String> moves) { this.moves = moves; }

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
