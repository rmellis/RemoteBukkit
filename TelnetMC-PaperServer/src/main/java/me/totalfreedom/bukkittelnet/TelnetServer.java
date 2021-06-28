package me.totalfreedom.bukkittelnet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import lombok.Getter;
import me.totalfreedom.bukkittelnet.TelnetConfigLoader.TelnetConfig;
import me.totalfreedom.bukkittelnet.api.Server;
import me.totalfreedom.bukkittelnet.session.ClientSession;

public class TelnetServer implements Server
{

    @Getter
    private final BukkitTelnet plugin;
    @Getter
    private final TelnetConfig config;
    @Getter
    private SocketListener socketListener;

    public TelnetServer(BukkitTelnet plugin, TelnetConfig config)
    {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void startServer()
    {
        // If the server is running, stop it
        stopServer();

        // Server address, optional.
        final InetAddress hostAddress;

        final String address = config.getAddress();
        if (address != null)
        {
            try
            {
                hostAddress = InetAddress.getByName(address);
            }
            catch (UnknownHostException ex)
            {
                TelnetLogger.severe("Cannot start server - Invalid address: " + config.getAddress());
                TelnetLogger.severe(ex);
                return;
            }
        }
        else
        {
            hostAddress = null;
        }

        // Server socket
        final ServerSocket serversocket;

        try
        {
            if (hostAddress == null)
            {
                serversocket = new ServerSocket(config.getPort());
            }
            else
            {
                serversocket = new ServerSocket(config.getPort(), 50, hostAddress);
            }
        }
        catch (IOException ex)
        {
            TelnetLogger.severe("Cannot start server - " + "Cant bind to " + (hostAddress == null ? "*" : hostAddress) + ":" + config.getPort());
            TelnetLogger.severe(ex);
            return;
        }

        socketListener = new SocketListener(this, serversocket);
        socketListener.start();

        final String host = serversocket.getInetAddress().getHostAddress().replace("0.0.0.0", "*");
        TelnetLogger.info("Server started on " + host + ":" + serversocket.getLocalPort());
    }

    @Override
    public void stopServer()
    {
        if (socketListener == null)
        {
            return;
        }

        socketListener.stopServer();
    }

    @Override
    public List<ClientSession> getSessions()
    {
        return socketListener.getSessions();
    }

}
