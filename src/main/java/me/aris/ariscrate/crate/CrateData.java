package me.aris.ariscrate.crate;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class CrateData {
    private final String name;
    private Location location;
    private final List<ItemStack> items = new ArrayList<>();

    public CrateData(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() { return name; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public List<ItemStack> getItems() { return items; }
}
