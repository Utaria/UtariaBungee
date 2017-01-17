package fr.utaria.utariabungee.commands;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.utils.BungeeMessages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.Utils;

public class UnmuteCommand extends Command{

	public UnmuteCommand() {
		super("unmute");
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
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/unmute <joueur|ip> <raison>"));
			return;
		}
		
		String reason = "";
		
		final String unmutedBy = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		
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
			if(!UtariaBungee.getModerationManager().playernameIsTempMuted(playername)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c n'est pas muté. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			final int muteId = UtariaBungee.getModerationManager().getPlayerMuteInformations(playername).getInteger("id");

			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_mutes", DatabaseSet.makeFields(
				"unmute_reason", reasonScheduled,
				"unmuted_by", unmutedBy,
				"unmute_date", new Timestamp(new Date().getTime())
			), DatabaseSet.makeConditions("id", muteId+"")));
			
			sender.sendMessage(new TextComponent(Config.prefix + "§7Le joueur §e" + playername + "§7 est maintenant démuté."));
		}else{
			
			final String ip = args[0];
			
			// Check if the player's IP is already banned
			if(!UtariaBungee.getModerationManager().ipIsBanned(ip) && !UtariaBungee.getModerationManager().ipIsTempBanned(ip)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cL'IP §6" + ip + "§c n'est pas mutée. Pour plus d'infos, tapez §9/lookup " + ip + "§c."));
				return;
			}
						
			final int muteId = UtariaBungee.getModerationManager().getIpMuteInformations(ip).getInteger("id");

			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_mutes", DatabaseSet.makeFields(
					"unmute_reason", reasonScheduled,
					"unmuted_by", unmutedBy,
					"unmute_date", new Timestamp(new Date().getTime())
			), DatabaseSet.makeConditions("id", muteId+"")));
			
			sender.sendMessage(new TextComponent(Config.prefix + "§7L'IP §e" + Utils.hideIP(ip) + "§7 est maintenant démutée."));
		}
	}
}
