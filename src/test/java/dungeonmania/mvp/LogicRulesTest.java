package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogicRulesTest {
    @Test
    @Tag("16-1")
    @DisplayName("Dryrun for logic switches")
    public void dryrunLogic() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_dryrun_logic", "c_dryrun_logic");
        assertTrue(TestUtils.countType(res, "switch_door") == 1);
        assertTrue(TestUtils.countType(res, "bomb") == 1);
        assertTrue(TestUtils.countType(res, "wire") == 1);
        assertTrue(TestUtils.countType(res, "light_bulb_off") >= 1);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.countType(res, "light_bulb_on") == 1);
    }

    @Test
    @Tag("16-2")
    @DisplayName("Test states of logicals after bomb explodes")
    public void bombExplodes() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_bfs", "c_logicTest_basic");

        TestUtils.getEntities(res, "light_bulb_off").get(0);
        assertEquals(2, TestUtils.getEntities(res, "light_bulb_off").size());

        // move player down
        res = dmc.tick(Direction.DOWN);

        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(2, TestUtils.getEntities(res, "light_bulb_off").size());

    }

    @Test
    @Tag("16-3")
    @DisplayName("Testing lightbulbs")
    public void lightBulbs() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_lightbulbs", "c_logicTest_basic");

        assertEquals(14, TestUtils.getEntities(res, "light_bulb_off").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(5, TestUtils.getEntities(res, "light_bulb_on").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        EntityResponse player = TestUtils.getPlayer(res).get();
        assertEquals(new Position(6, 3), player.getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("16-4")
    @DisplayName("Testing logical bombs")
    public void logicBombs() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_logicBombs", "c_logicTest_basic");

        assertEquals(3, TestUtils.getEntities(res, "bomb").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(8, TestUtils.getEntities(res, "wire").size());

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // Pick up Bomb
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);

        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(7, TestUtils.getEntities(res, "wire").size());
    }

    @Test
    @Tag("16-3")
    @DisplayName("Testing switch doors")
    public void switchDoors() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_switchDoors", "c_logicTest_basic");

        assertEquals(14, TestUtils.getEntities(res, "switch_door").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(5, TestUtils.getEntities(res, "switch_door_open").size());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        EntityResponse player = TestUtils.getPlayer(res).get();
        assertEquals(new Position(2, 4), player.getPosition());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        player = TestUtils.getPlayer(res).get();
        assertEquals(new Position(3, 3), player.getPosition());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        assertEquals(6, TestUtils.getEntities(res, "switch_door_open").size());
        player = TestUtils.getPlayer(res).get();
        assertEquals(new Position(6, 3), player.getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(5, TestUtils.getEntities(res, "switch_door_open").size());
    }

}
