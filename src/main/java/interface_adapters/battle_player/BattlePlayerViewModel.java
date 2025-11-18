package interface_adapters.battle_player;

public class BattlePlayerViewModel {

    public static final String VIEW_NAME = "battle player";

    private BattlePlayerState state = new BattlePlayerState();

    public String getViewName() {
        return VIEW_NAME;
    }

    public BattlePlayerState getState() {
        return state;
    }

    public void setState(BattlePlayerState state) {
        this.state = state;
    }
}
