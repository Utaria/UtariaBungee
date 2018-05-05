package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.util.UUtil;
import fr.utaria.utariadatabase.database.DatabaseAccessor;
import fr.utaria.utariadatabase.database.DatabaseManager;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class PlayerInfo extends DatabaseAccessor {

	private UtariaPlayer uPlayer;

	private int id;

	private double coins;

	private String password;

	PlayerInfo(UtariaPlayer utariaPlayer) {
		super("global");

		this.uPlayer = utariaPlayer;
		this.id = -1;
		this.coins = 0;

		this.reload();
	}

	public int getId() {
		return this.id;
	}

	public double getCoins() {
		return this.coins;
	}

	public String getCryptedPassword() {
		return this.password;
	}

	/*    Mise à jour des informations de puis la BDD    */
	private void reload() {
		DatabaseSet infoSet = this.getDB().select("id", "coins", "password")
				.from(PlayersManager.PLAYERS_TABLE).where("playername = ?")
				.attributes(uPlayer.getPlayerName()).find();

		if (infoSet != null) {
			this.id = infoSet.getInteger("id");
			this.coins = infoSet.getDouble("coins");
			this.password = infoSet.getString("password");

			// On charge les permissions du joueur en mémoire
			List<DatabaseSet> permSets = DatabaseManager.getDB("global")
					.select("perm", "value", "expiration").from("players_perms")
					.where("player_id = ?", "expiration IS NULL OR expiration >= NOW()")
					.attributes(this.id).findAll();

			if (permSets != null)
				for (DatabaseSet set : permSets)
					this.uPlayer.addPerm(set.getString("perm"), set.getInteger("value"), set.getTimestamp("expiration"));


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
	public String toString() {
		return "{PlayerInfo #" + this.hashCode() + " (playername=" + uPlayer.getPlayerName() + ")}";
	}

	public static PlayerInfo get(ProxiedPlayer player) {
		return UtariaPlayer.get(player).getPlayerInfo();
	}

}