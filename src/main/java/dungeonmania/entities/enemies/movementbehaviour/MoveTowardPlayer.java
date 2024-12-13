package dungeonmania.entities.enemies.movementbehaviour;

import dungeonmania.util.Position;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;

public class MoveTowardPlayer implements MovementBehaviour {
    private GameMap map;
    private Enemy enemy;
    private Player player;

    public MoveTowardPlayer(GameMap map, Enemy enemy, Player player) {
        this.map = map;
        this.enemy = enemy;
        this.player = player;

    }

    public Position nextPosition() {
        return map.dijkstraPathFind(enemy.getPosition(), player.getPosition(), enemy);
    }
}
