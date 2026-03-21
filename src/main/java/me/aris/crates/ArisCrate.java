package me.aris.crates;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import java.io.File;

public class ArisCrate extends JavaPlugin {
    private CrateManager crateManager;
    private File keyFile, msgFile;
    private FileConfiguration keyConfig, msgConfig;
    private boolean isFolia;

    @Override
    public void onEnable() {
        try {
            Class.forName("io.papermc.paper.threadedregionsapi.RegionScheduler");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
        loadFiles();
        crateManager = new CrateManager(this);
        CrateCommand cmd = new CrateCommand(this);
        getCommand("ariscrate").setExecutor(cmd);
        getCommand("ariscrate").setTabCompleter(cmd);
        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CrateExpansion(this).register();
        }
    }

    public void loadFiles() {
        saveDefaultConfig();
        reloadConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File cratesFolder = new File(getDataFolder(), "crates");
        if (!cratesFolder.exists()) cratesFolder.mkdirs();
        msgFile = new File(getDataFolder(), "message.yml");
        if (!msgFile.exists()) saveResource("message.yml", false);
        msgConfig = YamlConfiguration.loadConfiguration(msgFile);
        keyFile = new File(getDataFolder(), "playerkey.yml");
        if (!keyFile.exists()) try { keyFile.createNewFile(); } catch (Exception e) {}
        keyConfig = YamlConfiguration.loadConfiguration(keyFile);
    }

    public void sendMsg(org.bukkit.entity.Player p, String path, String... replace) {
        if (!msgConfig.contains("messages." + path)) return;
        String msg = crateManager.translateHex(msgConfig.getString("messages." + path + ".text"));
        for (int i = 0; i < replace.length; i += 2) msg = msg.replace(replace[i], replace[i+1]);
        if (msgConfig.getBoolean("messages." + path + ".chat", true)) p.sendMessage(msg);
        if (msgConfig.getBoolean("messages." + path + ".actionbar", false)) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
        }
    }

    public boolean isFolia() { return isFolia; }
    public FileConfiguration getKeyConfig() { return keyConfig; }
    public void saveKeyConfig() { try { keyConfig.save(keyFile); } catch (Exception e) {} }
    public CrateManager getCrateManager() { return crateManager; }
            }
