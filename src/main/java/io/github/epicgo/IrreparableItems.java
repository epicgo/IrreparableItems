package io.github.epicgo;

import io.github.epicgo.util.CommandMapUtil;
import io.github.epicgo.util.ConfigUtil;
import io.github.epicgo.util.ListenerUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class IrreparableItems extends JavaPlugin implements Listener {

    private ConfigUtil mainConfig;
    private List<String> irreparableNames = new ArrayList<>();

    @Override
    public void onLoad() {
        mainConfig = new ConfigUtil(this, "config");
    }

    @Override
    public void onEnable() {
        loadConfiguration();
        loadListeners();
        loadCommands();
    }

    @Override
    public void onDisable() {

    }

    private void loadConfiguration() {
        irreparableNames = mainConfig.getStringList("irreparable-names");
    }

    private void loadListeners() {
        ListenerUtil.registerListener(this, this);
    }

    private void loadCommands() {
        CommandMapUtil.registerCommand(this, new IrreparableCommand(this));
    }

    @EventHandler
    public void onAnvilInteract(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            Inventory inventory = event.getInventory();
            if (inventory instanceof AnvilInventory) {
                AnvilInventory anvilInventory = (AnvilInventory) inventory;
                InventoryView view = event.getView();

                int rawSlot = event.getRawSlot();
                if (rawSlot == view.convertSlot(rawSlot)) {
                    if (rawSlot == 2) {
                        ItemStack[] contents = anvilInventory.getContents();
                        ItemStack prepare = contents[0];
                        ItemStack analize = contents[1];

                        if (checkItem(prepare) || checkItem(analize)) {
                            player.closeInventory();

                            event.setResult(Event.Result.DENY);
                            player.sendMessage(ChatColor.RED + "!No puedes reparar este item¡");
                        }
                    }
                }
            }
        }
    }

    public boolean checkItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().stream().anyMatch(s -> irreparableNames.contains(s));
    }
}
