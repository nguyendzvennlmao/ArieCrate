package me.aris.ariscrate.crate;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
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
                if (Bukkit.getWorld(world) == null) continue;
                
                double x = cSec.getDouble("x");
                double y = cSec.getDouble("y");
                double z = cSec.getDouble("z");
                Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                
                Crate crate = new Crate(name, loc);
                crate.setCrateConfig(crateConfigs.get(name.toLowerCase()));
                
                ConfigurationSection itemsSec = cSec.getConfigurationSection("items");
                if (itemsSec != null) {
                    for (String slotStr : itemsSec.getKeys(false)) {
                        try {
                            int slot = Integer.parseInt(slotStr);
                            ItemStack item = itemsSec.getItemStack(slotStr);
                            if (item != null) {
                                crate.setItem(slot, item);
                            }
                        } catch (NumberFormatException ignored) {}
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
            if (loc.getWorld() != null) {
                cfg.set(path + "world", loc.getWorld().getName());
                cfg.set(path + "x", loc.getX());
                cfg.set(path + "y", loc.getY());
                cfg.set(path + "z", loc.getZ());
            }
            
            for (Map.Entry<Integer, ItemStack> entry : crate.getItems().entrySet()) {
                cfg.set(path + "items." + entry.getKey(), entry.getValue());
            }
        }
        plugin.saveConfig();
    }
    
    public void saveCrateConfig(String crateName) {
        File crateFolder = new File(plugin.getDataFolder(), "crate");
        if (!crateFolder.exists()) {
            crateFolder.mkdirs();
        }
        
        File crateFile = new File(crateFolder, crateName.toLowerCase() + ".yml");
        FileConfiguration cfg = new YamlConfiguration();
        
        cfg.set("name", crateName);
        cfg.set("display-name", "&f" + crateName.substring(0, 1).toUpperCase() + crateName.substring(1).toLowerCase() + " Crate");
        cfg.set("key-name", "&f" + crateName.substring(0, 1).toUpperCase() + crateName.substring(1).toLowerCase() + " Key");
        cfg.set("permission", "ariscrate.crate." + crateName.toLowerCase());
        cfg.set("choose-line1", "&7With a {key} ᴋᴇʏ you can choose");
        cfg.set("choose-line2", "&7which of the {items} you want");
        
        try {
            cfg.save(crateFile);
            crateConfigs.put(crateName.toLowerCase(), cfg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createCrate(String name, Location loc) {
        saveCrateConfig(name);
        
        FileConfiguration cfg = crateConfigs.get(name.toLowerCase());
        Crate crate = new Crate(name, loc);
        crate.setCrateConfig(cfg);
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
    
    public boolean deleteCrateItem(String name, int slot) {
        Crate crate = crates.get(name.toLowerCase());
        if (crate != null && crate.getItems().containsKey(slot)) {
            crate.getItems().remove(slot);
            saveCrates();
            return true;
        }
        return false;
    }
    
    public boolean moveCrate(String name, Location newLoc) {
        Crate crate = crates.get(name.toLowerCase());
        if (crate != null) {
            crate.setLocation(newLoc);
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
