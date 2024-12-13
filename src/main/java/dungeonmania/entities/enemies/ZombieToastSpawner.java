package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.snakes.Snake;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class ZombieToastSpawner extends PlaceableEntity implements Interactable {
    public static final int DEFAULT_SPAWN_INTERVAL = 0;

    public ZombieToastSpawner(Position position, int spawnInterval) {
        super(position);
    }

    public void spawn(Game game) {
        game.getEntityFactory().spawnZombie(game, this);
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
    }

    @Override
    public void interact(Player player, Game game) {
        player.zombieToastSpawnerInteract(game);
    }

    @Override
    public boolean isInteractable(Player player) {
        return Position.isAdjacent(player.getPosition(), getPosition()) && player.hasWeapon();
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return entity instanceof Snake;
    }

}
