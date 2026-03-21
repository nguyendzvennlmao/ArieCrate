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
    @Override public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "0";
        String keyName = plugin.crateConfig().getString("crates." + params + ".key.name");
        if (keyName == null) return "0";
        String coloredKey = keyName.replace("&", "§");
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(coloredKey)) {
                count += item.getAmount();
            }
        }
        return String.valueOf(count);
    }
}
