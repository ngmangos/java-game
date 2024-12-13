package dungeonmania.entities.logicals;

import dungeonmania.entities.PlaceableEntity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wire extends PlaceableEntity implements Conductor {
    private boolean isActivated;
    private int tickActivated;

    public Wire(Position position) {
        super(position);
        this.isActivated = false;
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return true;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public int getTickActivated() {
        return tickActivated;
    }

    public void setTickActivated(int tick) {
        this.tickActivated = tick;
    }

    public void activate(int tick) {
        this.tickActivated = tick;
        this.isActivated = !isActivated;
    }

}
