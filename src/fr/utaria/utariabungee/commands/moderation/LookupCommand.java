package fr.utaria.utariabungee.commands.moderation;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.moderation.sanctions.Ban;
import fr.utaria.utariabungee.moderation.sanctions.Kick;
import fr.utaria.utariabungee.moderation.sanctions.Mute;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.players.PlayersManager;
import fr.utaria.utariabungee.util.PlayerUtil;
import fr.utaria.utariabungee.util.UUtil;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class LookupCommand extends AbstractCommand {

	private ModerationManager manager;


	public LookupCommand() {
		super("lookup", "lk");

		this.manager = UtariaBungee.getInstance().getInstance(ModerationManager.class);

		this.setRequiredRank("Modérateur");
		this.setMinArgs(1);
	}

	@Override
	public void perform(CommandSender sender) {

	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo playerInfo) {
		String  who  = this.getArgument(0);
		boolean isIp = UUtil.stringIsIP(who);

		// On regarde d'abord si on a une information dans la base
		DatabaseSet wInfo = PlayersManager.getInfoAbout(who);

		if (wInfo == null) {
			player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Aucun information n'a été trouvée pour §6\"" + who + "\"§c."));
			return;
		}

		// Gestion des modules de lookup (en sous-commande)
		String module = this.getArgument(1);

		if (!isIp && "ban".equals(module)) {
			this.banModule(player, who);
			return;
		} else if (!isIp && "mute".equals(module)) {
			this.muteModule(player, who);
			return;
		} else if (!isIp && "kick".equals(module)) {
			this.kickModule(player, who);
			return;
		}

		// On prépare ce qu'il faut ...
		List<String> othAccounts = PlayersManager.getAccountsUsingIp(wInfo.getString("last_ip"));
		othAccounts.remove(who);
		String       accList     = (othAccounts.size() > 0) ? StringUtils.join(othAccounts, ", ") : "§caucun";

		String sanctions = this.manager.getRawSanctionsFor(who);

		String fConn = UUtil.dateToString(wInfo.getTimestamp("first_connection"));
		String lConn = UUtil.dateToString(wInfo.getTimestamp("last_connection"));
		String fip   = wInfo.getString("first_ip");
		String lIp   = wInfo.getString("last_ip");

		// ... et on affiche tout ce qu'on a récupéré au joueur !
		this.sendHeader(player, who);

		if (!isIp) {
			player.sendMessage(TextComponent.fromLegacyText("    §71ère connexion : §6" + fConn));
			player.sendMessage(TextComponent.fromLegacyText("    §7Dern connexion : §6" + lConn));
			player.sendMessage(TextComponent.fromLegacyText("    §71ère/Dern IP : §3" + fip + " / " + lIp));
			player.sendMessage(new TextComponent());
			player.sendMessage(TextComponent.fromLegacyText("    §7Autres comptes : §b" + accList));
			player.sendMessage(TextComponent.fromLegacyText("    §7Sanctions : " + sanctions));
		} else {
			player.sendMessage(TextComponent.fromLegacyText("    §7Comptes sur l'IP : §b" + accList));
			player.sendMessage(TextComponent.fromLegacyText("    §7Localisation : §cen développement"));
			player.sendMessage(TextComponent.fromLegacyText("    §7FAI : §cen développement"));
		}

		player.sendMessage(new TextComponent());
	}

	@Override
	public void performConsole(CommandSender sender) {

	}


	private void sendHeader(ProxiedPlayer player, String who) {
		boolean isIp = UUtil.stringIsIP(who);

		String state    = PlayerUtil.isConnected(who) ? "§aEn ligne" : "§cHors ligne";
		String version  = PlayerUtil.isConnected(who) ? " (" + PlayerUtil.getClientVersionOf(ProxyServer.getInstance().getPlayer(who)) + ")" : "";
		String titleEnd = (!isIp) ? " §8<§m---§r§8>§r " + state + version : "";

		player.sendMessage(new TextComponent());
		PlayerUtil.sendCenteredMessage(player, "§f§l" + who + titleEnd);
		player.sendMessage(new TextComponent());
	}

	private void banModule(ProxiedPlayer player, String who) {
		List<Ban> bans = this.manager.getBanListOf(who);

		this.sendHeader(player, who);

		for (Ban ban : bans) {
			StringBuilder b = new StringBuilder();

			b.append("  §7Date: §6").append(UUtil.dateToString(ban.getDate())).append(" §8- ");
			b.append("§7Raison: §f").append(ban.getReason()).append(" §8- ");
			b.append("§7Staff: §a").append(ban.getBannedBy());

			if (ban.isTemporary()) {
				b.append(" §8- §7Fin: §6").append(UUtil.dateToString(ban.getBanEnd()));
			}
			if (ban.getUnbanDate() != null) {
				b.append(" §8||| §7Débanni le: §6").append(UUtil.dateToString(ban.getUnbanDate()));
				b.append(" §8- §7Raison: §f").append(ban.getUnbanReason());
				b.append(" §8- §7Par: §a").append(ban.getUnbannedBy());
			}

			player.sendMessage(TextComponent.fromLegacyText(b.toString()));
			player.sendMessage(new TextComponent());
		}
	}

	private void muteModule(ProxiedPlayer player, String who) {
		List<Mute> mutes = this.manager.getMuteListOf(who);

		this.sendHeader(player, who);

		for (Mute mute: mutes) {
			StringBuilder b = new StringBuilder();

			b.append("  §7Date: §6").append(UUtil.dateToString(mute.getDate())).append(" §8- ");
			b.append("§7Raison: §f").append(mute.getReason()).append(" §8- ");
			b.append("§7Staff: §a").append(mute.getMutedBy());

			if (mute.isTemporary()) {
				b.append(" §8- §7Fin: §6").append(UUtil.dateToString(mute.getMuteEnd()));
			}
			if (mute.getUnmuteDate() != null) {
				b.append(" §8||| §7Débanni le: §6").append(UUtil.dateToString(mute.getUnmuteDate()));
				b.append(" §8- §7Raison: §f").append(mute.getUnmuteReason());
				b.append(" §8- §7Par: §a").append(mute.getUnmutedBy());
			}

			player.sendMessage(TextComponent.fromLegacyText(b.toString()));
			player.sendMessage(new TextComponent());
		}
	}

	private void kickModule(ProxiedPlayer player, String who) {
		List<Kick> kicks = this.manager.getKickListOf(who);

		this.sendHeader(player, who);

		for (Kick kick : kicks) {
			String b = "  §7Date: §6" + UUtil.dateToString(kick.getDate()) + " §8- " +
					"§7Raison: §f" + kick.getReason() + " §8- " +
					"§7Staff: §a" + kick.getKickedBy();

			player.sendMessage(TextComponent.fromLegacyText(b));
			player.sendMessage(new TextComponent());
		}
	}

}
