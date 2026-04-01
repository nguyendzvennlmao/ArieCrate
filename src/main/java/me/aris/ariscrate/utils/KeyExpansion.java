package me.aris.ariscrate.utils;

import me.aris.ariscrate.ArisCrate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KeyExpansion extends PlaceholderExpansion {
    private final ArisCrate plugin;
    public KeyExpansion(ArisCrate plugin) { this.plugin = plugin; }
    @Override public @NotNull String getIdentifier() { return "ariscrate"; }
    @Override public @NotNull String getAuthor() { return "VennLMAO"; }
    @Override public @NotNull String getVersion() { return "1.0"; }
    @Override public boolean persist() { return true; }
    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        if (p == null) return "0";
        return String.valueOf(plugin.getKeyManager().getKeys(p.getUniqueId(), params));
    }
}
