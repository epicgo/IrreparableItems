package io.github.epicgo.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Utilidad para interactuar con la instancia {@link CommandMap} del servidor.
 */
public final class CommandMapUtil {

    private static final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;
    private static final Field COMMAND_MAP_FIELD;

    static {
        try {
            // Acceso al constructor privado de PluginCommand
            COMMAND_CONSTRUCTOR = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            COMMAND_CONSTRUCTOR.setAccessible(true);

            // Acceso al campo privado commandMap de SimplePluginManager
            COMMAND_MAP_FIELD = SimplePluginManager.class.getDeclaredField("commandMap");
            COMMAND_MAP_FIELD.setAccessible(true);
        } catch (final NoSuchMethodException | NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Obtiene la instancia {@link CommandMap} del servidor.
     *
     * @return La instancia {@link CommandMap}.
     */
    public static CommandMap getCommandMap() {
        try {
            return ((CommandMap) COMMAND_MAP_FIELD.get(Bukkit.getServer().getPluginManager()));
        } catch (final Exception e) {
            throw new RuntimeException("No se pudo obtener CommandMap", e);
        }
    }

    /**
     * Registra un comando en el servidor.
     *
     * @param plugin  El plugin que registra el comando.
     * @param command El comando que se registrará.
     * @return El {@link Command} registrado o null si el comando ya está registrado.
     */
    public static Command registerCommand(Plugin plugin, Command command) {
        CommandMap commandMap = getCommandMap();

        // Verifica si el comando ya está registrado
        if (commandMap.getCommand(command.getName()) != null) {
            Bukkit.getLogger().warning("El comando '" + command.getName() + "' ya está registrado.");
            return null;
        }

        // Registra el comando y lo devuelve
        commandMap.register(plugin.getName(), command);
        return command;
    }
}
