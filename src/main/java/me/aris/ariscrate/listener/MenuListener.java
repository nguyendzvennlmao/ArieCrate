package me.aris.ariscrate.listener;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;

public class MenuListener implements Listener {
    private final ArisCrate plugin;
    public MenuListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();
        var gui = plugin.getGuiConfig();
        if (!title.equals(ColorUtils.color(gui.getString("confirm-gui.title")))) return;
        e.setCancelled(true);
        int slot = e.getRawSlot();
        p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.click")), 1, 1);
        if (slot == gui.getInt("confirm-gui.cancel-slot")) {
            p.closeInventory();
        } else if (slot == gui.getInt("confirm-gui.confirm-slot")) {
            handle(p, e.getInventory());
        }
    }

    private void handle(Player p, Inventory inv) {
        String type = plugin.getSelectingCrate().get(p.getUniqueId());
        File f = new File(plugin.getDataFolder(), "crates/" + type + ".yml");
        if (!f.exists()) return;
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        
        int rows = c.getInt("rows", 3);
        if (rows < 1) rows = 1;
        if (rows > 6) rows = 6;
        
        Inventory rewardInv = Bukkit.createInventory(null, rows * 9, ColorUtils.color(c.getString("title")));
        ItemStack reward = inv.getItem(plugin.getGuiConfig().getInt("confirm-gui.display-slot"));
        
        if (reward != null && plugin.getKeyManager().takeKeys(p.getUniqueId(), type, 1)) {
            p.getScheduler().execute(plugin, () -> {
                p.openInventory(rewardInv);
                p.getInventory().addItem(reward.clone());
            }, null, 1L);
            p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.open-success")), 1, 1);
        } else {
            p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.error")), 1, 1);
            plugin.sendNotify(p, "no-key", "%crate%", type);
        }
    }
                }
