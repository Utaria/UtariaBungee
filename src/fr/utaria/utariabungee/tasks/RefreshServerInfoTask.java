package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.managers.TaskManager;
import fr.utaria.utariabungee.UtariaBungee;

public class RefreshServerInfoTask implements Runnable {

    public RefreshServerInfoTask() {
        TaskManager.scheduleSyncRepeatingTask("refreshServerInfo", this, 20L * 3, 20L * 30); // 30sec
    }

    @Override
    public void run(){
        UtariaBungee.getServerInfoManager().refresh();
    }

}
