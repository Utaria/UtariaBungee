package fr.utaria.utariabungee.socket.listeners;

import fr.utaria.utariabungee.socket.packets.ReceivingPacket;
import fr.utaria.utariabungee.socket.packets.SendingPacket;

public interface SocketPacketListener {

	void onPacketReceived(ReceivingPacket packet);

	void onPacketSended(SendingPacket packet);

}
