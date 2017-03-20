package fr.utaria.utariabungee.managers;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.players.UtariaPlayer;
import fr.utaria.utariabungee.players.UtariaRank;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayersManager {

	public final static String PLAYERS_TABLE         = "players";
	public final static String PLAYERS_RANKS_TABLE   = "players_ranks";
	public final static String RANKS_TABLE           = "ranks";

	private static List<UtariaRank> ranks;

	static {
		ranks = new ArrayList<>();
	}


	public static void reloadRanks() {
		BungeeCord.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), () -> {
			List<DatabaseSet> sets = UtariaBungee.getDatabase().find(RANKS_TABLE);

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
			String defaultRankStr = Utils.getConfigValue("default_rank");

			if (defaultRankStr != null && Utils.isInteger(defaultRankStr)) {
				int defaultRankId = Integer.parseInt(defaultRankStr);

				for (UtariaRank rank : ranks)
					if (rank.getId() == defaultRankId)
						rank.setDefault(true);
					else
						rank.setDefault(false);
			}
		});
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

}
