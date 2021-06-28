package me.totalfreedom.bukkittelnet.api;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TelnetRequestDataTagsEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Map<Player, Map<String, Object>> dataTags = new HashMap<>();
    public TelnetRequestDataTagsEvent()
    {
        super(!Bukkit.getServer().isPrimaryThread());
        for (final Player player : Bukkit.getServer().getOnlinePlayers())
            dataTags.put(player, new HashMap<>());
    }
    public Map<Player, Map<String, Object>> getDataTags()
    {
        return dataTags;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
