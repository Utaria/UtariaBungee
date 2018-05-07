package fr.utaria.utariabungee.antivpn;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.util.UUtil;
import net.md_5.bungee.api.ProxyServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AntiVPNManager extends AbstractManager {

	private final static String[] BLACKLIST_URLS = new String[] {
			"http://myip.ms/files/blacklist/csf/latest_blacklist.txt",
			"https://www.badips.com/get/list/proxy/0",
			// "https://www.badips.com/get/list/bruteforce/0" -- Contient plus 310000 Ips, ralenti le réseau (?)
	};

	private final static String[] BLACKAPI_URLS = new String[] {
			"http://www.shroomery.org/ythan/proxycheck.php?ip=%s;Y",
			"http://www.stopforumspam.com/api?ip=%s;yes",
			"http://v1.nastyhosts.com/%s;\"suggestion\":\"deny\""
	};

	private VPNCache cache;

	private VPNExceptionList exceptionList;

	public AntiVPNManager() {
		super(UtariaBungee.getInstance());
	}

	@Override
	public void initialize() {
		this.cache = new VPNCache();
		this.exceptionList = new VPNExceptionList();

		try {
			this.cache.load();
			this.exceptionList.load();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		ProxyServer.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), this::loadBlackLists);
	}

	public void save() {
		try {
			this.cache.save();
			this.exceptionList.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public VPNExceptionList getExceptionList() {
		return this.exceptionList;
	}

	public boolean isBlackIp(String ip) {
		// Quelques IPs passent les tests ...
		if (this.isLocalIp(ip)) return false;

		// Les IPs qui sont dans la liste des exceptions passent le test!
		if (this.exceptionList.isInExceptionList(ip)) return false;

		// IP déjà enregistrée dans le cache ...
		if (this.cache.isCached(ip))
			return this.cache.isBlacklisted(ip);

		// ... sinon on lance les recherches !
		for (String apiUrl : BLACKAPI_URLS)
			if (this.checkIpInBlackApi(ip, apiUrl))
				return true;

		return false;
	}

	private boolean isLocalIp(String ip) {
		return ip.equals("localhost") || ip.equals("127.0.0.1");
	}

	private void loadBlackLists() {
		int nb = 0;
		long begin = System.currentTimeMillis();

		for (String blacklistUrl : BLACKLIST_URLS) {
			try {
				String data = this.grabUrl(blacklistUrl, 5000);

				for (String line : data.split("\\n"))
					if (UUtil.stringIsIP(line)) {
						this.cache.addIpData(line, true);
						nb++;
					}

			} catch (IOException ex) {
				UtariaBungee.getInstance().getLogger().warning("Impossible de charger la liste noire '" + blacklistUrl + "' !");
			}
		}

		UtariaBungee.getInstance().getLogger().info(nb + " IPs blacklistées chargées en " + (System.currentTimeMillis() - begin) + "ms !");
	}

	private boolean checkIpInBlackApi(String ip, String blackApiUrl) {
		String url    = blackApiUrl.split(";")[0];
		String needed = blackApiUrl.split(";")[1];

		try {
			String data = this.grabUrl(String.format(url, ip), 2000);
			boolean black = data.toLowerCase().contains(needed.toLowerCase());

			// Ajout de l'info en cache ...
			this.cache.addIpData(ip, black);

			return black;
		} catch (IOException e) {
			return false;
		}
	}

	private String grabUrl(String url, int timeout) throws IOException {
		StringBuilder response = new StringBuilder();

		URL website = new URL(url);
		URLConnection connection = website.openConnection();

		connection.setConnectTimeout(timeout);
		connection.setRequestProperty("User-Agent", "UtariaBungee v" + UtariaBungee.getInstance().getDescription().getVersion());

		String data;

		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			while ((data = in.readLine()) != null) {
				response.append(data).append('\n');
			}

			in.close();
		}

		return response.toString();
	}

}
