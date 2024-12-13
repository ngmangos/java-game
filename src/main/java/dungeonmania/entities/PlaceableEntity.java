package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public abstract class PlaceableEntity extends Entity {
    public static final int FLOOR_LAYER = 0;
    public static final int ITEM_LAYER = 1;
    public static final int DOOR_LAYER = 2;
    public static final int CHARACTER_LAYER = 3;

    private Position position;
    private Position previousPosition;
    private Position previousDistinctPosition;
    private Direction facing;

    public PlaceableEntity(Position position) {
        super();
        this.position = position;
        this.previousPosition = position;
        this.previousDistinctPosition = null;
        this.facing = null;
    }

    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return false;
    }

    public void onOverlap(GameMap map, PlaceableEntity entity) {
        return;
    }

    public void onMovedAway(GameMap map, PlaceableEntity entity) {
        return;
    }

    public void onDestroy(GameMap gameMap) {
        return;
    }

    public Position getPosition() {
        return position;
    }

    public Position getPreviousPosition() {
        return previousPosition;
    }

    public Position getPreviousDistinctPosition() {
        return previousDistinctPosition;
    }

    public void setPosition(Position position) {
        previousPosition = this.position;
        this.position = position;
        if (!previousPosition.equals(this.position)) {
            previousDistinctPosition = previousPosition;
        }
    }

    public void movePosition(Direction direction) {
        setPosition(Position.translateBy(position, direction));
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return this.facing;
    }
}
