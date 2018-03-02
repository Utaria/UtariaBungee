package fr.utaria.utariabungee.util;

import fr.utaria.utariabungee.UtariaBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class UUtil {

	private UUtil() {
	}

	public static String dateToString(Timestamp date) {
		return new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss").format(date);
	}

	public static int timeToInt(String time) { // Format = XhYY
		String[] p = time.split("h");

		return Integer.parseInt(p[0]) * 3600 + Integer.parseInt(p[1]) * 60;
	}

	public static String hideIP(String ip) {
		return ip.substring(0, ip.length() - 2) + "??";
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
		String color = null;

		StringBuilder messageBuilder = new StringBuilder();

		for (String word : words) {
			if (word.substring(0, 1).equals("&"))
				color = word.substring(1, 2);
			else
				if (color != null)
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
		if (ProxyServer.getInstance().getServerInfo(servername) == null) return false;
		InetSocketAddress address = ProxyServer.getInstance().getServerInfo(servername).getAddress();

		try {
			Socket socket = new Socket(address.getHostName(), address.getPort());

			socket.close();
			return true;
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static void kickAllPlayers(String reason) {
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
			player.disconnect(new TextComponent(reason));
	}

	public static void setProxyOnlineMode(boolean onlineMode) {
		try {
			ProxyServer server = ProxyServer.getInstance();

			Configuration conf;
			Field localField;

			conf = (Configuration) server.getClass().getField("config").get(server);

			localField = conf.getClass().getDeclaredField("onlineMode");
			localField.setAccessible(true);
			localField.set(conf, onlineMode);
		} catch (Exception localException) {
			UtariaBungee.getInstance().getLogger().warning("[UtariaBungee] Erreur interne lors du changement du mode \"online\" en : " + onlineMode + ".");
		}
	}

	public static String getPlayerIP(ProxiedPlayer player) {
		return player.getAddress().getAddress().getHostAddress();
	}

	public static String getConnectionIP(Connection connection) {
		return connection.getAddress().getAddress().getHostAddress();
	}

	public static void log(String message) {
		ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(message));
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean intArrayContains(int[] arr, int value) {
		for (int v : arr)
			if (v == value)
				return true;

		return false;
	}

}
