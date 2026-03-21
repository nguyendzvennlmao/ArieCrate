package me.aris.crates;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {
    private final ArisCrate plugin;
    public CrateListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        String name = plugin.getCrateManager().getCrateAt(e.getClickedBlock().getLocation());
        if (name != null) {
            e.setCancelled(true);
            plugin.getCrateManager().openPreview(e.getPlayer(), name);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();

        // FIX CLICK NHẦM TÚI ĐỒ
        if (e.getClickedInventory() == e.getView().getBottomInventory()) {
            if (title.startsWith("Preview: ") || title.startsWith("§8Xác nhận: ")) e.setCancelled(true);
            return;
        }

        if (title.startsWith("Preview: ")) {
            e.setCancelled(true);
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem == null || clickedItem.getType().isAir()) return;

            String crateName = title.replace("Preview: ", "");
            if (plugin.getCrateManager().getKeys(p.getName(), crateName) > 0) {
                plugin.getCrateManager().openConfirmMenu(p, crateName, clickedItem);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            } else {
                plugin.sendMsg(p, "no-key", "%crate%", crateName);
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
        } 
        else if (title.startsWith("§8Xác nhận: ")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            String crateName = title.split(": ")[1].replace("§1", "").trim();
            
            int cancelSlot = plugin.getConfig().getInt("confirm-gui.cancel.slot", 11);
            int confirmSlot = plugin.getConfig().getInt("confirm-gui.confirm.slot", 15);
            int displaySlot = plugin.getConfig().getInt("confirm-gui.item-display.slot", 13);

            if (slot == confirmSlot) {
                ItemStack itemToGive = e.getInventory().getItem(displaySlot);
                if (itemToGive != null && plugin.getCrateManager().takeKey(p.getName(), crateName)) {
                    p.getInventory().addItem(itemToGive.clone());
                    plugin.sendMsg(p, "open-success", "%crate%", crateName);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                    p.closeInventory();
                }
            } else if (slot == cancelSlot) {
                p.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
                p.closeInventory();
            }
        }
    }
                }
