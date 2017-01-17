package fr.utaria.utariabungee.listeners;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.TextUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerPingListener implements Listener {

    @EventHandler
    public void onPlayerPing(ProxyPingEvent e){
        ServerPing serverInfo = e.getResponse();
        ServerPing.Players players = serverInfo.getPlayers();

        String        motd        = (Config.maintenance) ? Config.maintenance_motd : Config.motd;
        String[]      motdLines;
        StringBuilder motdBuilder = new StringBuilder();

        // Maintenance mode
        if(Config.maintenance)
            serverInfo.setVersion(new ServerPing.Protocol(Config.maintenance_status, Short.MAX_VALUE));
        else
            serverInfo.setPlayers(new ServerPing.Players(Config.maxPlayers, players.getOnline(), players.getSample()));


        // Format MOTD & apply it
        motdLines = motd.split("%n%");

        for( String line : motdLines ) {
            if(line.contains("%c%")) {
                line = TextUtils.centerText(line.replace("%c%", ""), 260);
            }

            motdBuilder.append(line);
            motdBuilder.append("\n");
        }

        serverInfo.setDescription( motdBuilder.toString() );



        e.setResponse(serverInfo);
    }

}

