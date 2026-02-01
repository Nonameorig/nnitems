package by.noname.nnitems.manager;

import by.noname.nnitems.NNItems;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final NNItems plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public CooldownManager(NNItems plugin) {
        this.plugin = plugin;
    }

    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public boolean hasCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;

        long cooldownTime = cooldowns.get(player.getUniqueId());
        int cooldownSeconds = plugin.getConfigManager().getCooldown();
        return (System.currentTimeMillis() - cooldownTime) < (cooldownSeconds * 1000L);
    }

    public long getRemainingCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) return 0;

        long cooldownTime = cooldowns.get(player.getUniqueId());
        int cooldownSeconds = plugin.getConfigManager().getCooldown();
        long elapsed = System.currentTimeMillis() - cooldownTime;
        long remaining = (cooldownSeconds * 1000L) - elapsed;

        return Math.max(0, remaining / 1000);
    }

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}