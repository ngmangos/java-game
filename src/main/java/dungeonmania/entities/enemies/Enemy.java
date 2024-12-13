package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends PlaceableEntity implements Battleable {
    private BattleStatistics battleStatistics;

    public Enemy(Position position, double health, double attack) {
        super(position.asLayer(PlaceableEntity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(health, attack, 0, BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return entity instanceof Player || entity instanceof Enemy;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void onOverlap(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!player.isInvisible()) {
                map.getGame().battle(player, this);
            }
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
    }

    public abstract void move(Game game);

}
