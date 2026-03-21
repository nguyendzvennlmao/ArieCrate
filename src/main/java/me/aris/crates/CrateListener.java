package me.aris.crates;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
            Player p = e.getPlayer();
            plugin.getCrateManager().openPreview(p, crateName);
            if (plugin.getCrateManager().hasCorrectKey(p, crateName)) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
                    plugin.getCrateManager().openConfirmMenu(p, crateName), 30L);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (title.startsWith("Confirm: ")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            String name = title.replace("Confirm: ", "");
            if (e.getRawSlot() == 3) {
                plugin.getCrateManager().takeKey(p, name);
                plugin.getCrateManager().giveRandomReward(p, name);
                p.closeInventory();
                p.sendMessage("§a✔ Bạn đã mở rương thành công!");
            } else if (e.getRawSlot() == 5) {
                p.closeInventory();
            }
        } else if (title.startsWith("Preview: ")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (title.startsWith("Editing: ")) {
            String name = title.replace("Editing: ", "");
            plugin.getCrateManager().saveCrateItems(name, e.getInventory());
            e.getPlayer().sendMessage("§a[ArisCrate] Đã lưu phần thưởng rương!");
        }
    }
}
