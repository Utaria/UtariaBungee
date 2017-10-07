package fr.utaria.utariabungee;

import net.md_5.bungee.api.plugin.Plugin;

public class UtariaPlugin extends Plugin {

	public final <T> T getInstance(Class<T> clazz) {
		T inst = Managers.getInstance(clazz);

		if (inst == null)
			System.out.println("WARNING: " + clazz + " instance is null!");

		return inst;
	}

}