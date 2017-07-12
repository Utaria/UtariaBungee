package fr.utaria.utariabungee;

import net.md_5.bungee.api.ChatColor;

public class Config {

	public static int     socketServerPort   = 15000;
	public static String  prefix             = "§8[§bUtaria§8] §7";
	public static String  warningPrefix      = "§c(§4§l!§c) ";

    public static int moderationMinLevel = 30;
	public static int modoLevel          = 29;
    public static int adminMinLevel      = 40;

	/* [0, 10] = classical members */


    public static boolean maintenance              = false;
    public static String  maintenance_message      = "§cMaintenance en cours ! De retour très vite !\n§6Pour plus d'infos : §e@Utaria_FR";
    public static String  maintenance_motd         = "§c- Maintenance en cours -";
    public static String  maintenance_status       = "§cMaintenance en cours.";
    public static int     maintenance_logout_delay = 30;
	public static int     maintenanceMaxKickLevel  = 10;


    public static int     maxPlayers    = 100;
    public static String  motd          = "- motd -";
    public static boolean manualServers = false;

    public static String autoRestartTime    = "5h30";
    public static String autoRestartMessage = ChatColor.RED + "Utaria revient, le serveur est en cours de redémarrage.";
    public static int    autoMessageDelay   = 4; // En minute


    public static String maxModoMuteTime = "12h";
    public static String maxModoBanTime  = "7j";



	public final static String UPTODATE_PLUGINS_FOLDER = UtariaBungee.getInstance().getDataFolder().getParent() + "uptodate";
	public final static int    CONFIG_CACHE_EXPIRATION = 55; // (en sec)
	
}
