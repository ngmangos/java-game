package dungeonmania.entities.logicals.strategies;

import java.util.List;

import dungeonmania.entities.logicals.Conductor;

public class Or implements LogicalRules {
    @Override
    public boolean logicStrategy(List<Conductor> conductors) {
        return conductors.stream().anyMatch(Conductor::isActivated);
    }
}
