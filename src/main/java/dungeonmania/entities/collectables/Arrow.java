package dungeonmania.entities.collectables;

import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Arrow extends Collectable implements InventoryItem {
    public Arrow(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return true;
    }
}
