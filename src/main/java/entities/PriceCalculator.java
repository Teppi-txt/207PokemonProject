package entities;

public class PriceCalculator {
    private static final int BASE_MULTIPLIER = 20;
    private static final int SHINY_MULTIPLIER = 5;

    public static int getPrice(Pokemon p) {
        int hp = p.getStats().getStatMap().get("HP");
        return hp * BASE_MULTIPLIER;
    }

    public static int getShinyPrice(Pokemon p) {
        return getPrice(p) * SHINY_MULTIPLIER;
    }
}
