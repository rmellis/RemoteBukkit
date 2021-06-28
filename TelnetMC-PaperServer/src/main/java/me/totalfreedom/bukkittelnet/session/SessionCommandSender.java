package me.totalfreedom.bukkittelnet.session;

import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class SessionCommandSender implements ConsoleCommandSender
{

    private final ClientSession session;

    public SessionCommandSender(ClientSession session)
    {
        this.session = session;
    }

    @Override
    public void sendMessage(String message)
    {
        session.writeRawLine(message);
    }

    @Override
    public void sendMessage(String[] messages)
    {
        for (String message : messages)
        {
            sendMessage(message);
        }
    }

    @Override
    public String getName()
    {
        return this.session.getUserName();
    }

    @Override
    public Server getServer()
    {
        return Bukkit.getServer();
    }

    @Override
    public boolean isPermissionSet(String name)
    {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm)
    {
        return true;
    }

    @Override
    public boolean hasPermission(String name)
    {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm)
    {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
    {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin)
    {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
    {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks)
    {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment)
    {
    }

    @Override
    public void recalculatePermissions()
    {
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        return null;
    }

    @Override
    public boolean isOp()
    {
        return true;
    }

    @Override
    public void setOp(boolean value)
    {
    }

    @Override
    public boolean isConversing()
    {
        return false;
    }

    @Override
    public void acceptConversationInput(String string)
    {
    }

    @Override
    public boolean beginConversation(Conversation c)
    {
        return false;
    }

    @Override
    public void abandonConversation(Conversation c)
    {
    }

    @Override
    public void abandonConversation(Conversation c, ConversationAbandonedEvent cae)
    {
    }

    @Override
    public void sendRawMessage(String string)
    {
        session.writeRawLine(string);
    }

    @Override
    public Spigot spigot()
    {
        return new Spigot()
        {

            @Override
            public void sendMessage(BaseComponent component)
            {
                SessionCommandSender.this.sendMessage(component.toPlainText());
            }

            @Override
            public void sendMessage(BaseComponent... components)
            {
                for (BaseComponent bc : components)
                {
                    sendMessage(bc);
                }
            }

        };
    }

    @Override
    public void sendMessage(UUID uuid, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendRawMessage(UUID uuid, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}