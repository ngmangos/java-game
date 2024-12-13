package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class Dryrun {
    @Test
    @Tag("0-1")
    @DisplayName("Dryrun for enemy goal")
    public void dryrunEnemyGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_dryrun_enemyGoal", "c_dryrun_enemyGoal");
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    @Tag("0-6")
    @DisplayName("Dryrun for snake")
    public void dryrunSnake() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_dryrun_snake", "c_dryrun_snake");
        assertTrue(TestUtils.countType(res, "snake_head") == 1);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.countType(res, "snake_body") == 1);
    }
}
