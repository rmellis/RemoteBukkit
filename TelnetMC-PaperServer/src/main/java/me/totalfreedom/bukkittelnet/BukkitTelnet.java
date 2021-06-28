package me.totalfreedom.bukkittelnet;

import me.totalfreedom.bukkittelnet.api.Server;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitTelnet extends JavaPlugin
{

    private BukkitTelnet plugin;
    public TelnetConfigLoader config;
    public TelnetServer telnet;
    public TelnetLogAppender appender;
    public PlayerEventListener listener;

    @Override
    public void onLoad()
    {
        plugin = this;
        config = new TelnetConfigLoader(plugin);
        telnet = new TelnetServer(plugin, config.getConfig());
        appender = new TelnetLogAppender();
        listener = new PlayerEventListener(plugin);

        TelnetLogger.setPluginLogger(plugin.getLogger());
        TelnetLogger.setServerLogger(Bukkit.getLogger());
    }

    @Override
    public void onEnable()
    {
        config.load();

        appender.attach();

        telnet.startServer();

        getServer().getPluginManager().registerEvents(listener, plugin);

        getServer().getServicesManager().register(Server.class, telnet, this, ServicePriority.Normal);

        TelnetLogger.info(plugin.getName() + " v" + plugin.getDescription().getVersion() + " enabled");
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(plugin);

        appender.deattach();
        appender.removeAllSesssions();

        telnet.stopServer();

        TelnetLogger.info(plugin.getName() + " disabled");
    }
}
