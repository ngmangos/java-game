package dungeonmania.entities.logicals;

import java.util.List;

import dungeonmania.map.GameMap;

public interface LogicalObserver {
    public void notify(GameMap map);

    public void addAdjacentConductor(Conductor c);

    public void setConnectedLogicalEntities(List<Conductor> adjacentConductors);

}
