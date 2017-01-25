package fr.utaria.utariabungee.socket;


import fr.utaria.utariabungee.socket.listeners.SocketPacketListener;
import fr.utaria.utariabungee.socket.packets.ReceivingPacket;
import fr.utaria.utariabungee.socket.packets.SendingPacket;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocketPacketManager {

	private Map<Integer, Class<? extends ReceivingPacket>> receivingPackets;
	private Map<Integer, Class<? extends SendingPacket>> sendingPackets;

	private List<SocketPacketListener> listeners;


	SocketPacketManager() {
		this.receivingPackets = new HashMap<>();
		this.sendingPackets   = new HashMap<>();

		this.listeners = new ArrayList<>();
	}


	public void register(int id, Class<? extends ReceivingPacket> receivingPacket) {
		receivingPackets.put(id, receivingPacket);
	}
	public void register(int id, Class<? extends ReceivingPacket> receivingPacketClass, Class<? extends SendingPacket> sendingPacketClass) {
		receivingPackets.put(id, receivingPacketClass);
		sendingPackets.put(id, sendingPacketClass);
	}
	public void unregister(int id) {
		receivingPackets.remove(id);
		sendingPackets.remove(id);
	}


	public void registerListener(SocketPacketListener listener) {
		this.listeners.add(listener);
	}
	public void unregisterListener(SocketPacketListener listener) {
		this.listeners.remove(listener);
	}


	void processPacket(SocketServerClient client, DataInputStream packetStream) throws IOException {
		int packetID = packetStream.readInt();

		if (!receivingPackets.containsKey(packetID))
			throw new IllegalArgumentException("Packet #" + packetID + " non enregistré.");


		// On traite le paquet reçu
		Class<? extends ReceivingPacket> receivingPacketClass = receivingPackets.get(packetID);

		try {
			ReceivingPacket packet = receivingPacketClass.getConstructor(DataInputStream.class).newInstance(packetStream);
			packet.deserialize();

			// On envoie le paquet reçu aux listeners
			for (SocketPacketListener listener : this.listeners)
				listener.onPacketReceived(packet);


			// On traite et on ferme le packet
			packet.process();
			packet.close();


			// On regarde s'il y a un paquet de réponse
			if (sendingPackets.containsKey(packetID)) {
				Class<? extends SendingPacket> sendingPacketClass = sendingPackets.get(packetID);

				Constructor<? extends SendingPacket> constructor = sendingPacketClass.getConstructor(ReceivingPacket.class);

				// Si le constructeur dans le packet d'envoi prenant en paramètre un packet de réception existe,
				// cela signifie que cela peut être un packet de réponse à celui reçu.
				if (constructor != null) {
					SendingPacket packet2 = constructor.newInstance(packet);

					this.sendPacket(packet2, client);
				}
			}
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	void sendPacket(SendingPacket packet, SocketServerClient client) {
		// On envoie le paquet a envoyer aux listeners
		for (SocketPacketListener listener : this.listeners)
			listener.onPacketSended(packet);

		// On traite le packet et on l'envoie
		packet.process();
		packet.serialize();

		client.send(packet.getData());
		packet.close();
	}

}
