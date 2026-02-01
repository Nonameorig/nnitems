package by.noname.nnitems.items;

import by.noname.nnitems.NNItems;
import by.noname.nnitems.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class DefenderSword {
    private final NNItems plugin;
    private final NamespacedKey swordKey;
    private final NamespacedKey killsKey;

    public DefenderSword(NNItems plugin) {
        this.plugin = plugin;
        this.swordKey = new NamespacedKey(plugin, "defender_sword");
        this.killsKey = new NamespacedKey(plugin, "player_kills");
    }

    public ItemStack createSwordItem() {
        List<String> lore = (List<String>) plugin.getConfigManager().getGUISetting("sword-lore");

        return new ItemBuilder(Material.DIAMOND_SWORD)
                .setName(plugin.getConfigManager().getGUISetting("sword-name").toString())
                .setLore(lore)
                .setCustomModelData(1001)
                .addPersistentData(swordKey, PersistentDataType.BOOLEAN, true)
                .addPersistentData(killsKey, PersistentDataType.INTEGER, 0)
                .setUnbreakable(true)
                .build();
    }

    public boolean isDefenderSword(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(swordKey);
    }

    public int getKills(ItemStack item) {
        if (!isDefenderSword(item)) return 0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(killsKey, PersistentDataType.INTEGER, 0);
    }

    public void setKills(ItemStack item, int kills) {
        if (!isDefenderSword(item)) return;

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(killsKey, PersistentDataType.INTEGER, kills);
        item.setItemMeta(meta);
    }

    public NamespacedKey getSwordKey() {
        return swordKey;
    }

    public NamespacedKey getKillsKey() {
        return killsKey;
    }
}