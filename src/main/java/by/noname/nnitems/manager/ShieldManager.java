package by.noname.nnitems.manager;

import by.noname.nnitems.NNItems;
import by.noname.nnitems.items.DefenderShield;
import by.noname.nnitems.utils.ParticleUtils;
import by.noname.nnitems.utils.SoundUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class ShieldManager {
    private final NNItems plugin;
    private final Map<UUID, List<ArmorStand>> playerShields = new HashMap<>();
    private final Map<UUID, Integer> shieldDurations = new HashMap<>();
    private final Map<UUID, BukkitTask> shieldTasks = new HashMap<>();
    private final Map<UUID, BukkitTask> particleTasks = new HashMap<>();
    private final Map<UUID, Integer> activeShieldsCount = new HashMap<>();
    private final DefenderShield defenderShield;

    public ShieldManager(NNItems plugin) {
        this.plugin = plugin;
        this.defenderShield = new DefenderShield(plugin);
    }

    public void activateShields(Player player) {
        UUID playerId = player.getUniqueId();

        if (playerShields.containsKey(playerId)) {
            removeShields(playerId);
        }

        List<ArmorStand> shields = new ArrayList<>();
        int shieldCount = plugin.getConfigManager().getShieldCount();

        for (int i = 0; i < shieldCount; i++) {
            ArmorStand shield = defenderShield.createShield(player, i, shieldCount);
            shields.add(shield);
        }

        playerShields.put(playerId, shields);
        activeShieldsCount.put(playerId, shieldCount);
        shieldDurations.put(playerId, plugin.getConfigManager().getShieldDuration());

        SoundUtils.playSound(player, Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1.0f, 0.5f);
        spawnActivationParticles(player);

        BukkitTask durationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    removeShields(playerId);
                    return;
                }

                Integer duration = shieldDurations.get(playerId);
                if (duration == null || duration <= 0) {
                    removeShields(playerId);
                    SoundUtils.playSound(player, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
                    return;
                }

                shieldDurations.put(playerId, duration - 1);

                if (duration <= 3) {
                    SoundUtils.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL.value(), 0.5f, 1.0f);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        shieldTasks.put(playerId, durationTask);

        BukkitTask particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerShields.containsKey(playerId)) {
                    this.cancel();
                    return;
                }

                spawnShieldParticles(player);
            }
        }.runTaskTimer(plugin, 0L, 5L);

        particleTasks.put(playerId, particleTask);
    }

    public void breakShield(Player player) {
        UUID playerId = player.getUniqueId();
        List<ArmorStand> shields = playerShields.get(playerId);

        if (shields == null || shields.isEmpty()) return;

        if (!shields.isEmpty()) {
            ArmorStand shield = shields.remove(0);

            Location loc = shield.getLocation();
            SoundUtils.playSound(loc, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);
            spawnBreakParticles(loc);

            shield.remove();
        }

        int remaining = shields.size();
        activeShieldsCount.put(playerId, remaining);

        if (remaining <= 0) {
            SoundUtils.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.0f);
            plugin.getAbilityManager().makePlayerHelpless(player);
            removeShields(playerId);
            player.sendMessage(plugin.getConfigManager().getAbilityMessage("all-shields-broken"));
        } else {
            SoundUtils.playSound(player, Sound.BLOCK_ANVIL_LAND, 0.5f, 1.0f);
            String message = plugin.getConfigManager().getAbilityMessage("shield-break")
                    .replace("%count%", String.valueOf(remaining));
            player.sendMessage(message);
        }
    }

    private void spawnActivationParticles(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();

        ParticleUtils.spawnCircle(loc, Particle.FIREWORK, 2.0, 36, 1.5);
        ParticleUtils.spawnCircle(loc, Particle.GLOW, 2.5, 24, 2.0);

        for (int i = 0; i < 20; i++) {
            double angle = 2 * Math.PI * i / 20;
            double x = Math.cos(angle) * 2;
            double z = Math.sin(angle) * 2;

            Location particleLoc = loc.clone().add(x, 0.5, z);
            Vector direction = loc.clone().add(0, 2, 0).subtract(particleLoc).toVector().normalize().multiply(0.2);

            world.spawnParticle(Particle.SPELL_MOB, particleLoc, 0,
                    direction.getX(), direction.getY(), direction.getZ());
        }
    }

    private void spawnShieldParticles(Player player) {
        if (!playerShields.containsKey(player.getUniqueId())) return;

        List<ArmorStand> shields = playerShields.get(player.getUniqueId());
        if (shields == null || shields.isEmpty()) return;

        World world = player.getWorld();
        Color color = getGlowColor();

        for (ArmorStand shield : shields) {
            if (shield.isDead()) continue;

            Location loc = shield.getLocation().add(0, 0.5, 0);

            ParticleUtils.spawnColoredParticle(loc, color, 2);

            if (Math.random() < 0.3) {
                world.spawnParticle(Particle.GLOW, loc, 1, 0.1, 0.1, 0.1, 0);
            }
        }

        Location playerLoc = player.getLocation().add(0, 0.5, 0);
        ParticleUtils.spawnCircle(playerLoc, Particle.ENCHANT,
                plugin.getConfigManager().getShieldRadius() + 0.5, 12, 0.5);
    }

    private void spawnBreakParticles(Location location) {
        World world = location.getWorld();

        ParticleUtils.spawnColoredParticle(location, Color.RED, 30);

        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10;
            double x = Math.cos(angle);
            double z = Math.sin(angle);

            Location particleLoc = location.clone().add(0, 0.5, 0);
            Vector direction = new Vector(x, 0.5, z).normalize().multiply(0.3);

            world.spawnParticle(Particle.CRIT, particleLoc, 0,
                    direction.getX(), direction.getY(), direction.getZ(), 0.5);
        }
    }

    private Color getGlowColor() {
        String colorStr = plugin.getConfigManager().getGlowColor();
        switch (colorStr.toUpperCase()) {
            case "RED":
                return Color.RED;
            case "BLUE":
                return Color.BLUE;
            case "GREEN":
                return Color.GREEN;
            case "YELLOW":
                return Color.YELLOW;
            case "PURPLE":
                return Color.PURPLE;
            case "AQUA":
                return Color.AQUA;
            default:
                return Color.YELLOW;
        }
    }

    public boolean hasActiveShields(UUID playerId) {
        Integer count = activeShieldsCount.get(playerId);
        return count != null && count > 0;
    }

    public int getActiveShieldsCount(UUID playerId) {
        return activeShieldsCount.getOrDefault(playerId, 0);
    }

    private void updateShieldRotation(UUID playerId) {
        List<ArmorStand> shields = playerShields.get(playerId);
        if (shields == null || shields.isEmpty()) return;

        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;

        double radius = plugin.getConfigManager().getShieldRadius();
        double speed = plugin.getConfigManager().getRotationSpeed();
        long time = System.currentTimeMillis();

        for (int i = 0; i < shields.size(); i++) {
            ArmorStand shield = shields.get(i);
            if (shield.isDead()) continue;

            double angle = Math.toRadians((time / 50.0) * speed + (360.0 / shields.size() * i));
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            shield.teleport(player.getLocation().add(x, 1.5, z));
            shield.setRotation((float) Math.toDegrees(-angle), 0);
        }
    }

    public void runShieldRotation() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : playerShields.keySet()) {
                    updateShieldRotation(playerId);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void removeShields(UUID playerId) {
        List<ArmorStand> shields = playerShields.remove(playerId);
        if (shields != null) {
            shields.forEach(ArmorStand::remove);
        }

        BukkitTask task = shieldTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }

        BukkitTask particleTask = particleTasks.remove(playerId);
        if (particleTask != null) {
            particleTask.cancel();
        }

        shieldDurations.remove(playerId);
        activeShieldsCount.remove(playerId);
    }

    public void removeAllShields() {
        for (UUID playerId : new HashSet<>(playerShields.keySet())) {
            removeShields(playerId);
        }
    }
}