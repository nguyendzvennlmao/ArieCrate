package me.aris.crates;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrateExpansion extends PlaceholderExpansion {
    private final ArisCrate plugin;
    public CrateExpansion(ArisCrate plugin) { this.plugin = plugin; }
    @Override public @NotNull String getIdentifier() { return "ariscrate"; }
    @Override public @NotNull String getAuthor() { return "Aris"; }
    @Override public @NotNull String getVersion() { return "1.0"; }
    @Override public String onPlaceholderRequest(Player p, @NotNull String params) {
        if (p == null) return "0";
        int count = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().toLowerCase().contains(params.toLowerCase())) {
                count += item.getAmount();
            }
        }
        return String.valueOf(count);
    }
}
