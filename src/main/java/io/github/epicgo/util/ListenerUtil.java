package io.github.epicgo.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.function.Consumer;

public class ListenerUtil {

    public static void registerListener(Plugin plugin, Listener listener) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(listener, plugin);
    }

    public static void registerListener(Plugin plugin, Listener listener, Consumer<Listener> after) {
        registerListener(plugin, listener);
        after.accept(listener);
    }

    public static void unregisterListener(Plugin plugin, Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public static <T extends Event> void listen(Listener listener, Class<T> event, Consumer<T> handler) {
        Bukkit.getPluginManager().registerEvent(event, listener, EventPriority.NORMAL, (l, e) -> handler.accept((T) e), Bukkit.getPluginManager().getPlugins()[0], true);
    }

    public static <T extends Event> void listen(Listener listener, Class<T> event, EventPriority priority, Consumer<T> handler) {
        Bukkit.getPluginManager().registerEvent(event, listener, priority, (l, e) -> handler.accept((T) e), Bukkit.getPluginManager().getPlugins()[0], true);
    }

    public static <T extends Event> void listen(Plugin plugin, Class<T> event, EventPriority priority, Consumer<T> handler) {
        Bukkit.getPluginManager().registerEvent(event, new Listener() {
        }, priority, (l, e) -> handler.accept((T) e), plugin, true);
    }

    public static void unregisterAllListeners(Plugin plugin) {
        HandlerList.unregisterAll(plugin);
    }
}
