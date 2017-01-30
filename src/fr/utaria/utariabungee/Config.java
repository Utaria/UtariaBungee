package fr.utaria.utariabungee;

import net.md_5.bungee.api.ChatColor;

public class Config {

	public static int     socketServerPort   = 70000;
	public static String  prefix             = "§8[§bUtaria§8] §7";

    public static int moderationMinLevel = 30;
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
    public static boolean manualServers = UtariaBungee.getConfiguration().getBoolean("manual_servers");

    public static String autoRestartTime    = "5h30";
    public static String autoRestartMessage = ChatColor.RED + "Utaria revient, le serveur est en cours de redémarrage.";
	
}
