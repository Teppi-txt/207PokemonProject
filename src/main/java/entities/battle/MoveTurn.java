package entities.battle;

import entities.Player;
import entities.Pokemon;

public class MoveTurn extends Turn {
    private Move move;
    private int damage;
    private String effectiveness;
    private String statusEffect;
    private Player targetPlayer;

    public MoveTurn() {
        super();
    }

    public MoveTurn(int id, Player player, int turnNumber, Move move, Player targetPlayer) {
        super(id, player, turnNumber);
        this.move = move;
        this.targetPlayer = targetPlayer;
        this.damage = 0;
        this.effectiveness = "normal";
        this.statusEffect = "";
    }

    // Calculate damage using Gen I formula and apply to target
    @Override
    public void executeTurn() {
        final Pokemon targetPokemon = targetPlayer != null ? targetPlayer.getActivePokemon() : null;
        final Pokemon attackerPokemon = player != null ? player.getActivePokemon() : null;

        if (targetPokemon == null) {
            this.result = player.getName() + " used " + move.getName() + " but there was no target.";
            return;
        }

        if (attackerPokemon == null) {
            this.result = player.getName() + " has no active Pokemon to attack with.";
            return;
        }

        // Calculate damage using the Gen I formula
        final int calculatedDamage = DamageCalculator.calculateDamage(attackerPokemon, targetPokemon, move);

        final Stats targetStats = targetPokemon.getStats();
        final int startingHp = targetStats.getHp();
        final int remainingHp = Math.max(0, startingHp - calculatedDamage);

        targetStats.setHp(remainingHp);
        this.damage = startingHp - remainingHp;

        // Get effectiveness for message
        final String effectivenessMsg = DamageCalculator.getEffectivenessDescription(move, targetPokemon);
        this.effectiveness = effectivenessMsg;

        // Build result message
        final StringBuilder resultMsg = new StringBuilder();
        resultMsg.append(player.getName()).append(" used ").append(move.getName());

        if (calculatedDamage > 0) {
            resultMsg.append(" for ").append(damage).append(" damage");
            if (!"normal".equals(effectivenessMsg)) {
                resultMsg.append(" (").append(effectivenessMsg).append(")");
            }
            resultMsg.append(".");
        }
        else {
            resultMsg.append(" but it had no effect.");
        }

        if (remainingHp == 0) {
            targetPokemon.setStats(targetStats);
            // ensure faint status propagates
            resultMsg.append(" ").append(targetPokemon.getName()).append(" fainted!");
            this.result = resultMsg.toString();
            autoSwitchNextAvailable(targetPlayer);
        }
        else {
            resultMsg.append(" ")
                    .append(targetPokemon.getName()).append(" has ").append(remainingHp).append(" HP left.");
            this.result = resultMsg.toString();
        }
    }

    @Override
    public String getTurnDetails() {
        return "Turn "
                + turnNumber + " (ID: "
                + id
                + "): Player "
                + player.getName()
                + " used "
                + move.getName()
                + ". Damage: "
                + damage
                + ", Effectiveness: "
                + effectiveness
                + ". Result: "
                + result;
    }

    private void autoSwitchNextAvailable(Player player) {
        if (player == null) {
            return;
        }

        for (Pokemon pokemon : player.getTeam()) {
            if (!pokemon.isFainted()) {
                player.switchPokemon(pokemon);
                break;
            }
        }
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(String effectiveness) {
        this.effectiveness = effectiveness;
    }

    public String getStatusEffect() {
        return statusEffect;
    }

    public void setStatusEffect(String statusEffect) {
        this.statusEffect = statusEffect;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }
}
