package entities.open_pack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.Pokemon;

public class Pack {
    private static final int CARDS_PER_PACK = 5;
    private static final double SHINY_PROBABILITY = 0.05;
    // 5%

    private final int id;
    private final String type;
    private final List<Pokemon> cardPool;

    private final Random random = new Random();

    public Pack(int id, String type, List<Pokemon> cardPool) {
        this.id = id;
        this.type = type;
        this.cardPool = List.copyOf(cardPool);
        // copy to keep original data safe :)
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<Pokemon> getCardPool() {
        return cardPool;
    }

    public List<Pokemon> openPack() {
        final List<Pokemon> opened = new ArrayList<>();

        if (cardPool.isEmpty()) {
            return opened;
        }

        for (int i = 0; i < CARDS_PER_PACK; i++) {
            final int index = random.nextInt(cardPool.size());
            final Pokemon base = cardPool.get(index);
            final Pokemon card = base.copy();
            if (random.nextDouble() < SHINY_PROBABILITY) {
                card.setShiny(true);
            }
            opened.add(card);
        }

        return opened;
    }
}
