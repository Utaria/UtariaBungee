package fr.utaria.utariabungee.socket.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

abstract class Packet {

	private DataInputStream       input;
	private DataOutputStream      output;
	private ByteArrayOutputStream baos;


	// Paquet d'envoi
	public Packet(int id) {
		this.baos   = new ByteArrayOutputStream();
		this.output = new DataOutputStream(this.baos);

		try {
			this.output.writeInt(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Paquet de réception
	public Packet(DataInputStream dis) {
		this.input = dis;
	}


	public byte[] getData() {
		try {
			ByteArrayOutputStream finalArray = new ByteArrayOutputStream();
			DataOutputStream finalOutput = new DataOutputStream(finalArray);

			finalOutput.writeInt(this.baos.size());
			finalOutput.write(this.baos.toByteArray());
			finalOutput.close();

			byte[] data = finalArray.toByteArray();
			finalArray.close();
			return data;
		} catch(IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public void serialize() {
		try {
			this.serialize(this.output);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	public void deserialize() {
		try {
			this.deserialize(this.input);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean close() {
		try {
			if (this.output != null)
				this.output.close();
			if (this.input != null)
				this.input.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}


	protected abstract void serialize(DataOutputStream dos) throws IOException;
	protected abstract void deserialize(DataInputStream dis) throws IOException;

	public abstract void process();

}
