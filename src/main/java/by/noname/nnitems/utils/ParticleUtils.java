package by.noname.nnitems.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleUtils {

    public static void spawnParticle(Player player, Particle particle, Location location, int count) {
        player.spawnParticle(particle, location, count);
    }

    public static void spawnParticle(World world, Particle particle, Location location, int count) {
        world.spawnParticle(particle, location, count);
    }

    public static void spawnParticle(World world, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
    }

    public static void spawnColoredParticle(Location location, Color color, int count) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
        location.getWorld().spawnParticle(Particle.DUST, location, count, 0.5, 0.5, 0.5, 0, dustOptions);
    }

    public static void spawnCircle(Location center, Particle particle, double radius, int points, double height) {
        World world = center.getWorld();
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location point = center.clone().add(x, height, z);
            world.spawnParticle(particle, point, 1);
        }
    }

    public static void spawnSphere(Location center, Particle particle, double radius, int points) {
        World world = center.getWorld();
        for (int i = 0; i < points; i++) {
            double phi = Math.acos(1 - 2 * Math.random());
            double theta = 2 * Math.PI * Math.random();

            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi);
            double z = radius * Math.sin(phi) * Math.sin(theta);

            Location point = center.clone().add(x, y, z);
            world.spawnParticle(particle, point, 1);
        }
    }

    public static void spawnLine(Location start, Location end, Particle particle, double spacing) {
        World world = start.getWorld();
        Vector direction = end.clone().subtract(start).toVector();
        double distance = direction.length();
        direction.normalize();

        for (double d = 0; d < distance; d += spacing) {
            Location point = start.clone().add(direction.clone().multiply(d));
            world.spawnParticle(particle, point, 1);
        }
    }

    public static void spawnHelix(Location center, Particle particle, double radius, double height, int rotations, int pointsPerRotation) {
        World world = center.getWorld();
        int totalPoints = rotations * pointsPerRotation;

        for (int i = 0; i < totalPoints; i++) {
            double progress = (double) i / totalPoints;
            double angle = 2 * Math.PI * rotations * progress;
            double currentRadius = radius;
            double currentHeight = height * progress;

            double x = Math.cos(angle) * currentRadius;
            double z = Math.sin(angle) * currentRadius;

            Location point = center.clone().add(x, currentHeight, z);
            world.spawnParticle(particle, point, 1);
        }
    }
}