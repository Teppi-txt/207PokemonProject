package entities;

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

    // subtract move power from target and auto-switch if fainted
    @Override
    public void executeTurn() {
        Pokemon targetPokemon = targetPlayer != null ? targetPlayer.getActivePokemon() : null;

        if (targetPokemon == null) {
            this.result = player.getName() + " used " + move.getName() + " but there was no target.";
            return;
        }

        int power = Math.max(1, move.getPower());
        Stats targetStats = targetPokemon.getStats();
        int startingHp = targetStats.getHp();
        int remainingHp = Math.max(0, startingHp - power);

        targetStats.setHp(remainingHp);
        this.damage = startingHp - remainingHp;

        if (remainingHp == 0) {
            targetPokemon.setStats(targetStats); // ensure faint status propagates
            this.result = player.getName() + " used " + move.getName() + " for " + damage +
                    " damage. " + targetPokemon.getName() + " fainted!";
            autoSwitchNextAvailable(targetPlayer);
        } else {
            this.result = player.getName() + " used " + move.getName() + " for " + damage +
                    " damage. " + targetPokemon.getName() + " has " + remainingHp + " HP left.";
        }
    }

    @Override
    public String getTurnDetails() {
        return "Turn " + turnNumber + " (ID: " + id + "): Player " + player.getName() +
               " used " + move.getName() + ". Damage: " + damage +
               ", Effectiveness: " + effectiveness + ". Result: " + result;
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
