package fr.utaria.utariabungee;

import net.md_5.bungee.api.ChatColor;

public class Config {

	public static final boolean DEBUG_MODE = false;

	public final static String ERROR_PREFIX = "§8(§4§l✖§8) §c";

	public final static String INFO_PREFIX = "§8(§e§l✦§8) §7";

	public final static String SUCCESS_PREFIX = "§7(§2§l✔§7) §a";

	public final static String MOD_PREFIX = "§8[§c§lMOD§8] §7";

	public static int socketServerPort = 15000;

	/*   Configuration du mode Maintenance   */
	public final static String MAINTENANCE_STATUS = "§cMaintenance en cours.";

	public final static String MAINTENANCE_ALLOWED_PERM = "maintenance";

	public final static int MAINTENANCE_LOGOUT_DELAY = 30;


	// public static int     maxPlayers    = 100;
	// public static String  motd          = "- motd -";
	public static boolean manualServers = false;

	public static String autoRestartTime = "5h30";

	public static String autoRestartMessage = ChatColor.RED + "Utaria revient, le serveur est en cours de redémarrage.";

	public static int autoMessageDelay = 4; // En minute


	public static String maxModoMuteTime = "12h";

	public static String maxModoBanTime = "7j";


	public final static String UPTODATE_PLUGINS_FOLDER = UtariaBungee.getInstance().getDataFolder().getParent() + "uptodate";

	public final static int CONFIG_CACHE_EXPIRATION = 55; // (en sec)

}
