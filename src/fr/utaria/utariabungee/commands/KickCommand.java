package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Timestamp;
import java.util.Date;

public class KickCommand extends Command{

	public KickCommand() {
		super("kick");
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		// Il faut avoir les droits pour pouvoir faire ça !
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if (!PlayersManager.playerHasRankLevel(pp, Config.moderationMinLevel)) {
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}

		// Aide de la commande
		if (args.length < 2) {
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/kick <joueur|ip> <raison>"));
			return;
		}
		
		StringBuilder reason = new StringBuilder();
		
		final String kickedBy = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		boolean isIP = Utils.stringIsIP(args[0]);

		// On génère la raison en fonction des arguments passés à la commande
		for(int i = 1; i < args.length; i++)
			reason.append("§6").append(args[i]).append(" ");

		reason = new StringBuilder(reason.substring(0, reason.length() - 1));
		
		if (!isIP) {
			final String playername = args[0];
			
			// On regarde si le joueur est bien en ligne
			ProxiedPlayer player = UtariaBungee.getInstance().getProxy().getPlayer(playername);
			if (player == null) {
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c n'est pas en ligne. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			// On expulse le joueur avec le pseudo utilisé pour la sanction
			player.disconnect(new TextComponent("Vous avez été expulsé par " + kickedBy + " pour la raison : '" + reason + "'."));
			final String server   = player.getServer().getInfo().getName();


			// On sauvegarde la requête dans la base de données
			final String reasonScheduled = reason.toString();
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () ->
					UtariaBungee.getDatabase().save("bungee_kicks", DatabaseSet.makeFields(
						"player", playername,
						"reason", reasonScheduled,
						"server", server,
						"kicked_by", kickedBy,
						"date", new Timestamp(new Date().getTime())
					))
			);
		} else {
			final String ip = args[0];
						
			// On expulse tous les joueurs avec l'IP utilisée pour la sanction
			for (ProxiedPlayer player : UtariaBungee.getInstance().getProxy().getPlayers())
				if (player != null && ip.equals(Utils.getPlayerIP(player)))
					player.disconnect(new TextComponent("Vous avez été expulsé par " + kickedBy + " pour la raison : '" + reason + "'."));
						
			// On sauvegarde la requête dans la base de données
			final String reasonScheduled = reason.toString();
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () ->
					UtariaBungee.getDatabase().save("bungee_kicks", DatabaseSet.makeFields(
						"ip", ip,
						"reason", reasonScheduled,
						"kicked_by", kickedBy,
						"date", new Timestamp(new Date().getTime())
					))
			);
		}
		
		
		
		
		UtariaBungee.getInstance().getProxy().broadcast(new TextComponent(Config.prefix + "§e" + ((isIP) ? Utils.hideIP(args[0]) : args[0]) + "§7 a été expulsé pour §6" + reason + "§7."));
	}
	
}
