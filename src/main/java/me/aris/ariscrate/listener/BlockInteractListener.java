package me.aris.ariscrate.listener;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.Crate;
import me.aris.ariscrate.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BlockInteractListener implements Listener {
    private final ArisCrate plugin;

    public BlockInteractListener(ArisCrate plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null) {
                Crate crate = plugin.getCrateManager().getAllCrates().stream()
                    .filter(c -> isSameOrAdjacentBlock(c.getLocation(), event.getClickedBlock().getLocation()))
                    .findFirst().orElse(null);
                    
                if (crate != null) {
                    Player player = event.getPlayer();
                    
                    String permission = crate.getCrateConfig().getString("permission");
                    if (permission != null && !player.hasPermission(permission)) {
                        player.sendMessage(plugin.getMessageManager().getMessage("crate.need-permission")
                            .replace("{permission}", permission));
                        event.setCancelled(true);
                        return;
                    }
                    
                    event.setCancelled(true);
                    plugin.getSelectingCrate().put(player.getUniqueId(), crate.getName());
                    openChooseGui(player, crate);
                }
            }
        }
    }

    private void openChooseGui(Player player, Crate crate) {
        String title = plugin.getGuiConfig().getString("choose-gui.title", "&8ᴄʜᴏᴏsᴇ 1 ɪᴛᴇᴍ");
        int size = plugin.getGuiConfig().getInt("choose-gui.size", 27);
        
        Inventory inv = plugin.getServer().createInventory(null, size, ColorUtils.color(title));
        
        String bgMaterial = plugin.getGuiConfig().getString("choose-gui.background.material
