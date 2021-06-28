package me.totalfreedom.bukkittelnet.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class TelnetPreLoginEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private String name;
    private final String ip;
    private boolean bypassPassword;
    public TelnetPreLoginEvent(String ip, String name, boolean bypassPassword)
    {
        super(!Bukkit.getServer().isPrimaryThread());
        this.ip = ip;
        this.name = name;
        this.bypassPassword = bypassPassword;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }
    public void setCancelled(boolean cancel)
    {
        cancelled = cancel;
    }
    public String getIp()
    {
        return ip;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public boolean canBypassPassword()
    {
        return bypassPassword;
    }
    public void setBypassPassword(boolean bypassPassword)
    {
        this.bypassPassword = bypassPassword;
    }
    public HandlerList getHandlers()
    {
        return handlers;
    }
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
