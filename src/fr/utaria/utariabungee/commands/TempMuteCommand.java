package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.TimeParser;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TempMuteCommand extends Command{

	public TempMuteCommand() {
		super("tempmute");
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if( PlayerInfo.get(pp).getRankLevel() < 29 ){
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}
		
		if(args.length < 2){
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/tempmute §6<joueur|ip> §6<temps> §6<raison>"));
			return;
		}
		
		String reason = "";
		final String mutedBy = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		
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
		
		if(!isIP){
			
			final String playername = args[0];
			
			// Check if the playername is already muted
			if(UtariaBungee.getModerationManager().playernameIsTempMuted(playername)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c est déjà muté. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			// Send message 'muted!' to player with this playername
			ProxiedPlayer player = UtariaBungee.getInstance().getProxy().getPlayer(playername);
			if(player != null) player.sendMessage(new TextComponent(Config.prefix + "§7Vous avez été muté §5" + tsString + "§7 par §e" + mutedBy + "§7 pour §6" + reason + "§7."));
			
			final String server   = (player != null) ? player.getServer().getInfo().getName() : "none";
			
			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_mutes", DatabaseSet.makeFields(
				"player", playername,
				"reason", reasonScheduled,
				"server", server,
				"muted_by", mutedBy,
				"date", new Timestamp(new Date().getTime()),
				"mute_end", new Timestamp(System.currentTimeMillis() + time)
			)));
			
		}else{
			
			final String ip = args[0];
			
			// Check if the IP is already mutes
			if(UtariaBungee.getModerationManager().ipIsTempMuted(ip)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cL'IP §6" + ip + "§c est déjà mutée. Pour plus d'infos, tapez §9/lookup " + ip + "§c."));
				return;
			}

			// Send message 'muted!' to all players with this IP
			for(ProxiedPlayer player : UtariaBungee.getInstance().getProxy().getPlayers()){
				if(player != null && player.getAddress().getHostName().equalsIgnoreCase(ip)){
					player.sendMessage(new TextComponent(Config.prefix + "§7Vous avez été muté §5" + tsString + "§7 par §e" + mutedBy + "§7 pour §6" + reason + "§7."));
				}
			}
						
			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), new Runnable() {@Override public void run() {
				UtariaBungee.getDatabase().save("bungee_mutes", DatabaseSet.makeFields(
					"ip", ip,
					"reason", reasonScheduled,
					"muted_by", mutedBy,
					"date", new Timestamp(new Date().getTime()),
					"mute_end", new Timestamp(System.currentTimeMillis() + time)
				));
			}});
			
		}

		UtariaBungee.getInstance().getProxy().broadcast(new TextComponent(Config.prefix + "§e" + ((isIP) ? Utils.hideIP(args[0]) : args[0]) + "§7 a été muté §c" + tsString + "§7 pour §6" + reason + "§7."));
	}
}

