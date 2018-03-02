package fr.utaria.utariabungee.moderation;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.moderation.sanctions.Ban;
import fr.utaria.utariabungee.moderation.sanctions.Kick;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	public String getRawSanctionsFor(String who) {
		StringBuilder b = new StringBuilder();
		b.append(ChatColor.YELLOW);

		// On récupère les informations depuis la base de données ...
		int nbBan = this.getDB().select("id").from("bans").where("player = ?").attributes(who).findAll().size();
		int nbMute = this.getDB().select("id").from("mutes").where("player = ?").attributes(who).findAll().size();
		int nbKick = this.getDB().select("id").from("kicks").where("player = ?").attributes(who).findAll().size();

		boolean underban = this.getActiveBanFor(who) != null;
		boolean underMute = this.getActiveMuteFor(who) != null;

		// ... et on les formate !
		if (nbBan > 0) {
			b.append(nbBan).append(" ban").append((nbBan > 1) ? "s" : "");
			if (underban) b.append(" §d(A)§e");

			b.append(", ");
		}
		if (nbMute > 0) {
			b.append(nbMute).append(" mute").append((nbMute > 1) ? "s" : "");
			if (underMute) b.append(" §d(A)§e");

			b.append(", ");
		}
		if (nbKick > 0)
			b.append(nbKick).append(" kick").append((nbKick > 1) ? "s" : "");

		if (b.length() == 2)
			b.append(ChatColor.RED).append("aucune");
		if (b.substring(b.length() - 2, b.length()).equals(", "))
			b = new StringBuilder(b.substring(0, b.length() - 2));

		return b.toString();
	}

	public List<Ban> getBanListOf(String who) {
		List<Ban> bans = new ArrayList<>();

		List<DatabaseSet> sets = this.getDB().select().from("bans").where("player = ?").attributes(who).findAll();
		for (DatabaseSet set : sets)
			bans.add(Ban.fromDBSet(set));

		return bans;
	}

	public List<Mute> getMuteListOf(String who) {
		List<Mute> mutes = new ArrayList<>();

		List<DatabaseSet> sets = this.getDB().select().from("mutes").where("player = ?").attributes(who).findAll();
		for (DatabaseSet set : sets)
			mutes.add(Mute.fromDBSet(set));

		return mutes;
	}

	public List<Kick> getKickListOf(String who) {
		List<Kick> kicks = new ArrayList<>();

		List<DatabaseSet> sets = this.getDB().select().from("kicks").where("player = ?").attributes(who).findAll();
		for (DatabaseSet set : sets)
			kicks.add(Kick.fromDBSet(set));

		return kicks;
	}

	public Ban getActiveBanFor(final PendingConnection connection) {
		DatabaseSet banSet = this.getDB().select().from("bans")
				.where("(player = ? OR ip = ?)", "(ban_end IS NULL OR ban_end >= NOW())", "unban_date IS NULL")
				.attributes(connection.getName(), connection.getAddress().getAddress().getHostAddress())
				.order("date DESC").find();

		return banSet != null ? Ban.fromDBSet(banSet) : null;
	}

	private Ban getActiveBanFor(String who) {
		boolean isIp = UUtil.stringIsIP(who);
		String playername = (!isIp) ? who : null;
		String ip = (isIp) ? who : null;

		DatabaseSet banSet = this.getDB().select().from("bans")
				.where("(player = ? OR ip = ?)", "(ban_end IS NULL OR ban_end >= NOW())", "unban_date IS NULL")
				.attributes(playername, ip).find();

		return banSet != null ? Ban.fromDBSet(banSet) : null;
	}

	void loadActiveMuteFor(final ProxiedPlayer player) {
		if (this.mutedPlayers.get(player) != null) return;

		DatabaseSet muteSet = this.getDB().select().from("mutes")
				.where("(player = ? OR ip = ?)", "(mute_end IS NULL OR mute_end >= NOW())", "unmute_date IS NULL")
				.attributes(player.getName(), UUtil.getPlayerIP(player))
				.order("date DESC").find();

		this.mutedPlayers.put(player, Mute.fromDBSet(muteSet));
	}

	void removeCacheMuteOf(final ProxiedPlayer player) {
		this.mutedPlayers.remove(player);
	}

	public Mute.MuteState getMuteStateFor(final ProxiedPlayer player) {
		if (!this.mutedPlayers.containsKey(player)) return Mute.MuteState.LOADING;
		return (this.mutedPlayers.get(player) == null) ? Mute.MuteState.NO : Mute.MuteState.YES;
	}

	Mute getActiveMuteFor(final ProxiedPlayer player) {
		return this.mutedPlayers.get(player);
	}

	private Mute getActiveMuteFor(String who) {
		boolean isIp = UUtil.stringIsIP(who);
		String playername = (!isIp) ? who : null;
		String ip = (isIp) ? who : null;

		DatabaseSet muteSet = this.getDB().select().from("mutes")
				.where("(player = ? OR ip = ?)", "(mute_end IS NULL OR mute_end >= NOW())", "unmute_date IS NULL")
				.attributes(playername, ip).find();

		return muteSet != null ? Mute.fromDBSet(muteSet) : null;
	}

	public boolean ban(String who, String reason, CommandSender author, UTime expirationDelay) {
		// Vérification du temps
		if (expirationDelay != null && !expirationDelay.isValid()) return false;

		ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
			// On prépare ce qu'il faut.
			boolean isIp = UUtil.stringIsIP(who);
			UTime now = UTime.now();
			UTime end = (expirationDelay != null) ? expirationDelay.simpleAdd(now) : null;

			String server = (author instanceof ProxiedPlayer) ? ((ProxiedPlayer) author).getServer().getInfo().getName() : null;


			// On met à jour la base de données
			SavingQuery query = this.getDB().update("bans").fields("player", "ip", "reason", "server", "banned_by", "date", "ban_end");

			query.values(
					(!isIp) ? who : null,
					(isIp) ? who : null,
					ChatColor.stripColor(reason),
					server, author.getName(),
					now.getTimestamp(), (end != null) ? end.getTimestamp() : null
			);

			query.execute();


			// On envoie le message de la sanction à tout le monde ...!
			String nWho = (isIp) ? UUtil.hideIP(who) : who;
			String banTime = (end != null) ? " §6" + end.getStringFormat() : "";

			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(
					Config.MOD_PREFIX + "§e" + nWho + " §7a été banni" + banTime + " §7pour: §c" + reason
			));


			// On expulse le(s) joueur(s) concerné(s)
			PlayerUtil.kick(who, TextComponent.fromLegacyText(Ban.generateMessageFrom(reason, (end != null) ? end.getTimestamp() : null)));
		});

		return true;
	}

	public boolean mute(String who, String reason, CommandSender author, UTime expirationDelay) {
		// Vérification du temps
		if (expirationDelay != null && !expirationDelay.isValid()) return false;

		ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
			// On prépare ce qu'il faut.
			boolean isIp = UUtil.stringIsIP(who);
			UTime now = UTime.now();
			UTime end = (expirationDelay != null) ? expirationDelay.simpleAdd(now) : null;

			String server = (author instanceof ProxiedPlayer) ? ((ProxiedPlayer) author).getServer().getInfo().getName() : null;


			// On met à jour la base de données ...
			SavingQuery query = this.getDB().update("mutes").fields("player", "ip", "reason", "server", "muted_by", "date", "mute_end");

			query.values(
					(!isIp) ? who : null,
					(isIp) ? who : null,
					ChatColor.stripColor(reason),
					server, author.getName(),
					now.getTimestamp(), (end != null) ? end.getTimestamp() : null
			);

			query.execute();


			// ... et on ajoute le mute en mémoire pour tous les joueurs concernés et connectés !
			for (ProxiedPlayer player : PlayerUtil.listPlayers(who))
				this.loadActiveMuteFor(player);


			// On envoie le message de la sanction à tout le monde ...!
			String nWho = (isIp) ? UUtil.hideIP(who) : who;
			String muteTime = (end != null) ? " §6" + end.getStringFormat() : "";

			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(
					Config.MOD_PREFIX + "§e" + nWho + " §7a été muté" + muteTime + " §7pour: §c" + reason
			));


			// On envoie le message de la sanction au(x) joueur(s) concerné(s)
			PlayerUtil.sendMessage(who, TextComponent.fromLegacyText(Mute.generateMessageFrom(reason, (end != null) ? end.getTimestamp() : null)));
		});

		return true;
	}

	public boolean kick(String who, String reason, CommandSender author) {
		List<ProxiedPlayer> players = PlayerUtil.listPlayers(who);
		if (players.isEmpty()) return false;

		ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
			// On prépare ce qu'il faut.
			boolean isIp = UUtil.stringIsIP(who);
			UTime now = UTime.now();
			String server = (author instanceof ProxiedPlayer) ? ((ProxiedPlayer) author).getServer().getInfo().getName() : null;


			// On met à jour la base de données
			SavingQuery query = this.getDB().update("kicks").fields("player", "ip", "reason", "server", "kicked_by", "date");

			query.values(
					(!isIp) ? who : null,
					(isIp) ? who : null,
					ChatColor.stripColor(reason),
					server, author.getName(),
					now.getTimestamp()
			);

			query.execute();


			// On envoie le message de la sanction à tout le monde ...!
			String nWho = (isIp) ? UUtil.hideIP(who) : who;

			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(
					Config.MOD_PREFIX + "§e" + nWho + " §7a été expulsé pour: §c" + reason
			));


			// On envoie le message de la sanction au(x) joueur(s) concerné(s)
			PlayerUtil.kick(who, TextComponent.fromLegacyText(Kick.generateMessageFrom(reason)));
		});

		return true;
	}

	public boolean unban(String who, String reason, CommandSender author) {
		Ban activeBan = this.getActiveBanFor(who);
		if (activeBan == null) return false;

		ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
			UTime now = UTime.now();

			// On met à jour la base de données ...
			SavingQuery query = this.getDB().update("bans").fields("unbanned_by", "unban_date", "unban_reason")
					.values(author.getName(), now.getTimestamp(), reason)
					.where("(player = ? OR ip = ?)", "date = ?");

			query.attributes(activeBan.getPlayer(), activeBan.getIp(), activeBan.getDate());
			query.execute();

			// ... et c'est tout !
		});

		return true;
	}

	public boolean unmute(String who, String reason, CommandSender author) {
		Mute activeMute = this.getActiveMuteFor(who);
		if (activeMute == null) return false;

		ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
			UTime now = UTime.now();

			// On met à jour la base de données ...
			SavingQuery query = this.getDB().update("mutes").fields("unmuted_by", "unmute_date", "unmute_reason")
					.values(author.getName(), now.getTimestamp(), reason)
					.where("(player = ? OR ip = ?)", "date = ?");

			query.attributes(activeMute.getPlayer(), activeMute.getIp(), activeMute.getDate());
			query.execute();

			// ... et on supprime du cache le mute (joueur(s) connecté(s)) !
			for (ProxiedPlayer player : PlayerUtil.listPlayers(who))
				this.mutedPlayers.put(player, null);
		});

		return true;
	}

}
