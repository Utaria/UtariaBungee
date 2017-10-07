package fr.utaria.utariabungee;

import fr.utaria.utariadatabase.database.DatabaseAccessor;
import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractManager extends DatabaseAccessor implements Listener {

	protected UtariaPlugin plugin;


	public AbstractManager(UtariaPlugin plugin, Listener... listeners) {
		this(plugin, null, listeners);
	}

	public AbstractManager(UtariaPlugin plugin, String databaseName, Listener ... listeners) {
		super(databaseName);

		this.plugin = plugin;

		// On enregistre les écouteurs d'évènements liés au gestionnaire
		List<Listener> listenerList = new ArrayList<>();

		Collections.addAll(listenerList, listeners);
		listenerList.add(this);

		for (Listener listener : listenerList)
			this.registerListener(listener);


		// Affichage dans la console au chargement
		if (Config.DEBUG_MODE) {
			String full = this.getClass().toString();
			String s;

			try {
				full = full.substring(full.indexOf("fr.utaria.") + "fr.utaria.".length());
				String[] data = full.split("\\.");
				s = data[0] + ":" + data[data.length - 1];

				System.out.println("Gestionnaire " + s + " chargé !");
			} catch (Exception e) {
				System.out.println("Error parsing AbstractManager display name for " + this.getClass());
			}
		}


		// On lance l'initialisation du gestionnaire
		this.initialize();

		// On enregistre le gestionnaire auprès du plugin
		try {
			Managers.registerManager(this.getClass(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected void registerListener(Listener listener) {
		this.plugin.getProxy().getPluginManager().registerListener(this.plugin, listener);
	}


	public abstract void initialize();

}