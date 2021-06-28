package me.totalfreedom.bukkittelnet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.totalfreedom.bukkittelnet.session.ClientSession;

public class SocketListener extends Thread
{

    public static long LISTEN_THRESHOLD_MILLIS = 10000;
    //
    private final TelnetServer telnet;
    private final ServerSocket serverSocket;
    private final List<ClientSession> clientSessions = new ArrayList<>();
    private final Map<InetAddress, Long> recentIPs = new HashMap<>();

    public SocketListener(TelnetServer telnet, ServerSocket serverSocket)
    {
        this.telnet = telnet;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run()
    {
        while (!serverSocket.isClosed())
        {
            final Socket clientSocket;

            try
            {
                clientSocket = serverSocket.accept();
            }
            catch (IOException ex)
            {
                continue;
            }

            // Remove old entries
            recentIPs.entrySet().removeIf(inetAddressLongEntry -> inetAddressLongEntry.getValue() + LISTEN_THRESHOLD_MILLIS < System.currentTimeMillis());

            final InetAddress addr = clientSocket.getInetAddress();
            if (addr == null)
            {
                return; // Socket is not connected
            }

            // Connect Threshold
            if (recentIPs.containsKey(addr))
            {
                recentIPs.put(addr, System.currentTimeMillis());

                try
                {
                    final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    writer.write("Connection throttled. Please wait a minute and try again.\r\n");
                    writer.flush();
                }
                catch (IOException ignored)
                {
                }

                try
                {
                    clientSocket.close();
                }
                catch (IOException ignored)
                {
                }

                continue;
            }

            recentIPs.put(addr, System.currentTimeMillis());

            final ClientSession clientSession = new ClientSession(telnet, clientSocket);
            clientSessions.add(clientSession);
            clientSession.start();
            removeDisconnected();
        }

        TelnetLogger.info("Server closed");
    }

    private void removeDisconnected()
    {
        final Iterator<ClientSession> it = clientSessions.iterator();

        while (it.hasNext())
        {
            final ClientSession session = it.next();

            if (!session.syncIsConnected())
            {
                telnet.getPlugin().appender.removeSession(session);
                it.remove();
            }
        }
    }

    public void triggerPlayerListUpdates(final String playerListData)
    {
        clientSessions.forEach(session ->
                session.syncTriggerPlayerListUpdate(playerListData));
    }

    public void triggerDataUsageUpdates(final String usageData)
    {
        clientSessions.forEach(session ->
                session.syncUsageUpdate(usageData));
    }

    public void stopServer()
    {
        try
        {
            serverSocket.close();
        }
        catch (IOException ex)
        {
            TelnetLogger.severe(ex);
        }

        for (ClientSession session : clientSessions)
        {
            session.syncTerminateSession();
        }

        clientSessions.clear();

    }

    public List<ClientSession> getSessions()
    {
        return Collections.unmodifiableList(clientSessions);
    }
}
