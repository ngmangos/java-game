package dungeonmania.entities.logicals;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.logicals.strategies.LogicalRules;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Player;

public class LogicBomb extends Bomb implements LogicalObserver {
    private LogicalRules logic;
    private List<Conductor> adjacentConductors = new ArrayList<>();

    public LogicBomb(Position position, int radius, LogicalRules logic) {
        super(position, Bomb.DEFAULT_RADIUS);
        this.logic = logic;
    }

    public void setConnectedLogicalEntities(List<Conductor> adjacentConductors) {
        this.adjacentConductors = adjacentConductors;
    }

    @Override
    public void notify(GameMap map) {
        if (logic.logicStrategy(adjacentConductors)) {
            explode(map);
        }
    }

    @Override
    public void onPutDown(GameMap map, Position p) {
        setPosition(p);
        map.addEntity(this);
        setState(State.PLACED);
        List<Position> adjPosList = getPosition().getCardinallyAdjacentPositions();
        for (Position node : adjPosList) {
            List<PlaceableEntity> neighbours = map.getEntities(node);
            for (PlaceableEntity e : neighbours) {
                if (e instanceof Conductor) {
                    addAdjacentConductor((Conductor) e);
                }
            }
        }
        notify(map);
    }

    @Override
    public void onOverlap(GameMap map, PlaceableEntity entity) {
        if (getState() != State.SPAWNED)
            return;
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this))
                return;
            adjacentConductors.clear();
            map.destroyEntity(this);
        }
        setState(State.INVENTORY);
    }

    public void addAdjacentConductor(Conductor c) {
        if (!(adjacentConductors.contains(c))) {
            this.adjacentConductors.add(c);
        }
    }
}
