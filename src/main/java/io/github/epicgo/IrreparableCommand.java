package io.github.epicgo;

import com.google.common.base.Joiner;
import io.github.epicgo.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IrreparableCommand extends BaseCommand {

    private final IrreparableItems instance;

    public IrreparableCommand(IrreparableItems instance) {
        super("irreparable", Arrays.asList("irr", "unrepairable"), "irreparable.bypass", true);

        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            instance.getMainConfig().getStringList("irreparable-command-help").forEach(sender::sendMessage);
            return;
        }

        if (args[0].equalsIgnoreCase("namelist")) {
            sender.sendMessage(instance.getMainConfig().getString("irreparable-name-list") + Joiner.on(", ").join(instance.getIrreparableNames()));
            return;
        }

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("repairable")) {
            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED + "!No tienes ningun item en la mano para convertir¡");
                return;
            }

            if (itemInHand.getType().isBlock()) {
                sender.sendMessage(ChatColor.RED + "!No puedes convertir bloques en reparables¡");
                return;
            }

            if (!checkItem(itemInHand, false)) {
                sender.sendMessage(ChatColor.RED + "!No puedes convertir este item a reparable¡");
                return;
            }

            checkItem(itemInHand, true);
            sender.sendMessage(instance.getMainConfig().getString("irreparable-repairable"));
            return;
        }

        if (args[0].equalsIgnoreCase("unrepairable")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /irreparable unrepairable <index-0>");
                return;
            }

            String unrepairableName;

            try {
                unrepairableName = instance.getIrreparableNames().get(Integer.parseInt(args[1]));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "!No se ha encontrado ningun nombre unrepairable¡");
                return;
            }

            if(unrepairableName == null) {
                sender.sendMessage(ChatColor.RED + "!No se ha encontrado ningun nombre¡");
                return;
            }

            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED + "!No tienes ningun item en la mano para convertir¡");
                return;
            }

            if (itemInHand.getType().isBlock()) {
                sender.sendMessage(ChatColor.RED + "!No puedes convertir bloques en reparables¡");
                return;
            }

            if (checkItem(itemInHand, false)) {
                sender.sendMessage(ChatColor.RED + "!Ya tienes este item irreparable¡");
                return;
            }

            ItemMeta itemMeta = itemInHand.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if(lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', unrepairableName));

            itemMeta.setLore(lore);
            itemInHand.setItemMeta(itemMeta);
            sender.sendMessage(instance.getMainConfig().getString("irreparable-unrepairable"));
            return;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            instance.getMainConfig().load();
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
            return;
        }
    }

    public boolean checkItem(ItemStack itemStack, boolean remove) {
        ItemMeta meta = itemStack.getItemMeta();

        // Comprobamos si el ítem tiene metadatos (lore)
        if (meta != null && meta.hasLore()) {
            for (int index = 0; index < meta.getLore().size(); index++) {
                String lore = meta.getLore().get(index);
                if (instance.getIrreparableNames().contains(lore)) {
                    if (remove) {
                        meta.getLore().remove(index);
                    }
                    return true;
                }
            }
        }
        itemStack.setItemMeta(meta);

        return false;
    }
}
