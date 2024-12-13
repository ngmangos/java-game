package dungeonmania.entities;

import dungeonmania.map.GameMap;

import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.enemies.snakes.Snake;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.util.Position;

public class Door extends PlaceableEntity {
    private boolean open = false;
    private int number;

    public Door(Position position, int number) {
        super(position.asLayer(PlaceableEntity.DOOR_LAYER));
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        if (open || entity instanceof Spider || entity instanceof Snake) {
            return true;
        }
        return (entity instanceof Player && ((Player) entity).hasKey(this));
    }

    @Override
    public void onOverlap(GameMap map, PlaceableEntity entity) {
        if (!(entity instanceof Player))
            return;

        Player player = (Player) entity;
        Inventory inventory = player.getInventory();
        Key key = inventory.getFirst(Key.class);

        if (player.hasKey(this)) {
            inventory.remove(key);
            open();
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }

}
