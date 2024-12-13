package dungeonmania.entities.logicals;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.*;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends PlaceableEntity implements Conductor {
    private boolean isActivated;
    private int tickActivated;
    private List<Bomb> bombs = new ArrayList<>();
    private List<LogicalObserver> connectedLogicalEntities = new ArrayList<>();

    public void setConnectedLogicalEntities(List<LogicalObserver> connectedLogicalEntities) {
        this.connectedLogicalEntities = connectedLogicalEntities;
    }

    public List<LogicalObserver> getConnectedLogicalEntities() {
        return connectedLogicalEntities;
    }

    public Switch(Position position) {
        super(position);
    }

    public boolean isActivated() {
        return isActivated;
    }

    public int getTickActivated() {
        return tickActivated;
    }

    public void setTickActivated(int tick) {
        this.tickActivated = tick;
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (isActivated) {
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    public void addConnectedLogicalEntity(LogicalObserver entity) {
        if (!(connectedLogicalEntities.contains(entity))) {
            this.connectedLogicalEntities.add(entity);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Boulder) {
            isActivated = true;
            bombs.stream().forEach(b -> b.notify(map));

            map.bfsLogicFind(this);
            connectedLogicalEntities.stream().forEach(e -> e.notify(map));

            if (connectedLogicalEntities.stream().anyMatch(b -> b instanceof LogicBomb) || !(bombs.isEmpty())) {
                // explosion can disrupt logical entities
                // so we remove all their connections to be remade next tick
                map.removeLogicalConnections();
            }
        }
    }

    @Override
    public void onMovedAway(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Boulder) {
            isActivated = false;

            map.bfsLogicFind(this);
            connectedLogicalEntities.stream().forEach(e -> e.notify(map));
        }
    }
}
