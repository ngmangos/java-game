package dungeonmania.entities.enemies.snakes;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SnakeBody extends Snake {
    private SnakeHead snakeHead;
    private Snake previous;

    public SnakeBody(Position position, double health, double attack, boolean isInvincible, boolean isInvisible,
            double attackBuff, double healthMultBuff, double healthBuff, SnakeHead snakeHead, Snake previous) {
        super(position, health, attack, isInvincible, isInvisible, attackBuff, healthMultBuff, healthBuff);
        this.previous = previous;
        setNext(null);
        setSnakeHead(snakeHead);
    }

    public Snake getPrevious() {
        return previous;
    }

    public void setPrevious(Snake previous) {
        this.previous = previous;
    }

    @Override
    public void move(Game game) {
        return;
    }

    @Override
    public void onDestroy(GameMap map) {
        previous.setNext(null);
        List<SnakeBody> totalRemovedObservers = new ArrayList<>(getObservers());
        totalRemovedObservers.add(this);
        snakeHead.removeObservers(totalRemovedObservers);
        if (isInvincible() && getNext() != null && !snakeHead.isDeceased()) {
            getNext().toSnakeHead(map);
            return;
        }
        // remove broken off observers from upstream snake
        Game g = map.getGame();
        g.unsubscribe(getId());
        getObservers().stream().forEach(snakePart -> {
            map.destroyEntity(snakePart);
        });
    }

    public void toSnakeHead(GameMap map) {
        Game g = map.getGame();
        // take this entity off map
        map.destroyEntityNoResponse(this);
        g.unsubscribe(getId());
        // create snake head
        SnakeHead newSnakeHead = new SnakeHead(getPosition(), getHealth(), getAttack(), isInvincible(), isInvisible(),
                getAttackBuff(), getHealthMultBuff(), getHealthBuff(), getNext());
        newSnakeHead.setObservers(getObservers());
        if (getNext() != null) {
            getNext().setPrevious(newSnakeHead);
        }
        // Tell them to change snakeId
        getObservers().stream().forEach(observer -> observer.setSnakeHead(newSnakeHead));
        // upload new snake to the game
        g.getEntityFactory().addSnakeHead(g, newSnakeHead);

    }

    // the snake should move as a single entity, without any other entities moving in between
    // the snake body must move in an ordered way, by loopin through observers it is unknown what order they would be in
    public void follow(GameMap m) {
        m.moveTo(this, getPrevious().getPreviousDistinctPosition());
        if (getNext() != null) {
            getNext().follow(m);
        }
    }

    public void setSnakeHead(SnakeHead snakeHead) {
        this.snakeHead = snakeHead;
    }

    @Override
    public SnakeHead getSnakeHead() {
        return snakeHead;
    }

    @Override
    public String getSnakeId() {
        return snakeHead.getId();
    }

}
