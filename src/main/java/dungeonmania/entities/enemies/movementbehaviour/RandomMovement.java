package dungeonmania.entities.enemies.movementbehaviour;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class RandomMovement implements MovementBehaviour {
    private GameMap map;
    private Enemy enemy;

    public RandomMovement(GameMap map, Enemy enemy) {
        this.map = map;
        this.enemy = enemy;
    }

    public Position nextPosition() {
        Random randGen = new Random();
        List<Position> pos = enemy.getPosition().getCardinallyAdjacentPositions();
        pos = pos.stream().filter(p -> map.canMoveTo(enemy, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            return enemy.getPosition();
        } else {
            return pos.get(randGen.nextInt(pos.size()));
        }
    }

}
