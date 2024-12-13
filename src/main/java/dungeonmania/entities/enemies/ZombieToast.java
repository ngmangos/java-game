package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.movementbehaviour.MoveAwayFromPlayer;
import dungeonmania.entities.enemies.movementbehaviour.MovementBehaviour;
import dungeonmania.entities.enemies.movementbehaviour.RandomMovement;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;
    private MovementBehaviour movementBehaviour;

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack);
    }

    @Override
    public void move(Game game) {
        GameMap map = game.getMap();
        Player player = map.getPlayer();
        if (player.isInvincible()) {
            movementBehaviour = new MoveAwayFromPlayer(map, this, map.getPlayer());
        } else {
            movementBehaviour = new RandomMovement(map, this);
        }
        game.getMap().moveTo(this, movementBehaviour.nextPosition());
    }

}
