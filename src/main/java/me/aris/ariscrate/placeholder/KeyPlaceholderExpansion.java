package me.aris.ariscrate.placeholder;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.key.KeyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KeyPlaceholderExpansion extends PlaceholderExpansion {
    private final ArisCrate plugin;

    public KeyPlaceholderExpansion(ArisCrate plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "ariscrate";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "Aris";
    }

    @NotNull
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "0";
        }
        
        if (params.startsWith("key_")) {
            String crateName = params.substring(4);
            KeyManager km = plugin.getKeyManager();
            return String.valueOf(km.getKeys(player.getUniqueId(), crateName));
        }
        
        return "0";
    }
    }
