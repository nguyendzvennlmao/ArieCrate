package me.aris.ariscrate.listener;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.CrateData;
import me.aris.ariscrate.utils.ColorUtils;
import me.aris.ariscrate.utils.DogAdonisHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateInteractListener implements Listener {
    private final ArisCrate plugin;
    public CrateInteractListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) return;
        Location loc = e.getClickedBlock().getLocation();
        for (String name : plugin.getCrateManager().getNames()) {
            CrateData c = plugin.getCrateManager().get(name);
            if (c.getLocation().equals(loc)) {
                e.setCancelled(true);
                plugin.getSelectingCrate().put(e.getPlayer().getUniqueId(), c.getName());
                openCrate(e.getPlayer(), c);
                return;
            }
        }
    }

    private void openCrate(org.bukkit.entity.Player p, CrateData c) {
        var gui = plugin.getGuiConfig();
        Inventory inv = Bukkit.createInventory(null, gui.getInt("choose-gui.size"), ColorUtils.color(gui.getString("choose-gui.title")));
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = glass.getItemMeta();
        m.setDisplayName(" ");
        glass.setItemMeta(m);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, glass);
        c.getItems().forEach((slot, item) -> inv.setItem(slot, DogAdonisHook.freeze(item.clone())));
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.open-preview")), 1, 1);
    }
    }
