package me.aris.ariscrate.crate;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class CrateManager {
    private final ArisCrate plugin;
    private final Map<String, CrateData> crates = new HashMap<>();

    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    public void load() {
        crates.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("crates");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            Location loc = new Location(Bukkit.getWorld(sec.getString(key + ".world")), sec.getDouble(key + ".x"), sec.getDouble(key + ".y"), sec.getDouble(key + ".z"));
            CrateData data = new CrateData(key, loc);
            ConfigurationSection itemSec = sec.getConfigurationSection(key + ".items");
            if (itemSec != null) {
                for (String slot : itemSec.getKeys(false)) {
                    data.getItems().put(Integer.parseInt(slot), itemSec.getItemStack(slot));
                }
            }
            crates.put(key.toLowerCase(), data);
        }
    }

    public void save() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("crates", null);
        for (CrateData c : crates.values()) {
            String p = "crates." + c.getName() + ".";
            cfg.set(p + "world", c.getLocation().getWorld().getName());
            cfg.set(p + "x", c.getLocation().getX());
            cfg.set(p + "y", c.getLocation().getY());
            cfg.set(p + "z", c.getLocation().getZ());
            c.getItems().forEach((slot, item) -> cfg.set(p + "items." + slot, item));
        }
        plugin.saveConfig();
    }

    public void create(String name, Location loc) {
        crates.put(name.toLowerCase(), new CrateData(name, loc));
        save();
    }

    public void delete(String name) {
        crates.remove(name.toLowerCase());
        save();
    }

    public CrateData get(String name) { return crates.get(name.toLowerCase()); }
    public Set<String> getNames() { return crates.keySet(); }
              }
