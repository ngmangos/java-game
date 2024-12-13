package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class RequirementsTest {
    @Test
    @Tag("24-1")
    @DisplayName("Determining whether enemies can overlap")
    public void enemiesOverlap() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_requirementsTest_enemiesOverlap", "c_requirementsTest_enemiesOverlap");
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntities(res, "mercenary").get(0).getPosition(),
                TestUtils.getEntities(res, "mercenary").get(1).getPosition());
    }
}
