package fr.utaria.utariabungee.commands;

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
        if( !(sender instanceof ProxiedPlayer) ) return;
        ProxiedPlayer pp = (ProxiedPlayer) sender;

        if(PlayerInfo.get(pp).getRankLevel() < 40){
            BungeeMessages.cannotUseCommand(pp);
            return;
        }

        int startTime   = UtariaBungee.getAutoRestartManager().getSecondsBetweenStart();
        int restartTime = UtariaBungee.getAutoRestartManager().getRestartHour();
        int upTime      = UtariaBungee.getAutoRestartManager().getUpTime() / 1000;

        PlayerUtils.sendHorizontalLineWithText(pp, "§eDurée de fontionnement", ChatColor.GREEN);

        pp.sendMessage(" ");
        pp.sendMessage(new TextComponent("  §7 Durée de fonctionnement : §b" + TimeParser.secToHumanReadableString(startTime) ));
        pp.sendMessage(new TextComponent(""));

        if( !UtariaBungee.getAutoRestartManager().isDisabled() ) {
            pp.sendMessage(new TextComponent("  §7 Prochain redémarrage à : §6" + TimeParser.secToHumanReadableString(restartTime)));
            pp.sendMessage(new TextComponent("  §7 Redémarrage dans       : §e" + TimeParser.secToHumanReadableString(upTime)));
        } else {
            pp.sendMessage(new TextComponent("  §7 Redémarrage automatique §cdésactivé§7."));
            pp.sendMessage(new TextComponent("  §7 Tapez §a§l/report §r§7pour signaler le problème. Merci."));
        }
        pp.sendMessage(new TextComponent(""));
        pp.sendMessage(new TextComponent(""));
        pp.sendMessage(" ");

        PlayerUtils.sendHorizontalLine(pp, ChatColor.GREEN);

    }

}
