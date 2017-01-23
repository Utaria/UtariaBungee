package fr.utaria.utariabungee.managers;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fr.utaria.utariabungee.UtariaBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class TaskManager {

	private static HashMap<String, ScheduledTask> tasks = new HashMap<>();


	public static void scheduleSyncDelayedTask(String name, Runnable runnable){
		ScheduledTask task = BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), runnable, 0L, 0L, TimeUnit.MILLISECONDS);
		tasks.put(name, task);
	}

	public static void scheduleSyncDelayedTask(String name, Runnable runnable, long arg2){
		ScheduledTask task = BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), runnable, arg2 / 20, 0L, TimeUnit.SECONDS);
		tasks.put(name, task);
	}

	public static void scheduleSyncRepeatingTask(String name, Runnable runnable, long arg2, long arg3){
		ScheduledTask task = BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), runnable, arg2 / 20, arg3 / 20, TimeUnit.SECONDS);
		tasks.put(name, task);
	}



	public static void cancelTaskByName(String name){
		if(tasks.containsKey(name)){
			BungeeCord.getInstance().getScheduler().cancel(tasks.get(name));
			tasks.remove(name);
		}
	}

	public static void cancelAllTasks(){
		for(String name : tasks.keySet()){
			cancelTaskByName(name);
		}

		tasks.clear();
	}

}