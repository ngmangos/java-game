package dungeonmania.map;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.PlaceableEntity;
import dungeonmania.util.Position;

public class GraphNode {
    private Position position;
    private List<PlaceableEntity> entities = new ArrayList<>();

    private int weight = 1;

    public GraphNode(PlaceableEntity entity, int weight) {
        this(entity, entity.getPosition(), weight);
    }

    public GraphNode(PlaceableEntity entity) {
        this(entity, entity.getPosition(), 1);
    }

    public GraphNode(PlaceableEntity entity, Position p, int weight) {
        this.position = p;
        this.entities.add(entity);
        this.weight = weight;
    }

    public boolean canMoveOnto(GameMap map, PlaceableEntity entity) {
        return entities.size() == 0 || entities.stream().allMatch(e -> e.canMoveOnto(map, entity));
    }

    public int getWeight() {
        return weight;
    }

    public void addEntity(PlaceableEntity entity) {
        if (!this.entities.contains(entity))
            this.entities.add(entity);
    }

    public void removeEntity(PlaceableEntity entity) {
        entities.remove(entity);
    }

    public int size() {
        return entities.size();
    }

    public void mergeNode(GraphNode node) {
        List<PlaceableEntity> es = node.entities;
        es.forEach(this::addEntity);
    }

    public List<PlaceableEntity> getEntities() {
        return entities;
    }

    public Position getPosition() {
        return position;
    }

    public <T extends PlaceableEntity> boolean containsEntity(Class<T> entityClass) {
        return entities.stream().anyMatch(entity -> entityClass.isInstance(entity));
    }

    public <T extends PlaceableEntity> PlaceableEntity returnEntity(Class<T> entityClass) {
        for (PlaceableEntity entity : entities) {
            if (entityClass.isInstance(entity)) {
                return entity;
            }
        }
        return null;
    }
}
