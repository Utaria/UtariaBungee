package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.managers.TaskManager;

public class RefreshServersTask implements Runnable {

	public RefreshServersTask() {
		TaskManager.scheduleSyncRepeatingTask("refreshServersTask", this, 0, 20L * 30); // 30sec
	}


	@Override
	public void run() {
		UtariaBungee.getServerManager().reloadServersFromBDD();
	}

}
