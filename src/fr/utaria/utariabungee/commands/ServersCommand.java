package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.PlayerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ServersCommand extends Command {

	public ServersCommand() {
		super("servers");
	}


	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer){
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if( PlayerInfo.get(pp).getRankLevel() < Config.adminMinLevel ){
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}

		sender.sendMessage(new TextComponent(Config.prefix + "§7Liste des serveurs disponibles : §e" + UtariaBungee.getServerManager().getRawProxyServers()));
	}

}