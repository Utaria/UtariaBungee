package fr.utaria.utariabungee.antivpn;

import io.netty.util.internal.ConcurrentSet;

import java.io.*;

public class VPNExceptionList {

	private final static String FILE = "vpn-exceptions.cache";

	private ConcurrentSet<String> ipException;

	VPNExceptionList() {
		this.ipException = new ConcurrentSet<>();
	}

	boolean isInExceptionList(String ip) {
		return this.ipException.contains(ip);
	}

	public void addException(String ip) {
		this.ipException.add(ip);
	}

	public void removeException(String ip) {
		this.ipException.remove(ip);
	}

	void load() throws IOException, ClassNotFoundException {
		File file = new File("./" + FILE);

		if (file.exists() && file.isFile()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			this.ipException = (ConcurrentSet<String>) ois.readObject();
			ois.close();
		}
	}

	void save() throws IOException {
		File file = new File("./" + FILE);
		if (!file.exists() && !file.createNewFile()) return;

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this.ipException);
		oos.close();
	}

}
