package me.aris.ariscrate;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class CrateListener implements Listener {
    private final ArisCrate plugin;
    public CrateListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();

        if (title.startsWith("Editing: ")) return;

        if (p.hasMetadata("current_viewing_crate") || p.hasMetadata("opening_crate")) {
            e.setCancelled(true);
            
            if (p.hasMetadata("opening_crate")) {
                int slot = e.getRawSlot();
                String crateName = p.getMetadata("opening_crate").get(0).asString();
                
                if (slot == plugin.getConfig().getInt("confirm-gui.confirm-slot")) {
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
                        p.removeMetadata("opening_crate", plugin);
                        p.removeMetadata("current_viewing_crate", plugin);
                        p.closeInventory();
                    } else {
                        p.closeInventory();
                        plugin.sendMsg(p, "no-key", "%crate%", plugin.getCrateManager().toSmallCaps(crateName));
                    }
                } else if (slot == plugin.getConfig().getInt("confirm-gui.cancel-slot")) {
                    p.removeMetadata("opening_crate", plugin);
                    p.removeMetadata("current_viewing_crate", plugin);
                    p.closeInventory();
                    plugin.getCrateManager().playSound(p, "close-preview");
                }
                return;
            }

            if (e.getClickedInventory() == e.getView().getTopInventory()) {
                ItemStack item = e.getCurrentItem();
                if (item == null || item.getType().isAir()) return;
                String crateName = p.getMetadata("current_viewing_crate").get(0).asString();
                int keys = plugin.getKeyConfig().getInt(p.getName() + "." + crateName, 0);
                if (keys <= 0) {
                    p.closeInventory();
                    plugin.sendMsg(p, "no-key", "%crate%", plugin.getCrateManager().toSmallCaps(crateName));
                    return;
                }
                plugin.getCrateManager().openConfirmMenu(p, crateName, item);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getView().getTitle().startsWith("Editing: ")) {
            if (p.hasMetadata("editing_crate_name")) {
                String name = p.getMetadata("editing_crate_name").get(0).asString();
                plugin.getCrateManager().saveCrateItems(name, e.getInventory());
                p.sendMessage("§aĐã lưu vật phẩm vào rương: " + name);
                p.removeMetadata("editing_crate_name", plugin);
            }
        }
        if (!p.getOpenInventory().getTitle().contains("xáᴄ ɴʜậɴ")) {
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
