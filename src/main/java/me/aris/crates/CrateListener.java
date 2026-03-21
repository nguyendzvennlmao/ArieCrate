package me.aris.crates;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CrateListener implements Listener {
    private final ArisCrate plugin;
    public CrateListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (title.startsWith("Editing: ")) {
            plugin.getCrateManager().saveCrateItems(title.replace("Editing: ", ""), e.getInventory());
            e.getPlayer().sendMessage("§a[ArisCrate] Đã tự động lưu vật phẩm rương!");
        }
    }
          }
