package fr.utaria.utariabungee.listeners;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.managers.UtariaServer;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.utils.TimeParser;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Timestamp;
import java.util.Date;

public class PlayerJoinListener implements Listener{

	@EventHandler
	public void onPlayerLogin(LoginEvent e){
		final String playername = e.getConnection().getName();
		final String ip = e.getConnection().getAddress().getAddress().getHostAddress();


		/*      Protections Anti-bot      */
		if (UtariaBungee.getAntiBotManager().ipIsBot(ip)) {
			BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("L'IP " + ip + " semble etre un bot et a ete rejetee."));

			e.setCancelled(true);
			e.setCancelReason("Votre IP semble être non sécurisée, déconnexion.");
			e.getConnection().disconnect(new TextComponent("§cVotre IP semble être non sécurisée, déconnexion."));
			return;
		}

		/*      On interdit les joueurs avec un espace dans leur pseudo      */
		if (playername.contains(" ")) {
			e.setCancelled(true);
			e.setCancelReason("Votre pseudo comporte un espace donc vous ne pouvez pas vous connecter.");
			e.getConnection().disconnect(new TextComponent("§cVotre pseudo comporte un espace donc vous ne pouvez pas vous connecter."));
			return;
		}


		// On regarde si le serveur est actuellement en train de redémarrer
		if(UtariaBungee.getAutoRestartManager().restartIsInProgress()) {
            e.setCancelled(true);
            e.setCancelReason(Config.autoRestartMessage);
            return;
        }

		// Maintenance mode
		if(Config.maintenance){
			if(PlayerInfo.getRankLevelByName(playername) <= Config.maintenanceMaxKickLevel){
				e.setCancelled(true);
				e.setCancelReason(Config.maintenance_message);
			}
		}

		
		// Check if the player is temp banned
		if(UtariaBungee.getModerationManager().playernameIsTempBanned(playername)){
			DatabaseSet infos = UtariaBungee.getModerationManager().getPlayerBanInformations(playername);
			
			if(infos.getTimestamp("ban_end") != null){
				e.setCancelled(true);
				e.setCancelReason("§4§lVous avez été banni le§r §6§l" + Utils.dateToString(infos.getTimestamp("date")) + "§r§4§l par§r§6§l " + infos.getString("banned_by") + "§r§4§l. Raison: §r§e" + infos.getString("reason") + "§r§4§l. Il vous reste §r§e" +
				TimeParser.timeToString(infos.getTimestamp("ban_end")) + "§r§4§l de ban.");
								
				return;
			}
		}
		
		// Check if the player's IP is temp banned
		if(UtariaBungee.getModerationManager().ipIsTempBanned(ip)){
			DatabaseSet infos = UtariaBungee.getModerationManager().getIpBanInformations(ip);
			
			if(infos.getTimestamp("ban_end") != null){					
				e.setCancelled(true);
				e.setCancelReason("§4§lVous avez été banni le§r §6§l" + Utils.dateToString(infos.getTimestamp("date")) + "§r§4§l par§r§6§l " + infos.getString("banned_by") + "§r§4§l. Raison: §r§e" + infos.getString("reason") + "§r§4§l. Il vous reste §r§e" +
				TimeParser.timeToString(infos.getTimestamp("ban_end")) + "§r§4§l de ban.");
										
				return;
			}
		}
		
		
		
		// Check if the player is banned
		if(UtariaBungee.getModerationManager().playernameIsBanned(playername)){
			DatabaseSet infos = UtariaBungee.getModerationManager().getPlayerBanInformations(playername);
			
			e.setCancelled(true);
			e.setCancelReason("§4§lVous avez été banni d§finitivement le§r §6§l" + Utils.dateToString(infos.getTimestamp("date")) + "§r§4§l par§r§6§l " + infos.getString("banned_by") + "§r§4§l. Raison: §r§e" + infos.getString("reason") + ".");
			
			return;
		}
		
		// Check if the player's IP is banned
		if(UtariaBungee.getModerationManager().ipIsBanned(ip)) {
			DatabaseSet infos = UtariaBungee.getModerationManager().getIpBanInformations(ip);

			e.setCancelled(true);
			e.setCancelReason("§4§lVous avez été banni d§finitivement le§r §6§l" + Utils.dateToString(infos.getTimestamp("date")) + "§r§4§l par§r§6§l " + infos.getString("banned_by") + "§r§4§l. Raison: §r§e" + infos.getString("reason") + ".");

			return;
		}
	}

	@EventHandler
	public void onPlayerPostLogin(PostLoginEvent e){
		final ProxiedPlayer pp = e.getPlayer();

		// Détection d'un bot (première couche)
		String ip = Utils.getPlayerIP(pp);

		if (UtariaBungee.getAntiBotManager().ipIsBot(ip)) {
			BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("L'IP " + ip + " semble être un bot et a été rejetée."));

			pp.disconnect(new TextComponent("§cVotre IP semble être non sécurisée, déconnexion."));
			return;
		}
		// Deuxième couche de la protection
		if (!UtariaBungee.getAntiBotManager().passSecondProtection(e.getPlayer().getPendingConnection().isOnlineMode())) {
			pp.disconnect(new TextComponent("§cTrop de connexions, veuillez réessayer dans 10 secondes."));
			return;
		}


		// On met à jour les infos du joueur en base de données
		BungeeCord.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), () -> {
			try {
				PlayerInfo.get(pp);
				UtariaBungee.getDatabase().save("players", DatabaseSet.makeFields("last_ip", Utils.getPlayerIP(pp), "last_connection", new Timestamp(new Date().getTime())), DatabaseSet.makeConditions("playername", pp.getName()));
			} catch(Exception ignored){}
		});


        // Si le serveur est complet, on déconnecte le joueur, car il ne peut pas se connecter.
        if(PlayerInfo.get(pp).getRankLevel() < 10 && BungeeCord.getInstance().getOnlineCount() + 1 > Config.maxPlayers){
			pp.disconnect(new TextComponent("§cServeur complet ! Merci de réessayer plus tard."));
			return;
        }


		// On défini les titres dans la TABLIST
        pp.setTabHeader(
        		new TextComponent("§b§lUTARIA §f§l- §e§lLes serveurs de demain !"),
				new TextComponent("§aSite: §6utaria.fr   §aTS: §9ts.utaria.fr")
		);
	}


	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer pp = e.getPlayer();
		UtariaBungee.removePlayer(pp);

		// On le supprime du cache des messages privés
		UtariaBungee.getPMManager().clearFor(pp);
	}

}
