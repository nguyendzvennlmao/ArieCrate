package me.aris.ariscrate;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CrateExpansion extends PlaceholderExpansion {
    private final ArisCrate plugin;
    public CrateExpansion(ArisCrate plugin) { this.plugin = plugin; }
    @Override public @NotNull String getAuthor() { return "VennLMAO"; }
    @Override public @NotNull String getIdentifier() { return "ariscrate"; }
    @Override public @NotNull String getVersion() { return "1.1"; }
    @Override public boolean persist() { return true; }
    @Override public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "0";
        if (params.startsWith("keys_")) {
            String crateName = params.replace("keys_", "");
            return String.valueOf(plugin.getKeyConfig().getInt(player.getName() + "." + crateName, 0));
        }
        return null;
    }
}
