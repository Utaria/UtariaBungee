package fr.utaria.utariabungee.socket.custompackets;

import fr.utaria.utariabungee.socket.packets.ReceivingPacket;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketInRestart extends ReceivingPacket {

	private String hostRestart;

	private int portRestart;

	public PacketInRestart(DataInputStream dis) {
		super(dis);
	}

	public String getHost() {
		return this.hostRestart;
	}

	public int getport() {
		return this.portRestart;
	}

	@Override
	protected void deserialize(DataInputStream dis) throws IOException {
		this.hostRestart = dis.readUTF();
		this.portRestart = dis.readInt();
	}

	@Override
	public void process() {

	}

}
