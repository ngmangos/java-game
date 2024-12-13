package dungeonmania.entities;

import java.util.UUID;

public class Entity {
    private String entityId;

    public Entity() {
        this.entityId = UUID.randomUUID().toString();
    }

    public String getId() {
        return entityId;
    }

}
