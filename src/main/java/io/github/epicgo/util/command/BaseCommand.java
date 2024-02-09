package io.github.epicgo.util.command;

import lombok.Setter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class BaseCommand extends BukkitCommand {

    private final boolean forPlayersOnly;
    @Setter
    private boolean executeAsync;

    public BaseCommand(String name, List<String> aliases, String permission, boolean forPlayersOnly) {
        super(name);

        this.setAliases(aliases);
        this.setPermission(permission);
        this.forPlayersOnly = forPlayersOnly;
    }

    protected boolean checkConsoleSender(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores");
            return false;
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (this.forPlayersOnly && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "¡Solo jugadores!");
            return true;
        }

        if (this.getPermission() != null && !sender.hasPermission(this.getPermission())) {
            sender.sendMessage(ChatColor.RED + "¡No tienes permisos!");
            return true;
        }

        if (this.executeAsync) {
            final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> this.execute(sender, args));
        } else {
            this.execute(sender, args);
        }

        return true;
    }

    public abstract void execute(CommandSender sender, String[] args);

    protected boolean checkPlayer(CommandSender sender, Player player, String playerName) {
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "¡El jugador no está en línea!");
            return false;
        }
        return true;
    }

    protected boolean checkNumber(CommandSender sender, String number) {
        if (!NumberUtils.isNumber(number)) {
            sender.sendMessage(ChatColor.RED + "Es un número inválido.");
            return false;
        }
        return true;
    }

    protected boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "¡No tienes permisos!");
            return false;
        }
        return true;
    }
}
