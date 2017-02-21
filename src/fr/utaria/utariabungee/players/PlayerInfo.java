package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class PlayerInfo {

	private UtariaPlayer uPlayer;

	private Integer rankLevel = 0;
	private Double coins      = 0.0;

	private String gradeName;
	private String gradeColor;

	public PlayerInfo(UtariaPlayer utariaPlayer) {
		this.uPlayer = utariaPlayer;

		this.reload();
	}


	public Integer getRankLevel(){
		return this.rankLevel;
	}
	public Double  getCoins(){
		return this.coins;
	}
	public String  getGradeName(){
		return this.gradeName;
	}
	public String  getGradeColor(){
		return "§" + this.gradeColor;
	}
	public String  getGradePrefix(){
		if(getGradeName().equalsIgnoreCase("Membre")) return getGradeColor();
		return getGradeColor()+"["+getGradeName()+"] ";
	}

	private void reload() {
		Database db = UtariaBungee.getDatabase();

		// Récupération du compte depuis la base de données
		DatabaseSet set = db.findFirst("players", DatabaseSet.makeConditions("playername", uPlayer.getPlayerName()));

		// Si le compte existe, on récupère les données
		if( set != null ) {
			this.coins = set.getDouble("coins");


			// Récupération du grade le plus évolué
			List<DatabaseSet> ranksLinksSets = db.find("players_ranks", DatabaseSet.makeConditions(
					"player_id", String.valueOf(set.getInteger("id"))
			));

			DatabaseSet highestLevelSet = null;

			for (DatabaseSet rankLinkSet : ranksLinksSets) {
				DatabaseSet rankSet = db.findFirst("ranks", DatabaseSet.makeConditions(
						"id", String.valueOf(rankLinkSet.getInteger("rank_id"))
				));

				if (highestLevelSet == null || highestLevelSet.getInteger("level") < rankSet.getInteger("level"))
					highestLevelSet = rankSet;
			}

			if (highestLevelSet == null) {
				highestLevelSet = db.findFirst("ranks", DatabaseSet.makeConditions(
						"id", Utils.getConfigValue("default_rank")
				));
			}

			this.rankLevel  = highestLevelSet.getInteger("level");
			this.gradeName  = highestLevelSet.getString("name");
			this.gradeColor = highestLevelSet.getString("color");
		}
		// Sinon créé le compte à partir de valeurs par défaut
		else {
			this.createPlayerProfile();
		}
	}
	private void createPlayerProfile(){
		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

		UtariaBungee.getDatabase().save("players", DatabaseSet.makeFields(
				"playername", uPlayer.getPlayerName(),
				"uuid", uPlayer.getPlayerUniqueId().toString(),
				"first_connection", currentTimestamp,
				"last_connection", currentTimestamp,
				"first_ip", uPlayer.getIP(),
				"rank_id", 2
		), null, true);
	}


	public String toString() {
		return "{PlayerInfo;Player="+uPlayer.getPlayerName()+";RankLevel="+getRankLevel()+";"
				+ "Grade={Name="+getGradeName()+";Color="+getGradeColor()+"}}";
	}



	public static PlayerInfo get(ProxiedPlayer player){
		return UtariaPlayer.get(player).getPlayerInfo();
	}
	public static int        getRankLevelByName(String playername) {
		Database    db  = UtariaBungee.getDatabase();
		DatabaseSet set = db.findFirst("players", DatabaseSet.makeConditions("playername", playername));

		if( set == null )
			return -1;
		else
			return db.findFirst("ranks", DatabaseSet.makeConditions("id", String.valueOf(set.getInteger("rank_id")))).getInteger("level");
	}

}