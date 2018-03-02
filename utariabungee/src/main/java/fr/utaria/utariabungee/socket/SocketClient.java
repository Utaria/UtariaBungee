package fr.utaria.utariabungee.socket;


import fr.utaria.utariabungee.socket.packets.ReceivingPacket;
import fr.utaria.utariabungee.socket.packets.SendingPacket;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {

	private Socket socket;

	private DataOutputStream output;

	private DataInputStream input;

	private String host;

	private int port;

	private boolean connected;

	public SocketClient(String host, int port) {
		this.host = host;
		this.port = port;

		this.socket = new Socket();

		this.connected = this.connect();
	}

	public boolean isConnected() {
		return this.connected;
	}

	private boolean connect() {
		try {
			this.socket.connect(new InetSocketAddress(this.host, this.port));

			this.output = new DataOutputStream(this.socket.getOutputStream());
			this.input = new DataInputStream(this.socket.getInputStream());

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean disconnect() {
		if (this.socket == null) return false;

		try {
			this.socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean sendPacket(SendingPacket packet) {
		if (!this.connected) return false;

		try {
			// On traite le packet et on l'envoie
			packet.process();
			packet.serialize();

			this.output.write(packet.getData());
			packet.close();

			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public ReceivingPacket sendPacketWithResponse(SendingPacket packet, Class<? extends ReceivingPacket> responsePacketClass) {
		if (!this.sendPacket(packet)) return null;

		try {
			int length = input.readInt();
			byte[] buffer = new byte[length];

			input.readFully(buffer);
			DataInputStream packetStream = new DataInputStream(new ByteArrayInputStream(buffer));

			// On lit le numéro du packet qui ne nous intéresse pas dans le cadre d'une réponse.
			packetStream.readInt();

			ReceivingPacket rPacket = responsePacketClass.getConstructor(DataInputStream.class).newInstance(packetStream);

			rPacket.deserialize();
			rPacket.process();
			rPacket.close();

			return rPacket;
		} catch (IOException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
