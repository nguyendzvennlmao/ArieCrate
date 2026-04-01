package me.aris.ariscrate.task;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class KeyAllTask extends BukkitRunnable {
    private final ArisCrate plugin;

    public KeyAllTask(ArisCrate plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        String keyType = "common";
        int amount = 1;
        Bukkit.getOnlinePlayers().forEach(player -> {
            plugin.getKeyManager().give(player, keyType, amount, true);
            String sub = plugin.getConfig().getString("keyall.subtitle");
            if (sub != null) player.sendTitle("", plugin.format(sub), 10, 40, 10);
        });
    }
}
