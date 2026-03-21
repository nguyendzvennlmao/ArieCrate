package me.aris.crates;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CrateExpansion extends PlaceholderExpansion {
    private final ArisCrate plugin;

    public CrateExpansion(ArisCrate plugin) { this.plugin = plugin; }

    @Override @NotNull public String getAuthor() { return "Aris"; }
    @Override @NotNull public String getIdentifier() { return "ariscrate"; }
    @Override @NotNull public String getVersion() { return "1.0"; }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "0";
        if (params.startsWith("key_")) {
            String crateName = params.replace("key_", "");
            return String.valueOf(plugin.getKeyConfig().getInt(player.getName() + "." + crateName, 0));
        }
        return null;
    }
}
