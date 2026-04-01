package me.aris.ariscrate;

import me.aris.ariscrate.command.CrateAdminCommand;
import me.aris.ariscrate.key.KeyManager;
import me.aris.ariscrate.crate.CrateManager;
import me.aris.ariscrate.listener.CrateInteractListener;
import me.aris.ariscrate.listener.MenuListener;
import me.aris.ariscrate.task.KeyAllTask;
import me.aris.ariscrate.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.*;

public class ArisCrate extends JavaPlugin {
    private KeyManager keyManager;
    private CrateManager crateManager;
    private FileConfiguration msgCfg;
    private FileConfiguration guiCfg;
    private final Map<UUID, String> selectingCrate = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigs();
        this.keyManager = new KeyManager(this);
        this.keyManager.load();
        this.crateManager = new CrateManager(this);
        
        getCommand("acrate").setExecutor(new CrateAdminCommand(this));
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CrateInteractListener(this), this);
        new KeyAllTask(this).runTaskTimer(this, 72000L, 72000L);
    }

    public void loadConfigs() {
        reloadConfig();
        File fMsg = new File(getDataFolder(), "message.yml");
        if (!fMsg.exists()) saveResource("message.yml", false);
        msgCfg = YamlConfiguration.loadConfiguration(fMsg);
        File fGui = new File(getDataFolder(), "gui.yml");
        if (!fGui.exists()) saveResource("gui.yml", false);
        guiCfg = YamlConfiguration.loadConfiguration(fGui);
    }

    public void sendNotify(Player p, String path, String... ph) {
        String msg = msgCfg.getString("messages." + path);
        if (msg == null) return;
        for (int i = 0; i < ph.length; i += 2) {
            msg = msg.replace(ph[i], ph[i + 1]);
        }
        p.sendMessage(format(getConfig().getString("settings.prefix") + msg));
    }

    public String format(String text) { return ColorUtils.color(text); }
    public KeyManager getKeyManager() { return keyManager; }
    public CrateManager getCrateManagerLogic() { return crateManager; }
    public FileConfiguration getMessageConfig() { return msgCfg; }
    public FileConfiguration getGuiConfig() { return guiCfg; }
    public Map<UUID, String> getSelectingCrate() { return selectingCrate; }
            }
