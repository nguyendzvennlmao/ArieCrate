package me.aris.ariscrate.listener;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.CrateData;
import me.aris.ariscrate.utils.ColorUtils;
import me.aris.ariscrate.utils.DogAdonisHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuListener implements Listener {
    private final ArisCrate plugin;
    public MenuListener(ArisCrate plugin) { this.plugin = plugin; }

    @EventHandler
    public void onEditClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().startsWith("Edit: ")) {
            String name = e.getView().getTitle().replace("Edit: ", "");
            CrateData c = plugin.getCrateManager().get(name);
            if (c != null) {
                c.getItems().clear();
                for (int i = 0; i < e.getInventory().getSize(); i++) {
                    ItemStack item = e.getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) c.getItems().put(i, item);
                }
                plugin.getCrateManager().save();
                e.getPlayer().sendMessage("§aĐã lưu nội dung Crate.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();
        String chooseT = ColorUtils.color(plugin.getGuiConfig().getString("choose-gui.title"));
        String confirmT = ColorUtils.color(plugin.getGuiConfig().getString("confirm-gui.title"));
        if (!title.equals(chooseT) && !title.equals(confirmT)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getView().getTopInventory()) return;
        if (title.equals(chooseT)) handleChoose(e, p);
        else handleConfirm(e, p);
    }

    private void handleChoose(InventoryClickEvent e, Player p) {
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        String name = plugin.getSelectingCrate().get(p.getUniqueId());
        if (plugin.getKeyManager().getKeys(p.getUniqueId(), name) <= 0) {
            p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.error")), 1, 1);
            plugin.sendNotify(p, "no-key", "%crate%", name);
            return;
        }
        openConfirm(p, item.clone());
    }

    private void handleConfirm(InventoryClickEvent e, Player p) {
        int slot = e.getRawSlot();
        var gui = plugin.getGuiConfig();
        if (slot == gui.getInt("confirm-gui.cancel-slot")) {
            p.closeInventory();
        } else if (slot == gui.getInt("confirm-gui.confirm-slot")) {
            ItemStack reward = e.getInventory().getItem(gui.getInt("confirm-gui.display-slot"));
            String name = plugin.getSelectingCrate().get(p.getUniqueId());
            if (reward != null && plugin.getKeyManager().takeKeys(p.getUniqueId(), name, 1)) {
                p.getInventory().addItem(DogAdonisHook.unfreeze(reward.clone()));
                p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.open-success")), 1, 1);
                plugin.sendNotify(p, "open-success", "%crate%", name, "%item%", reward.getType().name());
                p.closeInventory();
            }
        }
    }

    private void openConfirm(Player p, ItemStack item) {
        var gui = plugin.getGuiConfig();
        Inventory inv = Bukkit.createInventory(null, gui.getInt("confirm-gui.size"), ColorUtils.color(gui.getString("confirm-gui.title")));
        
        ItemStack confirm = new ItemStack(Material.valueOf(gui.getString("confirm-gui.items.confirm.material")));
        ItemMeta cm = confirm.getItemMeta();
        cm.setDisplayName(ColorUtils.color(gui.getString("confirm-gui.items.confirm.name")));
        confirm.setItemMeta(cm);
        
        ItemStack cancel = new ItemStack(Material.valueOf(gui.getString("confirm-gui.items.cancel.material")));
        ItemMeta cam = cancel.getItemMeta();
        cam.setDisplayName(ColorUtils.color(gui.getString("confirm-gui.items.cancel.name")));
        cancel.setItemMeta(cam);

        inv.setItem(gui.getInt("confirm-gui.confirm-slot"), confirm);
        inv.setItem(gui.getInt("confirm-gui.cancel-slot"), cancel);
        inv.setItem(gui.getInt("confirm-gui.display-slot"), item);
        p.openInventory(inv);
    }
    }
