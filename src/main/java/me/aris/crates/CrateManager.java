package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class CrateManager {
    private final ArisCrate plugin;
    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    public void giveKey(Player target, String crateName, int amount) {
        int current = plugin.getKeyConfig().getInt(target.getName() + "." + crateName, 0);
        plugin.getKeyConfig().set(target.getName() + "." + crateName, current + amount);
        plugin.saveKeyConfig();
    }

    public int getKeys(String playerName, String crateName) {
        return plugin.getKeyConfig().getInt(playerName + "." + crateName, 0);
    }

    public boolean takeKey(String playerName, String crateName) {
        int current = getKeys(playerName, crateName);
        if (current <= 0) return false;
        plugin.getKeyConfig().set(playerName + "." + crateName, current - 1);
        plugin.saveKeyConfig();
        return true;
    }

    public void openEditMenu(Player p, String name) {
        Inventory inv = Bukkit.createInventory(null, 27, "Editing: " + name);
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
            if (inv.getItem(i) != null) plugin.getCrateConfig().set("crates." + name + ".rewards." + i, inv.getItem(i));
        }
        plugin.saveCrateConfig();
    }

    public void openPreview(Player p, String crateName) {
        Inventory inv = Bukkit.createInventory(null, 27, "Preview: " + crateName);
        if (plugin.getCrateConfig().contains("crates." + crateName + ".rewards")) {
            for (String key : plugin.getCrateConfig().getConfigurationSection("crates." + crateName + ".rewards").getKeys(false)) {
                int slot = Integer.parseInt(key);
                if (slot < 27) inv.setItem(slot, plugin.getCrateConfig().getItemStack("crates." + crateName + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public void openConfirmMenu(Player p, String crateName) {
        var cfg = plugin.getConfig();
        String title = cfg.getString("confirm-gui.title", "&8Confirm: %crate%").replace("%crate%", crateName).replace("&", "§");
        Inventory inv = Bukkit.createInventory(null, 27, title);
        
        ItemStack confirm = new ItemStack(Material.valueOf(cfg.getString("confirm-gui.confirm-item.material", "LIME_STAINED_GLASS_PANE")));
        ItemMeta cm = confirm.getItemMeta(); cm.setDisplayName(cfg.getString("confirm-gui.confirm-item.name", "&aXác nhận").replace("&", "§"));
        confirm.setItemMeta(cm);
        
        ItemStack cancel = new ItemStack(Material.valueOf(cfg.getString("confirm-gui.cancel-item.material", "RED_STAINED_GLASS_PANE")));
        ItemMeta nm = cancel.getItemMeta(); nm.setDisplayName(cfg.getString("confirm-gui.cancel-item.name", "&cHủy").replace("&", "§"));
        cancel.setItemMeta(nm);

        inv.setItem(cfg.getInt("confirm-gui.confirm-item.slot", 13), confirm);
        inv.setItem(cfg.getInt("confirm-gui.cancel-item.slot", 11), cancel);
        p.openInventory(inv);
    }

    public void createCrate(String name, Location loc) {
        plugin.getCrateConfig().set("crates." + name + ".location.world", loc.getWorld().getName());
        plugin.getCrateConfig().set("crates." + name + ".location.x", loc.getBlockX());
        plugin.getCrateConfig().set("crates." + name + ".location.y", loc.getBlockY());
        plugin.getCrateConfig().set("crates." + name + ".location.z", loc.getBlockZ());
        plugin.saveCrateConfig();
    }

    public void giveRandomReward(Player p, String crateName) {
        var section = plugin.getCrateConfig().getConfigurationSection("crates." + crateName + ".rewards");
        if (section == null) return;
        List<String> keys = new ArrayList<>(section.getKeys(false));
        if (keys.isEmpty()) return;
        String rKey = keys.get(new Random().nextInt(keys.size()));
        p.getInventory().addItem(plugin.getCrateConfig().getItemStack("crates." + crateName + ".rewards." + rKey).clone());
    }

    public String getCrateAt(Location loc) {
        if (plugin.getCrateConfig().getConfigurationSection("crates") == null) return null;
        for (String key : plugin.getCrateConfig().getConfigurationSection("crates").getKeys(false)) {
            String w = plugin.getCrateConfig().getString("crates." + key + ".location.world");
            int x = plugin.getCrateConfig().getInt("crates." + key + ".location.x");
            int y = plugin.getCrateConfig().getInt("crates." + key + ".location.y");
            int z = plugin.getCrateConfig().getInt("crates." + key + ".location.z");
            if (loc.getWorld().getName().equals(w) && loc.getBlockX() == x && loc.getBlockY() == y && loc.getBlockZ() == z) return key;
        }
        return null;
    }
    }
