package by.noname.nnitems.listeners;

import by.noname.nnitems.NNItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerDamageListener implements Listener {
    private final NNItems plugin;

    public PlayerDamageListener(NNItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (plugin.getShieldManager().hasActiveShields(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getAbilityMessage("damage-blocked"));
        }

        if (plugin.getAbilityManager().isHelpless(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();

        if (plugin.getAbilityManager().isHelpless(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (plugin.getAbilityManager().isHelpless(player)) {
            if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getZ() != event.getTo().getZ() ||
                    event.getFrom().getY() != event.getTo().getY()) {
                event.setTo(event.getFrom());
            }
        }
    }
}