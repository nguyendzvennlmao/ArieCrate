package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class CrateManager {
    private final ArisCrate plugin;
    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    public void openConfirmMenu(Player p, String crateName, ItemStack selectedItem) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Xác nhận: §1" + crateName);
        var cfg = plugin.getConfig();

        // Nút Hủy (11)
        ItemStack cancel = new ItemStack(Material.valueOf(cfg.getString("confirm-gui.cancel.material", "RED_STAINED_GLASS_PANE")));
        ItemMeta nm = cancel.getItemMeta();
        nm.setDisplayName(translateHex(cfg.getString("confirm-gui.cancel.name")));
        List<String> nl = new ArrayList<>();
        for (String s : cfg.getStringList("confirm-gui.cancel.lore")) nl.add(translateHex(s));
        nm.setLore(nl);
        cancel.setItemMeta(nm);

        // Nút Xác nhận (15)
        ItemStack confirm = new ItemStack(Material.valueOf(cfg.getString("confirm-gui.confirm.material", "LIME_STAINED_GLASS_PANE")));
        ItemMeta cm = confirm.getItemMeta();
        cm.setDisplayName(translateHex(cfg.getString("confirm-gui.confirm.name")));
        List<String> cl = new ArrayList<>();
        for (String s : cfg.getStringList("confirm-gui.confirm.lore")) cl.add(translateHex(s));
        cm.setLore(cl);
        confirm.setItemMeta(cm);

        inv.setItem(cfg.getInt("confirm-gui.cancel-slot", 11), cancel);
        inv.setItem(cfg.getInt("confirm-gui.display-slot", 13), selectedItem); // Hiện item đã chọn
        inv.setItem(cfg.getInt("confirm-gui.confirm-slot", 15), confirm);

        p.openInventory(inv);
    }

    public String translateHex(String message) {
        if (message == null) return "";
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        return matcher.appendTail(buffer).toString().replace("&", "§");
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
                inv.setItem(Integer.parseInt(key), plugin.getCrateConfig().getItemStack("crates." + crateName + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public String getCrateAt(org.bukkit.Location loc) {
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

    public void giveKey(Player target, String crateName, int amount) {
        int current = getKeys(target.getName(), crateName);
        plugin.getKeyConfig().set(target.getName() + "." + crateName, current + amount);
        plugin.saveKeyConfig();
    }
            }
