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

    public void addKey(String playerName, String crateName, int amount) {
        int current = plugin.getKeyConfig().getInt(playerName + "." + crateName, 0);
        plugin.getKeyConfig().set(playerName + "." + crateName, current + amount);
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
        var config = plugin.getConfig();
        String title = config.getString("confirm-gui.title").replace("%crate%", crateName).replace("&", "§");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack confirm = new ItemStack(Material.valueOf(config.getString("confirm-gui.confirm-item.material")));
        ItemMeta cm = confirm.getItemMeta();
        cm.setDisplayName(config.getString("confirm-gui.confirm-item.name").replace("&", "§"));
        List<String> clore = new ArrayList<>();
        for(String s : config.getStringList("confirm-gui.confirm-item.lore")) clore.add(s.replace("%crate%", crateName).replace("&", "§"));
        cm.setLore(clore); confirm.setItemMeta(cm);

        ItemStack cancel = new ItemStack(Material.valueOf(config.getString("confirm-gui.cancel-item.material")));
        ItemMeta nm = cancel.getItemMeta();
        nm.setDisplayName(config.getString("confirm-gui.cancel-item.name").replace("&", "§"));
        List<String> nlore = new ArrayList<>();
        for(String s : config.getStringList("confirm-gui.cancel-item.lore")) nlore.add(s.replace("&", "§"));
        nm.setLore(nlore); cancel.setItemMeta(nm);

        inv.setItem(config.getInt("confirm-gui.confirm-item.slot"), confirm);
        inv.setItem(config.getInt("confirm-gui.cancel-item.slot"), cancel);
        p.openInventory(inv);
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

    public void createCrate(String name, Location loc) {
        String path = "crates." + name;
        plugin.getCrateConfig().set(path + ".location.world", loc.getWorld().getName());
        plugin.getCrateConfig().set(path + ".location.x", loc.getBlockX());
        plugin.getCrateConfig().set(path + ".location.y", loc.getBlockY());
        plugin.getCrateConfig().set(path + ".location.z", loc.getBlockZ());
        plugin.saveCrateConfig();
    }
                    }
