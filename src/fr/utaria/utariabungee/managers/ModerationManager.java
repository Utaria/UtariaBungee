package fr.utaria.utariabungee.managers;

import java.sql.Timestamp;
import java.util.List;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;

public class ModerationManager {

	public DatabaseSet getPlayerBanInformations(String playername){
		Database db = UtariaBungee.getDatabase();
		
		List<DatabaseSet> sets = db.find("bungee_bans", DatabaseSet.makeConditions(
			"player", playername
		), DatabaseSet.makeOrderBy("ban_end", "DESC"));
		
		DatabaseSet r = null;
		for(DatabaseSet set : sets){
			if(set.getTimestamp("unban_date") != null) continue;
			
			if(r == null) r = set;
			if(set.getTimestamp("ban_end") == null) r = set;
		}
		return r;
	}
	public DatabaseSet getPlayerMuteInformations(String playername){
		Database db = UtariaBungee.getDatabase();
		
		List<DatabaseSet> sets = db.find("bungee_mutes", DatabaseSet.makeConditions(
			"player", playername
		), DatabaseSet.makeOrderBy("mute_end", "DESC"));
		
		DatabaseSet r = null;
		for(DatabaseSet set : sets){
			if(set.getTimestamp("unmute_date") != null) continue;
			
			if(r == null) r = set;
			if(set.getTimestamp("mute_end") == null) r = set;
		}
		return r;
	}

	public DatabaseSet getIpBanInformations(String ip){
		Database db = UtariaBungee.getDatabase();
		
		List<DatabaseSet> sets = db.find("bungee_bans", DatabaseSet.makeConditions(
			"ip", ip
		), DatabaseSet.makeOrderBy("ban_end", "DESC"));
		
		DatabaseSet r = null;
		for(DatabaseSet set : sets){
			if(set.getTimestamp("unban_date") != null) continue;
			
			if(r == null) r = set;
			if(set.getTimestamp("ban_end") == null) r = set;
		}
		return r;
	}
	public DatabaseSet getIpMuteInformations(String ip){
		Database db = UtariaBungee.getDatabase();
		
		List<DatabaseSet> sets = db.find("bungee_mutes", DatabaseSet.makeConditions(
			"ip", ip
		), DatabaseSet.makeOrderBy("mute_end", "DESC"));
		
		DatabaseSet r = null;
		for(DatabaseSet set : sets){
			if(set.getTimestamp("unmute_date") != null) continue;
			
			if(r == null) r = set;
			if(set.getTimestamp("mute_end") == null) r = set;
		}
		return r;
	}

	public boolean playernameIsTempBanned(String playername){
		Database db = UtariaBungee.getDatabase();
		
		List<DatabaseSet> sets = db.find("bungee_bans", DatabaseSet.makeConditions(
			"player", playername
		));
		
		for(DatabaseSet set : sets){
			Timestamp stamp = set.getTimestamp("ban_end");
			
			if(stamp == null) continue;
			if(set.getTimestamp("unban_date") != null) continue;
			
			if(stamp.getTime() > System.currentTimeMillis())
				return true;
		}
		
		return false;
	}
	public boolean playernameIsTempMuted(String playername){
		Database db = UtariaBungee.getDatabase();

		List<DatabaseSet> sets = db.find("bungee_mutes", DatabaseSet.makeConditions(
				"player", playername
		));

		for(DatabaseSet set : sets){
			Timestamp stamp = set.getTimestamp("mute_end");

			if(stamp == null) continue;
			if(set.getTimestamp("unmute_date") != null) continue;

			if(stamp.getTime() > System.currentTimeMillis())
				return true;
		}

		return false;
	}
	public boolean playernameIsBanned(String playername){
		Database db = UtariaBungee.getDatabase();

		List<DatabaseSet> sets = db.find("bungee_bans", DatabaseSet.makeConditions(
				"player", playername
		));

		for(DatabaseSet set : sets){
			if(set.getTimestamp("unban_date") != null) continue;

			if(set.getTimestamp("ban_end") == null)
				return true;
		}

		return false;
	}

	public boolean ipIsTempBanned(String ip){
		Database db = UtariaBungee.getDatabase();

		List<DatabaseSet> sets = db.find("bungee_bans", DatabaseSet.makeConditions(
			"ip", ip
		));

		for(DatabaseSet set : sets){
			Timestamp stamp = set.getTimestamp("ban_end");

			if(stamp == null) continue;
			if(set.getTimestamp("unban_date") != null) continue;

			if(stamp.getTime() > System.currentTimeMillis())
				return true;
		}

		return false;
	}
	public boolean ipIsTempMuted(String ip){
		Database db = UtariaBungee.getDatabase();

		List<DatabaseSet> sets = db.find("bungee_mutes", DatabaseSet.makeConditions(
			"ip", ip
		));

		for(DatabaseSet set : sets){
			Timestamp stamp = set.getTimestamp("mute_end");

			if(stamp == null) continue;
			if(set.getTimestamp("unmute_date") != null) continue;

			if(stamp.getTime() > System.currentTimeMillis())
				return true;
		}

		return false;
	}
	public boolean ipIsBanned(String ip){
		Database db = UtariaBungee.getDatabase();

		List<DatabaseSet> sets = db.find("bungee_bans", DatabaseSet.makeConditions(
				"ip", ip
		));

		for(DatabaseSet set : sets){
			if(set.getTimestamp("unban_date") != null) continue;

			if(set.getTimestamp("ban_end") == null)
				return true;
		}

		return false;
	}

}
