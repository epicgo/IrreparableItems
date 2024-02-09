package io.github.epicgo.util;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ConfigUtil {

    private final File file;
    private final YamlConfiguration bukkitConfiguration;

    public ConfigUtil(final JavaPlugin plugin, final String fileName) {
        this.file = initializeFile(plugin, fileName);
        this.bukkitConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    private File initializeFile(final JavaPlugin plugin, final String fileName) {
        final File pluginDataFolder = plugin.getDataFolder();
        if (!pluginDataFolder.exists()) {
            pluginDataFolder.mkdir();
        }

        final File configFile = new File(pluginDataFolder, fileName + ".yml");
        if (!configFile.exists()) {
            try {
                // Guardar el recurso predeterminado si el archivo no existe
                plugin.saveResource(fileName + ".yml", false);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return configFile;
    }

    // Métodos básicos de obtención y configuración de valores
    public boolean contains(final String path) {
        return bukkitConfiguration.contains(path);
    }

    public void insert(final String path, final Object value) {
        bukkitConfiguration.set(path, value);
    }

    public Object get(final String path) {
        return bukkitConfiguration.get(path, "");
    }

    public int getInt(final String path) {
        return bukkitConfiguration.getInt(path, 0);
    }

    public double getDouble(final String path) {
        return bukkitConfiguration.getDouble(path, 0.0);
    }

    public long getLong(final String path) {
        return bukkitConfiguration.getLong(path, 0L);
    }

    public float getFloat(final String path) {
        return (float) bukkitConfiguration.getDouble(path, 0F);
    }

    public boolean getBoolean(final String path) {
        return bukkitConfiguration.getBoolean(path, false);
    }

    public String getString(final String path) {
        return ChatColor.translateAlternateColorCodes('&', bukkitConfiguration.getString(path, ""));
    }

    public List<String> getStringList(final String path) {
        return bukkitConfiguration.getStringList(path)
                .stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList());
    }

    public List<String> getStringList(final String path, final boolean check) {
        // Comprobar si la configuración contiene el camino antes de obtener la lista
        if (check && !bukkitConfiguration.contains(path)) {
            return new ArrayList<>();
        }

        return getStringList(path);
    }

    public ConfigurationSection getSection(final String path) {
        return bukkitConfiguration.getConfigurationSection(path);
    }

    // Métodos de carga y guardado
    public void load() {
        try {
            bukkitConfiguration.load(file);
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            bukkitConfiguration.save(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
