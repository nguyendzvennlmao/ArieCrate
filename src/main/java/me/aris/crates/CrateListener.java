package me.aris.crates;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import net.md_5.bungee.api.ChatColor;

public class CrateListener implements Listener {
    private final ArisCrate plugin;
    public CrateListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();

        if (e.getClickedInventory() == e.getView().getBottomInventory()) {
            if (title.contains("Preview:") || title.contains("Xác nhận:")) e.setCancelled(true);
            return;
        }

        if (title.contains("Preview:")) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType().isAir()) return;
            String crateName = title.split(": ")[1].trim();
            if (plugin.getKeyConfig().getInt(p.getName() + "." + crateName, 0) > 0) {
                plugin.getCrateManager().openConfirmMenu(p, crateName, item);
            } else {
                plugin.sendMsg(p, "no-key", "%crate%", crateName);
            }
        } 
        else if (title.contains("Xác nhận:")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            String crateName = ChatColor.stripColor(title).split(": ")[1].trim();
            
            if (slot == plugin.getConfig().getInt("confirm-gui.confirm-slot", 15)) {
                if (p.getInventory().firstEmpty() == -1) {
                    plugin.sendMsg(p, "inv-full");
                    return;
                }
                ItemStack item = e.getInventory().getItem(plugin.getConfig().getInt("confirm-gui.display-slot", 13));
                if (item != null && plugin.getCrateManager().takeKey(p.getName(), crateName)) {
                    p.getInventory().addItem(item.clone());
                    plugin.sendMsg(p, "open-success", "%crate%", crateName);
                    p.closeInventory();
                }
            } else if (slot == plugin.getConfig().getInt("confirm-gui.cancel-slot", 11)) {
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onEditClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().startsWith("Editing: ")) {
            String name = e.getView().getTitle().replace("Editing: ", "");
            plugin.getCrateManager().saveCrateItems(name, e.getInventory());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        String name = plugin.getCrateManager().getCrateAt(e.getClickedBlock().getLocation());
        if (name != null) {
            e.setCancelled(true);
            plugin.getCrateManager().openPreview(e.getPlayer(), name);
        }
    }
                    }
