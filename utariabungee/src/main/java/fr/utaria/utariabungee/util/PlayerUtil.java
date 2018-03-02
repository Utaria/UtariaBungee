package fr.utaria.utariabungee.util;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.util.text.FontInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;


public class PlayerUtil {

	private final static int TCHATBOX_WIDTH = 154;

	private PlayerUtil() {
	}

	public static void sendCenteredMessage(ProxiedPlayer player, String message) {
		if (message == null || message.equals("")) {
			player.sendMessage("");
			return;
		}

		message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : message.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else
				if (previousCode) {
					previousCode = false;
					if (c == 'l' || c == 'L') {
						isBold = true;
						continue;
					} else isBold = false;
				} else {
					FontInfo fI = FontInfo.getFontInfo(c);
					messagePxSize += isBold ? fI.getBoldLength() : fI.getLength();
					messagePxSize++;
				}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = TCHATBOX_WIDTH - halvedMessageSize;
		int spaceLength = FontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb.toString() + message);
	}

	public static void sendHorizontalLine(ProxiedPlayer player) {
		sendHorizontalLine(player, null);
	}

	public static void sendHorizontalLine(ProxiedPlayer player, ChatColor color) {
		if (color == null) color = ChatColor.WHITE;
		sendCenteredMessage(player, color + PlayerUtil._repeatText("§m-", 53));
	}

	public static void sendHorizontalLineWithText(ProxiedPlayer player, String text, ChatColor color) {
		int n = 55;
		if (color == null) color = ChatColor.WHITE;

		n -= text.length() + 4;

		sendCenteredMessage(player, color + PlayerUtil._repeatText("§m-", n / 2) + "§r" + color + "[ " + text + color + " ]" + PlayerUtil._repeatText("§m-", n / 2));
	}

	public static void sendErrorMessage(ProxiedPlayer player, String message) {
		player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + message));
	}

	public static List<ProxiedPlayer> listPlayers(String who) {
		List<ProxiedPlayer> players = new ArrayList<>();

		if (UUtil.stringIsIP(who)) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
				if (UUtil.getPlayerIP(player).equals(who))
					players.add(player);
		} else {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(who);
			if (player != null && player.isConnected())
				players.add(player);
		}

		return players;
	}

	public static boolean isConnected(String playername) {
		return ProxyServer.getInstance().getPlayer(playername) != null;
	}

	public static String getClientVersionOf(ProxiedPlayer player) {
		int version = player.getPendingConnection().getVersion();

		switch (version) {
			case 47:
				return "1.8";
			case 107:
				return "1.9";
			case 108:
				return "1.9.1";
			case 109:
				return "1.9.2";
			case 110:
				return "1.9.4";
			case 210:
				return "1.10";
			case 315:
				return "1.11";
			case 316:
				return "1.11.2";
			case 335:
				return "1.12";
			case 338:
				return "1.12.1";
			case 340:
				return "1.12.2";

			default:
				return "???";
		}
	}

	public static boolean isLocalPlayer(ProxiedPlayer player) {
		return UUtil.getPlayerIP(player).equals("127.0.0.1");
	}

	public static void kick(String who, BaseComponent[] message) {
		for (ProxiedPlayer player : listPlayers(who))
			player.disconnect(message);
	}

	public static void sendMessage(String who, BaseComponent[] message) {
		for (ProxiedPlayer player : listPlayers(who))
			player.sendMessage(message);
	}

	private static String _repeatText(String str, int times) {
		StringBuilder r = new StringBuilder();

		for (int i = 0; i < times; i++)
			r.append(str);

		return r.toString();
	}

}
