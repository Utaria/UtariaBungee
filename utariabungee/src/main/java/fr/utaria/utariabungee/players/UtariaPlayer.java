package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.network.ProxyManager;
import fr.utaria.utariabungee.util.UUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UtariaPlayer {

	private ProxiedPlayer player;

	private PlayerInfo playerInfo;

	private List<Permission> permissions;

	public UtariaPlayer(ProxiedPlayer player) {
		this.player = player;
		this.permissions = new ArrayList<>();

		this.playerInfo = new PlayerInfo(this);
	}

	public ProxiedPlayer getPlayer() {
		return this.player;
	}

	public String getPlayerName() {
		return this.player.getName();
	}

	public UUID getPlayerUniqueId() {
		return this.player.getUniqueId();
	}

	public PlayerInfo getPlayerInfo() {
		return this.playerInfo;
	}

	public String getIP() {
		if (this.player == null) return "0.0.0.0";
		return UUtil.getPlayerIP(this.player);
	}

	public boolean isOnDefaultServer() {
		return this.player.getServer().getInfo().getName().equalsIgnoreCase(
				UtariaBungee.getInstance().getInstance(ProxyManager.class).getDefaultServer().getName()
		);
	}

	void addPerm(String permission, Object value, Timestamp expiration) {
		this.permissions.add(new Permission(permission, value, expiration));
	}

	public boolean hasPerm(String permission) {
		for (Permission perm : this.permissions)
			if (perm.isValid() && perm.matchPermission(permission))
				return true;

		return false;
	}

	public Object getPermValue(String permission) {
		Permission rPerm = null;

		for (Permission perm : this.permissions)
			if (perm.isValid() && perm.matchPermission(permission) &&
					(rPerm == null || rPerm.getName().length() > perm.getName().length()))
				rPerm = perm;

		return (rPerm != null) ? rPerm.getValue() : null;
	}

	public int getPermIntValue(String permission) {
		return this.getPermIntValue(permission, false);
	}

	public int getPermIntValue(String permission, boolean multiPerms) {
		int value = 0;

		for (Permission perm : this.permissions)
			if (perm.isValid() && perm.matchPermission(permission))
				if (perm.getValue() != null && perm.getValue() instanceof Integer) {
					value += (Integer) perm.getValue();

					if (!multiPerms)
						return value;
				}

		return value;
	}

	public static UtariaPlayer get(ProxiedPlayer player) {
		return PlayersManager.getPlayer(player);
	}

}
