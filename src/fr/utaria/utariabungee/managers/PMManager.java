package fr.utaria.utariabungee.managers;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PMManager {

	//           S      R
	private Map<UUID, UUID> lastSenderFor = new HashMap<>();


	public void sendPrivateMessageTo(ProxiedPlayer sender, ProxiedPlayer receiver, String message) {
		sender.sendMessage(new TextComponent("§b" + sender.getName() + "§8 > §a" + receiver.getName() + "§7 : " + message));
		receiver.sendMessage(new TextComponent("§b" + sender.getName() + "§8 > §a" + receiver.getName() + "§7 : " + message));

		this.lastSenderFor.put(receiver.getUniqueId(), sender.getUniqueId());
	}


	public UUID getLastSenderFor(ProxiedPlayer player) {
		return this.lastSenderFor.get(player.getUniqueId());
	}
	public void clearFor(ProxiedPlayer player) {
		this.lastSenderFor.remove(player.getUniqueId());
	}

}
