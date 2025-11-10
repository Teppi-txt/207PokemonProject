package entities;

public class Pokemon {
    String name;
    int id;

    public Pokemon(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name + "(#" + id + ", more data here)";
    }
}
