package me.totalfreedom.bukkittelnet.api;
import me.totalfreedom.bukkittelnet.SocketListener;
import me.totalfreedom.bukkittelnet.TelnetConfigLoader.TelnetConfig;
import me.totalfreedom.bukkittelnet.session.ClientSession;
import java.util.List;

public interface Server
{
    void startServer();
    void stopServer();

    @Deprecated
    SocketListener getSocketListener();
    TelnetConfig getConfig();
    List<ClientSession> getSessions();
}
