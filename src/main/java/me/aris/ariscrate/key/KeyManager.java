package me.aris.ariscrate.key;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyManager {
    private final ArisCrate plugin;
    private final Map<UUID, Map<String, Integer>> playerKeys = new HashMap<>();
    private File file;
    private FileConfiguration data;

    public KeyManager(ArisCrate plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "keys.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public void loadKeys() {
        playerKeys.clear();
        for (String uuidStr : data.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            Map<String, Integer> map = new HashMap<>();
            for (String key : data.getConfigurationSection(uuidStr).getKeys(false)) {
                int amount = data.getInt(uuidStr + "." + key);
                map.put(key.toLowerCase(), amount);
            }
            playerKeys.put(uuid, map);
        }
    }

    public void saveKeys() {
        for (Map.Entry<UUID, Map<String, Integer>> entry : playerKeys.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, Integer> keys : entry.getValue().entrySet()) {
                data.set(uuidStr + "." + keys.getKey().toLowerCase(), keys.getValue());
            }
        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getKeys(UUID uuid, String key) {
        return playerKeys.getOrDefault(uuid, new HashMap<>()).getOrDefault(key.toLowerCase(), 0);
    }

    public void addKeys(UUID uuid, String key, int amount) {
        Map<String, Integer> map = playerKeys.computeIfAbsent(uuid, u -> new HashMap<>());
        map.put(key.toLowerCase(), getKeys(uuid, key) + amount);
        saveKeys();
    }

    public boolean removeKey(UUID uuid, String key) {
        int current = getKeys(uuid, key);
        if (current <= 0) return false;
        Map<String, Integer> map = playerKeys.get(uuid);
        map.put(key.toLowerCase(), current - 1);
        saveKeys();
        return true;
    }

    public boolean takeKeys(UUID uuid, String key, int amount) {
        int current = getKeys(uuid, key);
        if (current < amount) return false;
        Map<String, Integer> map = playerKeys.get(uuid);
        map.put(key.toLowerCase(), current - amount);
        saveKeys();
        return true;
    }
          }
