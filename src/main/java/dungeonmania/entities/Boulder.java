package dungeonmania.entities;

import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.enemies.snakes.Snake;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Boulder extends PlaceableEntity {
    public Boulder(Position position) {
        super(position.asLayer(PlaceableEntity.CHARACTER_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Spider)
            return false;
        if ((entity instanceof Player && canPush(map, entity.getFacing())) || entity instanceof Snake)
            return true;
        return false;
    }

    @Override
    public void onOverlap(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Player) {
            map.moveTo(this, entity.getFacing());
        }
    }

    private boolean canPush(GameMap map, Direction direction) {
        Position newPosition = Position.translateBy(this.getPosition(), direction);
        for (PlaceableEntity e : map.getEntities(newPosition)) {
            if (!e.canMoveOnto(map, this))
                return false;
        }
        return true;
    }
}
