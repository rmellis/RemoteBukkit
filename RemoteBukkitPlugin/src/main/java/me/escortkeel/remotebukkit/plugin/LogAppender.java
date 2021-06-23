package me.escortkeel.remotebukkit.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

public class LogAppender extends AbstractAppender {
  private RemoteBukkitPlugin plugin;
  
  public LogAppender(RemoteBukkitPlugin plugin) {
    super("RemoteController", null, null);
    this.plugin = plugin;
    start();
  }
  
  public void append(LogEvent event) {
    this.plugin.broadcast((new SimpleDateFormat("hh:mm a")).format(new Date(event
            .getTimeMillis())) + " [" + event.getLevel().toString() + "] " + event.getMessage().getFormattedMessage());
  }
}
