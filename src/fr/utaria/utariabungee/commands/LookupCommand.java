package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.PlayerUtils;
import fr.utaria.utariabungee.utils.TimeParser;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LookupCommand extends Command{

	public LookupCommand(){
		super("lookup");
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		boolean hasTotalAccess = !(sender instanceof ProxiedPlayer);

		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if (!PlayersManager.playerHasRankLevel(pp, 29)) {
				BungeeMessages.cannotUseCommand(sender);
				return;
			} else if (PlayersManager.playerHasRankLevel(pp, Config.adminMinLevel))
				hasTotalAccess = true;
		}

		final boolean fHta = hasTotalAccess;

		
		if (args.length < 1 || args[0].equals("")) {
			if (hasTotalAccess) sender.sendMessage(new TextComponent(Config.prefix + "§7Utilisation: §6/lookup <joueur|ip>"));
			else                sender.sendMessage(new TextComponent(Config.prefix + "§7Utilisation: §6/lookup <joueur>"   ));

			return;
		}
		
		BungeeCord.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), () -> {
			if (!Utils.stringIsIP(args[0])) {

				if (args.length >= 2 && fHta) {
					printPlayerSanction(sender, args[0], args[1]);
					return;
				}

				final String        playername = args[0];
				final Database      database   = UtariaBungee.getDatabase();
				final ProxiedPlayer player     = BungeeCord.getInstance().getPlayer(playername);

				String onlineString = (player != null) ? "§a - En ligne" : "§c - Hors ligne";

				DatabaseSet set = database.findFirst("players", DatabaseSet.makeConditions(
					"playername", playername
				));

				if (set == null) {
					sender.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c n'existe pas."));
					return;
				}

				List<DatabaseSet> ipSets = database.request(
						"SELECT distinct * FROM players WHERE first_ip = ? OR last_ip = ? OR first_ip = ? OR last_ip = ?",
						Arrays.asList(set.getString("first_ip"), set.getString("first_ip"), set.getString("last_ip"), set.getString("last_ip"))
				);

				// On formatte la liste des autres comptes sur la même IP
				StringBuilder ips = new StringBuilder();
				for (DatabaseSet ipSet : ipSets)
					if (!ipSet.getString("playername").equals(playername))
						ips.append("§b").append(ipSet.getString("playername")).append("§7, ");

				if(ips.length() > 3) ips = new StringBuilder(ips.substring(0, ips.length() - 2));
				else ips = new StringBuilder("§cAucun");

				// Va permettre de formatter la liste des sanctions
				String sanctions = "";

				List<DatabaseSet> bans  = database.find("bungee_bans" , DatabaseSet.makeConditions("player", playername));
				List<DatabaseSet> kicks = database.find("bungee_kicks", DatabaseSet.makeConditions("player", playername));
				List<DatabaseSet> mutes = database.find("bungee_mutes", DatabaseSet.makeConditions("player", playername));

				if(bans.size()  > 0) sanctions += "§e" + bans.size()  + " ban"  + ((bans.size() > 1)  ? "s" : "") + "§7, ";
				if(mutes.size() > 0) sanctions += "§e" + mutes.size() + " mute" + ((mutes.size() > 1) ? "s" : "") + "§7, ";
				if(kicks.size() > 0) sanctions += "§e" + kicks.size() + " kick" + ((kicks.size() > 1) ? "s" : "") + "§7, ";

				if(sanctions.length() > 3) sanctions = sanctions.substring(0, sanctions.length() - 2);
				else                       sanctions = "§cAucune";

                // On récupère les différentes sanctions du joueur
                boolean hasSanction = false;
                String currentSanctions = "§7 - §dActuellement ";

				if (UtariaBungee.getModerationManager().playernameIsTempBanned(playername)) {
                    hasSanction = true;
                    currentSanctions += "§bbanni, ";
                }
                if (UtariaBungee.getModerationManager().playernameIsTempMuted(playername)) {
                    hasSanction = true;
                    currentSanctions += "§bmuté, ";
                }
                if (!currentSanctions.equals("§7 - §dActuellement "))
                	currentSanctions = currentSanctions.substring(0, currentSanctions.length() - 2);

				if (sender instanceof ProxiedPlayer) PlayerUtils.sendHorizontalLineWithText((ProxiedPlayer) sender, "§e" + args[0] + onlineString, ChatColor.BLUE);
				else                                 sender.sendMessage(new TextComponent("§9 ===============[ §e" + args[0] + onlineString + "§r§9 ]==============="));

				sender.sendMessage(new TextComponent(" "));
				sender.sendMessage(new TextComponent("§7 - Connexion (Pre/Der) : §6" + TimeParser.timeToString(set.getTimestamp("first_connection"), true) + "§7 / §6" + TimeParser.timeToString(set.getTimestamp("last_connection"), true) + "§7."));
				if (fHta) sender.sendMessage(new TextComponent("§7 - IP (Pre/Der) : §6" + set.getString("first_ip") + "§7 / §6" + set.getString("last_ip") + "§7."));
				else      sender.sendMessage(new TextComponent("§7 - IP (Pre/Der) : §8§knn.nnn.nnn.nn§7 / §8§knn.nnn.nnn.nn§7."));

				sender.sendMessage(new TextComponent(" "));
				sender.sendMessage(new TextComponent("§7 - Autres comptes : " + ips + "§7."));
				sender.sendMessage(new TextComponent("§7 - Sanctions : " + sanctions + "§7."));
                if (hasSanction) sender.sendMessage(new TextComponent(currentSanctions + "§7."));
                else             sender.sendMessage(new TextComponent(" "));

				sender.sendMessage(new TextComponent(" "));

				if (sender instanceof ProxiedPlayer) PlayerUtils.sendHorizontalLine((ProxiedPlayer) sender, ChatColor.BLUE);
				else                                 sender.sendMessage(new TextComponent("§9 =============================================================="));
			} else {
				if (!fHta) {
					sender.sendMessage(new TextComponent(Config.prefix + "§cVous n'avez pas accès à ces informations."));
					return;
				}

				String ip = args[0];

				if (sender instanceof ProxiedPlayer) PlayerUtils.sendHorizontalLineWithText((ProxiedPlayer) sender, "§eIP " + Utils.hideIP(ip), ChatColor.BLUE);
				else                                 sender.sendMessage(new TextComponent("§9 ===============[ §eIP " + Utils.hideIP(ip) + "§r§9 ]==============="));

				// On formatte la liste des autres comptes présent sur la même IP
				StringBuilder accounts = new StringBuilder();

				List<DatabaseSet> ipSets = UtariaBungee.getDatabase().request("SELECT distinct * from players where first_ip = ? or last_ip = ?;", Arrays.asList(ip, ip));

				for(DatabaseSet ipSet : ipSets)
					accounts.append("§b").append(ipSet.getString("playername")).append("§7, ");

				if (accounts.length() > 3) accounts = new StringBuilder(accounts.substring(0, accounts.length() - 2));
				else                       accounts = new StringBuilder("§cAucun");

				String ipPage;
				String country = "indisponible";
				String loc     = "indisponible";
				String fai     = "indisponible";

				try {

					ipPage = Utils.getUrlSource("http://ip-api.com/csv/" + ip);
					String[] params = ipPage.split(",");

					if (!params[0].equalsIgnoreCase("fail")) {
						country = params[1] + ", " + params[4];
						loc     = (params.length >  5) ? params[ 5] : "inconnue";
						fai     = (params.length > 10) ? params[10] : "inconnu";
					} else {
						country = "inconnu";
						loc     = "inconnue";
						fai     = "inconnu";
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

				sender.sendMessage(new TextComponent(" "));
				sender.sendMessage(new TextComponent("§7 - Comptes sur cette IP : " + accounts + "§7."));
				sender.sendMessage(new TextComponent("§7 - Provenance : §e" + country + "§7."));
				sender.sendMessage(new TextComponent("§7 - Localisation : §e" + loc + "§7."));
				sender.sendMessage(new TextComponent("§7 - FAI : §a" + fai + "§7."));

				if (sender instanceof ProxiedPlayer) { // Permet de formatter le message pour un joueur (dans le tchat)
					sender.sendMessage(new TextComponent(" "));
					sender.sendMessage(new TextComponent(" "));
					sender.sendMessage(new TextComponent(" "));
				}

				if (sender instanceof ProxiedPlayer) PlayerUtils.sendHorizontalLine((ProxiedPlayer) sender, ChatColor.BLUE);
				else                                 sender.sendMessage(new TextComponent("§9 =============================================================="));
			}
		});
	}
	
	
	private void printPlayerSanction(CommandSender sender, String playername, String module){
		final Database database   = UtariaBungee.getDatabase();

		if(module.equalsIgnoreCase("ban")){

			List<DatabaseSet> bans  = database.find("bungee_bans", DatabaseSet.makeConditions("player", playername), DatabaseSet.makeOrderBy("date", "DESC"));
			
			
			sender.sendMessage(new TextComponent(" "));
			
			sender.sendMessage(new TextComponent("§9=============[ §e" + playername + " §a- §a" + Utils.ucfirst(module) + "s§r§9 ]============="));
			sender.sendMessage(new TextComponent(" "));
			
			for(DatabaseSet ban : bans){
				boolean tempban = ban.getTimestamp("ban_end") != null;
				boolean unbanned = ban.getString("unbanned_by") != null;
				
				String unbannedString = "";
				if(unbanned) unbannedString  = "§a / Débanni le §2" + Utils.dateToString(ban.getTimestamp("unban_date")) + "§a par §b" + ban.getString("unbanned_by")
												+ "§a avec comme raison : §b" + ban.getString("unban_reason") + "§a.";
				
				if(!tempban){
					sender.sendMessage(new TextComponent("§7 - Le §6" + Utils.dateToString(ban.getTimestamp("date")) + "§7; Raison : §b" + ban.getString("reason")
							+ "§7; Par §e" + ban.getString("banned_by") + " §c(" + ((!ban.getString("server").equalsIgnoreCase("none")) ? ban.getString("server") : "aucun")
							+ ")§7." + unbannedString));
				}else{
					sender.sendMessage(new TextComponent("§7 - (Temp) Le §6" + Utils.dateToString(ban.getTimestamp("date")) + "§7; Raison : §b" + ban.getString("reason")
							+ "§7; Par §e" + ban.getString("banned_by") + " §c(" + ((!ban.getString("server").equalsIgnoreCase("none")) ? ban.getString("server") : "aucun")
							+ ")§7; Durée : §6" + TimeParser.timesToString(ban.getTimestamp("ban_end"), ban.getTimestamp("date")) + "§7." + unbannedString));
				}
				
				sender.sendMessage(new TextComponent(" "));
			}
			
			sender.sendMessage(new TextComponent(" "));
			
		}else if(module.equalsIgnoreCase("kick")){
			
			List<DatabaseSet> kicks  = database.find("bungee_kicks", DatabaseSet.makeConditions("player", playername), DatabaseSet.makeOrderBy("date", "DESC"));
			
			
			sender.sendMessage(new TextComponent(" "));
			
			sender.sendMessage(new TextComponent("§9=============[ §e" + playername + " §a- §a" + Utils.ucfirst(module) + "s§r§9 ]============="));
			sender.sendMessage(new TextComponent(" "));
			
			for(DatabaseSet kick : kicks){
				sender.sendMessage(new TextComponent("§7 - Le §6" + Utils.dateToString(kick.getTimestamp("date")) + "§7; Raison : §b" + kick.getString("reason")
						+ "§7; Par §e" + kick.getString("kicked_by") + " §c(" + ((!kick.getString("server").equalsIgnoreCase("none")) ? kick.getString("server") : "aucun")
						+ ")§7."));
				
				sender.sendMessage(new TextComponent(" "));
			}
			
			sender.sendMessage(new TextComponent(" "));
			
		}else if(module.equalsIgnoreCase("mute")){

			List<DatabaseSet> mutes  = database.find("bungee_mutes", DatabaseSet.makeConditions("player", playername), DatabaseSet.makeOrderBy("date", "DESC"));
			
			
			sender.sendMessage(new TextComponent(" "));
			
			sender.sendMessage(new TextComponent("§9=============[ §e" + playername + " §a- §a" + Utils.ucfirst(module) + "s§r§9 ]============="));
			sender.sendMessage(new TextComponent(" "));
			
			for(DatabaseSet mute : mutes){
				boolean tempmute = mute.getTimestamp("mute_end") != null;
				boolean unmuted = mute.getString("unmuted_by") != null;
				
				String unmutedString = "";
				if(unmuted) unmutedString  = "§a / Démuté le §2" + Utils.dateToString(mute.getTimestamp("unmute_date")) + "§a par §b" + mute.getString("unmuted_by")
												+ "§a avec comme raison : §b" + mute.getString("unmute_reason") + "§a.";
				
				if(!tempmute){
					sender.sendMessage(new TextComponent("§7 - Le §6" + Utils.dateToString(mute.getTimestamp("date")) + "§7; Raison : §b" + mute.getString("reason")
							+ "§7; Par §e" + mute.getString("muted_by") + " §c(" + ((!mute.getString("server").equalsIgnoreCase("none")) ? mute.getString("server") : "aucun")
							+ ")§7." + unmutedString));
				}else{
					sender.sendMessage(new TextComponent("§7 - (Temp) Le §6" + Utils.dateToString(mute.getTimestamp("date")) + "§7; Raison : §b" + mute.getString("reason")
							+ "§7; Par §e" + mute.getString("muted_by") + " §c(" + ((!mute.getString("server").equalsIgnoreCase("none")) ? mute.getString("server") : "aucun")
							+ ")§7; Durée : §6" + TimeParser.timesToString(mute.getTimestamp("mute_end"), mute.getTimestamp("date")) + "§7." + unmutedString));
				}
				
				sender.sendMessage(new TextComponent(" "));
			}
			
			sender.sendMessage(new TextComponent(" "));
			
		}else{
			sender.sendMessage(new TextComponent(Config.prefix + "§cModule §6" + module + "§c inconnu (ban, kick ou mute)."));
			return;
		}
	}
	
}
