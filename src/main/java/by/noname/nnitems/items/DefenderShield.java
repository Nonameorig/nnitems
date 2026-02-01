package by.noname.nnitems.items;

import by.noname.nnitems.NNItems;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class DefenderShield {
    private final NNItems plugin;

    public DefenderShield(NNItems plugin) {
        this.plugin = plugin;
    }

    public ArmorStand createShield(Player owner, int index, int total) {
        ArmorStand shield = owner.getWorld().spawn(owner.getLocation(), ArmorStand.class);

        shield.setVisible(false);
        shield.setGravity(false);
        shield.setInvulnerable(true);
        shield.setCollidable(false);
        shield.setCanPickupItems(false);
        shield.setMarker(true);
        shield.setSmall(true);
        shield.addScoreboardTag("defender_shield");
        shield.setCustomName("§eЩит " + (index + 1));
        shield.setCustomNameVisible(true);

        ItemStack shieldItem = new ItemStack(Material.SHIELD);
        shield.getEquipment().setItemInMainHand(shieldItem);

        ItemStack helmet = createGlowingHelmet();
        shield.getEquipment().setHelmet(helmet);

        return shield;
    }

    private ItemStack createGlowingHelmet() {
        ItemStack helmet = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();

        Color color = Color.fromRGB(255, 255, 0);
        if (plugin.getConfigManager().getGlowColor().equalsIgnoreCase("RED")) {
            color = Color.fromRGB(255, 0, 0);
        } else if (plugin.getConfigManager().getGlowColor().equalsIgnoreCase("BLUE")) {
            color = Color.fromRGB(0, 0, 255);
        } else if (plugin.getConfigManager().getGlowColor().equalsIgnoreCase("GREEN")) {
            color = Color.fromRGB(0, 255, 0);
        }

        meta.setColor(color);
        meta.setUnbreakable(true);
        helmet.setItemMeta(meta);

        return helmet;
    }
}