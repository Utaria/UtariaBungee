package fr.utaria.utariabungee.network.listener;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.antivpn.AntiVPNManager;
import fr.utaria.utariabungee.chat.PMManager;
import fr.utaria.utariabungee.chat.SpecialChannels;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.moderation.sanctions.Ban;
import fr.utaria.utariabungee.network.AutoRestartManager;
import fr.utaria.utariabungee.network.ProxyManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.players.PlayersManager;
import fr.utaria.utariabungee.util.PlayerUtil;
import fr.utaria.utariabungee.util.TextUtil;
import fr.utaria.utariabungee.util.UUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class NetworkListener implements Listener {

	private ProxyManager manager;

	private AntiVPNManager antiVPNManager;

	public NetworkListener(ProxyManager proxyManager) {
		this.manager = proxyManager;
		this.antiVPNManager = UtariaBungee.getInstance().getInstance(AntiVPNManager.class);
	}

	@EventHandler
	public void onPlayerPing(ProxyPingEvent e) {
		ServerPing serverInfo = e.getResponse();
		ServerPing.Players players = serverInfo.getPlayers();

		String[] motdLines;
		String motd = ChatColor.translateAlternateColorCodes('&', this.manager.getMotd());
		StringBuilder motdBuilder = new StringBuilder();


		// Mode maintenance ou non
		if (this.manager.getMaintenanceMode().isActive())
			serverInfo.setVersion(new ServerPing.Protocol(Config.MAINTENANCE_STATUS, Short.MAX_VALUE));
		else
			serverInfo.setPlayers(new ServerPing.Players(this.manager.getMaxPlayers(), players.getOnline(), players.getSample()));


		// Création du MOTD formaté
		motdLines = motd.split("%n%");

		for (String line : motdLines) {
			if (line.contains("%c%"))
				line = TextUtil.centerText(line.replace("%c%", ""), 260);

			motdBuilder.append(line);
			motdBuilder.append("\n");
		}

		serverInfo.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(motdBuilder.toString())));

		e.setResponse(serverInfo);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(PreLoginEvent event) {
		event.registerIntent(UtariaBungee.getInstance());

		ProxyServer.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), () -> {
			/* -------------------------------- */
			/*  Mode maintenance & redémarrage  */
			/* -------------------------------- */
			if (UtariaBungee.getInstance().getInstance(AutoRestartManager.class).restartIsInProgress()) {
				event.setCancelled(true);
				event.setCancelReason(Config.autoRestartMessage);
				this.completeEvent(event);

				return;
			}

			if (this.manager.getMaintenanceMode().isActive())
				if (!this.manager.isStaffmember(event.getConnection().getName())) {
					event.setCancelled(true);
					event.setCancelReason(this.manager.getMaintenanceMode().getKickMessage());
					event.completeIntent(UtariaBungee.getInstance());

					return;
				}


			/* ------------------- */
			/*  Serveur complet ?  */
			/* ------------------- */
			if (ProxyServer.getInstance().getOnlineCount() + 1 > this.manager.getMaxPlayers())
				if (!this.manager.isStaffmember(event.getConnection().getName())) {
					event.setCancelled(true);
					event.setCancelReason("§cServeur complet ! Merci de réessayer plus tard.");
					this.completeEvent(event);

					return;
				}

			/* --------------------------------------------- */
			/*  Application de la politique des pseudonymes  */
			/* --------------------------------------------- */
			String playername = event.getConnection().getName();

			// On interdit les joueurs des caractères non convenables dans leur pseudo
			// et ceux qui contient moins de 3 caractères.
			if (playername.length() < 3 || !playername.matches("^([a-zA-Z0-9_])*$")) {
				event.setCancelled(true);
				event.setCancelReason("Votre pseudonyme ne respecte pas notre règlement, veuillez changer.");
				this.completeEvent(event);

				return;
			}

			this.completeEvent(event);
		});
	}

	@EventHandler
	public void onPlayerLogin(LoginEvent event) {
		event.registerIntent(UtariaBungee.getInstance());

		ProxyServer.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), () -> {
			/* ------------ */
			/*  Modération  */
			/* ------------ */
			// On regarde si le joueur est banni ...
			Ban ban = UtariaBungee.getInstance().getInstance(ModerationManager.class).getActiveBanFor(event.getConnection());

			// ... et si c'est le cas, on annule sa connexion !
			if (ban != null) {
				event.setCancelled(true);
				event.setCancelReason(ban.getMessage());
				this.completeEvent(event);

				return;
			}

			/* --------------------- */
			/*  Protection anti-VPN  */
			/* --------------------- */
			String ip = UUtil.getConnectionIP(event.getConnection());

			if (this.antiVPNManager.isBlackIp(ip)) {
				UtariaBungee.getInstance().getLogger().warning("L'IP " + ip + " semble être un VPN et a été rejetée.");

				event.setCancelled(true);
				event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "Votre IP ne respecte pas notre règlement."));
			}

			this.completeEvent(event);
		});
	}

	@EventHandler
	public void onPlayerPostLogin(PostLoginEvent event) {
		ProxiedPlayer pp = event.getPlayer();

		ProxyServer.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), () -> {
			// On récupère le joueur en base !
			PlayerInfo.get(pp);

			// On ajoute le joueur aux canal de discussion "Staff" s'il fait partie du staff
			if (this.manager.isStaffmember(pp.getName()))
				UtariaBungee.getStaffChannel().addPlayer(pp);
		});
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ProxiedPlayer pp = event.getPlayer();
		PlayersManager.unloadPlayerInfo(pp);

		// On le supprime du cache des messages privés
		UtariaBungee.getInstance().getInstance(PMManager.class).clearFor(pp);

		// On le supprime de tous les canaux de discussion
		SpecialChannels.removePlayerFromAllChannels(pp);
	}

	@EventHandler
	public void onPlayerChat(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// On bloque les commandes dans le serveur de connexion
		// pour sécuriser le réseau.
		String serverName = player.getServer().getInfo().getName();
		if (event.isCommand() && (serverName.equals("connexion") || serverName.equals("default")) && !PlayerUtil.isLocalPlayer(player))
			event.setCancelled(true);


		/*---------------------------------------------------*/
		/*  Protection de diffusion du mot de passe          */
		/*---------------------------------------------------*/
		String message = event.getMessage();

		// On regarde si le message ne contient qu'un seul mot (le mdp?)
		if (!message.trim().contains(" ")) {
			String cryptedMessage = Hashing.sha1().hashString(message.trim(), Charsets.UTF_8).toString();

			if (cryptedMessage.equalsIgnoreCase(PlayerInfo.get(player).getCryptedPassword())) {
				event.setCancelled(true);
				player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Attention à ne pas envoyer votre mot de passe !"));
			}
		}
	}

	private void completeEvent(AsyncEvent asyncEvent) {
		asyncEvent.completeIntent(UtariaBungee.getInstance());
	}

}

