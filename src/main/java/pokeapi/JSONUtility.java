package pokeapi;

import org.json.JSONArray;

import java.util.ArrayList;

public class JSONUtility {
    public static ArrayList<String> jsonArrayToString(JSONArray jsonArray) {
        // Convert to ArrayList<String>
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }
}
