package dungeonmania.entities.enemies.movementbehaviour;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MoveAwayFromPlayer implements MovementBehaviour {
    private GameMap map;
    private Enemy enemy;
    private Player player;

    public MoveAwayFromPlayer(GameMap map, Enemy enemy, Player player) {
        this.map = map;
        this.enemy = enemy;
        this.player = player;

    }

    public Position nextPosition() {
        Position currentPos = enemy.getPosition();
        Position plrDiff = Position.calculatePositionBetween(player.getPosition(), currentPos);

        Position moveX = (plrDiff.getX() >= 0) ? Position.translateBy(currentPos, Direction.RIGHT)
                : Position.translateBy(currentPos, Direction.LEFT);
        Position moveY = (plrDiff.getY() >= 0) ? Position.translateBy(currentPos, Direction.UP)
                : Position.translateBy(currentPos, Direction.DOWN);
        Position offset = currentPos;
        if (plrDiff.getY() == 0 && map.canMoveTo(enemy, moveX))
            offset = moveX;
        else if (plrDiff.getX() == 0 && map.canMoveTo(enemy, moveY))
            offset = moveY;
        else if (Math.abs(plrDiff.getX()) >= Math.abs(plrDiff.getY())) {
            if (map.canMoveTo(enemy, moveX))
                offset = moveX;
            else if (map.canMoveTo(enemy, moveY))
                offset = moveY;
            else
                offset = currentPos;
        } else {
            if (map.canMoveTo(enemy, moveY))
                offset = moveY;
            else if (map.canMoveTo(enemy, moveX))
                offset = moveX;
            else
                offset = currentPos;
        }
        return offset;
    }
}
