package dungeonmania.entities.enemies.movementbehaviour;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class FollowPlayer implements MovementBehaviour {
    private GameMap map;
    private Mercenary mercenary;
    private Player player;

    public FollowPlayer(GameMap map, Mercenary mercenary, Player player) {
        this.map = map;
        this.mercenary = mercenary;
        this.player = player;
    }

    public Position nextPosition() {
        return mercenary.isAdjacentToPlayer() ? player.getPreviousDistinctPosition()
                : map.dijkstraPathFind(mercenary.getPosition(), player.getPosition(), mercenary);
    }
}
