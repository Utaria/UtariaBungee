package fr.utaria.utariabungee.socket;

import fr.utaria.utariabungee.socket.packets.SendingPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable {

	private int port;

	private Thread              thread;
	private SocketPacketManager packetManager;
	private ServerSocket        server;
	private boolean             running;


	public SocketServer(int port) {
		this.packetManager = new SocketPacketManager();

		this.port = port;

		this.start();
	}


	public void start() {
		this.running = true;

		// Création du processus à part qui va gérer le serveur
		this.thread  = new Thread(this);

		// On démarre le processus
		this.thread.start();
	}
	public void stop() {
		// On éteint proprement le serveur
		this.running = false;

		// On coupe le processus du serveur
		if (this.thread != null)
			this.thread.interrupt();
	}


	public SocketPacketManager getPacketManager() {
		return this.packetManager;
	}

	@Override
	public void run() {
		try {
			this.server = new ServerSocket(this.port);

			while (this.running) {
				Socket socket = this.server.accept();

				new SocketServerClient(this, socket);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (this.server != null)
					this.server.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}


	public void sendPacketTo(SendingPacket packet, SocketServerClient client) {
		this.getPacketManager().sendPacket(packet, client);
	}

}
