package me.aris.ariscrate.crate;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CrateManager {
    private final ArisCrate plugin;
    private final Map<String, Crate> crates = new HashMap<>();
    private final Map<String, FileConfiguration> crateConfigs = new HashMap<>();

    public CrateManager(ArisCrate plugin) {
        this.plugin = plugin;
    }

    public void loadCrates() {
        crates.clear();
        crateConfigs.clear();
        
        File crateFolder = new File(plugin.getDataFolder(), "crate");
        if (!crateFolder.exists()) {
            crateFolder.mkdirs();
            plugin.saveResource("crate/common.yml", false);
            plugin.saveResource("crate/crimson.yml", false);
        }

        File[] crateFiles = crateFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (crateFiles == null) return;

        for (File file : crateFiles) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            String name = cfg.getString("name");
            if (name == null) continue;
            
            crateConfigs.put(name.toLowerCase(), cfg);
        }

        FileConfiguration mainCfg = plugin.getConfig();
        ConfigurationSection section = mainCfg.getConfigurationSection("crates");
        if (section != null) {
            for (String name : section.getKeys(false)) {
                ConfigurationSection cSec = section.getConfigurationSection(name);
                if (cSec == null) continue;

                String world = cSec.getString("world");
                double x = cSec.getDouble("x");
                double y = cSec.getDouble("y");
                double z = cSec.getDouble("z");
                Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                
                Crate crate = new Crate(name, loc);
                crate.setCrateConfig(crateConfigs.get(name.toLowerCase()));
                
                ConfigurationSection itemsSec = cSec.getConfigurationSection("items");
                if (itemsSec != null) {
                    for (String slotStr : itemsSec.getKeys(false)) {
                        int slot = Integer.parseInt(slotStr);
                        crate.setItem(slot, itemsSec.getItemStack(slotStr));
                    }
                }
                crates.put(name.toLowerCase(), crate);
            }
        }
    }

    public void saveCrates() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("crates", null);
        
        for (Crate crate : crates.values()) {
            String path = "crates." + crate.getName() + ".";
            Location loc = crate.getLocation();
            cfg.set(path + "world", loc.getWorld().getName());
            cfg.set(path + "x", loc.getX());
            cfg.set(path + "y", loc.getY());
            cfg.set(path + "z", loc.getZ());
            
            for (Map.Entry<Integer, ItemStack> entry : crate.getItems().entrySet()) {
                cfg.set(path + "items." + entry.getKey(), entry.getValue());
            }
        }
        plugin.saveConfig();
    }

    public void createCrate(String name, Location loc) {
        Crate crate = new Crate(name, loc);
        crate.setCrateConfig(crateConfigs.get(name.toLowerCase()));
        crates.put(name.toLowerCase(), crate);
        saveCrates();
    }

    public boolean deleteCrate(String name) {
        Crate removed = crates.remove(name.toLowerCase());
        if (removed != null) {
            saveCrates();
            return true;
        }
        return false;
    }

    public boolean crateExists(String name) {
        return crates.containsKey(name.toLowerCase());
    }

    public Crate getCrate(String name) {
        return crates.get(name.toLowerCase());
    }

    public FileConfiguration getCrateConfig(String name) {
        return crateConfigs.get(name.toLowerCase());
    }

    public Set<String> getCrateNames() {
        return crates.keySet();
    }

    public Collection<Crate> getAllCrates() {
        return crates.values();
    }
                  }
