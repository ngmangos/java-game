package dungeonmania.entities.logicals.strategies;

import java.util.List;

import dungeonmania.entities.logicals.Conductor;

public class Xor implements LogicalRules {
    @Override
    public boolean logicStrategy(List<Conductor> conductors) {
        long activatedNum = conductors.stream().filter(Conductor::isActivated).count();

        return activatedNum == 1;
    }
}
