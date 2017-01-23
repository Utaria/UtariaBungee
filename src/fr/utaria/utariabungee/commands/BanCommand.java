package fr.utaria.utariabungee.commands;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.database.Database;
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
		
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if( PlayerInfo.get(pp).getRankLevel() < Config.moderationMinLevel ){
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}
		
		if(args.length < 2){
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/ban <joueur|ip> <raison>"));
			return;
		}
		
		String reason = "";
		
		final String bannedBy = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		
		Pattern patternIP = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher matchIP   = patternIP.matcher(args[0]);
		boolean isIP = matchIP.find();
		
		// Parse reason
		for(int i = 1; i < args.length; i++)
			reason += "§6" + args[i] + " ";
		reason = reason.substring(0, reason.length()-1);
		
		if(!isIP){
			
			final String playername = args[0];
			
			// Check if the playername is already banned
			if(UtariaBungee.getModerationManager().playernameIsBanned(playername)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c est déjà banni. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			// Ban player with this playername
			ProxiedPlayer player = UtariaBungee.getInstance().getProxy().getPlayer(playername);
			if(player != null) player.disconnect(new TextComponent("Vous avez été banni par " + bannedBy + " pour la raison : '" + reason + "'."));
			
			final String server   = (player != null) ? player.getServer().getInfo().getName() : "none";
			
			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), new Runnable() {@Override public void run() {
				UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
					"player", playername,
					"reason", reasonScheduled,
					"server", server,
					"banned_by", bannedBy,
					"date", new Timestamp(new Date().getTime())
				));
			}});
			
		}else{
			
			final String ip = args[0];
			
			// Check if the ip is already banned
			if(UtariaBungee.getModerationManager().ipIsBanned(ip)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cL'IP §6" + ip + "§c est déjà bannie. Pour plus d'infos, tapez §9/lookup " + ip + "§c."));
				return;
			}
						
			// Ban all players with this ip
			for(ProxiedPlayer player : UtariaBungee.getInstance().getProxy().getPlayers()){
				if(player != null && player.getAddress().getHostName().equalsIgnoreCase(ip)){
					player.disconnect(new TextComponent("Vous avez été banni par " + bannedBy + " pour la raison : '" + reason + "'."));
				}
			}
						
			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), new Runnable() {@Override public void run() {
				UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
					"ip", ip,
					"reason", reasonScheduled,
					"banned_by", bannedBy,
					"date", new Timestamp(new Date().getTime())
				));
			}});
			
		}
		
		
		
		
		UtariaBungee.getInstance().getProxy().broadcast(new TextComponent(Config.prefix + "§e" + args[0] + "§7 a été banni pour §6" + reason + "§7."));
	}
}
