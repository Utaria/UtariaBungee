package fr.utaria.utariabungee.commands;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.TimeParser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.Utils;

public class TempBanCommand extends Command{

	public TempBanCommand() {
		super("tempban");
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		// Il faut avoir les droits pour pouvoir faire ça !
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if (!PlayersManager.playerHasRankLevel(pp, 29)) {
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}

		// Aide de la commande
		if (args.length < 2) {
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/tempban <joueur|ip> <temps> <raison>"));
			return;
		}
		
		StringBuilder  reason = new StringBuilder();
		final String bannedBy = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		
		boolean isIP = Utils.stringIsIP(args[0]);

		// On formatte et on récupère le temps indiqué dans la commande
		final TimeParser.FormattedTime time = TimeParser.stringToTime(args[1]);

		if ((time == null || time.getTime() == -1)) {
			if (TimeParser.getErrors().size() > 0)
				sender.sendMessage(new TextComponent(Config.prefix + "§c" + TimeParser.getErrors().get(0)));

			return;
		}

		// On limite les modo+ à 7j de ban
		if (time.biggerThan(Config.maxModoBanTime) && sender instanceof ProxiedPlayer && !PlayersManager.playerHasRankLevel((ProxiedPlayer) sender, Config.modoLevel + 1)) {
			sender.sendMessage(new TextComponent(Config.prefix + "§cVous ne pouvez pas ban plus de §6" + Config.maxModoBanTime + "§c."));
			return;
		}
		
		final Timestamp tsAdded = new Timestamp(System.currentTimeMillis() + time.getTime());
		String tsString   = TimeParser.timeToString(tsAdded);
		
		// On génère la raison en fonction des arguments passés à la commande
		for(int i = 2; i < args.length; i++)
			reason.append("§6").append(args[i]).append(" ");
		reason = new StringBuilder(reason.substring(0, reason.length() - 1));
		
		if (!isIP) {
			final String playername = args[0];
			
			// On regarde si le pseudo n'a pas déjà été banni
			if (UtariaBungee.getModerationManager().playernameIsTempBanned(playername)) {
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c est déjà banni. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			// On envoie le message de la sanction au joueur concerné
			ProxiedPlayer player = UtariaBungee.getInstance().getProxy().getPlayer(playername);
			if(player != null) player.disconnect(new TextComponent("Vous avez été banni §e" + tsString + "§r par §6" + bannedBy + "§r pour la raison : §e'" + reason + "'§r."));
			
			final String server = (player != null) ? player.getServer().getInfo().getName() : "none";
			
			// On sauvegarde la sanction dans la base de données
			final String reasonScheduled = reason.toString();
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
				"player", playername,
				"reason", reasonScheduled,
				"server", server,
				"banned_by", bannedBy,
				"date", new Timestamp(new Date().getTime()),
				"ban_end", new Timestamp(System.currentTimeMillis() + time.getTime())
			)));
			
		} else {
			
			final String ip = args[0];

			// On regarde si l'IP a été déjà été bannie
			if(UtariaBungee.getModerationManager().ipIsTempBanned(ip)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cL'IP §6" + ip + "§c est déjà bannie. Pour plus d'infos, tapez §9/lookup " + ip + "§c."));
				return;
			}

			// On envoie le message aux joueurs sanctionnés avec la même IP
			for (ProxiedPlayer player : UtariaBungee.getInstance().getProxy().getPlayers())
				if (player != null && ip.equals(Utils.getPlayerIP(player)))
					player.disconnect(new TextComponent("Vous avez été banni §e" + tsString + "§r par §6" + bannedBy + "§r pour la raison : §e'" + reason + "'§r."));
						
			// On sauvegarde la sanction dans la base de données
			final String reasonScheduled = reason.toString();
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
				"ip", ip,
				"reason", reasonScheduled,
				"banned_by", bannedBy,
				"date", new Timestamp(new Date().getTime()),
				"ban_end", new Timestamp(System.currentTimeMillis() + time.getTime())
			)));
			
		}

		UtariaBungee.getInstance().getProxy().broadcast(new TextComponent(Config.prefix + "§e" + ((isIP) ? Utils.hideIP(args[0]) : args[0]) + "§7 a été banni §c" + tsString + "§7 pour §6" + reason + "§7."));
	}
}

