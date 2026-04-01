package me.aris.ariscrate;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {
    private final ArisCrate plugin;

    public CrateListener(ArisCrate plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        boolean isPreview = p.hasMetadata("current_viewing_crate");
        boolean isConfirm = p.hasMetadata("opening_crate");

        if (!isPreview && !isConfirm) return;
        if (p.hasMetadata("editing_crate")) return;

        if (e.getClickedInventory() == null) return;

        if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);

            if (isConfirm) {
                handleConfirmClick(e, p);
            } else if (isPreview) {
                handlePreviewClick(e, p);
            }
        } else {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || 
                e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
            }
        }
    }

    private void handlePreviewClick(InventoryClickEvent e, Player p) {
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        String crateName = p.getMetadata("current_viewing_crate").get(0).asString();
        plugin.getCrateManager().runTask(p, () -> {
            plugin.getCrateManager().openConfirmMenu(p, crateName, item);
        });
    }

    private void handleConfirmClick(InventoryClickEvent e, Player p) {
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
                clearMetadata(p);
                p.closeInventory();
            });
        } else if (slot == plugin.getConfig().getInt("confirm-gui.cancel-slot")) {
            plugin.getCrateManager().runTask(p, () -> {
                clearMetadata(p);
                p.closeInventory();
                plugin.getCrateManager().playSound(p, "close-preview");
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent e) {
        if (e.getWhoClicked().hasMetadata("current_viewing_crate") || e.getWhoClicked().hasMetadata("opening_crate")) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAction(InventoryClickEvent e) {
        if (e.getWhoClicked().hasMetadata("current_viewing_crate") || e.getWhoClicked().hasMetadata("opening_crate")) {
            InventoryAction action = e.getAction();
            if (action == InventoryAction.HOTBAR_SWAP || 
                action == InventoryAction.HOTBAR_MOVE_AND_READD ||
                action == InventoryAction.SWAP_WITH_CURSOR ||
                action == InventoryAction.CLONE_STACK) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
            }
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
            clearMetadata(p);
        }
    }

    private void clearMetadata(Player p) {
        p.removeMetadata("current_viewing_crate", plugin);
        p.removeMetadata("opening_crate", plugin);
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
