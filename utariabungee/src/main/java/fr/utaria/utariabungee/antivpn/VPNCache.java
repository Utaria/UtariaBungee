package fr.utaria.utariabungee.antivpn;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

class VPNCache {

	private final static String FILE = "vpn.cache";

	private ConcurrentHashMap<String, Boolean> ipCache;

	VPNCache() {
		this.ipCache = new ConcurrentHashMap<>();
	}

	boolean isCached(String ip) {
		return this.ipCache.containsKey(ip);
	}

	boolean isBlacklisted(String ip) {
		return this.isCached(ip) && this.ipCache.get(ip);
	}

	void addIpData(String ip, boolean black) {
		this.ipCache.put(ip, black);
	}

	void load() throws IOException, ClassNotFoundException {
		File file = new File("./" + FILE);

		if (file.exists() && file.isFile()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			this.ipCache = (ConcurrentHashMap<String, Boolean>) ois.readObject();
			ois.close();
		}
	}

	void save() throws IOException {
		File file = new File("./" + FILE);
		if (!file.exists() && !file.createNewFile()) return;

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this.ipCache);
		oos.close();
	}

}
