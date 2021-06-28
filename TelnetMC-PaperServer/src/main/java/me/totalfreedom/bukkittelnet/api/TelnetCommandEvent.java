package me.totalfreedom.bukkittelnet.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TelnetCommandEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private CommandSender sender;
    private String command;
    public TelnetCommandEvent(CommandSender sender, String command)
    {
        super(!Bukkit.getServer().isPrimaryThread());
        this.cancelled = false;
        this.sender = sender;
        this.command = command;
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
    public CommandSender getSender()
    {
        return sender;
    }
    public void setSender(CommandSender sender)
    {
        this.sender = sender;
    }
    public String getCommand()
    {
        return command;
    }
    public void setCommand(String command)
    {
        if (command == null)
            command = "";

        this.command = command;
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
