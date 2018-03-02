package fr.utaria.utariabungee.antibot;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.util.UUtil;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class AntiBotProtectionTask implements Runnable {

	private AntiBotManager antiBotManager;

	AntiBotProtectionTask(AntiBotManager antiBotManager, int delaySecs) {
		this.antiBotManager = antiBotManager;

		ProxyServer.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, delaySecs, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		// On regarde si la protection est bien activée
		if (this.antiBotManager.isProtectionActivated()) {
			this.antiBotManager.setProtectionActivated(false);

			// On désactive la protection online-mode qui consiste à autoriser
			// seulement les joueurs Premium
			UUtil.setProxyOnlineMode(false);

			// On affiche l'info dans la console
			UtariaBungee.getInstance().getLogger().log(Level.WARNING, "Anti-Bot desactivé.");

			// On supprime le cache de la protection
			this.antiBotManager.clearProtectionTask();
		}
	}

}
