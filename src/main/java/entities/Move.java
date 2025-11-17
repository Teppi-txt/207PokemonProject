package entities;

import org.json.JSONObject;

public class Move {
    private String name;
    private String type;
    private String damageClass;  // physical, special, or status
    private Integer power;       // int or null (for status)
    private Integer accuracy;    // percentage of a successful hit
    private Integer priority;    // higher priority moves first
    private String effect;
    // I'm not sure if we are using this but I see them in the api
    private int effect_chance; // Percentage chance that the move's additional effect occurs
    private String short_effect; //the move's additional effect, usually excluding damage info
    private int pp; // Power Points: the number of times this move can be used in battle.


    public static Move fromJSON(JSONObject jsonObject) {
        Move returnMove = new Move()
            .setName(jsonObject.getString("name"))
            .setType(jsonObject.getString("type"))
            .setDamageClass(jsonObject.getString("damageClass"))
            .setPower(jsonObject.getInt("power"))
            .setAccuracy(jsonObject.getInt("accuracy"))
            .setPriority(jsonObject.getInt("priority"))
            .setEffect(jsonObject.getString("effect"));
        return returnMove;
    }

    public String getName() { return name; }
    public Move setName(String name) { this.name = name; return this; }

    public String getType() { return type; }
    public Move setType(String type) { this.type = type; return this; }

    public String getDamageClass() { return damageClass; }
    public Move setDamageClass(String damageClass) { this.damageClass = damageClass; return this; }

    public Integer getPower() { return power; }
    public Move setPower(Integer power) { this.power = power; return this; }

    public Integer getAccuracy() { return accuracy; }
    public Move setAccuracy(Integer accuracy) { this.accuracy = accuracy; return this; }

    public Integer getPriority() { return priority; }
    public Move setPriority(int priority) { this.priority = priority; return this; }

    public String getEffect() { return effect; }
    public Move setEffect(String effect) { this.effect = effect; return this; }

    public int getEffect_chance() { return effect_chance; }
    public Move setEffect_chance(int effect_chance) { this.effect_chance =  effect_chance; return this; }

    public int getPp() { return pp; }
    public Move setPp(int pp) { this.pp = pp; return this; }

    public String getShortEffect() { return short_effect; }
    public Move setShortEffect(String short_effect) { this.short_effect = short_effect; return this; }


    @Override
    public String toString() {
        return "Move{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", damageClass='" + damageClass + '\'' +
                ", power=" + power +
                ", accuracy=" + accuracy +
                ", priority=" + priority +
                ", effect='" + effect + '\'' +
                '}';
    }

    public String toJSONString() {
        return "{" +
                "\"name\": \"" + name + '\"' +
                ", \"type\": \"" + type + '\"' +
                ", \"damageClass\": \"" + damageClass + '\"' +
                ", \"power\": \"" + power + "\"" +
                ", \"accuracy\": \"" + accuracy + "\"" +
                ", \"priority\": \"" + priority + "\"" +
                ", \"effect\": \"" + effect + '\"' +
                '}';
    }
}
