package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.managers.AntiBotManager;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class AntiBotProtectionTask implements Runnable {


	public AntiBotProtectionTask(int delaySecs) {
		BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, delaySecs, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		AntiBotManager manager = UtariaBungee.getAntiBotManager();

		// On regarde si la protection est bien activée
		if (manager.isProtectionActivated()) {
			manager.setProtectionActivated(false);

			// On désactive la protection online-mode qui consiste à autoriser
			// seulement les joueurs Premium
			Utils.setProxyOnlineMode(false);

			// On affiche l'info dans la console
			UtariaBungee.getInstance().getLogger().log(Level.WARNING, "Anti-Bot desactive.");

			// On supprime le cache de la protection
			manager.clearProtectionTask();
		}
	}

}
