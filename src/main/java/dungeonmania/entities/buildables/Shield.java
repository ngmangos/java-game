package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.InventoryItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class Shield extends Entity implements Buildable, InventoryItem, BattleItem {
    private int durability;
    private double defence;

    private static final List<Class<? extends InventoryItem>> INGREDIENTSA = Arrays.asList(Wood.class, Wood.class,
            Treasure.class);
    private static final List<Class<? extends InventoryItem>> INGREDIENTSB = Arrays.asList(Wood.class, Wood.class,
            Key.class);

    public Shield(int durability, double defence) {
        this.durability = durability;
        this.defence = defence;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.getPlayer().remove(this);
        }
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, defence, 1, 1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    public static boolean buildable(List<InventoryItem> items) {
        List<Class<? extends InventoryItem>> itemClasses = items.stream().map(item -> item.getClass())
                .collect(Collectors.toList());
        return itemClasses.containsAll(INGREDIENTSA) || itemClasses.containsAll(INGREDIENTSB);
    }

    public static List<Class<? extends InventoryItem>> ingredients(List<InventoryItem> items) {
        List<Class<? extends InventoryItem>> itemClasses = items.stream().map(item -> item.getClass())
                .collect(Collectors.toList());
        if (itemClasses.containsAll(INGREDIENTSA)) {
            return INGREDIENTSA;
        } else {
            return INGREDIENTSB;
        }
    }
}
