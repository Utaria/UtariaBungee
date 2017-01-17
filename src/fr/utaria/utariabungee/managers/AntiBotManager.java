package fr.utaria.utariabungee.managers;

import fr.utaria.utariabungee.UtariaBungee;
import net.md_5.bungee.BungeeCord;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AntiBotManager {

	private static final int AUTHORIZED_PLAYER_IN_PERIOD = 10;
	private static final int AUTH_PERIOD_SEC             =  5;

	private final Map<String, String> BLACKLIST_SITES = new HashMap<String, String>(){{
		put("http://www.stopforumspam.com/api?ip=", "yes");
		put("http://www.shroomery.org/ythan/proxycheck.php?ip=", "Y");
	}};

	private List<String> ipBlackList   = new ArrayList<>();
	private List<Long>   lastAuthTimes = new ArrayList<>();

	public AntiBotManager() {
		final AntiBotManager self = this;

		BungeeCord.getInstance().getScheduler().runAsync(UtariaBungee.getInstance(), self::_loadBlackList);
	}

	public boolean ipIsBot(String ip) {
		if (!this.strIsIp(ip)) return false;
		if (this.ipBlackList.contains(ip)) return true;

		if (this.isProxy(ip)) {
			this.ipBlackList.add(ip);
			return true;
		}

		return false;
	}


	/*     Seconde protection, activation si il y a beaucoup de connexion    */
	public boolean passSecondProtection() {
		// Nouvelle connexion tout de suite (en appelant la méthode)
		this.lastAuthTimes.add(System.currentTimeMillis());

		// Mise à jour de la liste des derniers temps de connexion
		int  nbPlayers = 0;
		long now       = System.currentTimeMillis();
		Iterator<Long> lastAuthTimesIterator = this.lastAuthTimes.iterator();

		while (lastAuthTimesIterator.hasNext()) {
			long time = lastAuthTimesIterator.next();

			if (now - time > AUTH_PERIOD_SEC * 1000) {
				lastAuthTimesIterator.remove();
				continue;
			}

			nbPlayers++;
		}

		return nbPlayers <= AUTHORIZED_PLAYER_IN_PERIOD;
	}


	private boolean isProxy(String ip) {
		// On exclu quelques IPs dont on est sûr
		if (ip.equals("127.0.0.1") || ip.equals("localhost") || ip.matches("192\\.168\\.[01]\\.[0-9]{1,3}"))
			return false;

		// On parcoure tous les sites un par un pour savoir si l'IP est un proxy
		for (String site : this.BLACKLIST_SITES.keySet()) {
			Scanner sc = null;

			try {
				String res = "";
				// On récupère les infos sur l'IP depuis le site
				sc = new Scanner(new URL(site + ip).openStream());

				// On monte la chaîne de caractères résultat dans laquelle sera stockée tout le contenu de la page
				while (sc.hasNextLine())
					res += sc.nextLine();

				// On regarde ensuite si le mot de succès est présent dans le retrour de la page
				String[] args = this.BLACKLIST_SITES.get(site).split(",");
				for (String arg : args)
					if (res.matches(arg) || res.contains(arg))
						return true;

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (sc != null) sc.close();
			}
		}

		return false;
	}
	private boolean strIsIp(String str) {
		return str.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
	}

	private void _loadBlackList() {
		Scanner sc = null;

		try {
			sc = new Scanner(new URL("http://myip.ms/files/blacklist/csf/latest_blacklist.txt").openStream());

			while (sc.hasNextLine()) {
				String ip = sc.nextLine();

				if (this.strIsIp(ip)) this.ipBlackList.add(ip);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sc != null) sc.close();
		}

	}

}
