package fr.utaria.utariabungee;

import fr.utaria.utariabungee.antibot.AntiBotManager;
import fr.utaria.utariabungee.chat.PMManager;
import fr.utaria.utariabungee.chat.SpecialChannel;
import fr.utaria.utariabungee.chat.SpecialChannels;
import fr.utaria.utariabungee.commands.CommandManager;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.network.AutoRestartManager;
import fr.utaria.utariabungee.network.ProxyManager;
import fr.utaria.utariabungee.players.PlayersManager;
import fr.utaria.utariabungee.socket.SocketServer;
import fr.utaria.utariabungee.socket.custompackets.PacketInRestart;
import fr.utaria.utariabungee.socket.listeners.SocketServerListener;
import fr.utaria.utariabungee.tasks.AutoMessageTask;
import fr.utaria.utariabungee.tasks.TabHeadersTask;
import fr.utaria.utariadatabase.database.DatabaseManager;
import fr.utaria.utariadatabase.util.ConfigTableAccessor;

public class UtariaBungee extends UtariaPlugin {

	private static UtariaBungee instance;

	private static SpecialChannel staffChannel;

	private SocketServer socketServer;

	public void onEnable() {
		instance = this;

		// On enregistre en mémoire les bases de données que l'on va utiliser
		DatabaseManager.registerDatabase("global");
		DatabaseManager.registerDatabase("moderation");
		DatabaseManager.applyMigrations("moderation");

		// Gestionnaires
		new AntiBotManager();
		new ModerationManager();

		new ProxyManager();

		new AutoRestartManager();
		new PMManager();

		new PlayersManager();
		new AutoMessageTask();
		new TabHeadersTask();

		new CommandManager();


		// Démarrage du serveur de socket
		Integer port = ConfigTableAccessor.getInteger("socket_server_port_bungee");
		if (port == null) port = Config.socketServerPort;

		this.socketServer = new SocketServer(port);

		this.socketServer.getPacketManager().registerListener(new SocketServerListener());
		this.socketServer.getPacketManager().register(2, PacketInRestart.class);


		// On enregistre les canaux de discussions spéciaux
		staffChannel = new SpecialChannel("Staff", "§d§lS >r %prefix% %player%: §f%message%", '!');
		SpecialChannels.registerSpecialChannel(staffChannel);
	}

	public void onDisable() {
		// On éteint le serveur de socket
		if (this.socketServer != null)
			this.socketServer.stop();
	}

	public static UtariaBungee getInstance() {
		return instance;
	}

	public static SpecialChannel getStaffChannel() {
		return staffChannel;
	}

}
