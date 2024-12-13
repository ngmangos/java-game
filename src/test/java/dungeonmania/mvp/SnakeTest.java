package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SnakeTest {
    @Test
    @Tag("20-1")
    @DisplayName("Test movement of snake for food: Snake closer to arrow")
    public void basicMovementA() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_moveForFoodA", "c_snakeTest_moveForFoodA");
        Position pos = TestUtils.getEntities(res, "snake_head").get(0).getPosition();
        assertEquals(new Position(5, 3), pos);
        Position posPlayer = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(new Position(1, 3), posPlayer);

        List<Position> movementTrajectory = new ArrayList<Position>(
                Arrays.asList(new Position(5, 3), new Position(4, 3), new Position(3, 3), new Position(3, 2)));

        List<Direction> playerDirections = new ArrayList<Direction>(
                Arrays.asList(Direction.UP, Direction.UP, Direction.RIGHT, Direction.RIGHT));
        for (int i = 0; i < 4; i++) {
            assertEquals(movementTrajectory.get(i), TestUtils.getEntities(res, "snake_head").get(0).getPosition());
            res = dmc.tick(playerDirections.get(i));
        }
    }

    @Test
    @Tag("20-2")
    @DisplayName("Test movement of snake for food: Snake closer to treasure")
    public void basicMovementB() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_moveForFoodB", "c_snakeTest_moveForFoodB");
        Position pos = TestUtils.getEntities(res, "snake_head").get(0).getPosition();
        assertEquals(new Position(5, 1), pos);
        Position posPlayer = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(new Position(1, 3), posPlayer);

        List<Position> movementTrajectory = new ArrayList<Position>(
                Arrays.asList(new Position(5, 1), new Position(6, 1), new Position(7, 1), new Position(7, 2)));

        List<Direction> playerDirections = new ArrayList<Direction>(
                Arrays.asList(Direction.UP, Direction.UP, Direction.RIGHT, Direction.RIGHT));
        for (int i = 0; i < 4; i++) {
            assertEquals(movementTrajectory.get(i), TestUtils.getEntities(res, "snake_head").get(0).getPosition());
            res = dmc.tick(playerDirections.get(i));
        }
    }

    @Test
    @Tag("20-3")
    @DisplayName("Test the effect of invincibility potions on the snake")
    public void invicibility() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_invincible", "c_snakeTest_invincible");
        Position pos = TestUtils.getEntities(res, "snake_head").get(0).getPosition();
        assertEquals(new Position(1, 2), pos);
        // player at 5, 1; snake to chop at 5, 2
        // new snake should be at 5,3
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.UP);
        }
        assertEquals(1, TestUtils.getEntities(res, "snake_head").size());
        assertEquals(5, TestUtils.getEntities(res, "snake_body").size());

        // player battles with snake body
        res = dmc.tick(Direction.DOWN);

        // the snake body is defeated (battles == 1) forming a new snake head
        // but, in the enemy moves part of the tick, the new head will move onto the players
        // location (battle == 2) and it will die, killing the entire new snake (no new head)
        assertEquals(2, res.getBattles().size());
        assertEquals(0, TestUtils.getEntities(res, "snake_body").size());
        assertEquals(1, TestUtils.getEntities(res, "snake_head").size());

        res = dmc.tick(Direction.UP);
    }

    @Test
    @Tag("20-4")
    @DisplayName("Test the movement of a severed new snake head")
    public void invicibilityMovement() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_invincibleMoveDifferent", "c_snakeTest_invincibleMoveDifferent");
        Position pos = TestUtils.getEntities(res, "snake_head").get(0).getPosition();
        assertEquals(new Position(1, 1), pos);
        // res = dmc.tick(Direction.DOWN);
        // assertEquals(res.getBattles().size(), 1);
        // player at 5, 1; snake to chop at 5, 2
        // new snake should be at 5,3
        for (int i = 0; i < 7; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        assertEquals(1, TestUtils.getEntities(res, "snake_head").size());
        assertEquals(7, TestUtils.getEntities(res, "snake_body").size());

        // player battles with snake body
        res = dmc.tick(Direction.UP);

        assertEquals(1, res.getBattles().size());
        assertEquals(5, TestUtils.getEntities(res, "snake_body").size());
        assertEquals(2, TestUtils.getEntities(res, "snake_head").size());

        // testing if the new snake head moves in a different direction than old snake head
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(4, 2))));
    }

    @Test
    @Tag("20-5")
    @DisplayName("Test the effect of arrow on snake")
    public void arrow() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_arrow", "c_snakeTest_arrow");
        assertEquals(1, TestUtils.getEntities(res, "player").size());
        for (int i = 0; i < 2; i++) {
            res = dmc.tick(Direction.LEFT);
        }
        assertEquals(1, res.getBattles().size());
        assertEquals(1, TestUtils.getEntities(res, "snake_body").size());
        assertEquals(1, TestUtils.getEntities(res, "snake_head").size());
        assertEquals(0, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("20-6")
    @DisplayName("Test the effect of treasure on snake")
    public void treasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_treasure", "c_snakeTest_treasure");
        assertEquals(1, TestUtils.getEntities(res, "player").size());
        for (int i = 0; i < 2; i++) {
            res = dmc.tick(Direction.LEFT);
        }
        assertEquals(1, res.getBattles().size());
        assertEquals(1, TestUtils.getEntities(res, "snake_body").size());
        assertEquals(1, TestUtils.getEntities(res, "snake_head").size());
        assertEquals(0, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("20-7")
    @DisplayName("Test the effect of key on snake")
    public void key() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_key", "c_snakeTest_key");
        assertEquals(1, TestUtils.getEntities(res, "player").size());
        for (int i = 0; i < 2; i++) {
            res = dmc.tick(Direction.LEFT);
        }
        assertEquals(1, res.getBattles().size());
        double initialPlayerHealth = res.getBattles().get(0).getInitialPlayerHealth();
        double initialEnemyHealth = res.getBattles().get(0).getInitialEnemyHealth();
        double delta = 0.01;
        assertTrue(initialPlayerHealth >= 15 - delta && initialPlayerHealth <= 15 + delta);
        assertTrue(initialEnemyHealth >= 16 - delta && initialEnemyHealth <= 16 + delta);
        assertEquals(1, TestUtils.getEntities(res, "snake_body").size());
        assertEquals(1, TestUtils.getEntities(res, "snake_head").size());
        assertEquals(0, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("20-7")
    @DisplayName("Test the effect of invisibility potion on snake")
    public void invisible() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_invisible", "c_snakeTest_invisible");
        assertEquals(1, TestUtils.getEntities(res, "player").size());
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.UP);
        }
        assertEquals(new Position(5, 2), TestUtils.getEntities(res, "snake_head").get(0).getPosition());
    }

    @Test
    @Tag("20-7")
    @DisplayName("Test if snakes block eachother")
    public void snakesBlockEachother() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_snakesCannotOverlap", "c_snakeTest_snakesCannotOverlap");
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(1, 1))));
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(3, 1))));
        res = dmc.tick(Direction.UP);
        // snakes path is blocked by other snake, so it doesnt move
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(1, 1))));
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(4, 1))));
    }

    @Test
    @Tag("20-7")
    @DisplayName("Test if invisible snakes dont block eachother")
    public void invisibleSnakesDontBlock() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_snakeTest_invisibleSnakesOverlap", "c_snakeTest_invisibleSnakesOverlap");
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(1, 1))));
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(4, 1))));
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        // the snake that should be block is now invisible so is no longer blocked by the other snake
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(3, 1))));
        assertTrue(TestUtils.getEntities(res, "snake_head").stream()
                .anyMatch(snake -> snake.getPosition().equals(new Position(4, 1))));
    }

}
