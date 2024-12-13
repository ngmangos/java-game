package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Exit extends PlaceableEntity {
    public Exit(Position position) {
        super(position.asLayer(PlaceableEntity.ITEM_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return true;
    }

}
