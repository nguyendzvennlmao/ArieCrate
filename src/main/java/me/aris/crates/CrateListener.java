package me.aris.crates;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();
        var cfg = plugin.getConfig();

        if (title.startsWith("Preview: ")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String crateName = title.replace("Preview: ", "");
            if (plugin.getCrateManager().getKeys(p.getName(), crateName) > 0) {
                plugin.getCrateManager().openConfirmMenu(p, crateName);
            } else {
                plugin.sendMsg(p, "no-key", "%crate%", crateName);
            }
        } else if (title.contains(cfg.getString("confirm-gui.title").split("%")[0].replace("&", "§"))) {
            e.setCancelled(true);
            String crateName = title.split(": ")[1].replace("§1", "");
            int slot = e.getRawSlot();
            if (slot == cfg.getInt("confirm-gui.confirm-item.slot")) {
                if (plugin.getCrateManager().takeKey(p.getName(), crateName)) {
                    plugin.getCrateManager().giveRandomReward(p, crateName);
                    plugin.sendMsg(p, "open-success", "%crate%", crateName);
                }
                p.closeInventory();
            } else if (slot == cfg.getInt("confirm-gui.cancel-item.slot")) p.closeInventory();
        }
    }
            }
