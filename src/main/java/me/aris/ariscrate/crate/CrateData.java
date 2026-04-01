package me.aris.ariscrate.crate;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class CrateData {
    private final String name;
    private Location location;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    public CrateData(String name, Location location) { this.name = name; this.location = location; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public void setLocation(Location loc) { this.location = loc; }
    public Map<Integer, ItemStack> getItems() { return items; }
}
