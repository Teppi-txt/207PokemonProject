package entities.battle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import entities.Pokemon;

/**
 * Calculates damage using the Generation I Pokemon damage formula:.
 * Damage = ((((2 × Level × Critical / 5) + 2) × Power × A/D) / 50 + 2) × STAB × Type1 × Type2 × random.
 */
public class DamageCalculator {

    private static final int DEFAULT_LEVEL = 50;
    private static final Random random = new Random();

    // Type effectiveness chart: typeChart.get(attackType).get(defenderType) = multiplier
    private static final Map<String, Map<String, Double>> TYPE_CHART = buildTypeChart();

    /**
     * Calculate damage for a move.
     *
     * @param attacker The attacking Pokemon
     * @param defender The defending Pokemon
     * @param move The move being used
     * @return The calculated damage (minimum 1 if move has power)
     */
    public static int calculateDamage(Pokemon attacker, Pokemon defender, Move move) {
        if (move == null || move.getPower() == null || move.getPower() <= 0) {
            return 0;
            // Status moves or null power deal no damage
        }

        if ("status".equalsIgnoreCase(move.getDamageClass())) {
            return 0;
            // Status moves deal no damage
        }

        final int power = move.getPower();
        final int level = DEFAULT_LEVEL;
        final int critical = 1;
        // No critical hits for simplicity

        // Get attack and defense stats based on damage class
        int attackStat;
        int defenseStat;

        if ("special".equalsIgnoreCase(move.getDamageClass())) {
            attackStat = attacker.getStats().getSpAttack();
            defenseStat = defender.getStats().getSpDefense();
        }
        else {
            // Physical or unknown defaults to physical
            attackStat = attacker.getStats().getAttack();
            defenseStat = defender.getStats().getDefense();
        }

        // Prevent division by zero
        if (defenseStat <= 0) {
            defenseStat = 1;
        }

        if (attackStat <= 0) {
            attackStat = 1;
        }

        // Calculate base damage using Gen I formula
        final double baseDamage = (((2.0 * level * critical / 5.0) + 2.0) * power * attackStat / defenseStat) / 50.0 + 2.0;

        // Apply STAB (Same Type Attack Bonus)
        final double stab = calculateSTAB(attacker, move);

        // Apply type effectiveness
        final double typeEffectiveness = calculateTypeEffectiveness(move, defender);

        // Apply random factor (0.85 to 1.0)
        final double randomFactor = 0.85 + (random.nextDouble() * 0.15);

        // Calculate final damage
        final double finalDamage = baseDamage * stab * typeEffectiveness * randomFactor;

        // Minimum 1 damage if move has power
        return Math.max(1, (int) Math.floor(finalDamage));
    }

    /**
     * Calculate STAB (Same Type Attack Bonus).
     * Returns 1.5 if move type matches one of attacker's types, 1.0 otherwise.
     */
    private static double calculateSTAB(Pokemon attacker, Move move) {
        if (move.getType() == null || attacker.getTypes() == null) {
            return 1.0;
        }

        final String moveType = move.getType().toLowerCase();
        for (String pokemonType : attacker.getTypes()) {
            if (pokemonType != null && pokemonType.toLowerCase().equals(moveType)) {
                return 1.5;
            }
        }
        return 1.0;
    }

    /**
     * Calculate type effectiveness multiplier.
     * Returns combined multiplier for defender's types.
     */
    private static double calculateTypeEffectiveness(Move move, Pokemon defender) {
        if (move.getType() == null || defender.getTypes() == null) {
            return 1.0;
        }

        final String moveType = move.getType().toLowerCase();
        double multiplier = 1.0;

        for (String defenderType : defender.getTypes()) {
            if (defenderType != null) {
                multiplier *= getTypeMultiplier(moveType, defenderType.toLowerCase());
            }
        }

        return multiplier;
    }

    /**
     * Get the effectiveness multiplier for an attack type vs a defender type.
     */
    private static double getTypeMultiplier(String attackType, String defenderType) {
        final Map<String, Double> attackEffectiveness = TYPE_CHART.get(attackType);
        if (attackEffectiveness != null && attackEffectiveness.containsKey(defenderType)) {
            return attackEffectiveness.get(defenderType);
        }
        return 1.0; // Neutral by default
    }

    /**
     * Get effectiveness description for display.
     */
    public static String getEffectivenessDescription(Move move, Pokemon defender) {
        final double effectiveness = calculateTypeEffectiveness(move, defender);
        if (effectiveness >= 2.0) {
            return "super effective";
        }
        else if (effectiveness > 1.0) {
            return "effective";
        }
        else if (effectiveness == 0) {
            return "no effect";
        }
        else if (effectiveness < 1.0) {
            return "not very effective";
        }
        return "normal";
    }

    /**
     * Build the type effectiveness chart.
     * 2.0 = super effective, 0.5 = not very effective, 0 = no effect
     */
    private static Map<String, Map<String, Double>> buildTypeChart() {
        final Map<String, Map<String, Double>> chart = new HashMap<>();

        // Normal type
        final Map<String, Double> normal = new HashMap<>();
        normal.put("rock", 0.5);
        normal.put("ghost", 0.0);
        normal.put("steel", 0.5);
        chart.put("normal", normal);

        // Fire type
        final Map<String, Double> fire = new HashMap<>();
        fire.put("fire", 0.5);
        fire.put("water", 0.5);
        fire.put("grass", 2.0);
        fire.put("ice", 2.0);
        fire.put("bug", 2.0);
        fire.put("rock", 0.5);
        fire.put("dragon", 0.5);
        fire.put("steel", 2.0);
        chart.put("fire", fire);

        // Water type
        final Map<String, Double> water = new HashMap<>();
        water.put("fire", 2.0);
        water.put("water", 0.5);
        water.put("grass", 0.5);
        water.put("ground", 2.0);
        water.put("rock", 2.0);
        water.put("dragon", 0.5);
        chart.put("water", water);

        // Electric type
        final Map<String, Double> electric = new HashMap<>();
        electric.put("water", 2.0);
        electric.put("electric", 0.5);
        electric.put("grass", 0.5);
        electric.put("ground", 0.0);
        electric.put("flying", 2.0);
        electric.put("dragon", 0.5);
        chart.put("electric", electric);

        // Grass type
        final Map<String, Double> grass = new HashMap<>();
        grass.put("fire", 0.5);
        grass.put("water", 2.0);
        grass.put("grass", 0.5);
        grass.put("poison", 0.5);
        grass.put("ground", 2.0);
        grass.put("flying", 0.5);
        grass.put("bug", 0.5);
        grass.put("rock", 2.0);
        grass.put("dragon", 0.5);
        grass.put("steel", 0.5);
        chart.put("grass", grass);

        // Ice type
        final Map<String, Double> ice = new HashMap<>();
        ice.put("fire", 0.5);
        ice.put("water", 0.5);
        ice.put("grass", 2.0);
        ice.put("ice", 0.5);
        ice.put("ground", 2.0);
        ice.put("flying", 2.0);
        ice.put("dragon", 2.0);
        ice.put("steel", 0.5);
        chart.put("ice", ice);

        // Fighting type
        final Map<String, Double> fighting = new HashMap<>();
        fighting.put("normal", 2.0);
        fighting.put("ice", 2.0);
        fighting.put("poison", 0.5);
        fighting.put("flying", 0.5);
        fighting.put("psychic", 0.5);
        fighting.put("bug", 0.5);
        fighting.put("rock", 2.0);
        fighting.put("ghost", 0.0);
        fighting.put("dark", 2.0);
        fighting.put("steel", 2.0);
        fighting.put("fairy", 0.5);
        chart.put("fighting", fighting);

        // Poison type
        final Map<String, Double> poison = new HashMap<>();
        poison.put("grass", 2.0);
        poison.put("poison", 0.5);
        poison.put("ground", 0.5);
        poison.put("rock", 0.5);
        poison.put("ghost", 0.5);
        poison.put("steel", 0.0);
        poison.put("fairy", 2.0);
        chart.put("poison", poison);

        // Ground type
        final Map<String, Double> ground = new HashMap<>();
        ground.put("fire", 2.0);
        ground.put("electric", 2.0);
        ground.put("grass", 0.5);
        ground.put("poison", 2.0);
        ground.put("flying", 0.0);
        ground.put("bug", 0.5);
        ground.put("rock", 2.0);
        ground.put("steel", 2.0);
        chart.put("ground", ground);

        // Flying type
        final Map<String, Double> flying = new HashMap<>();
        flying.put("electric", 0.5);
        flying.put("grass", 2.0);
        flying.put("fighting", 2.0);
        flying.put("bug", 2.0);
        flying.put("rock", 0.5);
        flying.put("steel", 0.5);
        chart.put("flying", flying);

        // Psychic type
        final Map<String, Double> psychic = new HashMap<>();
        psychic.put("fighting", 2.0);
        psychic.put("poison", 2.0);
        psychic.put("psychic", 0.5);
        psychic.put("dark", 0.0);
        psychic.put("steel", 0.5);
        chart.put("psychic", psychic);

        // Bug type
        final Map<String, Double> bug = new HashMap<>();
        bug.put("fire", 0.5);
        bug.put("grass", 2.0);
        bug.put("fighting", 0.5);
        bug.put("poison", 0.5);
        bug.put("flying", 0.5);
        bug.put("psychic", 2.0);
        bug.put("ghost", 0.5);
        bug.put("dark", 2.0);
        bug.put("steel", 0.5);
        bug.put("fairy", 0.5);
        chart.put("bug", bug);

        // Rock type
        final Map<String, Double> rock = new HashMap<>();
        rock.put("fire", 2.0);
        rock.put("ice", 2.0);
        rock.put("fighting", 0.5);
        rock.put("ground", 0.5);
        rock.put("flying", 2.0);
        rock.put("bug", 2.0);
        rock.put("steel", 0.5);
        chart.put("rock", rock);

        // Ghost type
        final Map<String, Double> ghost = new HashMap<>();
        ghost.put("normal", 0.0);
        ghost.put("psychic", 2.0);
        ghost.put("ghost", 2.0);
        ghost.put("dark", 0.5);
        chart.put("ghost", ghost);

        // Dragon type
        final Map<String, Double> dragon = new HashMap<>();
        dragon.put("dragon", 2.0);
        dragon.put("steel", 0.5);
        dragon.put("fairy", 0.0);
        chart.put("dragon", dragon);

        // Dark type
        final Map<String, Double> dark = new HashMap<>();
        dark.put("fighting", 0.5);
        dark.put("psychic", 2.0);
        dark.put("ghost", 2.0);
        dark.put("dark", 0.5);
        dark.put("fairy", 0.5);
        chart.put("dark", dark);

        // Steel type
        final Map<String, Double> steel = new HashMap<>();
        steel.put("fire", 0.5);
        steel.put("water", 0.5);
        steel.put("electric", 0.5);
        steel.put("ice", 2.0);
        steel.put("rock", 2.0);
        steel.put("steel", 0.5);
        steel.put("fairy", 2.0);
        chart.put("steel", steel);

        // Fairy type
        final Map<String, Double> fairy = new HashMap<>();
        fairy.put("fire", 0.5);
        fairy.put("fighting", 2.0);
        fairy.put("poison", 0.5);
        fairy.put("dragon", 2.0);
        fairy.put("dark", 2.0);
        fairy.put("steel", 0.5);
        chart.put("fairy", fairy);

        return chart;
    }
}
