package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ServersCommand extends Command {

	public ServersCommand() {
		super("servers");
	}


	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(new TextComponent(Config.prefix + "§7Liste des serveurs disponibles : §e" + UtariaBungee.getServerManager().getRawProxyServers()));
	}

}