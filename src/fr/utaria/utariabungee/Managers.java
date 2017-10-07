package fr.utaria.utariabungee;

import java.util.HashMap;

public class Managers {

	private static HashMap<Class<? extends AbstractManager>, AbstractManager> instances = new HashMap<>();


	static void registerManager(Class<? extends AbstractManager> clazz, AbstractManager instance) throws Exception {
		if (instances.containsKey(clazz))
			throw new Exception("Gestionnaire " + clazz + " dupliqué ! " + instance);

		instances.put(clazz, instance);
	}

	@SuppressWarnings({"unchecked"})
	protected static <T> T getInstance(Class<T> clazz) {
		AbstractManager instance = instances.get(clazz);

		if (instance == null)
			return null;
		if (clazz.isInstance(instance))
			return (T) instance;

		return null;
	}

}