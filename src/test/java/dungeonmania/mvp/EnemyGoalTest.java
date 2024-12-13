package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class EnemyGoalTest {
    @Test
    @Tag("21-1")
    @DisplayName("Test achieving a basic enemy goal: single mercenary")
    public void singleEnemyGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoalTest_singleMerc", "c_enemyGoalTest_singleMerc");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        // move player to right
        res = dmc.tick(Direction.DOWN);
        assertTrue(res.getBattles().size() != 0);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("21-2")
    @DisplayName("Test achieving a basic enemy goal: two mercenaries")
    public void doubleEnemyGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoalTest_DoubleMerc", "c_enemyGoalTest_DoubleMerc");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        // move player to right
        res = dmc.tick(Direction.DOWN);
        assertTrue(res.getBattles().size() != 0);

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        res = dmc.tick(Direction.DOWN);
        assertTrue(res.getBattles().size() != 0);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("21-3")
    @DisplayName("Testing if enemy goal works in a goal equation")
    public void complexEnemyGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoalTest_complex", "c_enemyGoalTest_complex");

        assertTrue(TestUtils.getGoals(res).contains(":enemies") && TestUtils.getGoals(res).contains(":treasure"));
        // move player to right
        for (int i = 0; i < 3; i++) {
            res = dmc.tick(Direction.RIGHT);
        }
        assertEquals(2, res.getBattles().size());
        assertTrue(!TestUtils.getGoals(res).contains(":enemies") && TestUtils.getGoals(res).contains(":treasure"));
        for (int i = 0; i < 4; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }
}
