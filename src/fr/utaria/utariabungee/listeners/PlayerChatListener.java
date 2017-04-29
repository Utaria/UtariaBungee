package fr.utaria.utariabungee.listeners;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.utils.TimeParser;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerChatListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(ChatEvent e) {
		if(!(e.getSender() instanceof ProxiedPlayer)) return;

		ProxiedPlayer player = (ProxiedPlayer) e.getSender();
		String ip = player.getAddress().getHostName();


		// Si le joueur est sur le serveur par défaut (autrement dit le serveur de connexion),
		// on ne fait rien, car il doit bien pouvoir taper son mot de passe ;)
		String defaultServerName = (UtariaBungee.getServerManager().getDefaultServer() != null) ? UtariaBungee.getServerManager().getDefaultServer().getName() : "";
		String currentServerName = player.getServer().getInfo().getName();

		// Retourne 127.0.0.1 si le joueur se connecte en local (mode dev)
		boolean devMode = player.getAddress().getAddress().getHostAddress().equals("127.0.0.1");

		if (!devMode && (defaultServerName.equals(currentServerName) || currentServerName.equals("default")))
			return;

		// On ne fait rien si c'est une commande
		if(e.getMessage().length() > 1 && e.getMessage().substring(0, 1).equals("/")) {
			// Si le joueur est sur le serveur de connexion, il ne peut taper aucune commande.
			// # SECURITE ULTIME
			if (defaultServerName.equals(currentServerName)) e.setCancelled(true);
			return;
		}

		// On regarde si le joueur a été muté (ou son IP)
		if (UtariaBungee.getModerationManager().playernameIsTempMuted(player.getName())) {
			DatabaseSet infos = UtariaBungee.getModerationManager().getPlayerMuteInformations(player.getName());
			
			e.setCancelled(true);
			player.sendMessage(new TextComponent(Config.prefix + "§7Vous avez été muté le §6" + Utils.dateToString(infos.getTimestamp("date")) + "§7 par §6" + infos.getString("muted_by") + "§7 pour §e" + infos.getString("reason") + "§7. Il vous reste §e" +
				TimeParser.timeToString(infos.getTimestamp("mute_end")) + "§7 de mute."));
		} else if (UtariaBungee.getModerationManager().ipIsTempMuted(ip)) {
			DatabaseSet infos = UtariaBungee.getModerationManager().getIpMuteInformations(ip);
			
			e.setCancelled(true);
			player.sendMessage(new TextComponent(Config.prefix + "§7Vous avez été muté le §6" + Utils.dateToString(infos.getTimestamp("date")) + "§7 par §6" + infos.getString("muted_by") + "§7 pour §e" + infos.getString("reason") + "§7. Il vous reste §e" +
				TimeParser.timeToString(infos.getTimestamp("mute_end")) + "§7 de mute."));
		}


		/*---------------------------------------------------*/
		/*  Protection de diffusion du mot de passe          */
		/*---------------------------------------------------*/
		String message = e.getMessage();

		// On regarde si le message ne contient qu'un seul mot (le mdp?)
		if (!message.trim().contains(" ")) {
			String cryptedMessage = Hashing.sha1().hashString(message.trim(), Charsets.UTF_8).toString();

			if (cryptedMessage.equalsIgnoreCase(PlayerInfo.get(player).getCryptedPassword())) {
				e.setCancelled(true);
				player.sendMessage(TextComponent.fromLegacyText(Config.warningPrefix + "Faites attention à ne pas envoyer votre mot de passe."));
			}
		}
	}
	
}
