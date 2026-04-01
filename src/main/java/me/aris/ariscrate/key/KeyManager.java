package me.aris.ariscrate.key;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.CrateData;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class KeyManager {
    private final ArisCrate plugin;
    private File file;
    private FileConfiguration config;
    private final Map<String, CrateData> crateCache = new HashMap<>();

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

    public void give(Player player, String type, int amount, boolean silent) {
        addKeys(player.getUniqueId(), type, amount);
        if (!silent) {
            player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.receive-key")), 1, 1);
            plugin.sendNotify(player, "receive-give", "%key%", type, "%amount%", String.valueOf(amount));
        }
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
        crateCache.put(name, new CrateData(name, loc));
    }

    public void delete(String name) {
        config.set("locations." + name, null);
        save();
        crateCache.remove(name);
    }

    public CrateData get(String name) {
        if (crateCache.containsKey(name)) return crateCache.get(name);
        if (config.contains("locations." + name)) {
            String world = config.getString("locations." + name + ".world");
            double x = config.getDouble("locations." + name + ".x");
            double y = config.getDouble("locations." + name + ".y");
            double z = config.getDouble("locations." + name + ".z");
            CrateData data = new CrateData(name, new Location(Bukkit.getWorld(world), x, y, z));
            crateCache.put(name, data);
            return data;
        }
        return null;
    }

    public Set<String> getNames() {
        if (config.getConfigurationSection("locations") == null) return Collections.emptySet();
        return config.getConfigurationSection("locations").getKeys(false);
    }

    public void save() {
        try { config.save(file); } catch (IOException e) {}
    }
    }
