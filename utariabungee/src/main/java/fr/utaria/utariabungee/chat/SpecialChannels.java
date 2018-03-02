package fr.utaria.utariabungee.chat;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

public class SpecialChannels implements Listener {

	private static List<SpecialChannel> channels;

	static {
		channels = new ArrayList<>();
	}

	public static void registerSpecialChannel(SpecialChannel channel) {
		channels.add(channel);
	}

	public static void unregisterSpecialChannel(SpecialChannel channel) {
		channels.remove(channel);
	}

	public static void removePlayerFromAllChannels(ProxiedPlayer player) {
		for (SpecialChannel channel : channels)
			if (channel.containsPlayer(player))
				channel.removePlayer(player);
	}

	private static boolean tryToSendInChannel(ProxiedPlayer player, String message) {
		boolean sended = false;
		char prefix = (message.length() > 0) ? message.charAt(0) : '\0';

		for (SpecialChannel channel : channels)
			if (channel.getPrefix() == prefix) {
				sended = channel.playerSendMessage(player, message);
				if (sended) break;
			}

		return sended;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerSendMessage(ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer)) return;
		if (e.isCancelled()) return;

		e.setCancelled(SpecialChannels.tryToSendInChannel((ProxiedPlayer) e.getSender(), e.getMessage()));
	}

}
