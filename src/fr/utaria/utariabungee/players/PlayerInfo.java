package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerInfo {

	private UtariaPlayer     uPlayer;
	private List<UtariaRank> ranks;

	private int    id;
	private double coins;


	public PlayerInfo(UtariaPlayer utariaPlayer) {
		this.uPlayer = utariaPlayer;
		this.id      = -1;
		this.coins   = 0;

		this.ranks   = new ArrayList<>();

		this.reload();
	}


	public int    getId   () { return this.id;    }
	public double getCoins() { return this.coins; }


	public List<UtariaRank> getRanks           () {
		List<UtariaRank> ranks = this.ranks;
		if (ranks.size() == 0) ranks.add(PlayersManager.getDefaultRank());
		return ranks;
	}
	public UtariaRank       getHighestRank     () {
		UtariaRank highest = null;

		// On recherche le grade le plus élevé (en level)
		for (UtariaRank rank : this.ranks)
			if (highest == null || rank.getLevel() > highest.getLevel())
				highest = rank;

		// Si le joueur n'a pas de grade, il a forcément le grade par défaut
		if (highest == null) highest = PlayersManager.getDefaultRank();

		return highest;
	}
	public int              getHighestRankLevel() {
		UtariaRank rank = this.getHighestRank();
		return (rank == null) ? -1 : rank.getLevel();
	}
	public boolean          hasRank(UtariaRank rank) {
		return this.ranks.contains(rank);
	}



	/*    Mise à jour des informations de puis la BDD    */
	private void   reload() {
		DatabaseSet set = UtariaBungee.getDatabase().findFirst(
				PlayersManager.PLAYERS_TABLE,
				DatabaseSet.makeConditions("playername", uPlayer.getPlayerName())
		);

		if (set != null) {
			this.id    = set.getInteger("id");
			this.coins = set.getDouble("coins");

			// On récupère les différent grades du joueur ...
			List<DatabaseSet> ranksSets = UtariaBungee.getDatabase().find(
					PlayersManager.PLAYERS_RANKS_TABLE,
					DatabaseSet.makeConditions("player_id", String.valueOf(this.id))
			);

			// ... puis on les enregistre en mémoire.
			for (DatabaseSet rankSet : ranksSets)
				this.ranks.add(PlayersManager.getRankById(rankSet.getInteger("rank_id")));
		}
	}


	@Override
	public  String toString() {
		return "{PlayerInfo #" + this.hashCode() + " (playername=" + uPlayer.getPlayerName() + " ranks=" + Arrays.toString(this.ranks.toArray()) + ")}";
	}



	public static PlayerInfo get(ProxiedPlayer player){
		return UtariaPlayer.get(player).getPlayerInfo();
	}

}