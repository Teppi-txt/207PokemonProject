package entities;

public class Move {
    private String name;
    private String type;
    private String damageClass;  // physical, special, or status
    private Integer power;       // int or null (for status)
    private Integer accuracy;    // percentage of a successful hit
    private Integer priority;    // higher priority moves first
    private String effect;

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
