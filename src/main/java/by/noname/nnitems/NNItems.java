package by.noname.nnitems;

import by.noname.nnitems.commands.CommandManager;
import by.noname.nnitems.config.ConfigManager;
import by.noname.nnitems.listeners.GUIListener;
import by.noname.nnitems.listeners.PlayerDamageListener;
import by.noname.nnitems.listeners.ShieldDamageListener;
import by.noname.nnitems.manager.AbilityManager;
import by.noname.nnitems.manager.CooldownManager;
import by.noname.nnitems.manager.ShieldManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NNItems extends JavaPlugin {
    private static NNItems instance;
    private ConfigManager configManager;
    private ShieldManager shieldManager;
    private CooldownManager cooldownManager;
    private AbilityManager abilityManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        shieldManager = new ShieldManager(this);
        cooldownManager = new CooldownManager(this);
        abilityManager = new AbilityManager(this);

        new CommandManager(this);

        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new ShieldDamageListener(this), this);

        shieldManager.runShieldRotation();

        getLogger().info("NNItems успешно запущен!");
    }

    @Override
    public void onDisable() {
        shieldManager.removeAllShields();
        abilityManager.onDisable();
        getLogger().info("NNItems отключен!");
    }

    public static NNItems getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ShieldManager getShieldManager() {
        return shieldManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }
}