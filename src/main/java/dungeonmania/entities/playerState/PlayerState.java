package dungeonmania.entities.playerState;

public abstract class PlayerState {
    public boolean isBaseState() {
        return this instanceof BaseState;
    }

    public boolean isInvincible() {
        return this instanceof InvincibleState;
    };

    public boolean isInvisible() {
        return this instanceof InvisibleState;
    };
}
