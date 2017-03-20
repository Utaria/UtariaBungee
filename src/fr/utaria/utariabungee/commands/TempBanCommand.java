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
		
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if (!PlayersManager.playerHasRankLevel(pp, Config.moderationMinLevel)) {
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}
		
		if (args.length < 2) {
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/tempban <joueur|ip> <temps> <raison>"));
			return;
		}
		
		String reason = "";
		final String bannedBy = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		
		Pattern patternIP = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher matchIP   = patternIP.matcher(args[0]);
		boolean isIP = matchIP.find();
		
		// Parse time
		String timeString = args[1];
		final long time = TimeParser.stringToTime(timeString);
		
		if(time == -1 && TimeParser.getErrors().size() > 0){
			sender.sendMessage(new TextComponent(Config.prefix + "§c" + TimeParser.getErrors().get(0)));
			return;
		}
		
		final Timestamp tsAdded = new Timestamp(System.currentTimeMillis() + time);
		String tsString   = TimeParser.timeToString(tsAdded);
		
		// Parse reason
		for(int i = 2; i < args.length; i++)
			reason += "§6" + args[i] + " ";
		reason = reason.substring(0, reason.length()-1);
		
		if(!isIP) {
			final String playername = args[0];
			
			// Check if the playername is already banned
			if(UtariaBungee.getModerationManager().playernameIsTempBanned(playername)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c est déjà banni. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			// Ban player with this playername
			ProxiedPlayer player = UtariaBungee.getInstance().getProxy().getPlayer(playername);
			if(player != null) player.disconnect(new TextComponent("Vous avez été banni §e" + tsString + "§r par §6" + bannedBy + "§r pour la raison : §e'" + reason + "'§r."));
			
			final String server   = (player != null) ? player.getServer().getInfo().getName() : "none";
			
			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
				"player", playername,
				"reason", reasonScheduled,
				"server", server,
				"banned_by", bannedBy,
				"date", new Timestamp(new Date().getTime()),
				"ban_end", new Timestamp(System.currentTimeMillis() + time)
			)));
			
		}else{
			
			final String ip = args[0];
			
			// Check if the IP is already banned
			if(UtariaBungee.getModerationManager().ipIsTempBanned(ip)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cL'IP §6" + ip + "§c est déjà bannie. Pour plus d'infos, tapez §9/lookup " + ip + "§c."));
				return;
			}

			// Ban all players with this IP
			for(ProxiedPlayer player : UtariaBungee.getInstance().getProxy().getPlayers()){
				if(player != null && player.getAddress().getHostName().equalsIgnoreCase(ip)){
					player.disconnect(new TextComponent("Vous avez été banni §e" + tsString + "§r par §6" + bannedBy + "§r pour la raison : §e'" + reason + "'§r."));
				}
			}
						
			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
				"ip", ip,
				"reason", reasonScheduled,
				"banned_by", bannedBy,
				"date", new Timestamp(new Date().getTime()),
				"ban_end", new Timestamp(System.currentTimeMillis() + time)
			)));
			
		}

		UtariaBungee.getInstance().getProxy().broadcast(new TextComponent(Config.prefix + "§e" + ((isIP) ? Utils.hideIP(args[0]) : args[0]) + "§7 a été banni §c" + tsString + "§7 pour §6" + reason + "§7."));
	}
}

