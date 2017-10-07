package fr.utaria.utariabungee.moderation.sanctions;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.util.time.UTime;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.ChatColor;

import java.sql.Timestamp;

public class Mute implements Sanction {

	private String player;
	private String ip;
	private String reason;
	private String server;
	private String mutedBy;
	private Timestamp date;

	private Timestamp muteEnd;

	private String    unmutedBy;
	private Timestamp unmuteDate;
	private String    unmuteReason;


	private Mute(String player, String ip, String reason, String server, String mutedBy, Timestamp date) {
		this.player  = player;
		this.ip      = ip;
		this.reason  = reason;
		this.server  = server;
		this.mutedBy = mutedBy;
		this.date    = date;
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
	public String    getMutedBy() {
		return this.mutedBy;
	}
	public Timestamp getDate() {
		return this.date;
	}

	public Timestamp getMuteEnd() {
		return this.muteEnd;
	}

	public String    getUnmutedBy() {
		return this.unmutedBy;
	}
	public Timestamp getUnmuteDate() {
		return this.unmuteDate;
	}
	public String    getUnmuteReason() {
		return this.unmuteReason;
	}


	public boolean isTemporary() {
		return this.muteEnd != null;
	}
	public boolean isValid() {
		return this.muteEnd == null || this.muteEnd.after(new Timestamp(System.currentTimeMillis()));
	}


	private void setUnbanData(String unmutedBy, Timestamp unmuteDate, String unmuteReason) {
		this.unmutedBy    = unmutedBy;
		this.unmuteDate   = unmuteDate;
		this.unmuteReason = unmuteReason;
	}
	private void setEndData(Timestamp muteEnd) {
		this.muteEnd = muteEnd;
	}



	@Override
	public String getMessage() {
		String end = "";

		if (this.isTemporary())
			end = " Fin le §6" + new UTime(this.muteEnd).toFrenchString() + "§c.";

		return Config.ERROR_PREFIX + "Votre compte a été mis en silencieux !" + end + " Raison: §f" + ChatColor.stripColor(this.reason);
	}

	public String getExpirationMessage() {
		return Config.INFO_PREFIX + "Votre sanction vient de prendre fin. Vous pouvez §amaintenant parler§7.";
	}


	public static Mute fromDBSet(DatabaseSet set) {
		if (set == null) return null;

		Mute mute = new Mute(
				set.getString("player"), set.getString("ip"), set.getString("reason"),
				set.getString("server"), set.getString("muted_by"), set.getTimestamp("date")
		);

		// Info d'unban
		if (set.getString("unmute_date") != null)
			mute.setUnbanData(set.getString("unmuted_by"), set.getTimestamp("unmute_date"), set.getString("unmute_reason"));

		// Info de ban temporaire
		if (set.getTimestamp("mute_end") != null)
			mute.setEndData(set.getTimestamp("mute_end"));

		return mute;
	}


	public enum MuteState {
		LOADING, YES, NO
	}

}
