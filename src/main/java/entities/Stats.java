package entities;

public class Stats {
    private int hp;
    private int attack;
    private int defense;
    private int spAttack;
    private int spDefense;
    private int speed;

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
        switch (name.toLowerCase()) {
            case "hp":
                setHp(value);
                break;
            case "attack":
            case "atk":
                setAttack(value);
                break;
            case "defense":
            case "def":
                setDefense(value);
                break;
            case "spattack":
            case "spatk":
            case "special-attack":
                setSpAttack(value);
                break;
            case "spdefense":
            case "spdef":
            case "special-defense":
                setSpDefense(value);
                break;
            case "speed":
            case "spe":
                setSpeed(value);
                break;
            default:
                throw new IllegalArgumentException("Unknown stat: " + name);
        }
    }


    @Override
    public String toString() {
        return String.format("hp: %d, attack: %d, defense: %d, sp.attack: %d, sp.defense: %d, speed: %d",
                hp, attack, defense, spAttack, spDefense, speed);
    }
}