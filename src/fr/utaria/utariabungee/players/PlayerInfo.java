package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.util.UUtil;
import fr.utaria.utariadatabase.database.DatabaseAccessor;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PlayerInfo extends DatabaseAccessor {

	private UtariaPlayer     uPlayer;
	private List<UtariaRank> ranks;

	private int    id;
	private double coins;
	private String password;


	PlayerInfo(UtariaPlayer utariaPlayer) {
		super("global");

		this.uPlayer = utariaPlayer;
		this.id      = -1;
		this.coins   = 0;

		this.ranks   = new ArrayList<>();

		this.reload();
	}


	public int    getId             () { return this.id;       }
	public double getCoins          () { return this.coins;    }
	public String getCryptedPassword() { return this.password; }


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
	private void reload() {
		DatabaseSet infoSet = this.getDB().select("id", "coins")
				                          .from(PlayersManager.PLAYERS_TABLE).where("playername = ?")
				                          .attributes(uPlayer.getPlayerName()).find();

		if (infoSet != null) {
			this.id       = infoSet.getInteger("id");
			this.coins    = infoSet.getDouble("coins");
			this.password = infoSet.getString("password");

			// On récupère les différent grades du joueur ...
			List<DatabaseSet> ranksSets = this.getDB().select().from(PlayersManager.PLAYERS_RANKS_TABLE)
					                                  .where("player_id = ?").attributes(this.id).findAll();

			// ... puis on les enregistre en mémoire.
			for (DatabaseSet rankSet : ranksSets) {
				int        rankId = rankSet.getInteger("rank_id");
				UtariaRank rank   = PlayersManager.getRankById(rankId);

				// Le grade doit exister pour pouvoir être appliqué.
				if (rank != null) this.ranks.add(rank);
			}

			// On oublie pas de renseigner en base qu'il vient de se re-connecter !
			this.getDB().update(PlayersManager.PLAYERS_TABLE).fields("last_ip", "last_connection")
					    .values(UUtil.getPlayerIP(this.uPlayer.getPlayer()), new Timestamp(System.currentTimeMillis()))
					    .where("id = ?").attributes(this.id)
					    .execute();
		} else
			this.createPlayerProfile();
	}
	private void createPlayerProfile() {
		Timestamp now = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

		this.getDB().update("players").fields("playername", "uuid", "first_connection", "last_connection", "first_ip")
				    .values(uPlayer.getPlayerName(), uPlayer.getPlayerUniqueId().toString(), now, now, uPlayer.getIP()).execute();
	}


	@Override
	public  String toString() {
		return "{PlayerInfo #" + this.hashCode() + " (playername=" + uPlayer.getPlayerName() + " ranks=" + Arrays.toString(this.ranks.toArray()) + ")}";
	}



	public static PlayerInfo get(ProxiedPlayer player){
		return UtariaPlayer.get(player).getPlayerInfo();
	}

}