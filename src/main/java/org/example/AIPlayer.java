//package org.example;
//
//import cards.Deck;
//import entities.Pokemon;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AIPlayer implements Player {
//    private String name;
//    private Deck deck;
//    private List<Pokemon> team;
//    private Pokemon activePokemon;
//    private String difficulty;
//    private int wins;
//    private int losses;
//
//    public AIPlayer() {
//        this.name = "AI Player";
//        this.deck = new Deck();
//        this.team = new ArrayList<>();
//        this.activePokemon = null;
//        this.difficulty = "medium";
//        this.wins = 0;
//        this.losses = 0;
//    }
//
//    public AIPlayer(String name) {
//        this.name = name;
//        this.deck = new Deck();
//        this.team = new ArrayList<>();
//        this.activePokemon = null;
//        this.difficulty = "medium";
//        this.wins = 0;
//        this.losses = 0;
//    }
//
//    public AIPlayer(String name, String difficulty) {
//        this.name = name;
//        this.deck = new Deck();
//        this.team = new ArrayList<>();
//        this.activePokemon = null;
//        this.difficulty = difficulty;
//        this.wins = 0;
//        this.losses = 0;
//    }
//
//    public AIPlayer(String name, Deck deck, String difficulty) {
//        this.name = name;
//        this.deck = deck;
//        this.team = new ArrayList<>();
//        this.activePokemon = null;
//        this.difficulty = difficulty;
//        this.wins = 0;
//        this.losses = 0;
//    }
//
//    @Override
//    public String getName() {
//        return this.name;
//    }
//
//    @Override
//    public Deck getDeck() {
//        return this.deck;
//    }
//
//    @Override
//    public Move chooseMove(Battle battle) {
//        // AI logic to choose a move would go here
//        // Could vary based on difficulty level
//        return new Move();
//    }
//
//    @Override
//    public Pokemon getActivePokemon() {
//        return activePokemon;
//    }
//
//    @Override
//    public void switchPokemon(Pokemon pokemon) {
//        if (team.contains(pokemon) && !pokemon.isFainted()) {
//            this.activePokemon = pokemon;
//        }
//    }
//
//    @Override
//    public List<Pokemon> getTeam() {
//        return team;
//    }
//
//    @Override
//    public void useItem(Item item, Pokemon target) {
//        // AI logic for using items on Pokemon would go here
//        // Could vary based on difficulty level
//    }
//
//    @Override
//    public boolean hasAvailablePokemon() {
//        for (Pokemon pokemon : team) {
//            if (!pokemon.isFainted()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isDefeated() {
//        return !hasAvailablePokemon();
//    }
//
//    public Battle initiateBattle(User player1, User player2) {
//        Battle battle = new Battle(0, player1, player2);
//        battle.startBattle();
//        return battle;
//    }
//
//    public void processTurn(Battle battle, Move move) {
//        // Logic to process a turn would go here
//        // This would update the battle state based on the move
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setDeck(Deck deck) {
//        this.deck = deck;
//    }
//
//    public String getDifficulty() {
//        return difficulty;
//    }
//
//    public void setDifficulty(String difficulty) {
//        this.difficulty = difficulty;
//    }
//
//    public int getWins() {
//        return wins;
//    }
//
//    public void setWins(int wins) {
//        this.wins = wins;
//    }
//
//    public int getLosses() {
//        return losses;
//    }
//
//    public void setLosses(int losses) {
//        this.losses = losses;
//    }
//
//    public void recordWin() {
//        this.wins++;
//    }
//
//    public void recordLoss() {
//        this.losses++;
//    }
//
//    public void setTeam(List<Pokemon> team) {
//        this.team = team;
//    }
//
//    public void setActivePokemon(Pokemon activePokemon) {
//        this.activePokemon = activePokemon;
//    }
//
//    public void addPokemonToTeam(Pokemon pokemon) {
//        this.team.add(pokemon);
//        if (activePokemon == null && !pokemon.isFainted()) {
//            this.activePokemon = pokemon;
//        }
//    }
//
//    public void removePokemonFromTeam(Pokemon pokemon) {
//        this.team.remove(pokemon);
//        if (activePokemon == pokemon) {
//            this.activePokemon = null;
//        }
//    }
//}
