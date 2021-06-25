/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
/**
 * @author Keeley Hoek (escortkeel@gmail.com)
 * @update rmellis - TelnetMC fork
 **/
public class LogAppender extends AbstractAppender {

    private final RemoteBukkitPlugin plugin;

    public LogAppender(RemoteBukkitPlugin plugin) {
	super("RemoteController", null, null);
	this.plugin = plugin;
	start();
    }

    @Override
    public void append(LogEvent event) {
	plugin.broadcast(new SimpleDateFormat("hh:mm a").format(
		new Date(event.getTimeMillis())) + " [" + event.getLevel().toString() + "] " + event.getMessage().getFormattedMessage());
    }
}
