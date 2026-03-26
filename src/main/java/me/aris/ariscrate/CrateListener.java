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

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();

        if (title.startsWith("Editing: ")) {
            return;
        }

        if (title.contains("ʀưᴏ̛ɴɢ") || title.contains("RƯƠNG")) {
            e.setCancelled(true);
            if (e.getClickedInventory() == e.getView().getBottomInventory()) return;
            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType().isAir()) return;
            if (!p.hasMetadata("current_viewing_crate")) return;
            String crateName = p.getMetadata("current_viewing_crate").get(0).asString();
            int keys = plugin.getKeyConfig().getInt(p.getName() + "." + crateName, 0);
            if (keys <= 0) {
                p.closeInventory();
                plugin.getCrateManager().playSound(p, "no-key");
                plugin.sendMsg(p, "no-key", "%crate%", crateName);
                return;
            }
            plugin.getCrateManager().openConfirmMenu(p, crateName, item);
        } else if (title.contains("xáᴄ ɴʜậɴ") || title.contains("XÁC NHẬN")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            if (!p.hasMetadata("opening_crate")) return;
            String crateName = p.getMetadata("opening_crate").get(0).asString();
            if (slot == plugin.getConfig().getInt("confirm-gui.confirm-slot")) {
                if (p.getInventory().firstEmpty() == -1) {
                    plugin.sendMsg(p, "inv-full");
                    return;
                }
                ItemStack item = e.getInventory().getItem(plugin.getConfig().getInt("confirm-gui.display-slot"));
                if (item != null && plugin.getCrateManager().takeKey(p.getName(), crateName)) {
                    p.getInventory().addItem(item.clone());
                    plugin.getCrateManager().playSound(p, "open-success");
                    plugin.sendMsg(p, "open-success", "%crate%", crateName);
                    p.closeInventory();
                } else { 
                    p.closeInventory();
                    plugin.sendMsg(p, "no-key", "%crate%", crateName);
                }
            } else if (slot == plugin.getConfig().getInt("confirm-gui.cancel-slot")) {
                p.closeInventory();
                plugin.getCrateManager().playSound(p, "close-preview");
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
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        String name = plugin.getCrateManager().getCrateAt(e.getClickedBlock().getLocation());
        if (name == null) return;
        e.setCancelled(true); 
        e.getPlayer().setMetadata("current_viewing_crate", new FixedMetadataValue(plugin, name));
        plugin.getCrateManager().openPreview(e.getPlayer(), name); 
    }
        }
