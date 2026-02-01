package by.noname.nnitems.manager;

import by.noname.nnitems.NNItems;
import by.noname.nnitems.utils.SoundUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityManager implements Listener {
    private final NNItems plugin;
    private final DataManager dataManager;
    private final Map<UUID, Integer> playerKills = new HashMap<>();
    private final Map<UUID, Boolean> helplessPlayers = new HashMap<>();

    public AbilityManager(NNItems plugin) {
        this.plugin = plugin;
        this.dataManager = new DataManager(plugin);
        loadPlayerKills();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void loadPlayerKills() {
        playerKills.putAll(dataManager.loadAllKills());
    }

    private void savePlayerKills() {
        dataManager.saveAllKills(playerKills);
    }

    public void activateAbility(Player player) {
        if (plugin.getCooldownManager().hasCooldown(player)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player);
            String message = plugin.getConfigManager().getAbilityMessage("on-cooldown")
                    .replace("%time%", String.valueOf(remaining));
            player.sendMessage(message);
            SoundUtils.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return;
        }

        plugin.getShieldManager().activateShields(player);
        plugin.getCooldownManager().setCooldown(player);

        player.sendMessage(plugin.getConfigManager().getAbilityMessage("ability-ready"));
        SoundUtils.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            incrementKills(killer);
            dataManager.savePlayerKills(killer.getUniqueId(), getPlayerKills(killer));

            SoundUtils.playSound(killer, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        }
    }

    private void incrementKills(Player player) {
        UUID playerId = player.getUniqueId();
        int kills = playerKills.getOrDefault(playerId, 0) + 1;
        playerKills.put(playerId, kills);

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && new by.noname.nnitems.items.DefenderSword(plugin).isDefenderSword(item)) {
            by.noname.nnitems.items.DefenderSword sword = new by.noname.nnitems.items.DefenderSword(plugin);
            sword.setKills(item, kills);
        }
    }

    public int getPlayerKills(Player player) {
        return playerKills.getOrDefault(player.getUniqueId(), 0);
    }

    public boolean isHelpless(Player player) {
        return helplessPlayers.getOrDefault(player.getUniqueId(), false);
    }

    public void makePlayerHelpless(Player player) {
        helplessPlayers.put(player.getUniqueId(), true);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,
                plugin.getConfigManager().getHelplessDuration() * 20, 10, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,
                plugin.getConfigManager().getHelplessDuration() * 20, 10, true, false, false));

        SoundUtils.playSound(player, Sound.ENTITY_WITHER_HURT, 1.0f, 0.5f);

        new BukkitRunnable() {
            int counter = plugin.getConfigManager().getHelplessDuration();

            @Override
            public void run() {
                if (!player.isOnline() || counter <= 0) {
                    helplessPlayers.remove(player.getUniqueId());
                    player.sendMessage(plugin.getConfigManager().getAbilityMessage("helpless-ended"));
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                    player.removePotionEffect(PotionEffectType.WEAKNESS);
                    SoundUtils.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
                    this.cancel();
                    return;
                }

                if (counter <= 3) {
                    SoundUtils.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.3f, 1.0f);
                }

                counter--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        by.noname.nnitems.items.DefenderSword sword = new by.noname.nnitems.items.DefenderSword(plugin);

        if (sword.isDefenderSword(item) && event.getAction().toString().contains("RIGHT")) {
            event.setCancelled(true);
            activateAbility(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerKills.putIfAbsent(player.getUniqueId(), dataManager.getPlayerKills(player.getUniqueId()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        dataManager.savePlayerKills(player.getUniqueId(), getPlayerKills(player));
    }

    public void onDisable() {
        savePlayerKills();
    }
}