package me.aris.crates;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {
    private final ArisCrate plugin;
    public CrateListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();
        if (title.contains("ʀưᴏ̛ɴɢ") || title.contains("xáᴄ ɴʜậɴ") || title.contains("RƯƠNG") || title.contains("XÁC NHẬN")) {
            e.setCancelled(true);
            if (e.getClickedInventory() == e.getView().getBottomInventory()) return;
            if (title.contains("ʀưᴏ̛ɴɢ") || title.contains("RƯƠNG")) {
                ItemStack item = e.getCurrentItem();
                if (item == null || item.getType().isAir()) return;
                String crateName = p.hasMetadata("current_viewing_crate") ? p.getMetadata("current_viewing_crate").get(0).asString() : "";
                if (!crateName.isEmpty()) plugin.getCrateManager().openConfirmMenu(p, crateName, item);
            } else if (title.contains("xáᴄ ɴʜậɴ") || title.contains("XÁC NHẬN")) {
                int slot = e.getRawSlot();
                String crateName = p.hasMetadata("opening_crate") ? p.getMetadata("opening_crate").get(0).asString() : "";
                if (slot == plugin.getConfig().getInt("confirm-gui.confirm-slot")) {
                    if (p.getInventory().firstEmpty() == -1) { plugin.sendMsg(p, "inv-full"); return; }
                    ItemStack item = e.getInventory().getItem(plugin.getConfig().getInt("confirm-gui.display-slot"));
                    if (item != null && plugin.getCrateManager().takeKey(p.getName(), crateName)) {
                        p.getInventory().addItem(item.clone());
                        plugin.sendMsg(p, "open-success", "%crate%", crateName);
                        p.closeInventory();
                    } else { plugin.sendMsg(p, "no-key", "%crate%", crateName); p.closeInventory(); }
                } else if (slot == plugin.getConfig().getInt("confirm-gui.cancel-slot")) p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        String title = e.getView().getTitle();
        if (title.contains("ʀưᴏ̛ɴɢ") || title.contains("xáᴄ ɴʜậɴ") || title.contains("RƯƠNG") || title.contains("XÁC NHẬN")) e.setCancelled(true);
    }

    @EventHandler
    public void onEditClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().startsWith("Editing: ")) plugin.getCrateManager().saveCrateItems(e.getView().getTitle().replace("Editing: ", ""), e.getInventory());
        e.getPlayer().removeMetadata("opening_crate", plugin);
        e.getPlayer().removeMetadata("current_viewing_crate", plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        String name = plugin.getCrateManager().getCrateAt(e.getClickedBlock().getLocation());
        if (name != null) { 
            e.setCancelled(true); 
            e.getPlayer().setMetadata("current_viewing_crate", new org.bukkit.metadata.FixedMetadataValue(plugin, name));
            plugin.getCrateManager().openPreview(e.getPlayer(), name); 
        }
    }
                    }
