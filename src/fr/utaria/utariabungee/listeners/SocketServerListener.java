package fr.utaria.utariabungee.listeners;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.socket.custompackets.PacketInRestart;
import fr.utaria.utariabungee.socket.listeners.SocketPacketListener;
import fr.utaria.utariabungee.socket.packets.ReceivingPacket;
import fr.utaria.utariabungee.socket.packets.SendingPacket;
import net.md_5.bungee.BungeeCord;

public class SocketServerListener implements SocketPacketListener {

	@Override
	public void onPacketReceived(ReceivingPacket packet) {
		if (packet instanceof PacketInRestart && UtariaBungee.getAutoRestartManager().restartIsInProgress()) {
			/* Comme pour le moment, seul le serveur survie est lié,
			   dès qu'on reçoit le message de restart du serveur, on
			   peut redémarrer le proxy.

			   /!\ A modifier à l'avenir ! /!\                                */


			// On met à jour les plugins avant le restart
			UtariaBungee.getAutoRestartManager().updatePlugins();

			// On arrête le serveur x)
			BungeeCord.getInstance().getLogger().info("Tous les serveurs viennent d'être redémarrés. Redémarrage du proxy...");
			BungeeCord.getInstance().stop("Redémarrage automatique");
		}
	}

	@Override
	public void onPacketSended(SendingPacket packet) {

	}

}
