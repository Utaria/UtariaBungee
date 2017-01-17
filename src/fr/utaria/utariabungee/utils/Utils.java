package fr.utaria.utariabungee.utils;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class Utils {

	public static String dateToString(Timestamp date) {
		return new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss").format(date);
	}

	public static int timeToInt(String time) { // Format = XhYY
        String[] p = time.split("h");

        return Integer.parseInt(p[0]) * 3600 + Integer.parseInt(p[1]) * 60;
    }
	
	public static String hideIP(String ip) {
		return ip.substring(0, ip.length()-2) + "??";
	}

	
	public static String ucfirst(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1, word.length());
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
    public static String  getPlayerIP(ProxiedPlayer player) {
        return player.getAddress().getAddress().getHostAddress();
    }

    public static void log(String message) {
	    BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(message));
    }



    public static String getConfigValue(String key){
        Database db = UtariaBungee.getDatabase();

        List<DatabaseSet> sets = db.find("config", DatabaseSet.makeConditions(
                "key", key
        ));

        if(sets.size() == 0) return null;
        else return sets.get(0).getString("value");
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
}
