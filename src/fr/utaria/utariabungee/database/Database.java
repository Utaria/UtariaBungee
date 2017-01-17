package fr.utaria.utariabungee.database;

import fr.utaria.utariabungee.UtariaBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.config.Configuration;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Database {


	private static String host = "localhost";
	private static Integer port = 3306;
	private static String user = "root";
	private static String pass = "";
	private static String DB = "utaria";

	private static BasicDataSource source;
	private static String          lastRequest;
	private static List<String>    tables;

	private boolean debugMessage = false;


	public Database() {
		if( UtariaBungee.getConfiguration() != null ) {
			Configuration config = UtariaBungee.getConfiguration();

			Database.host = config.getString("mysql.host");
			Database.port = config.getInt("mysql.port");
			Database.user = config.getString("mysql.user");
			Database.pass = config.getString("mysql.pass");
			Database.DB   = config.getString("mysql.database");
		}

		this.createPool();
	}


	public static Connection getConnection() {
		try {
			return (Database.source != null) ? Database.source.getConnection() : null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Boolean    isReady(){
		return (source != null);
	}


	public void    createDatabase(String dbName) {
		Statement s;
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + Database.host + ":" + Database.port + "/?user=" + Database.user + "&password=" + Database.pass);
			s = conn.createStatement();
			s.executeUpdate("CREATE DATABASE " + dbName);

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void    emptyTable(String tableName) {
		Statement s;
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + Database.host + ":" + Database.port + "/?user=" + Database.user + "&password=" + Database.pass);
			s = conn.createStatement();

			s.executeUpdate("USE " + Database.DB);
			s.executeUpdate("SET SQL_SAFE_UPDATES=0;");
			s.executeUpdate("truncate " + tableName);
			s.executeUpdate("SET SQL_SAFE_UPDATES=1;");

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Boolean tableExists(String table){
		if(!Database.isReady()) return false;

		if(tables == null) tables = getTables();
		return tables.contains(table);
	}

	public String getMySQLVersion(){
		Connection conn = null;

		try {
			conn = getConnection();

			return (Database.isReady() ? conn.getMetaData().getDatabaseProductVersion() : "0.0");
		} catch (SQLException e) {
			e.printStackTrace();
			return "-1";
		} finally {
			closeConnection(conn);
		}
	}
	public List<String> getTables(){
		Connection conn     = null;
		List<String> tables = new ArrayList<>();

		try{
			conn = getConnection();

			assert conn != null;
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet result     = dbm.getTables(null, null, "%", null);

			while(result.next()){
				tables.add(result.getString(3));
			}
		} catch(Exception e){
			e.printStackTrace();
			return tables;
		} finally {
			closeConnection(conn);
		}

		return tables;
	}


	public List<DatabaseSet> find(String table) {
		return this.find(table, null);
	}

	public List<DatabaseSet> find(String table, Map<String, String> conditions) {
		return this.find(table, conditions, null);
	}

	public List<DatabaseSet> find(String table, Map<String, String> conditions, List<String> orderby) {
		return this.find(table, conditions, orderby, null);
	}

	public List<DatabaseSet> find(String table, Map<String, String> conditions, List<String> orderby, List<String> fields) {
		return find(table, conditions, orderby, fields, null);
	}

	public List<DatabaseSet> find(String table, Map<String, String> conditions, List<String> orderby, List<String> fields, List<Integer> limit) {
		List<DatabaseSet> result = null;
		PreparedStatement sql    = null;
		Connection        conn   = null;

		if (!Database.isReady()) return null;

		String strFields = "*";
		if(fields != null){
			strFields = "";

			for(String field : fields)
				strFields += field + ",";

			strFields = strFields.substring(0, strFields.length() - 1);
		}

		// Format fields & elements
		String req = "SELECT " + strFields + " FROM `" + table + "`";
		ArrayList<String> stringsToExec = new ArrayList<>();
		if (conditions != null) {
			int count = conditions.size();
			int index = 1;

			req += " WHERE ";
			for (String k : conditions.keySet()) {
				String v = conditions.get(k);

				if (index != count)
					req += "`" + k + "` = ? AND ";
				else
					req += "`" + k + "` = ?";

				stringsToExec.add(v);

				index++;
			}
		}

		if (orderby != null) req += " ORDER BY " + orderby.get(0) + " " + orderby.get(1);
		if (limit != null) req += " LIMIT " + limit.get(0) + "," + limit.get(1);

		try {
			lastRequest = req;
			conn = getConnection();
			sql  = conn.prepareStatement(req);

			int i = 1;
			for (String s : stringsToExec) {
				sql.setString(i, s);
				i++;
			}

			result = DatabaseSet.resultSetToDatabaseSet(sql.executeQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(sql);
			closeConnection(conn);
		}

		return result;
	}


	public DatabaseSet findFirst(String table) {
		List<DatabaseSet> r = this.find(table);
		if (r == null) return null;
		else return r.get(0);
	}

	public DatabaseSet findFirst(String table, Map<String, String> conditions) {
		List<DatabaseSet> r = this.find(table, conditions);
		if (r == null || r.size() == 0) return null;
		else                            return r.get(0);
	}

	public DatabaseSet findFirst(String table, Map<String, String> conditions, List<String> orderby) {
		List<DatabaseSet> r = this.find(table, conditions, orderby);
		if (r == null) return null;
		else return r.get(0);
	}

	public DatabaseSet findFirst(String table, Map<String, String> conditions, List<String> orderby, List<String> fields) {
		List<DatabaseSet> r = this.find(table, conditions, orderby, fields);
		if (r == null) return null;
		else return r.get(0);
	}

	public DatabaseSet findFirst(String table, Map<String, String> conditions, List<String> orderby, List<String> fields, List<Integer> limit) {
		List<DatabaseSet> r = this.find(table, conditions, orderby, fields, limit);

		if (r == null || r.size() == 0 ) return null;
		else                             return r.get(0);
	}



	public boolean save(String table, Map<String, Object> fields) {
		return this.save(table, fields, null);
	}

	public boolean save(String table, Map<String, Object> fields, Map<String, String> conditions) {
		return this.save(table, fields, conditions, false);
	}

	public boolean save(String table, Map<String, Object> fields, Map<String, String> conditions, boolean replace) {
		Connection        conn = null;
		PreparedStatement sql  = null;

		// Si la base de données n'est pas prête, on ne fait rien.
		if(!Database.isReady()) return false;

		// Make request string
		String req = "";
		List<Object> objsToExec = new ArrayList<Object>();
		if (conditions == null) { // INSERT
			String exec = "INSERT";
			if (replace) exec = "REPLACE";

			req += exec + " INTO `" + table + "` (";

			int keyCount = fields.keySet().size();
			int i = 1;
			int j = 1;

			// Keys
			for (String key : fields.keySet()) {
				if (i != keyCount)
					req += "`" + key + "`,";
				else
					req += "`" + key + "`)";

				i++;
			}

			req += " VALUES (";
			// Values
			for (String key : fields.keySet()) {
				if (j != keyCount)
					req += "?,";
				else
					req += "?)";

				objsToExec.add(fields.get(key));
				j++;
			}
		} else { // UPDATE
			req += "UPDATE `" + table + "` SET ";

			// Keys & Values
			int keyCount = fields.keySet().size();
			int i = 1;

			for (String key : fields.keySet()) {
				Object o = fields.get(key);

				if (i != keyCount)
					req += "`" + key + "`=?, ";
				else
					req += "`" + key + "`=?";

				objsToExec.add(o);
				i++;
			}

			// Conditions
			int CondsCount = conditions.size();
			int CondsIndex = 1;

			req += " WHERE ";
			for (String k : conditions.keySet()) {
				String v = conditions.get(k);

				if (CondsIndex != CondsCount)
					req += "`" + k + "` = ? AND ";
				else
					req += "`" + k + "` = ?";

				objsToExec.add(v);

				CondsIndex++;
			}
		}

		try {
			lastRequest = req;
			conn = getConnection();
			sql  = conn.prepareStatement(req);

			int i = 1;
			for (Object o : objsToExec) {
				if (o instanceof String)
					sql.setString(i, (String) o);
				else if (o instanceof Integer)
					sql.setInt(i, (Integer) o);
				else if (o instanceof Long)
					sql.setLong(i, (Long) o);
				else if (o instanceof Float)
					sql.setFloat(i, (Float) o);
				else if (o instanceof Double)
					sql.setDouble(i, (Double) o);
				else if (o instanceof Timestamp) {
					sql.setTimestamp(i, (Timestamp) o);
				}else{
					sql.setNull(i, Types.TIMESTAMP);
				}

				i++;
			}

			sql.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeStatement(sql);
			closeConnection(conn);
		}
	}


	public int request(String request) {
		Statement s     = null;
		Connection conn = null;
		int        res  = -1;

		try {
			conn = getConnection();
			s    = conn.createStatement();

			res = s.executeUpdate(request);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(s);
			closeConnection(conn);
		}

		return res;
	}
	public List<DatabaseSet> request(String req, List<String> stringsToExec) {
		PreparedStatement sql    = null;
		Connection        conn   = null;
		List<DatabaseSet> result = null;

		try {
			conn = getConnection();

			lastRequest = req;
			assert conn != null;

			sql = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

			if( stringsToExec != null ) {
				int i = 1;
				for(String s : stringsToExec){
					sql.setString(i, s);
					i++;
				}
			}

			result = DatabaseSet.resultSetToDatabaseSet(sql.executeQuery());
		} catch (SQLException e) { e.printStackTrace(); }
		finally{
			closeStatement(sql);
			closeConnection(conn);
		}

		return result;
	}


	public boolean delete(String table, Map<String, String> conditions) {
		Connection        conn = null;
		PreparedStatement sql  = null;

		// Make request string
		String req = "DELETE FROM " + table;
		List<Object> objsToExec = new ArrayList<Object>();

		// Conditions
		int CondsCount = conditions.size();
		int CondsIndex = 1;

		req += " WHERE ";
		for (String k : conditions.keySet()) {
			String v = conditions.get(k);

			if (CondsIndex != CondsCount)
				req += k + " = ? AND";
			else
				req += k + " = ?";

			objsToExec.add(v);

			CondsIndex++;
		}

		try {
			lastRequest = req;
			conn = getConnection();
			sql  = conn.prepareStatement(req);

			int i = 1;
			for (Object o : objsToExec) {
				if (o instanceof String)
					sql.setString(i, (String) o);
				else if (o instanceof Integer)
					sql.setInt(i, (Integer) o);
				else if (o instanceof Float)
					sql.setFloat(i, (Float) o);
				else if (o instanceof Double)
					sql.setDouble(i, (Double) o);
				else if (o instanceof Timestamp)
					sql.setTimestamp(i, (Timestamp) o);

				i++;
			}

			sql.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeStatement(sql);
			closeConnection(conn);
		}
	}


	public String getLastRequest() {
		return lastRequest;
	}



	private void createPool() {
		try {
			source = new BasicDataSource();

			source.setDriverClassName("com.mysql.jdbc.Driver");
			source.setUrl("jdbc:mysql://" + Database.host + ":" + Database.port + "/" + Database.DB);
			source.setUsername(Database.user);
			source.setPassword(Database.pass);

			source.setInitialSize(2);
			source.setMaxOpenPreparedStatements(5);
			source.setMaxTotal(10);

			source.setValidationQuery("SELECT COUNT(*) FROM servers");
		} catch(Exception e) {
			source = null;
			BungeeCord.getInstance().stop("Le module MySQL est requis pour pouvoir demarrer!");
		}
	}
	private void closeStatement(Statement statement){
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void closeConnection(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
