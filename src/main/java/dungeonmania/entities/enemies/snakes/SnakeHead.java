package dungeonmania.entities.enemies.snakes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SnakeHead extends Snake {
    private static final List<Class<? extends PlaceableEntity>> FOOD = Arrays.asList(Treasure.class, Key.class,
            Arrow.class, InvisibilityPotion.class, InvincibilityPotion.class);
    private boolean deceased = false;
    public static final double DEFAULT_HEALTH = 10;
    public static final double DEFAULT_ATTACK = 5;
    public static final double DEFAULT_ATTACK_BUFF = 2;
    public static final double DEFAULT_HEALTH_MULT_BUFF = 2;
    public static final double DEFAULT_HEALTH_BUFF = 2;

    public SnakeHead(Position position, double health, double attack, boolean isInvincible, boolean isInvisible,
            double attackBuff, double healthMultBuff, double healthBuff, SnakeBody next) {
        super(position, health, attack, isInvincible, isInvisible, attackBuff, healthMultBuff, healthBuff);
        setNext(next);
    }

    public SnakeHead(Position position, double health, double attack, boolean isInvincible, boolean isInvisible,
            double attackBuff, double healthMultBuff, double healthBuff) {
        super(position, health, attack, isInvincible, isInvisible, attackBuff, healthMultBuff, healthBuff);
        setNext(null);
    }

    public void eatFood(PlaceableEntity foodItem, Game game) {
        receiveBuff(foodItem);
        addToSnake(game);
        getObservers().stream().forEach(snake -> snake.receiveBuff(foodItem));
    }

    @Override
    public void onDestroy(GameMap map) {
        deceased = true;
        System.out.println("here we are again: snake head" + getObservers().stream()
                .map(snakeBody -> snakeBody.getId().substring(0, 5)).collect(Collectors.toList()));
        Game g = map.getGame();
        g.unsubscribe(getId());
        getObservers().stream().forEach(snakePart -> {
            map.destroyEntity(snakePart);
        });
    }

    @Override
    public String getSnakeId() {
        return getId();
    }

    @Override
    public void move(Game game) {
        GameMap m = game.getMap();
        m.moveTo(this, m.bfs(this, FOOD));
        if (getNext() != null) {
            getNext().follow(m);
        }
        Position currPos = getPosition();
        for (Class<? extends PlaceableEntity> entityClass : FOOD) {
            if (m.positionContainsEntity(entityClass, currPos)) {
                PlaceableEntity foodItem = m.returnEntity(entityClass, currPos);
                eatFood(foodItem, game);
                m.destroyEntity(foodItem);
            }
        }
    }

    public boolean isDeceased() {
        return deceased;
    }

    @Override
    public SnakeHead getSnakeHead() {
        return this;
    }

}
