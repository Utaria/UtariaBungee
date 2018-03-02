package fr.utaria.utariabungee.moderation;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.moderation.sanctions.Mute;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ModerationListener implements Listener {

	private ModerationManager manager;

	ModerationListener(ModerationManager manager) {
		this.manager = manager;
	}

	// La partie vérification de bannissement se fait dans le
	// NetworkListener car on ne peut pas mettre en attente deux fois le même event.

	@EventHandler
	public void onPlayerPostLogin(PostLoginEvent event) {
		// On récupère les données de mute sur le joueur et on les mets en cache.
		UtariaBungee.getInstance().getProxy().getScheduler().runAsync(
				UtariaBungee.getInstance(), () ->
						this.manager.loadActiveMuteFor(event.getPlayer())
		);
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		// On supprime son mute de la mémoire
		this.manager.removeCacheMuteOf(event.getPlayer());
	}

	@EventHandler
	public void onPlayerChat(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer) || ((ProxiedPlayer) event.getSender()).getServer() == null)
			return;
		if (event.isCommand()) return;

		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		Mute.MuteState muteState = this.manager.getMuteStateFor(player);

		// Si le joueur est sur le serveur de connexion,
		// il faut lui laisser taper son son mot de passe.
		if (player.getServer().getInfo().getName().equals("connexion")) return;

		switch (muteState) {
			case YES:
				player.sendMessage(TextComponent.fromLegacyText(this.manager.getActiveMuteFor(player).getMessage()));
				event.setCancelled(true);
				break;
			case LOADING:
				player.sendMessage(TextComponent.fromLegacyText("§8(§4§l✖§8) §cNous chargeons vos données, veuillez patienter."));
				event.setCancelled(true);
				break;
		}
	}

}
