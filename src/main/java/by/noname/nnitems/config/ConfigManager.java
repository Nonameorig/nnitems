package by.noname.nnitems.config;

import by.noname.nnitems.NNItems;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final NNItems plugin;
    private FileConfiguration config;

    private int cooldown;
    private int shieldDuration;
    private int helplessDuration;
    private int requiredKills;
    private int shieldCount;
    private double shieldRadius;
    private double rotationSpeed;
    private String glowColor;

    private Map<String, String> messages = new HashMap<>();
    private Map<String, String> abilityMessages = new HashMap<>();
    private Map<String, Object> guiSettings = new HashMap<>();

    public ConfigManager(NNItems plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        cooldown = config.getInt("defender-sword.cooldown", 120);
        shieldDuration = config.getInt("defender-sword.shield-duration", 15);
        helplessDuration = config.getInt("defender-sword.helpless-duration", 3);
        requiredKills = config.getInt("defender-sword.required-kills", 5);
        shieldCount = config.getInt("defender-sword.shield-count", 6);
        shieldRadius = config.getDouble("defender-sword.shield-radius", 2.0);
        rotationSpeed = config.getDouble("defender-sword.rotation-speed", 5.0);
        glowColor = config.getString("defender-sword.glow-color", "YELLOW");

        abilityMessages.put("damage-blocked", config.getString("abilities.damage-blocked-message"));
        abilityMessages.put("shield-break", config.getString("abilities.shield-break-message"));
        abilityMessages.put("all-shields-broken", config.getString("abilities.all-shields-broken"));
        abilityMessages.put("helpless-ended", config.getString("abilities.helpless-ended"));
        abilityMessages.put("ability-ready", config.getString("abilities.ability-ready"));
        abilityMessages.put("on-cooldown", config.getString("abilities.on-cooldown"));

        messages.put("no-permission", config.getString("messages.no-permission"));
        messages.put("player-only", config.getString("messages.player-only"));
        messages.put("sword-given", config.getString("messages.sword-given"));

        guiSettings.put("title", config.getString("gui.title"));
        guiSettings.put("sword-name", config.getString("gui.sword-name"));
        guiSettings.put("sword-lore", config.getStringList("gui.sword-lore"));
        guiSettings.put("border-item", config.getString("gui.border-item"));
        guiSettings.put("border-name", config.getString("gui.border-name"));
        guiSettings.put("kill-requirement", config.getString("gui.kill-requirement"));
        guiSettings.put("ready-to-take", config.getString("gui.ready-to-take"));
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getShieldDuration() {
        return shieldDuration;
    }

    public int getHelplessDuration() {
        return helplessDuration;
    }

    public int getRequiredKills() {
        return requiredKills;
    }

    public int getShieldCount() {
        return shieldCount;
    }

    public double getShieldRadius() {
        return shieldRadius;
    }

    public double getRotationSpeed() {
        return rotationSpeed;
    }

    public String getGlowColor() {
        return glowColor;
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "");
    }

    public String getAbilityMessage(String key) {
        return abilityMessages.getOrDefault(key, "");
    }

    public Object getGUISetting(String key) {
        return guiSettings.get(key);
    }
}