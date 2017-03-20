package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UtariaCommand extends Command {

    public UtariaCommand() {
        super("utaria");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (!PlayersManager.playerHasRankLevel(pp, Config.adminMinLevel)) {
                BungeeMessages.cannotUseCommand(sender);
                return;
            }
        }

        if(args.length < 1){
            sender.sendMessage(new TextComponent(Config.prefix + "Utilisation : §6/utaria <setmaxplayers|setmotd|setmaintenance> [arguments...]"));
            return;
        }

        switch(args[0]){
            case "help":
            case "?":
            case "aide":

                sender.sendMessage(new TextComponent("§aAide de la commande Utaria."));
                sender.sendMessage(" ");
                sender.sendMessage(new TextComponent("§bgetmotd §8- §eAfficher le MOTD"));
                sender.sendMessage(new TextComponent("§bmaintenance §8- §eAfficher des infos sur la maintenance en cours"));
                sender.sendMessage(" ");
                sender.sendMessage(new TextComponent("§bsetmaxplayers §8- §eDéfinir le nombre maximum de joueurs"));
                sender.sendMessage(new TextComponent("§bsetmotd §8- §eDéfinir le MOTD"));
                sender.sendMessage(new TextComponent("§bsetmaintenance §8- §eActiver ou non la maintenance"));
                sender.sendMessage(new TextComponent("§bsetmaintenancemsg §8- §eDéfinir le message de maintenance"));
                sender.sendMessage(new TextComponent("§bsetmaintenancemotd §8- §eDéfinir le MOTD pendant la maintenance"));

                break;

            case "getmotd":
                sender.sendMessage(new TextComponent(Config.prefix + "§7Motd: " + Config.motd.replaceAll("%newline%", " ")));
                break;

            case "maintenance":
                String status = (Config.maintenance) ? "§aActivée" : "§cDésactivée";
                sender.sendMessage(new TextComponent(Config.prefix + "§7Status de la maintenance: " + status + "§7."));
                break;

            case "setmaxplayers":
                if(args.length < 2){
                    sender.sendMessage(new TextComponent(Config.prefix + "Utilisation : §6/utaria setmaxplayers <nombre>"));
                    return;
                }

                String maxplayersS = args[1];
                if( !this.isInteger(maxplayersS) ) {
                    sender.sendMessage(new TextComponent(Config.prefix + "§6" + maxplayersS + "§c n'est pas un nombre. Veuillez réessayer."));
                    return;
                }

                Integer maxplayers = Integer.valueOf(maxplayersS);
                if(maxplayers < 1) maxplayers = 1;

                Config.maxPlayers = maxplayers;
                Utils.updateConfigValue( "maxplayers", String.valueOf(maxplayers) );
                sender.sendMessage(new TextComponent(Config.prefix + "§aLe nombre de joueurs maximum est passé à §2" + maxplayersS + "§a."));
                break;
        }

    }

    private boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }


}

