package me.aris.ariscrate;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {
    private final ArisCrate plugin;
    public CrateListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        
        boolean isPreview = p.hasMetadata("current_viewing_crate");
        boolean isConfirm = p.hasMetadata("opening_crate");
        
        if (!isPreview && !isConfirm) return;
        if (p.hasMetadata("editing_crate")) return;

        e.setCancelled(true);
        e.setResult(Event.Result.DENY);

        if (e.getClickedInventory() == null) return;

        if (isConfirm) {
            int slot = e.getRawSlot();
            String crateName = p.getMetadata("opening_crate").get(0).asString();
            
            if (slot == plugin.getConfig().getInt("confirm-gui.confirm-slot")) {
                plugin.getCrateManager().runTask(p, () -> {
                    if (p.getInventory().firstEmpty() == -1) {
                        plugin.sendMsg(p, "inv-full");
                        p.closeInventory();
                        return;
                    }
                    ItemStack item = e.getInventory().getItem(plugin.getConfig().getInt("confirm-gui.display-slot"));
                    if (item != null && plugin.getCrateManager().takeKey(p.getName(), crateName)) {
                        p.getInventory().addItem(item.clone());
                        plugin.getCrateManager().playSound(p, "open-success");
                        plugin.sendMsg(p, "open-success", "%crate%", plugin.getCrateManager().toSmallCaps(crateName));
                    } else {
                        plugin.sendMsg(p, "no-key", "%crate%", plugin.getCrateManager().toSmallCaps(crateName));
                    }
                    p.removeMetadata("opening_crate", plugin);
                    p.removeMetadata("current_viewing_crate", plugin);
                    p.closeInventory();
                });
            } else if (slot == plugin.getConfig().getInt("confirm-gui.cancel-slot")) {
                plugin.getCrateManager().runTask(p, () -> {
                    p.removeMetadata("opening_crate", plugin);
                    p.removeMetadata("current_viewing_crate", plugin);
                    p.closeInventory();
                    plugin.getCrateManager().playSound(p, "close-preview");
                });
            }
            return;
        }

        if (isPreview && e.getClickedInventory().equals(e.getView().getTopInventory())) {
            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType().isAir()) return;
            
            String crateName = p.getMetadata("current_viewing_crate").get(0).asString();
            plugin.getCrateManager().runTask(p, () -> {
                plugin.getCrateManager().openConfirmMenu(p, crateName, item);
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent e) {
        if (e.getWhoClicked().hasMetadata("current_viewing_crate") || e.getWhoClicked().hasMetadata("opening_crate")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (p.hasMetadata("editing_crate")) {
            String crateName = p.getMetadata("editing_crate").get(0).asString();
            plugin.getCrateManager().saveCrateRewards(crateName, e.getInventory());
            p.removeMetadata("editing_crate", plugin);
        }
        if (!e.getView().getTitle().contains("xáᴄ ɴʜậɴ")) {
            p.removeMetadata("current_viewing_crate", plugin);
            p.removeMetadata("opening_crate", plugin);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        String name = plugin.getCrateManager().getCrateAt(e.getClickedBlock().getLocation());
        if (name == null) return;
        e.setCancelled(true);
        plugin.getCrateManager().openPreview(e.getPlayer(), name);
    }
                                                 }
