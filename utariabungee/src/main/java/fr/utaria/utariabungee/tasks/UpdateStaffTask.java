package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.network.ProxyManager;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.ProxyServer;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateStaffTask implements Runnable {

	private ProxyManager manager;

	public UpdateStaffTask(ProxyManager manager) {
		this.manager = manager;

		ProxyServer.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 2, 30, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		// On lance la tâche qu'en mode maintenance.
		if (!this.manager.getMaintenanceMode().isActive()) return;

		List<DatabaseSet> sets = this.manager.getDB().select("playername").from("players")
				.join("players_perms", "players_perms.player_id", "players.id")
				.where("perm = ? OR perm = ?").attributes(Config.MAINTENANCE_ALLOWED_PERM, "*")
				.findAll();

		this.manager.getStaffList().clear();

		for (DatabaseSet set : sets)
			this.manager.getStaffList().add(set.getString("playername"));
	}

}
