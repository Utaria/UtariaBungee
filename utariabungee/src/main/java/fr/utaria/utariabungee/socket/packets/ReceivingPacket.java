package fr.utaria.utariabungee.socket.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ReceivingPacket extends Packet {

	public ReceivingPacket(DataInputStream dis) {
		super(dis);
	}

	@Override
	protected void serialize(DataOutputStream dos) throws IOException {
	}

	@Override
	protected abstract void deserialize(DataInputStream dis) throws IOException;

	@Override
	public abstract void process();

}
