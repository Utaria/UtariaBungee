package fr.utaria.utariabungee.socket.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class SendingPacket extends Packet {

	private int id;

	public SendingPacket(int id) {
		super(id);
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	@Override
	protected void deserialize(DataInputStream dis) throws IOException {
	}

	@Override
	protected abstract void serialize(DataOutputStream dos) throws IOException;

	@Override
	public abstract void process();

}
