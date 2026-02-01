package by.noname.nnitems.manager;

import by.noname.nnitems.NNItems;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    private final NNItems plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(NNItems plugin) {
        this.plugin = plugin;
        setupDataFile();
    }

    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("playerdata.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void savePlayerKills(UUID playerId, int kills) {
        dataConfig.set("kills." + playerId.toString(), kills);
        saveData();
    }

    public int getPlayerKills(UUID playerId) {
        return dataConfig.getInt("kills." + playerId.toString(), 0);
    }

    public Map<UUID, Integer> loadAllKills() {
        Map<UUID, Integer> killsMap = new HashMap<>();

        if (dataConfig.contains("kills")) {
            for (String key : dataConfig.getConfigurationSection("kills").getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(key);
                    int kills = dataConfig.getInt("kills." + key);
                    killsMap.put(playerId, kills);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata.yml: " + key);
                }
            }
        }

        return killsMap;
    }

    public void saveAllKills(Map<UUID, Integer> killsMap) {
        for (Map.Entry<UUID, Integer> entry : killsMap.entrySet()) {
            dataConfig.set("kills." + entry.getKey().toString(), entry.getValue());
        }
        saveData();
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml: " + e.getMessage());
        }
    }

    public void reloadData() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
}