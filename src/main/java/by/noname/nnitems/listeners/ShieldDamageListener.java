package by.noname.nnitems.listeners;

import by.noname.nnitems.NNItems;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ShieldDamageListener implements Listener {
    private final NNItems plugin;

    public ShieldDamageListener(NNItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShieldDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        ArmorStand armorStand = (ArmorStand) event.getEntity();

        if (armorStand.getScoreboardTags().contains("defender_shield")) {
            if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                    event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onShieldExplode(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        ArmorStand armorStand = (ArmorStand) event.getEntity();

        if (armorStand.getScoreboardTags().contains("defender_shield")) {
            if (event.getDamager() instanceof EnderCrystal) {
                Player owner = findShieldOwner(armorStand);
                if (owner != null) {
                    plugin.getShieldManager().breakShield(owner);
                    int remaining = plugin.getShieldManager().getActiveShieldsCount(owner.getUniqueId());
                    String message = plugin.getConfigManager().getAbilityMessage("shield-break")
                            .replace("%count%", String.valueOf(remaining));
                    owner.sendMessage(message);
                }
                armorStand.remove();
                event.setCancelled(true);
            }
        }
    }

    private Player findShieldOwner(ArmorStand shield) {
        for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
            if (shield.getLocation().distance(player.getLocation()) < 10) {
                if (plugin.getShieldManager().hasActiveShields(player.getUniqueId())) {
                    return player;
                }
            }
        }
        return null;
    }
}