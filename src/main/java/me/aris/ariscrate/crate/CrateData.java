package me.aris.ariscrate.crate;

import org.bukkit.Location;

public class CrateData {
    private final String name;
    private final Location location;

    public CrateData(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() { return name; }
    public Location getLocation() { return location; }
}
