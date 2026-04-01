package me.aris.ariscrate.task;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.Bukkit;

public class KeyAllTask {
    private long seconds;
    private final long resetTime;
    private final ArisCrate plugin;

    public KeyAllTask(ArisCrate plugin) {
        this.plugin = plugin;
        this.resetTime = parse(plugin.getConfig().getString("keyall.time"));
        this.seconds = resetTime;
        run();
    }

    private void run() {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> {
            if (seconds <= 0) {
                Bukkit.getOnlinePlayers().forEach(p -> plugin.give(p, "common", 1, true));
                seconds = resetTime;
            }
            seconds--;
        }, 20L, 20L);
    }

    private long parse(String s) {
        if (s == null) return 3600;
        s = s.toLowerCase();
        if (s.endsWith("h")) return Long.parseLong(s.replace("h", "")) * 3600;
        if (s.endsWith("m")) return Long.parseLong(s.replace("m", "")) * 60;
        if (s.endsWith("s")) return Long.parseLong(s.replace("s", ""));
        return Long.parseLong(s);
    }

    public String getTime() {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format("%02d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }
                  }
