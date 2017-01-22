package fr.utaria.utariabungee;

import fr.utaria.utariabungee.database.Database;
import fr.utaria.utariabungee.listeners.PlayerChatListener;
import fr.utaria.utariabungee.listeners.PlayerJoinListener;
import fr.utaria.utariabungee.listeners.PlayerPingListener;
import fr.utaria.utariabungee.listeners.TabCompletionListener;
import fr.utaria.utariabungee.managers.*;
import fr.utaria.utariabungee.players.UtariaPlayer;
import fr.utaria.utariabungee.socket.SocketServer;
import fr.utaria.utariabungee.commands.*;
import fr.utaria.utariabungee.tasks.AutoMessageTask;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class UtariaBungee extends Plugin{

	private static UtariaBungee            instance;
	private static ArrayList<UtariaPlayer> utariaPlayers = new ArrayList<>();

	private static ModerationManager moderationManager;
	private static ServerManager     serverManager;

    private static ServerInfoManager  serverInfoManager;
    private static AntiBotManager     antiBotManager;
	private static AutoRestartManager autoRestartManager;
	private static PMManager          pmManager;
	
	private SocketServer socketServer;
	private Database     database;



	public void onEnable(){
		instance = this;


		// Configuration
		loadConfiguration();

		// Base de données
		database = new Database();


		// Gestionnaires
		moderationManager  = new ModerationManager();
		serverManager      = new ServerManager();

        serverInfoManager  = new ServerInfoManager();
		antiBotManager     = new AntiBotManager();
		autoRestartManager = new AutoRestartManager();
		pmManager          = new PMManager();


		new AutoMessageTask();


		// Démarrage du serveur de socket
		this.socketServer = new SocketServer();

		
		// On enregistre les commandes
		PluginManager pm = getProxy().getPluginManager();

		pm.registerCommand(this, new BanCommand());
		pm.registerCommand(this, new KickCommand());
		pm.registerCommand(this, new TempBanCommand());
		pm.registerCommand(this, new TempMuteCommand());
		pm.registerCommand(this, new UnbanCommand());
		pm.registerCommand(this, new UnmuteCommand());
		pm.registerCommand(this, new LookupCommand());

		pm.registerCommand(this, new MessageCommand());
		pm.registerCommand(this, new ResponseCommand());
		pm.registerCommand(this, new SpyCommand());

		pm.registerCommand(this, new StaffCommand());

		pm.registerCommand(this, new UtariaCommand());
		pm.registerCommand(this, new UptimeCommand());
		pm.registerCommand(this, new ForceRestartCommand());

		pm.registerCommand(this, new ServerCommand());
		pm.registerCommand(this, new ServersCommand());


		// On enregistre les écouteurs d'évènements
		pm.registerListener(this, new PlayerJoinListener());
		pm.registerListener(this, new PlayerChatListener());
		pm.registerListener(this, new PlayerPingListener());

		pm.registerListener(this, new TabCompletionListener());
	}

	public void onDisable(){
		// Close socket server
		if(this.socketServer != null){
			this.socketServer.stop();
			while(!this.socketServer.getServerInstance().isClosed()){}
		}

	}



	private void loadConfiguration() {
		if ( !getDataFolder().exists() )
			getDataFolder().mkdir();

		File file = new File(getDataFolder(), "config.yml");


		if ( !file.exists() ) {
			try (InputStream in = getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static UtariaBungee       getInstance(){
		return instance;
	}
	public static ModerationManager  getModerationManager(){
		return moderationManager;
	}
	public static ServerManager      getServerManager() {
		return serverManager;
	}
    public static ServerInfoManager  getServerInfoManager(){
        return serverInfoManager;
    }
    public static AntiBotManager     getAntiBotManager() {
    	return antiBotManager;
	}
	public static AutoRestartManager getAutoRestartManager() {
		return autoRestartManager;
	}
	public static PMManager          getPMManager() {
    	return pmManager;
	}

	public static UtariaPlayer       getPlayer(ProxiedPlayer player) {
		for (UtariaPlayer utariaPlayer : utariaPlayers) {
			if (utariaPlayer == null || utariaPlayer.getPlayer() == null) continue;

			if (utariaPlayer.getPlayer().getUniqueId().equals(player.getUniqueId()))
				return utariaPlayer;
		}

		UtariaPlayer utariaPlayer = new UtariaPlayer(player);
		utariaPlayers.add(utariaPlayer);
		return utariaPlayer;
	}
	public static Database           getDatabase() {
    	return UtariaBungee.getInstance().database;
	}

	public static void removePlayer(ProxiedPlayer pp) {
    	UtariaPlayer uP = UtariaPlayer.get(pp);

    	if( utariaPlayers.contains(uP) )
    		utariaPlayers.remove(uP);
	}

	public static Configuration getConfiguration() {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(UtariaBungee.getInstance().getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void          log(String message){
		getInstance().getProxy().getLogger().info(message);
	}
}
