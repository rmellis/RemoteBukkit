package me.totalfreedom.bukkittelnet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import me.totalfreedom.bukkittelnet.session.ClientSession;
import me.totalfreedom.bukkittelnet.session.FilterMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

public class TelnetLogAppender extends AbstractAppender
{
    private static final long serialVersionUID = 234234L;

    private final Set<ClientSession> sessions;
    private final SimpleDateFormat dateFormat;

    public TelnetLogAppender()
    {
        super("telnetMC", null, null, true, Property.EMPTY_ARRAY);

        this.sessions = new HashSet<>();
        this.dateFormat = new SimpleDateFormat("HH:mm:ss");

        super.start();
    }

    public Set<ClientSession> getSessions()
    {
        return Collections.unmodifiableSet(sessions);
    }

    public boolean addSession(ClientSession session)
    {
        return sessions.add(session);
    }

    public boolean removeSession(ClientSession session)
    {
        return sessions.remove(session);
    }

    public void removeAllSesssions()
    {
        sessions.clear();
    }

    @Override
    public void append(LogEvent event)
    {
        final String message = event.getMessage().getFormattedMessage();

        for (ClientSession session : sessions)
        {
            try
            {
                if (!session.syncIsConnected())
                {
                    continue;
                }

                boolean chat = message.startsWith("<")
                        || message.startsWith("[Server")
                        || message.startsWith("[CONSOLE") || message.startsWith("[TotalFreedomMod] [ADMIN]");

                if (session.getFilterMode() == FilterMode.CHAT_ONLY && !chat)
                {
                    continue;
                }

                if (session.getFilterMode() == FilterMode.NON_CHAT_ONLY && chat)
                {
                    continue;
                }

                session.writeRawLine(formatMessage(message, event));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private String formatMessage(String message, LogEvent event)
    {
        final StringBuilder builder = new StringBuilder();
        final Throwable ex = event.getThrown();

        builder.append("[");
        builder.append(dateFormat.format(new Date()));
        builder.append(" ");
        builder.append(event.getLevel().name().toUpperCase());
        builder.append("]: ");
        builder.append(message);

        if (ex != null)
        {
            StringWriter writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            builder.append(writer);
        }

        return builder.toString();
    }

    public void attach()
    {
        ((Logger) LogManager.getRootLogger()).addAppender(this);
    }

    public void deattach()
    {
        ((Logger) LogManager.getRootLogger()).removeAppender(this);
    }

}
