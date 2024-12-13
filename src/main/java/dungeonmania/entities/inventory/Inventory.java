package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.Sword;

public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables() {
        List<String> result = new ArrayList<String>();
        if (Bow.buildable(items)) {
            result.add("bow");
        }
        if (Shield.buildable(items)) {
            result.add("shield");
        }
        return result;
    }

    public InventoryItem checkBuildCriteria(Player p, boolean remove, boolean forceShield, EntityFactory factory) {
        if (Bow.buildable(items) && !forceShield) {
            if (remove) {
                List<Class<? extends InventoryItem>> itemClasses = Bow.ingredients();
                itemClasses.stream().forEach(itemClass -> items.remove(getFirstInstance(itemClass)));
            }
            return factory.buildBow();
        } else if (Shield.buildable(items)) {
            if (remove) {
                List<Class<? extends InventoryItem>> itemClasses = Shield.ingredients(items);
                itemClasses.stream().forEach(itemClass -> items.remove(getFirstInstance(itemClass)));

            }
            return factory.buildShield();
        }
        return null;
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int getFirstInstance(Class<T> itemType) {
        int i = 0;
        for (InventoryItem item : items) {
            if (itemType.isInstance(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId))
                return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

}
