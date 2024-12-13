package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.InventoryItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

public class Bow extends Entity implements Buildable, InventoryItem, BattleItem {
    private int durability;
    private static final List<Class<? extends InventoryItem>> INGREDIENTS = Arrays.asList(Arrow.class, Arrow.class,
            Arrow.class, Wood.class);

    public Bow(int durability) {
        this.durability = durability;
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
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, 0, 2, 1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    public static boolean buildable(List<InventoryItem> items) {
        List<Class<? extends InventoryItem>> itemClasses = items.stream().map(item -> item.getClass())
                .collect(Collectors.toList());
        return itemClasses.containsAll(INGREDIENTS);
    }

    public static List<Class<? extends InventoryItem>> ingredients() {
        return INGREDIENTS;
    }
}
