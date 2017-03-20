package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.players.UtariaPlayer;
import fr.utaria.utariabungee.Config;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffCommand extends Command {

    public StaffCommand() {
        super("staff", null, "team");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        String onlineStaff = "";

        for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
            UtariaPlayer uPlayer = UtariaPlayer.get(player);
            PlayerInfo info = uPlayer.getPlayerInfo();

            if ( PlayersManager.playerHasRankLevel(player, 10) )
                onlineStaff += info.getHighestRank().getPrefix() + uPlayer.getPlayerName() + "§7, ";
        }

        if ( onlineStaff.equals("") ) onlineStaff = "§cAucun membre du staff en ligne.";
        else onlineStaff = "§7Staff en ligne : " + onlineStaff.substring(0, onlineStaff.length() - 2) + ".";

        sender.sendMessage(new TextComponent(Config.prefix + onlineStaff));
    }

}
