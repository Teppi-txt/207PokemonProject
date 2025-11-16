package entities;

import java.io.Serializable;
import org.json.JSONObject;

import java.util.Map;

public class Stats implements Serializable {
    private static final long serialVersionUID = 1L;
    private int hp;
    private int attack;
    private int defense;
    private int spAttack;
    private int spDefense;
    private int speed;

    // some moves have stat level increases, so STAT_LEVEL maps the level to the multiplier for the stat
    // i.e. ATK raised by +2 -> ATK *= STAT_LEVEL.get(+2)
    public static final Map<Integer, Double> STAT_LEVEL = Map.ofEntries(
            Map.entry(-6, 2.0 / 8.0),
            Map.entry(-5, 2.0 / 7.0),
            Map.entry(-4, 2.0 / 6.0),
            Map.entry(-3, 2.0 / 5.0),
            Map.entry(-2, 2.0 / 4.0),
            Map.entry(-1, 2.0 / 3.0),
            Map.entry(0,  1.0),
            Map.entry(+1, 3.0 / 2.0),
            Map.entry(+2, 4.0 / 2.0),
            Map.entry(+3, 5.0 / 2.0),
            Map.entry(+4, 6.0 / 2.0),
            Map.entry(+5, 7.0 / 2.0),
            Map.entry(+6, 8.0 / 2.0)
    );

    public Stats() {
        this(0, 0, 0, 0, 0, 0);
    }

    public Stats(int hp, int attack, int defense, int spAttack, int spDefense, int speed) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.spAttack = spAttack;
        this.spDefense = spDefense;
        this.speed = speed;
    }

    public static Stats fromJSON(JSONObject stats) {
        Stats statsObj = new Stats();
        statsObj.hp = stats.getInt("hp");
        statsObj.attack = stats.getInt("attack");
        statsObj.defense = stats.getInt("defense");
        statsObj.spAttack = stats.getInt("sp_attack");
        statsObj.spDefense = stats.getInt("sp_defense");
        statsObj.speed = stats.getInt("speed");
        return statsObj;
    }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public int getSpAttack() { return spAttack; }
    public void setSpAttack(int spAttack) { this.spAttack = spAttack; }

    public int getSpDefense() { return spDefense; }
    public void setSpDefense(int spDefense) { this.spDefense = spDefense; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public double getMultiplier(int level) {
        return STAT_LEVEL.get(level);
    }


    public Stats copy() {
        return new Stats(hp, attack, defense, spAttack, spDefense, speed);
    }

    public void add(Stats other) {
        this.hp += other.hp;
        this.attack += other.attack;
        this.defense += other.defense;
        this.spAttack += other.spAttack;
        this.spDefense += other.spDefense;
        this.speed += other.speed;
    }

    // im not sure if this is good or terrible code
    public void setStat(String name, int value) {
        switch (normalizeStatName(name)) {
            case "hp":
                setHp(value);
                break;
            case "attack":
                setAttack(value);
                break;
            case "defense":
                setDefense(value);
                break;
            case "spattack":
                setSpAttack(value);
                break;
            case "spdefense":
                setSpDefense(value);
                break;
            case "speed":
                setSpeed(value);
                break;
            default:
                throw new IllegalArgumentException("Unknown stat: " + name);
        }
    }

    public int getStat(String name) {
        switch (normalizeStatName(name)) {
            case "hp":
                return getHp();
            case "attack":
                return getAttack();
            case "defense":
                return getDefense();
            case "spattack":
                return getSpAttack();
            case "spdefense":
                return getSpDefense();
            case "speed":
                return getSpeed();
            default:
                throw new IllegalArgumentException("Unknown stat: " + name);
        }
    }

    private String normalizeStatName(String name) {
        switch (name.toLowerCase()) {
            case "hp":
            case "health":
                return "hp";
            case "attack":
            case "atk":
                return "attack";
            case "defense":
            case "def":
                return "defense";
            case "special-attack":
            case "spatk":
            case "sp-attack":
                return "spattack";
            case "special-defense":
            case "spdef":
            case "sp-defense":
                return "spdefense";
            case "speed":
            case "spe":
                return "speed";
            default:
                throw new IllegalArgumentException("Unknown stat: " + name);
        }
    }

    @Override
    public String toString() {
        return String.format("hp: %d, attack: %d, defense: %d, sp.attack: %d, sp.defense: %d, speed: %d",
                hp, attack, defense, spAttack, spDefense, speed);
    }

    public String toJSONString() {
        return String.format("{\"hp\": %d, \"attack\": %d, \"defense\": %d, \"sp_attack\": %d, \"sp_defense\": %d, \"speed\": %d}",
                hp, attack, defense, spAttack, spDefense, speed);
    }
}