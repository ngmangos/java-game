package dungeonmania.entities.collectables;

import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Collectable extends PlaceableEntity {
    public Collectable(Position position) {
        super(position);
    }

    @Override
    public void onOverlap(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this))
                return;
            map.destroyEntity(this);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return true;
    }
}
