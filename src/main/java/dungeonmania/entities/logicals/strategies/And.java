package dungeonmania.entities.logicals.strategies;

import java.util.List;

import dungeonmania.entities.logicals.Conductor;

public class And implements LogicalRules {
    @Override
    public boolean logicStrategy(List<Conductor> conductors) {
        long activatedNum = conductors.stream().filter(Conductor::isActivated).count();
        boolean activatedAll = conductors.stream().allMatch(Conductor::isActivated);

        return activatedNum >= 2 && activatedAll;
    }
}
