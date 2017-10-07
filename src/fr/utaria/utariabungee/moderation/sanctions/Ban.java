package fr.utaria.utariabungee.moderation.sanctions;

import fr.utaria.utariabungee.util.time.UTime;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.ChatColor;

import java.sql.Timestamp;

public class Ban implements Sanction {

	private String player;
	private String ip;
	private String reason;
	private String server;
	private String bannedBy;
	private Timestamp date;

	private Timestamp banEnd;

	private String    unbannedBy;
	private Timestamp unbanDate;
	private String    unbanReason;


	private Ban(String player, String ip, String reason, String server, String bannedBy, Timestamp date) {
		this.player   = player;
		this.ip       = ip;
		this.reason   = reason;
		this.server   = server;
		this.bannedBy = bannedBy;
		this.date     = date;
	}


	public String    getPlayer() {
		return this.player;
	}
	public String    getIp() {
		return this.ip;
	}
	public String    getReason() {
		return this.reason;
	}
	public String    getServer() {
		return this.server;
	}
	public String    getBannedBy() {
		return this.bannedBy;
	}
	public Timestamp getDate() {
		return this.date;
	}

	public Timestamp getBanEnd() {
		return this.banEnd;
	}

	public String    getUnbannedBy() {
		return this.unbannedBy;
	}
	public Timestamp getUnbanDate() {
		return this.unbanDate;
	}
	public String    getUnbanReason() {
		return this.unbanReason;
	}


	public boolean isTemporary() {
		return this.banEnd != null;
	}


	private void setUnbanData(String unbannedBy, Timestamp unbanDate, String unbanReason) {
		this.unbannedBy  = unbannedBy;
		this.unbanDate   = unbanDate;
		this.unbanReason = unbanReason;
	}
	private void setEndData(Timestamp banEnd) {
		this.banEnd = banEnd;
	}

	@Override
	public String getMessage() {
		String type = (this.isTemporary()) ? "§cTu es temporairement banni du serveur !" : "§cTu es définitivement banni du serveur !";

		String reason  = "§7Raison : §f" + ChatColor.stripColor(this.reason);
		String endDate = "";

		if (this.isTemporary())
			endDate = "§7Fin : §6" + new UTime(this.banEnd).toFrenchString() + "\n";

		return type + "\n\n" + endDate + reason;
	}


	public static Ban fromDBSet(DatabaseSet set) {
		if (set == null) return null;

		Ban ban = new Ban(
				set.getString("player"), set.getString("ip"), set.getString("reason"),
				set.getString("server"), set.getString("banned_by"), set.getTimestamp("date")
		);

		// Info d'unban
		if (set.getString("unbanned_by") != null)
			ban.setUnbanData(set.getString("unbanned_by"), set.getTimestamp("unban_date"), set.getString("unban_reason"));

		// Info de ban temporaire
		if (set.getTimestamp("ban_end") != null)
			ban.setEndData(set.getTimestamp("ban_end"));

		return ban;
	}

	public static String generateMessageFrom(String reason, Timestamp banEnd) {
		Ban ban = new Ban(null, null, reason, null, null, null);
		if (banEnd != null) ban.setEndData(banEnd);

		return ban.getMessage();
	}

}
