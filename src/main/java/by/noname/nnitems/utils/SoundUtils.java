package by.noname.nnitems.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, volume, pitch);
    }

    public static void playSound(Player player, String sound, float volume, float pitch) {
        try {
            Sound s = Sound.valueOf(sound);
            player.playSound(player.getLocation(), s, SoundCategory.PLAYERS, volume, pitch);
        } catch (IllegalArgumentException e) {
            player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(sound), SoundCategory.PLAYERS, volume, pitch);
        }
    }

    public static void playSound(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, SoundCategory.PLAYERS, volume, pitch);
    }

    public static void playSound(World world, Location location, Sound sound, float volume, float pitch) {
        world.playSound(location, sound, SoundCategory.PLAYERS, volume, pitch);
    }

    public static void playSoundToAll(Location location, Sound sound, float volume, float pitch, double radius) {
        location.getWorld().getNearbyEntities(location, radius, radius, radius).forEach(entity -> {
            if (entity instanceof Player) {
                ((Player) entity).playSound(location, sound, SoundCategory.PLAYERS, volume, pitch);
            }
        });
    }
}