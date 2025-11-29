/*package entities;

public class SwitchTurn extends Turn {
    private Pokemon previousPokemon;
    private Pokemon newPokemon;

    public SwitchTurn() {
        super();
    }

    public SwitchTurn(int id, Player player, int turnNumber, Pokemon previousPokemon, Pokemon newPokemon) {
        super(id, player, turnNumber);
        this.previousPokemon = previousPokemon;
        this.newPokemon = newPokemon;
    }

    @Override
    public void executeTurn() {
        // Logic to switch Pokemon would go here
        // Update the player's active Pokemon
        player.switchPokemon(newPokemon);
        this.result = "Switched from " + (previousPokemon != null ? previousPokemon.getName() : "none") +
                      " to " + newPokemon.getName();
    }

    @Override
    public String getTurnDetails() {
        return "Turn " + turnNumber + " (ID: " + id + "): Player " + player.getName() +
               " switched from " + (previousPokemon != null ? previousPokemon.getName() : "none") +
               " to " + newPokemon.getName() + ". Result: " + result;
    }

    public Pokemon getPreviousPokemon() {
        return previousPokemon;
    }

    public void setPreviousPokemon(Pokemon previousPokemon) {
        this.previousPokemon = previousPokemon;
    }

    public Pokemon getNewPokemon() {
        return newPokemon;
    }

    public void setNewPokemon(Pokemon newPokemon) {
        this.newPokemon = newPokemon;
    }
}*/
