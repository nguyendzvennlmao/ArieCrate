package me.aris.ariscrate.key;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class KeyManager {
    private final ArisCrate plugin;
    private final Map<UUID, Map<String, Integer>> data = new HashMap<>();
    private File file;
    private FileConfiguration cfg;

    public KeyManager(ArisCrate plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "keys.yml");
        if (!file.exists()) { try { file.createNewFile(); } catch (IOException ignored) {} }
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void load() {
        data.clear();
        for (String id : cfg.getKeys(false)) {
            UUID uuid = UUID.fromString(id);
            Map<String, Integer> map = new HashMap<>();
            ConfigurationSection s = cfg.getConfigurationSection(id);
            if (s != null) {
                for (String k : s.getKeys(false)) map.put(k.toLowerCase(), s.getInt(k));
            }
            data.put(uuid, map);
        }
    }

    public void save() {
        data.forEach((uuid, map) -> {
            map.forEach((k, v) -> cfg.set(uuid.toString() + "." + k, v));
        });
        try { cfg.save(file); } catch (IOException ignored) {}
    }

    public int getKeys(UUID uuid, String key) {
        return data.getOrDefault(uuid, new HashMap<>()).getOrDefault(key.toLowerCase(), 0);
    }

    public void addKeys(UUID uuid, String key, int amount) {
        Map<String, Integer> map = data.computeIfAbsent(uuid, k -> new HashMap<>());
        map.put(key.toLowerCase(), getKeys(uuid, key) + amount);
        save();
    }

    public boolean takeKeys(UUID uuid, String key, int amount) {
        int current = getKeys(uuid, key);
        if (current < amount) return false;
        data.get(uuid).put(key.toLowerCase(), current - amount);
        save();
        return true;
    }
  }
