package dungeonmania.entities.enemies.snakes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Snake extends Enemy {
    private List<SnakeBody> observers = new ArrayList<SnakeBody>();
    private SnakeBody next;
    private boolean isInvincible;
    private boolean isInvisible;
    private double attackBuff;
    private double healthMultBuff;
    private double healthBuff;

    public Snake(Position position, double health, double attack, boolean isInvincible, boolean isInvisible,
            double attackBuff, double healthMultBuff, double healthBuff) {
        super(position, health, attack);
        this.isInvincible = isInvincible;
        this.isInvisible = isInvisible;
        this.attackBuff = attackBuff;
        this.healthMultBuff = healthMultBuff;
        this.healthBuff = healthBuff;
    }

    public List<SnakeBody> getObservers() {
        return observers;
    }

    public void setObservers(List<SnakeBody> observers) {
        this.observers = observers;
    }

    public SnakeBody getNext() {
        return next;
    }

    public void setNext(SnakeBody next) {
        this.next = next;
    }

    public boolean isInvisible() {
        return isInvisible;
    };

    public boolean isInvincible() {
        return isInvincible;
    };

    public SnakeBody addToSnake(Game g) {
        if (next == null) {
            // this should be factory function as it has to add to game subscribers
            SnakeBody newSnakeBody = new SnakeBody(getPreviousDistinctPosition(), getHealth(), getAttack(),
                    isInvincible(), isInvisible(), getAttackBuff(), getHealthMultBuff(), getHealthBuff(),
                    getSnakeHead(), this);
            g.getEntityFactory().addSnakeBody(g, newSnakeBody);
            setNext(newSnakeBody);
            observers.add(newSnakeBody);
            return newSnakeBody;
        }
        SnakeBody newSnakeBody = next.addToSnake(g);
        observers.add(newSnakeBody);
        return newSnakeBody;
    }

    public void removeObservers(List<SnakeBody> observers) {
        if (getNext() != null) {
            getNext().removeObservers(observers);
        }
        this.observers = this.observers.stream().filter(snakeObserver -> !observers.contains(snakeObserver))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        if (entity instanceof Snake) {
            Snake snake = (Snake) entity;
            if (snake.isInvisible && !snake.getSnakeId().equals(getSnakeId())) {
                return true;
            }
            return false;
        }
        return entity instanceof Player || entity instanceof Enemy;
    }

    public double getHealth() {
        return getBattleStatistics().getHealth();
    }

    public double getAttack() {
        return getBattleStatistics().getAttack();
    }

    public abstract String getSnakeId();

    public abstract SnakeHead getSnakeHead();

    public double getAttackBuff() {
        return attackBuff;
    }

    public double getHealthMultBuff() {
        return healthMultBuff;
    }

    public double getHealthBuff() {
        return healthBuff;
    }

    public void receiveBuff(PlaceableEntity foodItem) {
        if (foodItem instanceof Treasure) {
            getBattleStatistics().buffHealth(getHealthBuff());
        } else if (foodItem instanceof Key) {
            getBattleStatistics().multBuffHealth(getHealthMultBuff());
        } else if (foodItem instanceof Arrow) {
            getBattleStatistics().buffAttack(getAttackBuff());
        } else if (foodItem instanceof InvisibilityPotion) {
            setInvisible(true);
        } else if (foodItem instanceof InvincibilityPotion) {
            setInvincible(true);
        }
    }

    public void setInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }

    public void setInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }

}
