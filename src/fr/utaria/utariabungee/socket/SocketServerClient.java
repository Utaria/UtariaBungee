package fr.utaria.utariabungee.socket;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketServerClient implements Runnable {

	private Socket       socket;
	private SocketServer server;

	private DataInputStream  dis;
	private DataOutputStream dos;


	public SocketServerClient(SocketServer server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;

		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());

		new Thread(this).start();
	}


	public InetAddress getAddress() {
		return this.socket.getInetAddress();
	}



	public void send(byte[] data) {
		try {
			this.dos.write(data);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public void run() {
		try {
			int    length = this.dis.readInt();
			byte[] buffer = new byte[length];

			this.dis.readFully(buffer);
			DataInputStream packetStream = new DataInputStream(new ByteArrayInputStream(buffer));

			this.server.getPacketManager().processPacket(this, packetStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
