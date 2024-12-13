package dungeonmania.goals;

import dungeonmania.Game;

public class GoalOr implements Goal {
    private Goal goal1;
    private Goal goal2;

    public GoalOr(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) {
            return false;
        }

        return goal1.achieved(game) || goal2.achieved(game);
    }

    public String toString(Game game) {
        if (this.achieved(game)) {
            return "";
        }

        if (achieved(game)) {
            return "";
        } else {
            return "(" + goal1.toString(game) + " OR " + goal2.toString(game) + ")";
        }
    }
}
