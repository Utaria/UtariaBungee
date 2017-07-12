package fr.utaria.utariabungee.utils;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.conf.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {

    private static List<ConfigRequest> lastConfigReq;


    private Utils() {}

    static {
        lastConfigReq = new ArrayList<>();
    }



	public static String dateToString(Timestamp date) {
		return new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss").format(date);
	}

	public static int timeToInt(String time) { // Format = XhYY
        String[] p = time.split("h");

        return Integer.parseInt(p[0]) * 3600 + Integer.parseInt(p[1]) * 60;
    }
	
	public static String  hideIP(String ip) {
		return ip.substring(0, ip.length()-2) + "??";
	}
	public static boolean stringIsIP(String str) {
		return Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
				.matcher(str)
				.find();
    }

	
	public static String ucfirst(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1, word.length());
	}
	public static String formatMessageColors(String message) {
        String words[] = message.split(" ");
        String color   = null;

		StringBuilder messageBuilder = new StringBuilder();

		for (String word : words) {
            if (word.substring(0, 1).equals("&"))
                color = word.substring(1, 2);
            else if (color != null)
                word = '&' + color + word;

            messageBuilder.append(word).append(" ");
        }

		message = messageBuilder.toString();
		message = ChatColor.translateAlternateColorCodes('&', message.substring(0, message.length() - 1));

        return message;
    }

	public static String getUrlSource(String url) throws IOException {
        URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();
    }


    public static boolean serverIsOnline(String servername) {
        if(BungeeCord.getInstance().getServerInfo(servername) == null) return false;
        InetSocketAddress address = BungeeCord.getInstance().getServerInfo(servername).getAddress();

        try{
            Socket socket = new Socket(address.getHostName(), address.getPort());

            socket.close();
            return true;
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
    public static void    kickAllPlayers(String reason) {
        for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
            player.disconnect(new TextComponent(reason));
    }
    public static void    setProxyOnlineMode(boolean onlineMode) {
        try {
            Configuration localConfiguration = BungeeCord.getInstance().config;
            Field localField;

            localField = localConfiguration.getClass().getDeclaredField("onlineMode");
            localField.setAccessible(true);
            localField.set(localConfiguration, onlineMode);
        } catch (Exception localException) {
            UtariaBungee.getInstance().getLogger().warning("[UtariaBungee] Erreur interne lors du changement du mode \"online\" en : " + onlineMode + ".");
        }
    }
    public static String  getPlayerIP(ProxiedPlayer player) {
        return player.getAddress().getAddress().getHostAddress();
    }

    public static void log(String message) {
	    BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(message));
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static String getConfigValue(String key){
    	// On supprime les requêtes expirées ...
		lastConfigReq.removeIf((request) -> System.currentTimeMillis() > request.getTime() + Config.CONFIG_CACHE_EXPIRATION * 1000);

    	// ... puis on regarde si la valeur n'est pas déjà en cache ...
		for (ConfigRequest lastReq : lastConfigReq)
			if (lastReq.getKey().equals(key))
				return lastReq.getValue();

		// ... sinon on va la chercher dans la BDD et on la met en cache.
        Database          db   = UtariaBungee.getDatabase();
        List<DatabaseSet> sets = db.find("config", DatabaseSet.makeConditions("key", key));

        String value = (sets.size() == 0) ? null : sets.get(0).getString("value");

		if (value != null) lastConfigReq.add(new ConfigRequest(key, value));
		return value;
	}
    public static void updateConfigValue(String key, String value){
        Database db = UtariaBungee.getDatabase();

        List<DatabaseSet> sets = db.find("config", DatabaseSet.makeConditions(
                "key", key
        ));

        if(sets.size() == 0){
            db.save("config", DatabaseSet.makeFields("key", key, "value", value));
        }else{
            db.save("config", DatabaseSet.makeFields("value", value), DatabaseSet.makeConditions("key", key));
        }
    }

    public static boolean intArrayContains(int[] arr, int value) {
        for (int v : arr)
            if (v == value)
                return true;

        return false;
    }


    private static class ConfigRequest {
    	private String key;
    	private String value;
    	private long   time;

    	public ConfigRequest(String key, String value) {
			this.key   = key;
			this.value = value;
			this.time  = System.currentTimeMillis();
		}

		public String getKey  () { return this.key;   }
		public String getValue() { return this.value; }
		public long   getTime () { return this.time;  }

		public String toString() { return "{ConfigRequest #" + this.hashCode() + " (key=" + key + " value=" + value + " time=" + time + ")}"; }
	}

}
