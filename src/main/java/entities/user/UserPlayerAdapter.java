package entities.user;

import entities.*;
import entities.battle.Battle;
import entities.battle.Deck;
import entities.battle.Move;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that adapts a User to the Player interface.
 * This allows Users to participate in battles as Players.
 * Implements Serializable for LangGraph4j state management.
 */
public class UserPlayerAdapter implements Player, Serializable {
    private static final long serialVersionUID = 1L;

    private final User user;
    private Pokemon activePokemon;
    private Deck deck;

    public UserPlayerAdapter(User user) {
        this.user = user;
        String deckName = (user.getName() == null || user.getName().isEmpty())
            ? "User Deck"
            : user.getName() + " Deck";
        this.deck = new Deck(user.getId(), deckName);
        // Set first available Pokemon as active
        if (user.getOwnedPokemon() != null && !user.getOwnedPokemon().isEmpty()) {
            for (Pokemon pokemon : user.getOwnedPokemon()) {
                if (!pokemon.isFainted()) {
                    this.activePokemon = pokemon;
                    break;
                }
            }
        }
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public Deck getDeck() {
        return deck;
    }

    @Override
    public Move chooseMove(Battle battle) {
        // Simple implementation - choose first available move from active Pokemon
        if (activePokemon != null && activePokemon.getMoves() != null && !activePokemon.getMoves().isEmpty()) {
            // Return a basic move - in a real implementation, this would be more sophisticated
            return new Move()
                .setName(activePokemon.getMoves().get(0))
                .setType("normal")
                .setPower(40);
        }
        return new Move().setName("Struggle").setType("normal").setPower(50);
    }

    @Override
    public Pokemon getActivePokemon() {
        return activePokemon;
    }

    @Override
    public void switchPokemon(Pokemon pokemon) {
        // Verify the Pokemon belongs to the user
        if (user.getOwnedPokemon().contains(pokemon) && !pokemon.isFainted()) {
            this.activePokemon = pokemon;
        }
    }

    @Override
    public List<Pokemon> getTeam() {
        return new ArrayList<>(user.getOwnedPokemon());
    }

    @Override
    public void useItem(Item item, Pokemon target) {
        // Implementation for using items
        // This would apply the item's effect to the target Pokemon
    }

    @Override
    public boolean hasAvailablePokemon() {
        if (user.getOwnedPokemon() == null || user.getOwnedPokemon().isEmpty()) {
            return false;
        }
        for (Pokemon pokemon : user.getOwnedPokemon()) {
            if (!pokemon.isFainted()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDefeated() {
        return !hasAvailablePokemon();
    }

    public User getUser() {
        return user;
    }
}
