package entities.battle;

/**
 * Ability represents a Pokémon's inherent trait or skill that can
 * affect stats, status conditions, or interactions with moves.
 * Example: "limber" prevents paralysis.
 */
public class Ability {
    private int id;             // Unique identifier for the ability
    private String name;
    private String effect;      // Full description of what the ability does
    private boolean isMainSeries; // Whether this ability is part of main series games

    public Ability(int id, String name, String effect, String shortEffect, String generation, boolean isMainSeries) {
        this.id = id;
        this.name = name;
        this.effect = effect;
        this.isMainSeries = isMainSeries;
    }

    public int getId() { return id; }
    public Ability setId(int id) { this.id = id; return this; }

    public String getName() { return name; }
    public Ability setName(String name) { this.name = name; return this; }

    public String getEffect() { return effect; }
    public Ability setEffect(String effect) { this.effect = effect; return this; }

    public boolean isMainSeries() { return isMainSeries; }
    public Ability setMainSeries(boolean mainSeries) { isMainSeries = mainSeries; return this; }

    public String toJSONString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"" + name + "\"," +
                "\"effect\":\"" + effect + "\"," +
                "\"isMainSeries\":" + isMainSeries +
                "}";
    }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}
