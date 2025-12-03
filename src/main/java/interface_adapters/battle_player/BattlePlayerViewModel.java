package interface_adapters.battle_player;

import interface_adapters.ViewModel;

/**
 * ViewModel for a battle vs player.
 */

public class BattlePlayerViewModel extends ViewModel<BattlePlayerState> {

    public static final String VIEW_NAME = "battle player";
    public static final String STATE_PROPERTY = "state";

    public BattlePlayerViewModel() {
        super(VIEW_NAME);
        super.setState(new BattlePlayerState());
    }

    @Override
    public void setState(BattlePlayerState state) {
        super.setState(state);
        firePropertyChanged(STATE_PROPERTY);
    }
}
