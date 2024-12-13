package dungeonmania.goals;

import dungeonmania.Game;

public class GoalEnemy implements Goal {
    private int target;

    public GoalEnemy(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) {
            return false;
        }

        return game.getDefeatedEnemyCount() >= target;
    }

    public String toString(Game game) {
        if (this.achieved(game)) {
            return "";
        }

        return ":enemies";
    }
}
