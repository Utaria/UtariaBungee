package fr.utaria.utariabungee.chat;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.UtariaBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class PMManager extends AbstractManager {

	//           S      R
	private Map<UUID, UUID> lastSenderFor = new HashMap<>();
	private List<ProxiedPlayer> playersSpying = new ArrayList<>();

	public PMManager() {
		super(UtariaBungee.getInstance());
	}

	@Override
	public void initialize() {

	}

	public void sendPrivateMessageTo(ProxiedPlayer sender, ProxiedPlayer receiver, String message) {
		// On envoie le message à l'expéditeur et au destinataire
		receiver.sendMessage(TextComponent.fromLegacyText("§d" + sender.getName() + "§7 à §bvous§7: " + message));
		sender.sendMessage(TextComponent.fromLegacyText("§bVous§7 à §d" + receiver.getName() + "§7: " + message));

		// On envoie le message aux joueurs en mode SPY
		BaseComponent[] spyText = null;
		if (this.playersSpying.size() > 0)
			spyText = TextComponent.fromLegacyText("§a§l+§d" + sender.getName() + "§8 à §d" + receiver.getName() + "§7: " + message);

		for (ProxiedPlayer player : this.playersSpying)
			if (player.isConnected() && player != sender && player != receiver)
				player.sendMessage(spyText);

		// On affiche le message dans la console pour avoir un retour sur les messages privés
		ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText("[PM] " + sender.getName() + " > " + receiver.getName() + " : " + message));

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

			ProxyServer.getInstance().getConsole().sendMessage(new TextComponent("[PM] " + player.getDisplayName() + " vient de desactiver le mode SPY."));
			return false;
		} else {
			this.playersSpying.add(player);

			ProxyServer.getInstance().getConsole().sendMessage(new TextComponent("[PM] " + player.getDisplayName() + " vient d'activer le mode SPY."));
			return true;
		}
	}

}
