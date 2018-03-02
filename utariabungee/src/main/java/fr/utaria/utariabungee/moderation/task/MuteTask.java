package fr.utaria.utariabungee.moderation.task;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.moderation.sanctions.Mute;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MuteTask implements Runnable {

	private ModerationManager manager;

	public MuteTask(ModerationManager manager) {
		this.manager = manager;

		ProxyServer.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 0, 5, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		// Suppression des mutes plus valides dans la mémoire.
		for (Map.Entry<ProxiedPlayer, Mute> muteEntry : this.manager.getMutedPlayers().entrySet()) {
			ProxiedPlayer player = muteEntry.getKey();
			Mute mute = muteEntry.getValue();

			// Fin de la sanction
			if (mute != null && !mute.isValid()) {
				if (player.isConnected())
					player.sendMessage(TextComponent.fromLegacyText(mute.getExpirationMessage()));

				this.manager.getMutedPlayers().put(player, null);
			}
		}
	}

}
