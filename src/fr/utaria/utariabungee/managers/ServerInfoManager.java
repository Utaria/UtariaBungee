package fr.utaria.utariabungee.managers;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.UtariaPlayer;
import fr.utaria.utariabungee.players.UtariaRank;
import fr.utaria.utariabungee.tasks.RefreshServerInfoTask;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class ServerInfoManager {

    private boolean firstCheck = true;

    public ServerInfoManager() {
        this.refresh();
        new RefreshServerInfoTask();
    }

    public void refresh() {
        String maxPlayers       = Utils.getConfigValue("maxplayers");
        String motd             = Utils.getConfigValue("motd");
        String maintenance      = Utils.getConfigValue("maintenance");
        String maintenance_motd = Utils.getConfigValue("maintenance_motd");


        boolean lastMaintenanceMode = Config.maintenance;


        if(maxPlayers == null) Utils.updateConfigValue("maxplayers", Config.maxPlayers + "");
        else Config.maxPlayers = Integer.valueOf(maxPlayers);

        if(motd == null) Utils.updateConfigValue("motd", Config.motd);
        else Config.motd = motd;

        if(maintenance_motd == null) Utils.updateConfigValue("maintenance_motd", Config.maintenance_motd);
        else Config.maintenance_motd = maintenance_motd;

        if(maintenance == null) Utils.updateConfigValue("maintenance", Config.maintenance + "");
        else Config.maintenance = Boolean.valueOf(maintenance);


        if(!lastMaintenanceMode && !firstCheck && Config.maintenance) this.runMaintenanceMode();
        firstCheck = false;
    }


    private void runMaintenanceMode(){
        BungeeCord.getInstance().broadcast(new TextComponent(Config.prefix + "§cLe mode maintenance vient d'être §cactivé §csur §cnotre §créseau. §cVous §callez §cêtre §cdéconnectés §cdans §6" + Config.maintenance_logout_delay + " secondes§c."));

        BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), () -> {
			for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()){
				if (!PlayersManager.playerHasRankLevel(player, Config.maintenanceMaxKickLevel)) {
					player.disconnect(new TextComponent("§cVous venez d'être déconnecté pour cause d'une maintenance."));
				} else {
					UtariaRank playerRank = PlayersManager.getPlayerHighestRank(player);

					assert playerRank != null;
					player.sendMessage(new TextComponent(Config.prefix + "En tant que " + playerRank.getColor() + playerRank.getName() + "§7, vous n'avez pas été exclu."));
				}
			}
		}, Config.maintenance_logout_delay, TimeUnit.SECONDS);
    }
}
