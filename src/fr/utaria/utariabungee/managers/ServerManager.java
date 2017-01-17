package fr.utaria.utariabungee.managers;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.tasks.RefreshServersTask;
import net.md_5.bungee.api.ProxyServer;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {

	private List<UtariaServer> servers = new ArrayList<>();


	public ServerManager() {
		// Désactivation du manager si besoin
		if (Config.manualServers) return;

		// Au démarrage au supprime tous les serveurs de la configuration
		this.clear();

		// On mets à jour la liste des serveurs depuis la BDD
		this.reloadServersFromBDD();

		// On lance la tâche de raffraichissement automatique
		new RefreshServersTask();
	}


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

	public String       getRawProxyServers() {
		String r = "";

		for(String serverName : getProxy().getServers().keySet())
			r += serverName + ", ";

		if(r.length()  > 2) r = r.substring(0, r.length() - 2);
		if(r.length() == 0) r = "[]";

		return r;
	}


	public void addServer(UtariaServer server) {
		servers.add(server);
		getProxy().getServers().put(server.getName(), server.getServerInfo());
	}
	public void removeServer(UtariaServer server) {
		servers.remove(server);
		getProxy().getServers().remove(server.getName());
	}

	public UtariaServer getDefaultServer() {
		for(UtariaServer server : this.servers)
			if(server.isDefault())
				return server;
		return null;
	}


	public void clear() {
		this.servers.clear();

		// On supprime tous les serveurs, sauf celui par défaut.
		for(String name : getProxy().getServers().keySet())
			if( !name.equals("default") )
				getProxy().getServers().remove(name);
	}


	private ProxyServer getProxy() {
		return UtariaBungee.getInstance().getProxy();
	}
	public  void        reloadServersFromBDD() {
		final ServerManager self = this;

		this.servers.clear();

		TaskManager.scheduleSyncDelayedTask("reload-servers", () -> {
			Database          db         = UtariaBungee.getDatabase();

			List<DatabaseSet> results    = db.find("servers");
			DatabaseSet       defaultRes = db.findFirst("config", DatabaseSet.makeConditions("key", "default_server"));

			for(DatabaseSet result : results) {
				UtariaServer uServer = new UtariaServer(
						result.getInteger("id"),
						result.getString("name"),
						result.getString("ip"),
						result.getInteger("port"),
						result.getInteger("rank_level_needed")
				);

				if( defaultRes != null && defaultRes.getString("value").equals(uServer.getName()))
					uServer.setDefault(true);

				self.addServer(uServer);
			}

		});
	}

}
