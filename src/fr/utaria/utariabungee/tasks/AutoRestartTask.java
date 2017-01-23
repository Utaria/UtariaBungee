package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.managers.TaskManager;
import fr.utaria.utariabungee.UtariaBungee;

public class AutoRestartTask implements Runnable {

    public AutoRestartTask() {
        TaskManager.scheduleSyncRepeatingTask("autoRestartTask", this, 20L, 10 * 20L); // 10 sec
    }

    @Override
    public void run() {
        UtariaBungee.getAutoRestartManager().update();
    }

}
