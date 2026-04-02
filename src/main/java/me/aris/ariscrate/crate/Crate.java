package me.aris.ariscrate.crate;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Crate {
    private final String name;
    private Location location;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private FileConfiguration crateConfig;

    public Crate(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public void setItem(int slot, ItemStack item) {
        items.put(slot, item);
    }

    public FileConfiguration getCrateConfig() {
        return crateConfig;
    }

    public void setCrateConfig(FileConfiguration crateConfig) {
        this.crateConfig = crateConfig;
    }
}
