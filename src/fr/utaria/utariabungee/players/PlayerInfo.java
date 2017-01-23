package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.Calendar;

public class PlayerInfo {

	private UtariaPlayer uPlayer;

	private Integer rankLevel = 0;
	private Integer coins     = 0;

	private String gradeName;
	private String gradeColor;

	public PlayerInfo(UtariaPlayer utariaPlayer){
		this.uPlayer = utariaPlayer;

		this.reload();
	}


	public Integer getRankLevel(){
		return this.rankLevel;
	}
	public Integer getCoins(){
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

	private void reload(){
		Database db = UtariaBungee.getDatabase();

		// Récupération du compte depuis la base de données
		DatabaseSet set = db.findFirst("players", DatabaseSet.makeConditions("playername", uPlayer.getPlayerName()));

		// Si le compte existe, on récupère les données
		if( set != null ) {
			this.coins = set.getInteger("coins");

			int grade_id = set.getInteger("grade_id");
			DatabaseSet gradeSet = db.findFirst("grades", DatabaseSet.makeConditions("id", grade_id+""));

			this.rankLevel  = gradeSet.getInteger("rank_level");
			this.gradeName  = gradeSet.getString("name");
			this.gradeColor = gradeSet.getString("color");
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
				"grade_id", 2
		), null, true);
	}


	public String toString(){
		return "{PlayerInfo;Player="+uPlayer.getPlayerName()+";RankLevel="+getRankLevel()+";"
				+ "Grade={Name="+getGradeName()+";Color="+getGradeColor()+"}}";
	}



	public static PlayerInfo get(ProxiedPlayer player){
		return UtariaPlayer.get(player).getPlayerInfo();
	}
	public static int        getRankLevelByName(String playername) {
		Database    db  = UtariaBungee.getDatabase();
		DatabaseSet set = db.findFirst("players", DatabaseSet.makeConditions("playername", playername));

		if( set == null ) return -1;
		else {
			DatabaseSet gradeSet = db.findFirst("grades", DatabaseSet.makeConditions("id", String.valueOf(set.getInteger("grade_id"))));

			return gradeSet.getInteger("rank_level");
		}
	}

}