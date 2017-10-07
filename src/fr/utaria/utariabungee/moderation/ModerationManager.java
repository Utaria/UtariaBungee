package fr.utaria.utariabungee.moderation;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.moderation.sanctions.Ban;
import fr.utaria.utariabungee.moderation.sanctions.Mute;
import fr.utaria.utariabungee.moderation.task.MuteTask;
import fr.utaria.utariabungee.util.PlayerUtil;
import fr.utaria.utariabungee.util.UUtil;
import fr.utaria.utariabungee.util.time.UTime;
import fr.utaria.utariadatabase.query.SavingQuery;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

public class ModerationManager extends AbstractManager {

	private HashMap<ProxiedPlayer, Mute> mutedPlayers;


	public ModerationManager() {
		super(UtariaBungee.getInstance(), "moderation");

		this.mutedPlayers = new HashMap<>();

		this.registerListener(new ModerationListener(this));

		// Lancement de la tâche automatique pour rafraîchir
		// les mutes des joueurs connectés.
		new MuteTask(this);
	}

	@Override
	public void initialize() {

	}


	public HashMap<ProxiedPlayer, Mute> getMutedPlayers() {
		return this.mutedPlayers;
	}


	public Ban getActiveBanFor(final PendingConnection connection) {
		DatabaseSet banSet = this.getDB().select().from("bans")
			                       	     .where("(player = ? OR ip = ?)", "(ban_end IS NULL OR ban_end >= NOW())", "unban_date IS NULL")
				                         .attributes(connection.getName(), connection.getAddress().getAddress().getHostAddress())
				                         .order("date DESC").find();

		return banSet != null ? Ban.fromDBSet(banSet) : null;
	}
	public boolean isBanned(String who) {
		boolean isIp       = UUtil.stringIsIP(who);
		String  playername = (!isIp) ? who : null;
		String  ip         = ( isIp) ? who : null;

		DatabaseSet banSet = this.getDB().select().from("bans")
				.where("(player = ? AND ip = ?)", "(ban_end IS NULL OR ban_end >= NOW())", "unban_date IS NULL")
				.attributes(playername, ip).find();

		return banSet != null;
	}

	void           loadActiveMuteFor(final ProxiedPlayer player) {
		if (this.mutedPlayers.containsKey(player)) return;

		DatabaseSet muteSet = this.getDB().select().from("mutes")
				                          .where("(player = ? OR ip = ?)", "(mute_end IS NULL OR mute_end >= NOW())", "unmute_date IS NULL")
				                          .attributes(player.getName(), UUtil.getPlayerIP(player))
				                          .order("date DESC").find();

		this.mutedPlayers.put(player, Mute.fromDBSet(muteSet));
	}
	void           removeCacheMuteOf(final ProxiedPlayer player) {
		this.mutedPlayers.remove(player);
	}
	Mute.MuteState getMuteStateFor(final ProxiedPlayer player) {
		if (!this.mutedPlayers.containsKey(player)) return Mute.MuteState.LOADING;
		return (this.mutedPlayers.get(player) == null) ? Mute.MuteState.NO : Mute.MuteState.YES;
	}
	Mute           getActiveMuteFor(final ProxiedPlayer player) {
		return this.mutedPlayers.get(player);
	}


	public void ban(String who, String reason, CommandSender author, UTime expirationDelay) {
		ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
			// On prépare ce qu'il faut.
			boolean isIp = UUtil.stringIsIP(who);
			UTime   now  = UTime.now();
			UTime   end  = (expirationDelay != null) ? now.simpleAdd(expirationDelay) : null;

			String server = (author instanceof ProxiedPlayer) ? ((ProxiedPlayer) author).getServer().getInfo().getName() : null;


			// On met à jour la base de données
			SavingQuery query = this.getDB().update("bans").fields("player", "ip", "reason", "server", "banned_by", "date", "ban_end");

			query.values(
					(!isIp) ? who : null,
					( isIp) ? who : null,
					ChatColor.stripColor(reason),
					server, author.getName(),
					now.getTimestamp(), end
			);

			query.execute();


			// On envoie le message de la sanction à tout le monde ...!
			String nWho    = (isIp) ? UUtil.hideIP(who) : who;
			String banTime = (end != null) ? " §6" + null : "";

			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(
					Config.MOD_PREFIX + "§e" + nWho + " §7a été banni" + banTime + " §7pour: §c" + reason
			));


			// On expulse le(s) joueur(s) concerné(s)
			PlayerUtil.kick(who, TextComponent.fromLegacyText(Ban.generateMessageFrom(reason, (end != null) ? end.getTimestamp() : null)));
		});
	}

}
