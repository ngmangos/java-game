package dungeonmania.entities.logicals.strategies;

import java.util.List;

import dungeonmania.entities.logicals.Conductor;

public class CoAnd implements LogicalRules {
    @Override
    public boolean logicStrategy(List<Conductor> conductors) {
        int activatedTick = conductors.get(0).getTickActivated();
        for (Conductor c : conductors) {
            if (c.getTickActivated() != activatedTick) {
                return false;
            }
            activatedTick = c.getTickActivated();
        }

        long activatedNum = conductors.stream().filter(Conductor::isActivated).count();
        boolean activatedAll = conductors.stream().allMatch(Conductor::isActivated);

        return activatedNum >= 2 && activatedAll;
    }
}
