package fr.utaria.utariabungee.managers;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class PMManager {

	//           S      R
	private Map<UUID, UUID>     lastSenderFor = new HashMap<>();
	private List<ProxiedPlayer> playersSpying = new ArrayList<>();


	public void sendPrivateMessageTo(ProxiedPlayer sender, ProxiedPlayer receiver, String message) {
		// On génère le texte du message
		BaseComponent[] text = TextComponent.fromLegacyText("§b" + sender.getName() + "§8 > §a" + receiver.getName() + "§7 : " + message);

		// On envoie le message à l'éxpéditeur et au destinataire
		sender.sendMessage(text);
		receiver.sendMessage(text);

		// On envoie le message aux joueurs en mode SPY
		BaseComponent[] spyText = null;
		if (this.playersSpying.size() > 0)
			spyText = TextComponent.fromLegacyText("§8[Spy] §b" + sender.getName() + "§8 > §a" + receiver.getName() + "§7 : " + message);

		for (ProxiedPlayer player : this.playersSpying)
			if (player.isConnected() && player != sender && player != receiver)
				player.sendMessage(spyText);

		// On affiche le message dans la console pour avoir un retour sur les messages privés
		BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("[PM] " + sender.getName() + " > " + receiver.getName() + " : " + message));

		// On met en cache le dernier joueur auquel l'expéditeur a parlé
		this.lastSenderFor.put(receiver.getUniqueId(), sender.getUniqueId());
	}


	public UUID getLastSenderFor(ProxiedPlayer player) {
		return this.lastSenderFor.get(player.getUniqueId());
	}
	public void clearFor(ProxiedPlayer player) {
		this.lastSenderFor.remove(player.getUniqueId());
		this.playersSpying.remove(player);
	}


	public boolean togglePlayerSpyMode(ProxiedPlayer player) {
		if (this.playersSpying.contains(player)) {
			this.playersSpying.remove(player);

			BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("[PM] " + player.getDisplayName() + " vient de desactiver le mode SPY."));
			return false;
		} else {
			this.playersSpying.add(player);

			BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("[PM] " + player.getDisplayName() + " vient d'activer le mode SPY."));
			return true;
		}
	}

}
