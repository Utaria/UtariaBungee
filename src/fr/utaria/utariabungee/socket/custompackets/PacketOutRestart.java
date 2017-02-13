package fr.utaria.utariabungee.socket.custompackets;

import fr.utaria.utariabungee.socket.packets.SendingPacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class PacketOutRestart extends SendingPacket {

	public PacketOutRestart() {
		super(1);
	}

	@Override
	protected void serialize(DataOutputStream dos) throws IOException {

	}

	@Override
	public void process() {

	}
}
