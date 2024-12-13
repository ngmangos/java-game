package dungeonmania.entities.logicals;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.logicals.strategies.LogicalRules;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LightBulb extends PlaceableEntity implements LogicalObserver {
    private LogicalRules logic;
    private boolean isActivated;
    private List<Conductor> adjacentConductors = new ArrayList<>();

    public LightBulb(Position position, LogicalRules logic) {
        super(position);
        this.logic = logic;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setConnectedLogicalEntities(List<Conductor> adjacentConductors) {
        this.adjacentConductors = adjacentConductors;
    }

    public void addAdjacentConductor(Conductor c) {
        if (!(adjacentConductors.contains(c))) {
            this.adjacentConductors.add(c);
        }
    }

    public void notify(GameMap map) {
        boolean activate = logic.logicStrategy(adjacentConductors);
        this.isActivated = activate;
    }
}