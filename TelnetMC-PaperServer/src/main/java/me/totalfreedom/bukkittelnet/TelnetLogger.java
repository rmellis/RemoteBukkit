package me.totalfreedom.bukkittelnet;

import java.util.logging.Logger;
import org.apache.commons.lang.exception.ExceptionUtils;

public final class TelnetLogger
{

    private static Logger serverLogger = null;
    private static Logger pluginLogger = null;

    private TelnetLogger()
    {
    }

    public static void setServerLogger(Logger serverLogger)
    {
        TelnetLogger.serverLogger = serverLogger;
    }

    public static void setPluginLogger(Logger pluginLogger)
    {
        TelnetLogger.pluginLogger = pluginLogger;
    }

    public static void rawInfo(String message)
    {
        serverLogger.info(message);
    }

    public static void info(String message)
    {
        pluginLogger.info(message);
    }

    public static void rawWarning(String message)
    {
        serverLogger.warning(message);
    }

    public static void warning(String message)
    {
        pluginLogger.warning(message);
    }

    public static void rawSevere(Object message)
    {
        final String line;

        if (message instanceof Throwable)
        {
            line = ExceptionUtils.getStackTrace((Throwable) message);
        }
        else
        {
            line = String.valueOf(message);
        }

        serverLogger.severe(line);
    }

    public static void severe(Object message)
    {
        final String line;

        if (message instanceof Throwable)
        {
            line = ExceptionUtils.getStackTrace((Throwable) message);
        }
        else
        {
            line = String.valueOf(message);
        }

        pluginLogger.severe(line);
    }
}
