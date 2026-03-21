package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CrateManager {
    private final ArisCrate plugin;
    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    public void createCrate(String name, Location loc) {
        String path = "crates." + name;
        plugin.getCrateConfig().set(path + ".location.world", loc.getWorld().getName());
        plugin.getCrateConfig().set(path + ".location.x", loc.getBlockX());
        plugin.getCrateConfig().set(path + ".location.y", loc.getBlockY());
        plugin.getCrateConfig().set(path + ".location.z", loc.getBlockZ());
        plugin.getCrateConfig().set(path + ".rows", 3);
        plugin.saveCrateConfig();
    }

    public void openEditMenu(Player p, String name) {
        if (!plugin.getCrateConfig().contains("crates." + name)) return;
        int rows = plugin.getCrateConfig().getInt("crates." + name + ".rows", 3);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Editing: " + name);
        if (plugin.getCrateConfig().contains("crates." + name + ".rewards")) {
            for (String key : plugin.getCrateConfig().getConfigurationSection("crates." + name + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.getCrateConfig().getItemStack("crates." + name + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public void saveCrateItems(String name, Inventory inv) {
        plugin.getCrateConfig().set("crates." + name + ".rewards", null);
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null) plugin.getCrateConfig().set("crates." + name + ".rewards." + i, item);
        }
        Bukkit.getGlobalRegionScheduler().execute(plugin, plugin::saveCrateConfig);
    }
          }
