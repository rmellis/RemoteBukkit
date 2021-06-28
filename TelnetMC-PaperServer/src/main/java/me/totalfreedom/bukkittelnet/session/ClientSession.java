package me.totalfreedom.bukkittelnet.session;

import me.totalfreedom.bukkittelnet.api.TelnetPreLoginEvent;
import me.totalfreedom.bukkittelnet.api.TelnetCommandEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import me.totalfreedom.bukkittelnet.TelnetLogger;
import me.totalfreedom.bukkittelnet.TelnetServer;
import me.totalfreedom.bukkittelnet.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

public final class ClientSession extends Thread
{

    public static final Pattern NON_ASCII_FILTER = Pattern.compile("[^\\x20-\\x7E]");
    public static final Pattern AUTH_INPUT_FILTER = Pattern.compile("[^_a-zA-Z0-9]");
    public static final Pattern COMMAND_INPUT_FILTER = Pattern.compile("^[^_a-zA-Z0-9/?!.]+");
    //
    private final TelnetServer telnet;
    private final Socket clientSocket;
    private final String clientAddress;
    private final SessionCommandSender commandSender;
    //
    private FilterMode filterMode = FilterMode.NONE;
    //
    private BufferedWriter writer;
    private BufferedReader reader;
    private String username = "";
    private volatile boolean terminated = false;
    private volatile boolean authenticated = false;
    private boolean enhancedMode = false;
    private boolean enhancedPlusMode = false;

    public ClientSession(TelnetServer telnet, Socket clientSocket)
    {
        this.telnet = telnet;
        this.clientSocket = clientSocket;
        this.clientAddress = clientSocket.getInetAddress().getHostAddress();
        this.commandSender = new SessionCommandSender(this);
    }

    @Override
    public void run()
    {
        try
        {
            synchronized (clientSocket)
            {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            }
        }
        catch (IOException ex)
        {
            TelnetLogger.severe(ex);
            syncTerminateSession();
            return;
        }

        writeLine("Session Started.");

        if (!authenticate())
        {
            writeLine("Authentication failed.");
            syncTerminateSession();
        }

        writeLine("Logged in as " + username + ".");
        TelnetLogger.info(clientAddress + " logged in as \"" + username + "\".");

        // Start feeding data to the client.
        telnet.getPlugin().appender.addSession(this);

        mainLoop();

        syncTerminateSession();

        telnet.getPlugin().listener.triggerUsageUpdates();
    }

    public boolean syncIsAuthenticated()
    {
        return authenticated;
    }

    public boolean syncIsConnected()
    {
        synchronized (clientSocket)
        {
            return !clientSocket.isClosed();
        }
    }

    public synchronized void syncTerminateSession()
    {
        if (terminated)
        {
            return;
        }

        terminated = true;

        if (authenticated)
        {
            TelnetLogger.info("Closing connection: " + clientAddress + (username.isEmpty() ? "" : " (" + username + ")"));
        }

        // Stop feeding the client with data
        telnet.getPlugin().appender.removeSession(this);

        synchronized (clientSocket)
        {
            writeLine("Closing connection...");
            try
            {
                clientSocket.close();
            }
            catch (IOException ignored)
            {
            }
        }
    }

    public String getUserName()
    {
        return username;
    }

    public SessionCommandSender getCommandSender()
    {
        return commandSender;
    }

    public FilterMode getFilterMode()
    {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode)
    {
        this.filterMode = filterMode;
    }

    public void writeLine(String message)
    {
        writeRawLine("[" + (username.isEmpty() ? "" : username + "@") + "BukkitTelnet]$ " + message);
    }

    public void writeRawLine(String message)
    {
        if (writer == null || !syncIsConnected())
        {
            return;
        }

        try
        {
            writer.write(":" + ChatColor.stripColor(message) + "\r\n");
            writer.flush();
        }
        catch (IOException ignored)
        {
        }
    }

    public void flush()
    {
        if (writer == null || !syncIsConnected())
        {
            return;
        }

        try
        {
            writer.flush();
        }
        catch (IOException ignored)
        {
        }
    }

    public void syncExecuteCommand(final String command)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final Server server = Bukkit.getServer();

                final TelnetCommandEvent event = new TelnetCommandEvent(commandSender, command);
                server.getPluginManager().callEvent(event);

                if (event.isCancelled() || event.getCommand().isEmpty())
                {
                    return;
                }

                server.dispatchCommand(event.getSender(), event.getCommand());
            }
        }.runTask(telnet.getPlugin());
    }

    private boolean authenticate()
    {
        if (terminated)
        {
            return false;
        }

        boolean passAuth = false;

        // Pre-authenticate IP addresses
        if (clientAddress != null)
        {
            final Map<String, List<String>> admins = telnet.getConfig().getAdmins();

            // For every admin
            for (String name : admins.keySet())
            {

                // For every IP of each admin
                for (String ip : admins.get(name))
                {
                    if (Util.fuzzyIpMatch(ip, clientAddress, 3))
                    {
                        passAuth = true;
                        this.username = name;
                        break;
                    }
                }
            }
        }

        // TelnetPreLoginEvent authentication
        final TelnetPreLoginEvent event = new TelnetPreLoginEvent(clientAddress, username, passAuth);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled())
        {
            return false;
        }

        if (event.canBypassPassword())
        {
            if (!event.getName().isEmpty()) // If the name hasn't been set, we'll ask for it.
            {
                this.username = event.getName();
                return true;
            }

            passAuth = true;
        }

        // Username
        boolean validUsername = false;

        int tries = 0;
        while (tries++ < 3)
        {
            writeLine("Username: ");

            String input;
            try
            {
                input = reader.readLine();
            }
            catch (IOException ex)
            {
                break;
            }

            if (input == null)
            {
                break;
            }

            if (input.isEmpty())
            {
                continue;
            }

            input = AUTH_INPUT_FILTER.matcher(input).replaceAll("").trim();

            if (input.isEmpty() || input.length() > 20)
            {
                writeLine("Invalid username.");
                continue;
            }

            this.username = input;
            validUsername = true;
            break;
        }

        if (!validUsername)
        {
            return false;
        }

        // If the TelnetPreLoginEvent authenticates the password,
        // don't ask for it.
        if (passAuth)
        {
            authenticated = true;
            return true;
        }

        // Password
        tries = 0;
        while (tries++ < 3)
        {
            writeLine("Password: ");

            String input;

            try
            {
                input = reader.readLine();
            }
            catch (IOException ex)
            {
                return false;
            }

            if (input == null)
            {
                break;
            }
            if (input.isEmpty())
            {
                continue;
            }

            input = AUTH_INPUT_FILTER.matcher(input).replaceAll("").trim();

            if (telnet.getConfig().getPassword().equals(input))
            {
                authenticated = true;
                return true;
            }

            writeLine("Invalid password.");
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException ignored)
            {
            }
        }

        return false;
    }

    private void mainLoop()
    {
        if (terminated)
        {
            return;
        }

        // Process commands
        while (syncIsConnected())
        {
            // Read a command
            String command;
            try
            {
                command = reader.readLine();
            }
            catch (SocketException ex)
            {
                break;
            }
            catch (IOException ex)
            {
                TelnetLogger.severe(ex);
                break;
            }

            if (command == null)
            {
                break;
            }
            else if (command.isEmpty())
            {
                continue;
            }

            command = COMMAND_INPUT_FILTER.matcher(NON_ASCII_FILTER.matcher(command).replaceAll("")).replaceFirst("").trim();
            if (command.isEmpty())
            {
                continue;
            }

            if (command.toLowerCase().startsWith("telnet"))
            {
                executeTelnetCommand(command);
                continue;
            }

            syncExecuteCommand(command);
        }
    }

    private void executeTelnetCommand(final String command)
    {
        if ("telnet.help".equalsIgnoreCase(command))
        {
            writeLine("--- Telnet commands ---");
            writeLine("telnet.help  - See all of the telnet commands.");
            writeLine("telnet.stop  - Shut the server down.");
            writeLine("telnet.log   - Change your logging settings.");
            writeLine("telnet.exit  - Quit the telnet session.");
        }
        else if ("telnet.stop".equalsIgnoreCase(command))
        {
            writeLine("Shutting down the server...");
            TelnetLogger.warning(username + ": Shutting down the server...");
            Bukkit.shutdown();
            System.exit(0);
        }
        else if ("telnet.log".equalsIgnoreCase(command))
        {
            switch (filterMode)
            {
                case NONE:
                {
                    filterMode = FilterMode.CHAT_ONLY;
                    writeLine("Showing only chat logs.");
                    break;
                }
                case CHAT_ONLY:
                {
                    filterMode = FilterMode.NON_CHAT_ONLY;
                    writeLine("Showing only non-chat logs.");
                    break;
                }
                case NON_CHAT_ONLY:
                {
                    filterMode = FilterMode.NONE;
                    writeLine("Showing all logs.");
                    break;
                }
            }
        }
        else if ("telnet.exit".equalsIgnoreCase(command))
        {
            writeLine("Goodbye. <3");
            syncTerminateSession();
        }
        else if ("telnet.enhanced".equalsIgnoreCase(command))
        {
            enhancedMode = !enhancedMode;
            writeLine((enhancedMode ? "A" : "Dea") + "ctivated enhanced mode.");
            if (enhancedMode)
            {
                telnet.getPlugin().listener.triggerPlayerListUpdates();
            }
        }
        else if ("telnet.enhancedplus".equalsIgnoreCase(command))
        {
            enhancedPlusMode = !enhancedPlusMode;
            writeLine((enhancedPlusMode ? "A" : "Dea") + "ctivated enhanced+ mode.");
        }
        else
        {
            writeLine("Invalid telnet command, use \"telnet.help\" to view help.");
        }
    }

    public void syncTriggerPlayerListUpdate(String playerListData)
    {
        if (!enhancedMode)
        {
            return;
        }

        synchronized (clientSocket)
        {
            if (clientSocket.isClosed())
            {
                return;
            }

            writeLine("playerList~" + playerListData);
        }
    }

    public void syncUsageUpdate(String usageData)
    {
        if (!enhancedPlusMode)
        {
            return;
        }

        synchronized (clientSocket)
        {
            if (clientSocket.isClosed())
            {
                return;
            }

            writeLine("usage~" + usageData);
        }
    }
}