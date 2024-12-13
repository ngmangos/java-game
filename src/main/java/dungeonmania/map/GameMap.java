package dungeonmania.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.PlaceableEntity;
import dungeonmania.entities.Player;
import dungeonmania.entities.Portal;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.logicals.Conductor;
import dungeonmania.entities.logicals.LogicalObserver;
import dungeonmania.entities.logicals.Switch;
import dungeonmania.entities.logicals.Wire;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class GameMap {
    private Game game;
    private Map<Position, GraphNode> nodes = new HashMap<>();
    private Player player;

    /**
     * Initialise the game map
     * 1. pair up portals
     * 2. register all movables
     * 3. register all spawners
     * 4. register bombs and switches
     * 5. more...
     */
    public void init() {
        initPairPortals();
        initRegisterMovables();
        initRegisterSpawners();
        initRegisterBombsAndSwitches();
    }

    private void initRegisterBombsAndSwitches() {
        List<Bomb> bombs = getEntities(Bomb.class).stream().filter(bomb -> bomb.getClass() == Bomb.class)
                .collect(Collectors.toList());
        List<Switch> switchs = getEntities(Switch.class);
        for (Bomb b : bombs) {
            for (Switch s : switchs) {
                if (Position.isAdjacent(b.getPosition(), s.getPosition())) {
                    b.subscribe(s);
                    s.subscribe(b);
                }
            }
        }
    }

    // Pair up portals if there's any
    private void initPairPortals() {
        Map<String, Portal> portalsMap = new HashMap<>();
        nodes.forEach((k, v) -> {
            v.getEntities().stream().filter(Portal.class::isInstance).map(Portal.class::cast).forEach(portal -> {
                String color = portal.getColor();
                if (portalsMap.containsKey(color)) {
                    portal.bind(portalsMap.get(color));
                } else {
                    portalsMap.put(color, portal);
                }
            });
        });
    }

    private void initRegisterMovables() {
        List<Enemy> enemies = getEntities(Enemy.class);
        enemies.forEach(e -> {
            game.register(() -> e.move(game), Game.AI_MOVEMENT, e.getId());
        });
    }

    private void initRegisterSpawners() {
        List<ZombieToastSpawner> zts = getEntities(ZombieToastSpawner.class);
        zts.forEach(e -> {
            game.register(() -> e.spawn(game), Game.AI_MOVEMENT, e.getId());
        });
        game.register(() -> game.getEntityFactory().spawnSpider(game), Game.AI_MOVEMENT, "spawnSpiders");
    }

    public void moveTo(PlaceableEntity entity, Position position) {
        if (!canMoveTo(entity, position))
            return;

        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(position);
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    public void moveTo(PlaceableEntity entity, Direction direction) {
        if (!canMoveTo(entity, Position.translateBy(entity.getPosition(), direction)))
            return;
        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.movePosition(direction);
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    private void triggerMovingAwayEvent(PlaceableEntity entity) {
        List<Runnable> callbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity)
                callbacks.add(() -> e.onMovedAway(this, entity));
        });
        callbacks.forEach(callback -> {
            callback.run();
        });
    }

    private void triggerOverlapEvent(PlaceableEntity entity) {
        List<Runnable> overlapCallbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity)
                overlapCallbacks.add(() -> e.onOverlap(this, entity));
        });
        overlapCallbacks.forEach(callback -> {
            callback.run();
        });
    }

    public boolean canMoveTo(PlaceableEntity entity, Position position) {
        return !nodes.containsKey(position) || nodes.get(position).canMoveOnto(this, entity);
    }

    public Position dijkstraPathFind(Position src, Position dest, PlaceableEntity entity) {
        // if inputs are invalid, don't move
        if (!nodes.containsKey(src) || !nodes.containsKey(dest))
            return src;

        Map<Position, Integer> dist = new HashMap<>();
        Map<Position, Position> prev = new HashMap<>();
        Map<Position, Boolean> visited = new HashMap<>();

        prev.put(src, null);
        dist.put(src, 0);

        PriorityQueue<Position> q = new PriorityQueue<>((x, y) -> Integer
                .compare(dist.getOrDefault(x, Integer.MAX_VALUE), dist.getOrDefault(y, Integer.MAX_VALUE)));
        q.add(src);

        while (!q.isEmpty()) {
            Position curr = q.poll();
            if (curr.equals(dest) || dist.get(curr) > 200)
                break;
            // check portal
            if (nodes.containsKey(curr) && nodes.get(curr).getEntities().stream().anyMatch(Portal.class::isInstance)) {
                Portal portal = nodes.get(curr).getEntities().stream().filter(Portal.class::isInstance)
                        .map(Portal.class::cast).collect(Collectors.toList()).get(0);
                List<Position> teleportDest = portal.getDestPositions(this, entity);
                teleportDest.stream().filter(p -> !visited.containsKey(p)).forEach(p -> {
                    dist.put(p, dist.get(curr));
                    prev.put(p, prev.get(curr));
                    q.add(p);
                });
                continue;
            }
            visited.put(curr, true);
            List<Position> neighbours = curr.getCardinallyAdjacentPositions().stream()
                    .filter(p -> !visited.containsKey(p)).filter(p -> !nodes.containsKey(p) || canMoveTo(entity, p))
                    .collect(Collectors.toList());

            neighbours.forEach(n -> {
                int newDist = dist.get(curr) + (nodes.containsKey(n) ? nodes.get(n).getWeight() : 1);
                if (newDist < dist.getOrDefault(n, Integer.MAX_VALUE)) {
                    q.remove(n);
                    dist.put(n, newDist);
                    prev.put(n, curr);
                    q.add(n);
                }
            });
        }
        Position ret = dest;
        if (prev.get(ret) == null || ret.equals(src))
            return src;
        while (!prev.get(ret).equals(src)) {
            ret = prev.get(ret);
        }
        return ret;
    }

    public Position bfs(PlaceableEntity movingEntity, List<Class<? extends PlaceableEntity>> classesOfInterest) {
        // if inputs are invalid, don't move
        Map<Position, Position> prev = new HashMap<>();
        Map<Position, Boolean> visited = new HashMap<>();

        Position src = movingEntity.getPosition();
        // determine if there are any objects of the input classes on the map
        if (!getEntities().stream().anyMatch(entity -> {
            for (Class<? extends PlaceableEntity> classOfInterest : classesOfInterest) {
                if (classOfInterest.isInstance(entity)) {
                    return true;
                }
            }
            return false;
        })) {
            return src;
        }

        prev.put(src, null);
        Queue<Position> q = new LinkedList<Position>();
        q.add(src);
        Position dest = src;
        while (!q.isEmpty()) {
            Position curr = q.poll();
            visited.put(curr, true);
            if (nodes.containsKey(curr) && classesOfInterest.stream()
                    .anyMatch(entityClass -> nodes.get(curr).containsEntity(entityClass))) {
                dest = curr;
                break;
            }
            List<Position> neighbours = curr.getCardinallyAdjacentPositions().stream()
                    .filter(p -> !visited.containsKey(p))
                    .filter(p -> !nodes.containsKey(p) || canMoveTo(movingEntity, p)).collect(Collectors.toList());
            neighbours.forEach(n -> {
                prev.put(n, curr);
                q.add(n);
            });
        }
        if (dest.equals(src)) {
            return src;
        }
        while (!prev.get(dest).equals(src)) {
            dest = prev.get(dest);
        }
        return dest;
    }

    public <T extends PlaceableEntity> boolean positionContainsEntity(Class<T> entityClass, Position position) {
        if (nodes.containsKey(position)) {
            return nodes.get(position).containsEntity(entityClass);
        }
        return false;
    }

    public <T extends PlaceableEntity> PlaceableEntity returnEntity(Class<T> entityClass, Position position) {
        return nodes.get(position).returnEntity(entityClass);
    }

    public void bfsLogicFind(Switch s) {
        s.setTickActivated(game.getTick());
        // check entities surrounding starting switch
        linkConnectedLogicals(s, s);

        Map<Position, Boolean> visited = new HashMap<>();

        Queue<Position> q = new LinkedList<Position>();
        q.add(s.getPosition());
        while (!q.isEmpty()) {
            Position curr = q.poll();
            visited.put(curr, true);

            // get neighbouring wires
            List<Position> neighbours = curr.getCardinallyAdjacentPositions();
            List<Wire> wires = new ArrayList<Wire>();
            for (Position neighbourPos : neighbours) {
                List<PlaceableEntity> entities = getEntities(neighbourPos);
                wires.addAll(entities.stream().filter(entity -> entity instanceof Wire).map(entity -> (Wire) entity)
                        .collect(Collectors.toList()));
            }

            for (Wire wire : wires) {
                if (!visited.containsKey(wire.getPosition())) {
                    q.add(wire.getPosition());
                    visited.put(wire.getPosition(), true);
                    wire.activate(game.getTick());
                    linkConnectedLogicals(wire, s);
                }
            }
        }
    }

    public void linkConnectedLogicals(PlaceableEntity p, Switch s) {
        if (!(p instanceof Conductor)) {
            return;
        }

        // check entities surrounding a conductor
        Position conductorPos = p.getPosition();
        List<Position> neighbours = conductorPos.getCardinallyAdjacentPositions();
        Conductor conductor = (Conductor) p;

        for (Position neighbourPos : neighbours) {
            List<PlaceableEntity> entities = getEntities(neighbourPos);
            List<LogicalObserver> logicalEntities = entities.stream()
                    .filter(entity -> entity instanceof LogicalObserver).map(entity -> (LogicalObserver) entity)
                    .collect(Collectors.toList());

            for (LogicalObserver curr : logicalEntities) {
                curr.addAdjacentConductor(conductor);
                s.addConnectedLogicalEntity(curr);
                checkDisconnectedWires(curr);
            }

        }
    }

    public void checkDisconnectedWires(LogicalObserver entity) {
        // check entities surrounding a logicalObeserver to look for wires not part of logical circuits
        List<Position> neighbours = ((PlaceableEntity) entity).getPosition().getCardinallyAdjacentPositions();
        for (Position n : neighbours) {
            List<PlaceableEntity> entities = getEntities(n);
            List<Wire> wires = entities.stream().filter(w -> w instanceof Wire).map(w -> (Wire) w)
                    .collect(Collectors.toList());

            wires.forEach(w -> entity.addAdjacentConductor(w));
        }
    }

    public void removeLogicalConnections() {
        List<PlaceableEntity> placeables = getEntities(PlaceableEntity.class);

        // clear logicalEntities attatched to switches
        List<Switch> switches = placeables.stream().filter(entity -> entity instanceof Switch)
                .map(entity -> (Switch) entity).collect(Collectors.toList());
        switches.forEach(c -> c.setConnectedLogicalEntities(new ArrayList<>()));

        // clear conductors attatched to logicalEntities
        List<LogicalObserver> logicalObservers = placeables.stream().filter(entity -> entity instanceof LogicalObserver)
                .map(entity -> (LogicalObserver) entity).collect(Collectors.toList());
        logicalObservers.forEach(observer -> {
            observer.setConnectedLogicalEntities(new ArrayList<>());
            observer.notify(game.getMap());
        });
    }

    public void removeNode(PlaceableEntity entity) {
        Position p = entity.getPosition();
        if (nodes.containsKey(p)) {
            nodes.get(p).removeEntity(entity);
            if (nodes.get(p).size() == 0) {
                nodes.remove(p);
            }
        }
    }

    public void destroyEntity(PlaceableEntity entity) {
        removeNode(entity);
        entity.onDestroy(this);
    }

    // used to destroy the snake head, as it will be instantly replaced
    public void destroyEntityNoResponse(PlaceableEntity entity) {
        removeNode(entity);
    }

    public void addEntity(PlaceableEntity entity) {
        addNode(new GraphNode(entity));
    }

    public void addNode(GraphNode node) {
        Position p = node.getPosition();

        if (!nodes.containsKey(p))
            nodes.put(p, node);
        else {
            GraphNode curr = nodes.get(p);
            curr.mergeNode(node);
            nodes.put(p, curr);
        }
    }

    public PlaceableEntity getEntity(String id) {
        PlaceableEntity res = null;
        for (Map.Entry<Position, GraphNode> entry : nodes.entrySet()) {
            List<PlaceableEntity> es = entry.getValue().getEntities().stream().filter(e -> e.getId().equals(id))
                    .collect(Collectors.toList());
            if (es != null && es.size() > 0) {
                res = es.get(0);
                break;
            }
        }
        return res;
    }

    public List<PlaceableEntity> getEntities(Position p) {
        GraphNode node = nodes.get(p);
        return (node != null) ? node.getEntities() : new ArrayList<>();
    }

    public List<PlaceableEntity> getEntities() {
        List<PlaceableEntity> entities = new ArrayList<>();
        nodes.forEach((k, v) -> entities.addAll(v.getEntities()));
        return entities;
    }

    public <T extends PlaceableEntity> List<T> getEntities(Class<T> type) {
        return getEntities().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
