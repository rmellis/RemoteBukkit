# TelnetMC - Work in progress.... 
## Pre-Release Alpha builds can be found in the release section
## General
TelnetMC is an modern fork of RemoteBukkit, it allows server owners to gain full console access to there Minecraft server via the Telnet protocol. You do this by running a ether the provided client or any telnet console application. Once the client is launched you then need to enter your servers IP/domain, port, username and password. The password is defined in the configuration of TelnetMC. No need to worry about people intercepting your connection to the console, TelnetMC has built in SHA512 encryption.

TelnetMC supports multiple and simultaneous connections.

## Use
### Plugin
Simply place the plugin in the standard Bukkit plugin directory and it will automatically generate it's configuration the next time the server is run. The 3 options in the plugin config file are self explanatory (username, password and port) and the defaults are:

    user: user
    pass: changeme
    port: 25500 

### GUI Client
TelnetMC has an easy to use GUI client which you can use to connect to the plugin. Just double click on the GUI's jarfile to execute it.

### Script login to skip dialog
Run the GUI via a console  cmd/terminal with the following arguments and it will attempt to use the supplied parameters to login automatically:

Use: [address:port] [user] [pass] <switches>

### Telnet Client (advanced)

TelnetMC will work with any telnet client. Just connect to the normal server port as you would with the console or GUI clients and then supply the server's username and then password on separate lines, followed by a third, blank line.
e.g.
    $ telnet [address:ip]
    [user]
    [pass]
    [blank line]   <-- press enter

##Download
The latest builds can be downloaded [here](https://github.com/rmellis/TelnetMC/releases).
this includes Alpha and Beta builds so use with caution.
