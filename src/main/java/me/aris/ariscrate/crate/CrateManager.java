package me.aris.ariscrate.crate;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class CrateManager {
    private final ArisCrate plugin;

    public CrateManager(ArisCrate plugin) {
        this.plugin = plugin;
    }

    public void openCrate(org.bukkit.entity.Player player, CrateData data) {
        List<ItemStack> items = data.getItems();
        if (items.isEmpty()) return;
        ItemStack win = items.get(new java.util.Random().nextInt(items.size()));
        player.getInventory().addItem(win);
        player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(plugin.getConfig().getString("sounds.open-success")), 1, 1);
    }
}
