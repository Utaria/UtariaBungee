package fr.utaria.utariabungee.moderation.sanctions;

import fr.utaria.utariadatabase.result.DatabaseSet;

import java.sql.Timestamp;

public class Kick implements Sanction {

	private String player;

	private String ip;

	private String reason;

	private String server;

	private String kickedBy;

	private Timestamp date;

	private Kick(String player, String ip, String reason, String server, String kickedBy, Timestamp date) {
		this.player = player;
		this.ip = ip;
		this.reason = reason;
		this.server = server;
		this.kickedBy = kickedBy;
		this.date = date;
	}

	public String getPlayer() {
		return this.player;
	}

	public String getIp() {
		return this.ip;
	}

	public String getReason() {
		return this.reason;
	}

	public String getServer() {
		return this.server;
	}

	public String getKickedBy() {
		return this.kickedBy;
	}

	public Timestamp getDate() {
		return this.date;
	}

	@Override
	public String getMessage() {
		return "§cTu as été expulsé du serveur !\n\n§7Raison : §f" + this.reason;
	}

	public static Kick fromDBSet(DatabaseSet set) {
		if (set == null) return null;

		return new Kick(
				set.getString("player"), set.getString("ip"), set.getString("reason"),
				set.getString("server"), set.getString("kicked_by"), set.getTimestamp("date")
		);
	}

	public static String generateMessageFrom(String reason) {
		return new Kick(null, null, reason, null, null, null).getMessage();
	}

}
