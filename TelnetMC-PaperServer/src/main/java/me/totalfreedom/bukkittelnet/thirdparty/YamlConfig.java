package me.totalfreedom.bukkittelnet.thirdparty;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Represents a definable YAML configuration.
 *
 * @see YamlConfiguration
 */
public class YamlConfig extends YamlConfiguration
{

    private final Plugin PLUGIN;
    private final File CONFIG_FILE;
    private final boolean COPY_DEFAULTS;

    /**
     * Creates a new YamlConfig instance.
     *
     * <p>
     * Example:
     * <pre>
     * YamlConfig config = new YamlConfig(this, "config.yml", true);
     * config.load();
     * </pre></p>
     *
     * @param plugin The plugin to which the config belongs.
     * @param fileName The filename of the config file.
     * @param copyDefaults If the defaults should be copied and/loaded from a config in the plugin jar-file.
     */
    public YamlConfig(Plugin plugin, String fileName, boolean copyDefaults)
    {
        this(plugin, FileUtils.getPluginFile(plugin, fileName), copyDefaults);
    }

    /**
     * Creates a new YamlConfig instance.
     *
     * <p>
     * Example:
     * <pre>
     * YamlConfig config = new YamlConfig(this, new File(plugin.getDataFolder() + "/players", "DarthSalamon.yml"), false);
     * config.load();
     * </pre></p>
     *
     * @param plugin The plugin to which the config belongs.
     * @param file The file of the config file.
     * @param copyDefaults If the defaults should be copied and/loaded from a config in the plugin jar-file.
     */
    public YamlConfig(Plugin plugin, File file, boolean copyDefaults)
    {
        this.PLUGIN = plugin;
        this.CONFIG_FILE = file;
        this.COPY_DEFAULTS = copyDefaults;
    }

    /**
     * Saves the configuration to the predefined file.
     *
     * @see #YamlConfig(Plugin, String, boolean)
     */
    public void save()
    {
        try
        {
            super.save(CONFIG_FILE);
        }
        catch (Exception ex)
        {
            PLUGIN.getLogger().severe("Could not save configuration file: " + CONFIG_FILE.getName());
            PLUGIN.getLogger().severe(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Loads the configuration from the predefined file.
     *
     * <p>
     * Optionally, if loadDefaults has been set to true, the file will be copied over from the default inside the jar-file of the owning plugin.</p>
     *
     * @see #YamlConfig(Plugin, String, boolean)
     */
    public void load()
    {
        try
        {
            if (COPY_DEFAULTS)
            {
                if (!CONFIG_FILE.exists())
                {
                    CONFIG_FILE.getParentFile().mkdirs();
                    try
                    {
                        FileUtils.copy(PLUGIN.getResource(CONFIG_FILE.getName()), CONFIG_FILE);
                    }
                    catch (IOException ex)
                    {
                        PLUGIN.getLogger().severe("Could not write default configuration file: " + CONFIG_FILE.getName());
                        PLUGIN.getLogger().severe(ExceptionUtils.getStackTrace(ex));
                    }
                    PLUGIN.getLogger().info("Installed default configuration " + CONFIG_FILE.getName());
                }

                super.addDefaults(getDefaultConfig());
            }

            super.load(CONFIG_FILE);
        }
        catch (Exception ex)
        {
            PLUGIN.getLogger().severe("Could not load configuration file: " + CONFIG_FILE.getName());
            PLUGIN.getLogger().severe(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Returns the raw YamlConfiguration this config is based on.
     *
     * @return The YamlConfiguration.
     * @see YamlConfiguration
     */
    public YamlConfiguration getConfig()
    {
        return this;
    }

    /**
     * Returns the default configuration as been stored in the jar-file of the owning plugin.
     *
     * @return The default configuration.
     */
    public YamlConfiguration getDefaultConfig()
    {
        final YamlConfiguration DEFAULT_CONFIG = new YamlConfiguration();
        try
        {
            final String configString = IOUtils.toString(new InputStreamReader(PLUGIN.getResource(CONFIG_FILE.getName())));
            DEFAULT_CONFIG.loadFromString(configString);
        }
        catch (Throwable ex)
        {
            PLUGIN.getLogger().severe("Could not load default configuration: " + CONFIG_FILE.getName());
            PLUGIN.getLogger().severe(ExceptionUtils.getStackTrace(ex));
            return null;
        }
        return DEFAULT_CONFIG;
    }
}
