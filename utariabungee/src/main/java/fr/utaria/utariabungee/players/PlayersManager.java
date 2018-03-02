package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.util.UUtil;
import fr.utaria.utariadatabase.database.Database;
import fr.utaria.utariadatabase.database.DatabaseManager;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayersManager extends AbstractManager {

	public final static String PLAYERS_TABLE = "players";

	private static List<UtariaPlayer> players;

	public PlayersManager() {
		super(UtariaBungee.getInstance(), "global");
		players = new ArrayList<>();
	}

	@Override
	public void initialize() {

	}

	public static UtariaPlayer getPlayer(ProxiedPlayer player) {
		for (UtariaPlayer utariaPlayer : players)
			if (utariaPlayer.getPlayer().getUniqueId().equals(player.getUniqueId()))
				return utariaPlayer;

		UtariaPlayer utariaPlayer = new UtariaPlayer(player);

		players.add(utariaPlayer);
		return utariaPlayer;
	}

	public static PlayerInfo getPlayerInfo(ProxiedPlayer player) {
		UtariaPlayer uPlayer = UtariaPlayer.get(player);
		if (uPlayer == null) return null;
		return uPlayer.getPlayerInfo();
	}

	public static DatabaseSet getInfoAbout(String who) {
		Database db = DatabaseManager.getDB("global");

		if (!UUtil.stringIsIP(who))
			return db.select().from("players").where("playername = ?").attributes(who).find();
		else
			return db.select().from("players").where("first_ip = ? or last_ip = ?").attributes(who, who).find();
	}

	public static List<String> getAccountsUsingIp(String ip) {
		Database db = DatabaseManager.getDB("global");
		List<String> acc = new ArrayList<>();

		List<DatabaseSet> sets = db.select("playername").from("players").where("first_ip = ? OR last_ip = ?").attributes(ip, ip).findAll();
		for (DatabaseSet set : sets)
			acc.add(set.getString("playername"));

		return acc;
	}

	public static boolean unloadPlayerInfo(ProxiedPlayer player) {
		UtariaPlayer uPlayer = PlayersManager.getPlayer(player);
		if (uPlayer == null) return false;

		players.remove(uPlayer);
		return true;
	}

}
