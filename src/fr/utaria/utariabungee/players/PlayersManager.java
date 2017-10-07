package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.util.UUtil;
import fr.utaria.utariadatabase.result.DatabaseSet;
import fr.utaria.utariadatabase.util.ConfigTableAccessor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayersManager extends AbstractManager {

	public final static String PLAYERS_TABLE         = "players";
	public final static String PLAYERS_RANKS_TABLE   = "players_ranks";
	public final static String RANKS_TABLE           = "ranks";

	private static List<UtariaPlayer> players;
	private static List<UtariaRank> ranks;


	public PlayersManager() {
		super(UtariaBungee.getInstance(), "global");

		players = new ArrayList<>();
		ranks   = new ArrayList<>();

		this.reloadRanks();
	}

	@Override
	public void initialize() {

	}




	public void reloadRanks() {
		List<DatabaseSet> sets = this.getDB().select().from(RANKS_TABLE).findAll();

		/*   On mets à jour les grades depuis la BDD   */
		for (DatabaseSet set : sets)
			ranks.add(new UtariaRank(
					set.getInteger("id"),
					set.getString("name"),
					set.getString("color"),
					set.getInteger("level"),
					set.getString("expiry_time")
			));

		/*   On va ensuite chercher le grade par défaut (s'il existe)   */
		String defaultRankStr = ConfigTableAccessor.getString("default_rank");

		if (defaultRankStr != null && UUtil.isInteger(defaultRankStr)) {
			int defaultRankId = Integer.parseInt(defaultRankStr);

			for (UtariaRank rank : ranks)
				if (rank.getId() == defaultRankId)
					rank.setDefault(true);
				else
					rank.setDefault(false);
		}
	}


	public static UtariaPlayer     getPlayer(ProxiedPlayer player) {
		for (UtariaPlayer utariaPlayer : players)
			if (utariaPlayer.getPlayer().getUniqueId().equals(player.getUniqueId()))
				return utariaPlayer;

		UtariaPlayer utariaPlayer = new UtariaPlayer(player);

		players.add(utariaPlayer);
		return utariaPlayer;
	}
	public static PlayerInfo       getPlayerInfo(ProxiedPlayer player) {
		UtariaPlayer uPlayer = UtariaPlayer.get(player);
		if (uPlayer == null) return null;
		return uPlayer.getPlayerInfo();
	}
	public static List<UtariaRank> getPlayerRanks(ProxiedPlayer player) {
		PlayerInfo info = PlayersManager.getPlayerInfo(player);
		return (info == null) ? null : info.getRanks();
	}
	public static UtariaRank       getPlayerHighestRank(ProxiedPlayer player) {
		PlayerInfo info = PlayersManager.getPlayerInfo(player);
		return (info == null) ? null : info.getHighestRank();
	}

	public static List<UtariaRank> getRanks() {
		return ranks;
	}
	public static List<UtariaRank> getRanksWithLevel(int level) {
		List<UtariaRank> ranks1 = new ArrayList<>();

		ranks1.addAll(ranks);
		ranks1.removeIf((rank) -> rank.getLevel() != level);

		return ranks1;
	}
	public static UtariaRank       getDefaultRank() {
		for (UtariaRank rank : ranks)
			if (rank.isDefault())
				return rank;
		return null;
	}
	public static UtariaRank       getRankById(int rankId) {
		for (UtariaRank rank : ranks)
			if (rank.getId() == rankId)
				return rank;
		return null;
	}
	public static UtariaRank       getRankByName(String name) {
		for (UtariaRank rank : ranks)
			if (rank.getName().equals(name))
				return rank;
		return null;
	}

	public static boolean playerHasRank(ProxiedPlayer player, UtariaRank rank) {
		PlayerInfo info = PlayersManager.getPlayerInfo(player);
		return info != null && info.hasRank(rank);
	}
	public static boolean playerHasRankLevel(ProxiedPlayer player, int rankLevel) {
		UtariaRank rank = PlayersManager.getPlayerHighestRank(player);
		return rank != null && rank.getLevel() >= rankLevel;
	}
	public static boolean defaultRankExists() {
		return PlayersManager.getDefaultRank() != null;
	}
	public static boolean rankExists(UtariaRank rank) {
		for (UtariaRank rank2 : ranks)
			if (rank2.getId() == rank.getId())
				return true;
		return false;
	}


	public static boolean unloadPlayerInfo(ProxiedPlayer player) {
		UtariaPlayer uPlayer = PlayersManager.getPlayer(player);
		if (uPlayer == null) return false;

		players.remove(uPlayer);
		return true;
	}

}
