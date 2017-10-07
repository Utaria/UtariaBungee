package fr.utaria.utariabungee.network;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.network.listener.NetworkListener;
import fr.utaria.utariabungee.tasks.UpdateStaffTask;
import fr.utaria.utariabungee.util.TaskUtil;
import fr.utaria.utariadatabase.result.DatabaseSet;
import fr.utaria.utariadatabase.util.ConfigTableAccessor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyManager extends AbstractManager implements Runnable {

	private MaintenanceMode maintenanceMode;

	private List<String> staff;
	private List<UtariaServer> servers;


	public ProxyManager() {
		super(UtariaBungee.getInstance(), "global");

		this.maintenanceMode = new MaintenanceMode();

		this.staff = new ArrayList<>();
		this.servers = new ArrayList<>();

		this.registerListener(new NetworkListener(this));

		// On charge les serveurs manuellement depuis la configuration, si on le souhaite.
		if (Config.manualServers) this.reloadServersFromConfig();
		else {
			// Au démarrage au supprime tous les serveurs de la configuration
			this.clear();

			// On mets à jour la liste des serveurs depuis la BDD
			this.reloadServersFromBDD();
		}

		// On lance la tâche de rafraichissement automatique ...
		TaskUtil.scheduleSyncRepeatingTask("refreshProxyTask", this, 5L, 20L * 30);

		// ... et de mise à jour de la liste du staff.
		new UpdateStaffTask(this);
	}

	@Override
	public void initialize() {

	}


	public int    getMaxPlayers() {
		return ConfigTableAccessor.getInteger("maxplayers");
	}
	public String getMotd() {
		return (this.maintenanceMode.isActive()) ? this.maintenanceMode.getMotd() : ConfigTableAccessor.getString("motd");
	}

	/* ------------------------------- */
	/*  Gestion des serveurs du proxy  */
	/* ------------------------------- */
	public UtariaServer getServerById(int id) {
		for(UtariaServer server : this.servers)
			if(server.getId() == id)
				return server;
		return null;
	}
	public UtariaServer getServerWithName(String name) {
		for (UtariaServer server : this.servers)
			if ( server.getName().equals(name) )
				return server;
		return null;
	}

	public void addServer(UtariaServer server) {
		servers.add(server);
		ProxyServer.getInstance().getServers().put(server.getName(), server.getServerInfo());
	}
	public void removeServer(UtariaServer server) {
		servers.remove(server);
		ProxyServer.getInstance().getServers().remove(server.getName());
	}

	public UtariaServer getDefaultServer() {
		for(UtariaServer server : this.servers)
			if(server.isDefault())
				return server;

		return null;
	}


	/* ------------------ */
	/*  Mode maintenance  */
	/* ------------------ */
	public MaintenanceMode getMaintenanceMode() {
		return this.maintenanceMode;
	}
	public List<String> getStaffList() {
		return this.staff;
	}
	public boolean      isStaffmember(String playername) {
		return this.staff.contains(playername);
	}


	@Override
	public void run() {
		// On recharge les informations du serveur ...
		this.reloadServerInfos();

		// ... mais aussi les serveurs qui y sont attachés.
		if (!Config.manualServers)
			this.reloadServersFromBDD();
	}

	private void clear() {
		this.servers.clear();

		// On supprime tous les serveurs, sauf celui par défaut.
		ProxyServer.getInstance().getServers().entrySet().removeIf(entry -> !entry.getKey().equals("default"));
	}


	private void reloadServerInfos() {

	}

	private void reloadServersFromConfig() {
		int i = 0;

		for (Map.Entry<String, ServerInfo> serverInfo : ProxyServer.getInstance().getServers().entrySet()) {
			// Création d'une instance de l'objet depuis la configuration du proxy
			UtariaServer uServer = new UtariaServer(
					i, serverInfo.getKey(),
					serverInfo.getValue().getAddress().getHostName(),
					serverInfo.getValue().getAddress().getPort(),
					0);

			// Par défaut le port du serveur de socket est 100 au-dessus.
			uServer.setSocketServerPort(serverInfo.getValue().getAddress().getPort() + 1000);

			this.addServer(uServer);
			i++;
		}
	}
	private void reloadServersFromBDD() {
		final ProxyManager self = this;

		this.servers.clear();

		TaskUtil.scheduleSyncDelayedTask("reload-network", () -> {
			List<DatabaseSet> results    = this.getDB().select().from("servers").findAll();
			String defaultServer = ConfigTableAccessor.getString("default_server");

			for(DatabaseSet result : results) {
				UtariaServer uServer = new UtariaServer(
						result.getInteger("id"),
						result.getString("name"),
						result.getString("ip"),
						result.getInteger("port"),
						result.getInteger("rank_level_needed")
				);

				uServer.setSocketServerPort(result.getInteger("socket_server_port"));
				uServer.setDefault(uServer.getName().equals(defaultServer));

				self.addServer(uServer);
			}

		});
	}

}
