package me.totalfreedom.bukkittelnet.api;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TelnetRequestUsageEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    public TelnetRequestUsageEvent()
    {
        super(!Bukkit.getServer().isPrimaryThread());
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
