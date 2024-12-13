package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.enemies.snakes.Snake;
import dungeonmania.util.Position;

public class Wall extends PlaceableEntity {
    public Wall(Position position) {
        super(position.asLayer(PlaceableEntity.CHARACTER_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Snake) {
            Snake snake = (Snake) entity;
            return snake.isInvisible();
        }
        return entity instanceof Spider;
    }

}
