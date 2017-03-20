package fr.utaria.utariabungee.chat;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class SpecialChannel {

	private List<ProxiedPlayer> players;

	private String name;
	private String format;
	private char   prefix;



	public SpecialChannel(String name, String format, char prefix) {
		this.name   = name;
		this.format = format;
		this.prefix = prefix;

		this.players = new ArrayList<>();
	}


	public List<ProxiedPlayer> getPlayers() { return this.players; }
	public String              getName   () { return this.name;    }
	public String              getFormat () { return this.format;  }
	public char                getPrefix () { return this.prefix;  }

	public boolean             containsPlayer(ProxiedPlayer player) {
		return this.players.contains(player);
	}


	public void addPlayer(ProxiedPlayer player) {
		this.addPlayer(player, false);
	}
	public void addPlayer(ProxiedPlayer player, boolean notifyPlayer) {
		this.players.add(player);

		// On notifie le joueur de son arrivée dans le canal
		if (notifyPlayer) {
			player.sendMessage(new TextComponent(Config.prefix + "§eVous pouvez parler dans le canal §b" + this.name + "§7."));
			player.sendMessage(new TextComponent(Config.prefix + "§7Pour cela, utilisez §6" + this.prefix + "§7 devant vos messages."));
		}
	}
	public void removePlayer(ProxiedPlayer player) {
		this.players.remove(player);
	}



	public boolean playerSendMessage(ProxiedPlayer sender, String message) {
		if (!this.players.contains(sender)) return false;
		String formattedMessage = this.formatMessage(sender, message);

		for (ProxiedPlayer player : this.players)
			player.sendMessage(new TextComponent(formattedMessage));

		UtariaBungee.getInstance().getLogger().info("[Canal " + this.name + "] " + formattedMessage);

		return true;
	}


	private String formatMessage(ProxiedPlayer sender, String message) {
		String finalMessage = this.format;

		finalMessage = finalMessage.replaceAll("%prefix%"  , PlayerInfo.get(sender).getHighestRank().getPrefix().replace(" ", ""));
		finalMessage = finalMessage.replaceAll("%player%"  , sender.getDisplayName());
		finalMessage = finalMessage.replaceAll("%message%" , message.substring(1));

		return finalMessage;
	}

}
