package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.PlayerUtils;
import fr.utaria.utariabungee.utils.TimeParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UptimeCommand extends Command {

    public UptimeCommand() {
        super("uptime");
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

        int startTime   = UtariaBungee.getAutoRestartManager().getSecondsBetweenStart();
        int restartTime = UtariaBungee.getAutoRestartManager().getRestartHour();
        int upTime      = UtariaBungee.getAutoRestartManager().getUpTime() / 1000;

        if (sender instanceof ProxiedPlayer) PlayerUtils.sendHorizontalLineWithText((ProxiedPlayer) sender, "§eDurée de fontionnement", ChatColor.GREEN);
        else                                 sender.sendMessage(new TextComponent(ChatColor.GREEN + " ---- Durée de fonctionnement --- "));

        sender.sendMessage(" ");
        sender.sendMessage(new TextComponent("  §7 Durée de fonctionnement : §b" + TimeParser.secToHumanReadableString(startTime) ));
        sender.sendMessage(new TextComponent(""));

        if( !UtariaBungee.getAutoRestartManager().isDisabled() ) {
            sender.sendMessage(new TextComponent("  §7 Prochain redémarrage à : §6" + TimeParser.secToHumanReadableString(restartTime)));
            sender.sendMessage(new TextComponent("  §7 Redémarrage dans       : §e" + TimeParser.secToHumanReadableString(upTime)));
        } else {
            sender.sendMessage(new TextComponent("  §7 Redémarrage automatique §cdésactivé§7."));
            sender.sendMessage(new TextComponent("  §7 Tapez §a§l/report §r§7pour signaler le problème. Merci."));
        }
        sender.sendMessage(new TextComponent(""));
        sender.sendMessage(new TextComponent(""));
        sender.sendMessage(" ");

        if (sender instanceof ProxiedPlayer) PlayerUtils.sendHorizontalLine((ProxiedPlayer) sender, ChatColor.GREEN);
        else                                 sender.sendMessage(new TextComponent(ChatColor.GREEN + " --------------------------------- "));

    }

}
