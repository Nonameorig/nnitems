package by.noname.nnitems.commands;

import by.noname.nnitems.NNItems;
import by.noname.nnitems.items.DefenderSword;
import by.noname.nnitems.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GetDefenderSwordCommand {
    private final NNItems plugin;
    private final DefenderSword defenderSword;

    public GetDefenderSwordCommand(NNItems plugin) {
        this.plugin = plugin;
        this.defenderSword = new DefenderSword(plugin);
    }

    public void execute(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27,
                plugin.getConfigManager().getGUISetting("title").toString());

        for (int i = 0; i < 27; i++) {
            ItemStack border = new ItemBuilder(Material.valueOf(
                    plugin.getConfigManager().getGUISetting("border-item").toString()))
                    .setName(plugin.getConfigManager().getGUISetting("border-name").toString())
                    .build();
            gui.setItem(i, border);
        }

        ItemStack swordItem = defenderSword.createSwordItem();
        gui.setItem(13, swordItem);

        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
    }
}