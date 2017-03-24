package fr.utaria.utariabungee.commands;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.utaria.utariabungee.managers.PlayersManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.Utils;

public class UnbanCommand extends Command{

	public UnbanCommand() {
		super("unban");
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
		
		if(args.length < 2){
			sender.sendMessage(new TextComponent(Config.prefix + "Utilisation de la commande : §6/unban <joueur|ip> <raison>"));
			return;
		}
		
		String reason = "";
		
		final String unbannedBy = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE";
		
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
			if(!UtariaBungee.getModerationManager().playernameIsBanned(playername) && !UtariaBungee.getModerationManager().playernameIsTempBanned(playername)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c n'est pas banni. Pour plus d'infos, tapez §9/lookup " + playername + "§c."));
				return;
			}
			
			final int banId = UtariaBungee.getModerationManager().getPlayerBanInformations(playername).getInteger("id");

			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
				"unban_reason", reasonScheduled,
				"unbanned_by", unbannedBy,
				"unban_date", new Timestamp(new Date().getTime())
			), DatabaseSet.makeConditions("id", banId+"")));
			
			sender.sendMessage(new TextComponent(Config.prefix + "§7Le joueur §e" + playername + "§7 est maintenant débanni."));
		}else{
			
			final String ip = args[0];
			
			// Check if the player's IP is already banned
			if(!UtariaBungee.getModerationManager().ipIsBanned(ip) && !UtariaBungee.getModerationManager().ipIsTempBanned(ip)){
				sender.sendMessage(new TextComponent(Config.prefix + "§cL'IP §6" + ip + "§c n'est pas bannie. Pour plus d'infos, tapez §9/lookup " + ip + "§c."));
				return;
			}
						
			final int banId = UtariaBungee.getModerationManager().getIpBanInformations(ip).getInteger("id");

			// Save the request into the database
			final String reasonScheduled = reason;
			UtariaBungee.getInstance().getProxy().getScheduler().runAsync(UtariaBungee.getInstance(), () -> UtariaBungee.getDatabase().save("bungee_bans", DatabaseSet.makeFields(
					"unban_reason", reasonScheduled,
					"unbanned_by", unbannedBy,
					"unban_date", new Timestamp(new Date().getTime())
			), DatabaseSet.makeConditions("id", banId+"")));
			
			sender.sendMessage(new TextComponent(Config.prefix + "§7L'IP §e" + Utils.hideIP(ip) + "§7 est maintenant débannie."));
		}
	}
}
