package me.aris.ariscrate.key;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.Collections;

public class KeyManager {
    private final ArisCrate plugin;
    private File file;
    private FileConfiguration config;

    public KeyManager(ArisCrate plugin) {
        this.plugin = plugin;
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "keys.yml");
        if (!file.exists()) {
            try { 
                file.getParentFile().mkdirs();
                file.createNewFile(); 
            } catch (IOException e) {}
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void addKeys(UUID uuid, String type, int amount) {
        int current = getKeys(uuid, type);
        config.set(uuid.toString() + "." + type, current + amount);
        save();
    }

    public boolean takeKeys(UUID uuid, String type, int amount) {
        int current = getKeys(uuid, type);
        if (current < amount) return false;
        config.set(uuid.toString() + "." + type, current - amount);
        save();
        return true;
    }

    public int getKeys(UUID uuid, String type) {
        return config.getInt(uuid.toString() + "." + type, 0);
    }

    public void create(String name, Location loc) {
        config.set("locations." + name + ".world", loc.getWorld().getName());
        config.set("locations." + name + ".x", loc.getX());
        config.set("locations." + name + ".y", loc.getY());
        config.set("locations." + name + ".z", loc.getZ());
        save();
    }

    public void delete(String name) {
        config.set("locations." + name, null);
        save();
    }

    public Object get(String path) {
        return config.get(path);
    }

    public Set<String> getNames() {
        if (config.getConfigurationSection("locations") == null) return Collections.emptySet();
        return config.getConfigurationSection("locations").getKeys(false);
    }

    public void save() {
        try { 
            config.save(file); 
        } catch (IOException e) {}
    }
                   }
