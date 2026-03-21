package me.aris.crates;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CrateListener implements Listener {
    private final ArisCrate plugin;
    public CrateListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        String crateName = plugin.getCrateManager().getCrateAt(e.getClickedBlock().getLocation());
        if (crateName != null) {
            e.setCancelled(true);
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.getPlayer().sendMessage("§aBạn đã mở rương: §e" + crateName);
                // Logic mở rương tại đây
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (title.startsWith("Editing: ")) {
            plugin.getCrateManager().saveCrateItems(title.replace("Editing: ", ""), e.getInventory());
            e.getPlayer().sendMessage("§a[ArisCrate] Đã tự động lưu!");
        }
    }
                                          }
