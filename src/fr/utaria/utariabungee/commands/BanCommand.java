package fr.utaria.utariabungee.commands;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.BungeeMessages;

public class BanCommand extends Command{

	public BanCommand() {
		super("ban");
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;

			if (!PlayersManager.playerHasRankLevel(pp, Config.moderationMinLevel)) {
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}
		
		if (args.length < 2) {
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/ban <joueur|ip> <raison>"));
			return;
		}
		
		StringBuilder reason = new StringBuilder();

		String  bannedBy  = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		Pattern patternIP = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher matchIP   = patternIP.matcher(args[0]);
		boolean isIP      = matchIP.find();

		
		// On récupère la raison écrite par le joueur
		for(int i = 1; i < args.length; i++)
			reason.append("§6").append(args[i]).append(" ");

		reason = new StringBuilder(reason.substring(0, reason.length() - 1));


		if (!isIP) {
			String playername = args[0];
			
			// On regarde si le joueur n'a pas déjà été banni
			if (UtariaBungee.getModerationManager().playernameIsBanned(playername)) {
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c est déjà banni. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			// On ban le joueur avec le pseudo en question
			ProxiedPlayer player = UtariaBungee.getInstance().getProxy().getPlayer(playername);
			if (player != null)
				player.disconnect(new TextComponent("Vous avez été banni par " + bannedBy + " pour la raison : '" + reason + "'."));


			// On sauvegarde le ban dans la base de données
			String server          = (player != null) ? player.getServer().getInfo().getName() : "none";
			String reasonScheduled = reason.toString();

			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () ->
					UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
						"player", playername,
						"reason", reasonScheduled,
						"server", server,
						"banned_by", bannedBy,
						"date", new Timestamp(new Date().getTime())
					))
			);
		} else {
			final String ip = args[0];

			// On regarde si l'IP a pas déjà été bannie
			if (UtariaBungee.getModerationManager().ipIsBanned(ip)) {
				sender.sendMessage(new TextComponent(Config.prefix + "§cL'IP §6" + ip + "§c est déjà bannie. Pour plus d'infos, tapez §9/lookup " + ip + "§c."));
				return;
			}

			// On ban tous les joueurs avec cette IP là
			for (ProxiedPlayer player : UtariaBungee.getInstance().getProxy().getPlayers())
				if (player != null && player.getAddress().getHostName().equalsIgnoreCase(ip))
					player.disconnect(new TextComponent("Vous avez été banni par " + bannedBy + " pour la raison : '" + reason + "'."));


			// On sauvegarde le ban dans la base de données
			final String reasonScheduled = reason.toString();
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () ->
					UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
							"ip", ip,
							"reason", reasonScheduled,
							"banned_by", bannedBy,
							"date", new Timestamp(new Date().getTime())
					))
			);
		}
		
		
		UtariaBungee.getInstance().getProxy().broadcast(new TextComponent(Config.prefix + "§e" + ((isIP) ? Utils.hideIP(args[0]) : args[0]) + "§7 a été banni pour §6" + reason + "§7."));
	}
}
