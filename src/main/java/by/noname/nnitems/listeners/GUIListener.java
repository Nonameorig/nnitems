package by.noname.nnitems.listeners;

import by.noname.nnitems.NNItems;
import by.noname.nnitems.items.DefenderSword;
import by.noname.nnitems.utils.SoundUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
    private final NNItems plugin;
    private final DefenderSword defenderSword;

    public GUIListener(NNItems plugin) {
        this.plugin = plugin;
        this.defenderSword = new DefenderSword(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = plugin.getConfigManager().getGUISetting("title").toString().replace("&", "§");

        if (event.getView().getTitle().equals(title)) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (defenderSword.isDefenderSword(event.getCurrentItem())) {
                int kills = plugin.getAbilityManager().getPlayerKills(player);
                int required = plugin.getConfigManager().getRequiredKills();

                if (kills >= required) {
                    player.getInventory().addItem(event.getCurrentItem());
                    player.sendMessage(plugin.getConfigManager().getMessage("sword-given"));
                    SoundUtils.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                    SoundUtils.playSound(player, Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1.0f, 1.0f);
                    player.closeInventory();
                } else {
                    player.sendMessage(plugin.getConfigManager().getGUISetting("kill-requirement").toString()
                            .replace("%kills%", String.valueOf(required - kills)));
                    SoundUtils.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = plugin.getConfigManager().getGUISetting("title").toString().replace("&", "§");

        if (event.getView().getTitle().equals(title)) {
            SoundUtils.playSound((Player) event.getPlayer(), Sound.BLOCK_CHEST_CLOSE, 1.0f, 1.0f);
        }
    }
}